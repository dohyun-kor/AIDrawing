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

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    UserService uService;

    @Autowired
    RoomService rService;

    // 방 별로 그림 데이터 저장 (방 ID -> 그림 데이터 리스트)
    private final Map<String, List<String>> roomDrawings = new ConcurrentHashMap<>();

    // 전체 클라이언트 세션 (세션 ID -> 세션 객체) - 선택사항 (전체 관리용)
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 방 별 세션 관리 (방 ID -> 해당 방에 접속한 세션 Set)
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        System.out.println("사용자 연결됨: " + session.getId());
        // 여기서 기본적으로 방에 넣을 필요가 있다면 클라이언트로부터 방 정보를 받아서 추가하는 로직을 구현
        // 예시로 임시로 아무 방에나 넣는다면 아래와 같이 할 수 있음.
        // addSessionToRoom("defaultRoom", session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            System.out.println("수신자:" + session + "\n수신된 메시지: " + payload);

            Map<String, String> data = parseJson(payload);
            String roomId = data.get("roomId");
            String event = data.get("event");

            if ("join".equals(event)) {
                // 사용자가 특정 방에 입장하는 경우, 해당 방의 세션에 추가
                addSessionToRoom(roomId, session);
                sendExistingDrawings(session, roomId);
                broadcastMessageToRoom(roomId, payload, session);
                rService.incrementUserCount(Integer.parseInt(roomId));
            } else if("leave".equals(event)){
                removeSessionFromRoom(roomId, session);
                rService.decrementUserCount(Integer.parseInt(roomId));
            } else if ("draw".equals(event)) {
                // 해당 방의 그림 데이터 저장
                roomDrawings.putIfAbsent(roomId, new ArrayList<>());
                roomDrawings.get(roomId).add(payload);
                // 해당 방에만 브로드캐스트
                broadcastMessageToRoom(roomId, payload, session);
            } else if ("clearDrawing".equals(event)) {
                // 해당 방의 그림 데이터 삭제
                roomDrawings.remove(roomId);
                // 해당 방의 모든 클라이언트에게 알림
                broadcastMessageToRoom(roomId, payload, null);
            } else if ("chat".equals(event)){
                broadcastMessageToRoom(roomId,payload,session);
            }
        } catch (Exception e) {
            System.err.println("WebSocket 메시지 처리 중 오류 발생: " + e.getMessage());
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    private void addSessionToRoom(String roomId, WebSocketSession session) {
        roomSessions.putIfAbsent(roomId, ConcurrentHashMap.newKeySet());
        roomSessions.get(roomId).add(session);
        System.out.println("세션 " + session.getId() + " 가 방 " + roomId + " 에 추가됨.");
    }

    private void removeSessionFromRoom(String roomId, WebSocketSession session) {
        if (roomSessions.containsKey(roomId)) {
            if(roomSessions.get(roomId).contains(session)){
                roomSessions.get(roomId).remove(session);
                System.out.println("세션 " + session.getId() + " 가 방 " + roomId + " 에서 제거됨.");
            }
        }
    }

    // 특정 방에 있는 클라이언트에게만 메시지를 브로드캐스트
    private void broadcastMessageToRoom(String roomId, String message, WebSocketSession excludeSession) throws IOException {
        Set<WebSocketSession> clients = roomSessions.get(roomId);
        if (clients != null) {
            synchronized (clients) {
                for (WebSocketSession client : clients) {
                    // 제외할 세션이 있다면 건너뜀
                    if (excludeSession != null && client.getId().equals(excludeSession.getId())) {
                        continue;
                    }
                    if (client.isOpen()) {
                        try {
                            client.sendMessage(new TextMessage(message));
                        } catch (IOException e) {
                            System.err.println("WebSocket 메시지 전송 중 오류 발생: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private void sendExistingDrawings(WebSocketSession session, String roomId) throws IOException {
        if (roomDrawings.containsKey(roomId)) {
            for (String drawing : roomDrawings.get(roomId)) {
                session.sendMessage(new TextMessage(drawing));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        // 모든 방에서 해당 세션을 제거하도록 반복 처리
        for (String roomId : roomSessions.keySet()) {
            removeSessionFromRoom(roomId, session);
        }
        System.out.println("사용자 연결 종료: " + session.getId() + " 상태: " + status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket 오류 발생: " + exception.getMessage());
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
        sessions.remove(session.getId());
        for (String roomId : roomSessions.keySet()) {
            removeSessionFromRoom(roomId, session);
        }
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
