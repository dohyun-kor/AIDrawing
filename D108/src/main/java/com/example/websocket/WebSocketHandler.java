package com.example.websocket;

import com.example.model.service.RoomService;
import com.example.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    UserService uService;

    @Autowired
    RoomService rService;

    // 세션 ID ↔ userId 매핑
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    // 방 별 그림 데이터 저장
    private final Map<String, List<String>> roomDrawings = new ConcurrentHashMap<>();

    // 전체 클라이언트 세션 (세션 ID -> 세션 객체)
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 방 별 세션 관리 (방 ID -> 해당 방에 접속한 세션 Set)
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        System.out.println("사용자 연결됨: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            System.out.println("수신자: " + session + "\n수신된 메시지: " + payload);

            Map<String, String> data = parseJson(payload);
            String roomId = data.get("roomId");
            String event = data.get("event");

            if ("join".equals(event)) {
                String userId = data.get("userId");
                sessionUserMap.put(session.getId(), userId); // 세션 ID와 userId 매핑

                addSessionToRoom(roomId, session);
                sendExistingDrawings(session, roomId);
                sendExistingParticipants(session, roomId);
                rService.incrementUserCount(Integer.parseInt(roomId), userId);
                broadcastMessageToRoom(roomId, payload, session);
            } else if ("leave".equals(event)) {
                handleUserLeave(session, roomId);
            } else if ("draw".equals(event)) {
                roomDrawings.putIfAbsent(roomId, new CopyOnWriteArrayList<>());
                roomDrawings.get(roomId).add(payload);
                broadcastMessageToRoom(roomId, payload, session);
            } else if ("cleardrawing".equals(event)) {
                roomDrawings.remove(roomId);
                broadcastMessageToRoom(roomId, payload, null);
            } else if ("chat".equals(event)) {
                broadcastMessageToRoom(roomId, payload, session);
            } else if("start".equals(event)){
                gamestart(roomId);
                broadcastMessageToRoom(roomId, payload, session);
            } else if("colorchange".equals(event) || "changeroominfo".equals(event)){
                broadcastMessageToRoom(roomId, payload, session);
            }
        } catch (Exception e) {
            System.err.println("WebSocket 메시지 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
    private void gamestart(String roomId){
        rService.startGame(Integer.parseInt(roomId));
    }

    private void handleUserLeave(WebSocketSession session, String roomId) throws IOException {
        String userId = sessionUserMap.get(session.getId());
        if (userId != null) {
            String currentHostId = rService.getRoomHost(Integer.parseInt(roomId));

            // 사용자가 방장이라면 방장 변경
            if (userId.equals(currentHostId)) {
                List<String> participants = rService.getParticipants(Integer.parseInt(roomId));
                participants.remove(userId); // 떠나는 유저 제외

                if (!participants.isEmpty()) {
                    String newHostId = participants.get(0); // 첫 번째 유저를 새 방장으로 선택
                    rService.setRoomHost(Integer.parseInt(roomId), newHostId); // Redis에서 변경

                    // 방장 변경 메시지 broadcast
                    String hostChangeMessage = "{\"event\":\"hostchange\", \"userId\":\"" + newHostId + "\"}";
                    broadcastMessageToRoom(roomId, hostChangeMessage, null);
                }
            }

            // 기존 로직 유지
            rService.decrementUserCount(Integer.parseInt(roomId), userId);
            sessionUserMap.remove(session.getId());
        }
        removeSessionFromRoom(roomId, session);
        broadcastMessageToRoom(roomId, "{\"event\":\"leave\", \"userId\":\"" + userId + "\"}", null);
    }


    private void addSessionToRoom(String roomId, WebSocketSession session) {
        roomSessions.putIfAbsent(roomId, ConcurrentHashMap.newKeySet());
        roomSessions.get(roomId).add(session);
        System.out.println("세션 " + session.getId() + " 가 방 " + roomId + " 에 추가됨.");
    }

    private void removeSessionFromRoom(String roomId, WebSocketSession session) {
        if (roomSessions.containsKey(roomId)) {
            roomSessions.get(roomId).remove(session);
            System.out.println("세션 " + session.getId() + " 가 방 " + roomId + " 에서 제거됨.");
        }
    }

    private void broadcastMessageToRoom(String roomId, String message, WebSocketSession excludeSession) throws IOException {
        Set<WebSocketSession> clients = roomSessions.get(roomId);
        if (clients != null) {
            synchronized (clients) {
                for (WebSocketSession client : clients) {
                    if (excludeSession != null && client.getId().equals(excludeSession.getId())) {
                        continue;
                    }
                    sendMessageSafely(client, message);
                }
            }
        }
    }

    private void sendMessageSafely(WebSocketSession session, String message) {
        synchronized (session) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    System.err.println("메시지 전송 오류: " + e.getMessage());
                }
            }
        }
    }

    private void sendExistingDrawings(WebSocketSession session, String roomId) throws IOException {
        if (roomDrawings.containsKey(roomId)) {
            for (String drawing : roomDrawings.get(roomId)) {
                sendMessageSafely(session, drawing);
            }
        }
    }

    private void sendExistingParticipants(WebSocketSession session, String roomId) throws IOException {
        if(rService.getUserCount(Integer.parseInt(roomId)) >= 1){
            List<String> existingParticipants = rService.getParticipants(Integer.parseInt(roomId));
            String hostId = rService.getRoomHost(Integer.parseInt(roomId));

            // JSON 형태의 메시지 생성
            String message = createExistingUserMessage(existingParticipants, hostId);

            // 클라이언트에게 전송
            sendMessageSafely(session, message);
        }
    }

    // 기존 참가자 정보와 hostId를 JSON 문자열로 변환
    private String createExistingUserMessage(List<String> participants, String hostId) {
        String usersJson = participants.toString(); // 리스트를 JSON 배열 형식으로 변환
        return "{\"event\": \"existinguser\", \"users\": " + usersJson + ", \"hostId\": \"" + hostId + "\"}";
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
        handleUserDisconnect(session);
        System.out.println("사용자 연결 종료: " + session.getId() + " 상태: " + status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws IOException {
        System.err.println("WebSocket 오류 발생: " + exception.toString());
        handleUserDisconnect(session);
        if (session.isOpen()) {
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException e) {
                System.err.println("소켓 종료 오류: " + e.getMessage());
            }
        }
    }

    private void handleUserDisconnect(WebSocketSession session) throws IOException {
        String userId = sessionUserMap.get(session.getId());
        if (userId != null) {
            for (String roomId : roomSessions.keySet()) {
                if (roomSessions.get(roomId).contains(session)) {
                    // 현재 방장의 ID 확인
                    String currentHostId = rService.getRoomHost(Integer.parseInt(roomId));

                    // 유저 카운트 감소 및 방에서 제거
                    rService.decrementUserCount(Integer.parseInt(roomId), userId);
                    removeSessionFromRoom(roomId, session);

                    // 모든 사용자에게 leave 이벤트 알림
                    broadcastMessageToRoom(roomId, "{\"event\":\"leave\", \"userId\":\"" + userId + "\"}", null);

                    // 현재 떠나는 유저가 방장인지 확인
                    if (userId.equals(currentHostId)) {
                        // 새로운 방장 선택
                        Set<WebSocketSession> remainingSessions = roomSessions.get(roomId);
                        if (remainingSessions != null && !remainingSessions.isEmpty()) {
                            // 남아있는 유저 중 무작위로 새로운 방장 선택
                            WebSocketSession newHostSession = remainingSessions.iterator().next();
                            String newHostId = sessionUserMap.get(newHostSession.getId());

                            // Redis에서 새로운 방장 정보 업데이트
                            rService.setRoomHost(Integer.parseInt(roomId), newHostId);

                            // 새로운 방장 정보를 모든 클라이언트에게 broadcast
                            String hostChangeMessage = "{\"event\":\"hostchange\", \"userId\":\"" + newHostId + "\"}";
                            broadcastMessageToRoom(roomId, hostChangeMessage, null);
                        }
                    }
                }
            }
            sessionUserMap.remove(session.getId());
        }
        sessions.remove(session.getId());
    }


    private Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.replaceAll("[{}\"]", "");
        for (String pair : json.split(",")) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return map;
    }
}
