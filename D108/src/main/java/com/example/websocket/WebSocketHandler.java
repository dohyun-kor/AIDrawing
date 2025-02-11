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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
                addSessionToRoom(roomId, session);
                sendExistingDrawings(session, roomId);
                broadcastMessageToRoom(roomId, payload, session);
                rService.incrementUserCount(Integer.parseInt(roomId));
            } else if ("leave".equals(event)) {
                removeSessionFromRoom(roomId, session);
            } else if ("draw".equals(event)) {
                // 그림 데이터 추가 (CopyOnWriteArrayList 사용)
                roomDrawings.putIfAbsent(roomId, new CopyOnWriteArrayList<>());
                roomDrawings.get(roomId).add(payload);
                broadcastMessageToRoom(roomId, payload, session);
            } else if ("clearDrawing".equals(event)) {
                roomDrawings.remove(roomId);
                broadcastMessageToRoom(roomId, payload, null);
            } else if ("chat".equals(event)) {
                broadcastMessageToRoom(roomId, payload, session);
            }
        } catch (Exception e) {
            System.err.println("WebSocket 메시지 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
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
            roomSessions.get(roomId).remove(session);
            rService.decrementUserCount(Integer.parseInt(roomId));
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

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        for (String roomId : roomSessions.keySet()) {
            removeSessionFromRoom(roomId, session);
        }
        System.out.println("사용자 연결 종료: " + session.getId() + " 상태: " + status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket 오류 발생: " + exception.toString());
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
