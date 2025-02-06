package com.example.websocket;

import com.example.model.dto.UserDto;
import com.example.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {


    @Autowired
    UserService uService;
    // 방 별로 그림 데이터 저장 (방 ID -> 그림 데이터 리스트)
    private final Map<String, List<String>> roomDrawings = new ConcurrentHashMap<>();
    
    // 현재 접속 중인 클라이언트 세션 (세션 ID -> 세션 객체)
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        System.out.println("사용자 연결됨: " + session.getId());
        System.out.println("container 이름으로 바꿈.");
        UserDto newuser = new UserDto("dnen14@naver.com","ssafy","ssafy","ssafyKing");
        uService.join(newuser);
        System.out.println("유저 정보 :" + newuser);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            System.out.println("수신된 메시지: " + payload);

            Map<String, String> data = parseJson(payload);
            String roomId = data.get("roomId");

            if ("draw".equals(data.get("event"))) {
                roomDrawings.putIfAbsent(roomId, new ArrayList<>());
                roomDrawings.get(roomId).add(payload);
            } else if ("join".equals(data.get("event"))) {
                sendExistingDrawings(session, roomId);
            } else if ("clearDrawing".equals(data.get("event"))) {
                roomDrawings.remove(roomId); // 해당 방의 그림 데이터 삭제
                broadcastMessageToRoom(roomId, payload); // 모든 클라이언트에게 알림
            }

            // 모든 클라이언트에게 그림 데이터 브로드캐스트 (Thread-safe)
            synchronized (this) {
                for (WebSocketSession client : sessions.values()) {
                    if (client.isOpen() && client.getId() != session.getId()) {
                        client.sendMessage(new TextMessage(payload));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("WebSocket 메시지 처리 중 오류 발생: " + e.getMessage());
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    private void sendExistingDrawings(WebSocketSession session, String roomId) throws IOException {
        if (roomDrawings.containsKey(roomId)) {
            for (String drawing : roomDrawings.get(roomId)) {
                session.sendMessage(new TextMessage(drawing));
            }
        }
    }

    private void broadcastMessageToRoom(String roomId, String message) throws IOException {
        synchronized (this) {
            for (WebSocketSession client : sessions.values()) {
                if (client.isOpen()) {
                    try {
                        client.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        System.err.println("🚨 WebSocket 메시지 전송 중 오류 발생: " + e.getMessage());
                    }
                }
            }
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        System.out.println("사용자 연결 종료: " + session.getId() + " 상태: " + status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket 오류 발생: " + exception.getMessage());
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
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

