package com.example.websocket;

import com.example.model.dto.DifficultyDto;
import com.example.model.dto.RoomDto;
import com.example.model.service.DifficultyService;
import com.example.model.service.RoomService;
import com.example.model.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    UserService uService;

    @Autowired
    RoomService rService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    DifficultyService difficultyService;

    private static final String ROOM_PREFIX = "room:";
    // 세션 ID ↔ userId 매핑
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    // 방 별 그림 데이터 저장
    private final Map<String, List<String>> roomDrawings = new ConcurrentHashMap<>();

    // 전체 클라이언트 세션 (세션 ID -> 세션 객체)
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 방 별 세션 관리 (방 ID -> 해당 방에 접속한 세션 Set)
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> roundTimerTask;

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
//                sendExistingDrawings(session, roomId);
                rService.incrementUserCount(Integer.parseInt(roomId), userId);
                sendExistingParticipants(session, roomId);
                broadcastMessageToRoom(roomId, payload, session);
            } else if ("leave".equals(event)) {
                handleUserLeave(session, roomId);
            } else if ("draw".equals(event)) {
//                roomDrawings.putIfAbsent(roomId, new CopyOnWriteArrayList<>());
//                roomDrawings.get(roomId).add(payload);
                broadcastMessageToRoom(roomId, payload, session);
            } else if ("cleardrawing".equals(event)) {
//                roomDrawings.remove(roomId);
                broadcastMessageToRoom(roomId, payload, null);
            } else if ("chat".equals(event)) {
                broadcastMessageToRoom(roomId, payload, session);
            } else if ("start".equals(event)) {
                gamestart(roomId, 0,1);
                broadcastMessageToRoom(roomId, payload, session);
            } else if ("colorchange".equals(event) || "changeroominfo".equals(event)) {
                broadcastMessageToRoom(roomId, payload, session);
            }
        } catch (Exception e) {
            System.err.println("WebSocket 메시지 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    private void gamestart(String roomId, int nowturn, int currentround) throws IOException, InterruptedException {
        RoomDto room = rService.selectRoom(Integer.parseInt(roomId));
        String key = ROOM_PREFIX + roomId;

        redisTemplate.opsForHash().put(key, "currentround", currentround);
        redisTemplate.opsForHash().put(key, "maxround", room.getRounds());
        redisTemplate.opsForHash().put(key, "remaintime", room.getRoundTime());
        redisTemplate.opsForHash().put(key, "turn", nowturn);
        redisTemplate.opsForHash().put(key, "topic", "wait");


        // 참여자 목록 가져오기
        ArrayList<String> participants = (ArrayList<String>) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "participants");
        String currentPlayer = participants.get(nowturn);
        System.out.println("현재 턴인 유저 : " + currentPlayer);

        // 넥스트 라운드로 보낼 메시지 내용 생성
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "nextround");
        messageMap.put("round", currentround); // 현재 라운드 번호
        messageMap.put("turn", currentPlayer); // 현재 턴을 가진 유저

        // 방의 모든 유저에게 메시지 전송
        broadcastMessageToRoom(roomId, createJsonMessage(messageMap), null);

        List<DifficultyDto> topics = difficultyService.getTopicsByDifficulty(room.getLevel().toString(), 2);

        // 원하는 형태로 topic을 가공
        List<String> topicList = topics.stream()
                .map(DifficultyDto::getTopic)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("topic", topicList);
        // JSON으로 변환하여 사용하거나, 다른 형태로 활용 가능
        try {
            String jsonResponse = createJsonMessage(response);
            // jsonResponse를 broadcast하거나, 다른 용도로 사용
            System.out.println("topic Response: " + jsonResponse);  // 예시: 콘솔에 출력
            broadcastMessageToRoom(roomId, jsonResponse, null); // 추가: 클라이언트에 전송
        } catch (IOException e) {
            System.err.println("JSON 변환 오류: " + e.getMessage());
        }
        waitTopicSelect(roomId,nowturn);
    }

    public void waitTopicSelect(String roomId, int nowturn) throws IOException, InterruptedException {
        AtomicInteger remainTime = new AtomicInteger(15);

        // 시간만큼 대기하며, 매초마다 broadcastRemainingTime 호출
        while (remainTime.get()>0) {
            String topic = (String) redisTemplate.opsForHash().get(ROOM_PREFIX+roomId,"topic");
            if(!topic.equals("wait")) break;
            // 방송하기 (remainTime을 전달)
            broadcastRemainingTime(roomId, remainTime);
            // 1초 대기
            Thread.sleep(1000);
            // remainTime 1초 감소
            remainTime.decrementAndGet();
        }

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "topicselect");
        messageMap.put("roomId", roomId); // 다음 라운드 번호
        startRoundTimer(roomId,nowturn);
    }

    private void startRoundTimer(String roomId, int nowturn) {
        AtomicInteger remainingTime = new AtomicInteger((Integer) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "remaintime"));

        roundTimerTask = scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (remainingTime.get() <= 0) {
                try {
                    endRound(roomId, nowturn);
                } catch (IOException | InterruptedException e) {
                    System.out.println("다음 라운드로 넘어가는 도중 에러 발생");
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            } else {
                remainingTime.decrementAndGet();
                redisTemplate.opsForHash().put(ROOM_PREFIX + roomId, "remaintime", remainingTime.get());
                try {
                    broadcastRemainingTime(roomId, remainingTime);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void endRound(String roomId, int nowturn) throws IOException, InterruptedException {
        // 현재 라운드 번호를 가져와서 +1
        Integer round = (Integer) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "round");
        redisTemplate.opsForHash().put(ROOM_PREFIX + roomId, "round", round + 1);

        // 타이머 종료
        cancelRoundTimer();

        // 현재 라운드와 최대 라운드 비교
        int curround = (int) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "currentround");
        int maxround = (int) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "maxround");
        if (curround+1 > maxround) {
            endGame(roomId); // 게임 종료 처리
            return;
        }

        int count = (int) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "numbers");

        // 다음 턴 유저 계산 (현재 턴 + 1, count로 나눈 나머지)
        int nextturn = (nowturn + 1) % count;

        // 새로운 라운드 시작
        gamestart(roomId, nextturn,curround+1);
    }

    private void endGame(String roomId) throws IOException {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "endgame");
        messageMap.put("roomId", roomId);

        // ObjectMapper를 사용해 메시지 객체를 JSON 형식으로 변환
        broadcastMessageToRoom(roomId, createJsonMessage(messageMap), null);
    }

    private void cancelRoundTimer() {
        if (roundTimerTask != null && !roundTimerTask.isCancelled()) {
            roundTimerTask.cancel(true);
        }
    }

    private void broadcastRemainingTime(String roomId, AtomicInteger remainingTime) throws IOException {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "remaintime");
        messageMap.put("remaintime", remainingTime);
        broadcastMessageToRoom(roomId, createJsonMessage(messageMap), null);
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
                    broadcastHostChange(roomId, newHostId);
                }
            }

            // 기존 로직 유지
            rService.decrementUserCount(Integer.parseInt(roomId), userId);
            sessionUserMap.remove(session.getId());
        }
        removeSessionFromRoom(roomId, session);
        broadcastLeaveMessage(roomId, userId);
    }

    private void broadcastHostChange(String roomId, String newHostId) throws IOException {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "hostchange");
        messageMap.put("userId", newHostId);
        broadcastMessageToRoom(roomId, createJsonMessage(messageMap), null);
    }

    private void broadcastLeaveMessage(String roomId, String userId) throws IOException {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "leave");
        messageMap.put("userId", userId);
        broadcastMessageToRoom(roomId, createJsonMessage(messageMap), null);
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

    public void broadcastMessageToRoom(String roomId, String message, WebSocketSession excludeSession) throws IOException {
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
        System.out.println(redisTemplate.opsForHash().get(ROOM_PREFIX+roomId, "numbers"));
        if (rService.getUserCount(Integer.parseInt(roomId)) >= 1) {
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
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "existinguser");
        messageMap.put("users", participants);
        messageMap.put("hostId", hostId);
        return createJsonMessage(messageMap);
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
                    broadcastLeaveMessage(roomId, userId);

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
                            broadcastHostChange(roomId, newHostId);
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
                map.put(keyValue[0], keyValue[1]);
            }
        }
        return map;
    }

    private String createJsonMessage(Map<String, Object> messageMap) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(messageMap);
        } catch (IOException e) {
            System.err.println("JSON 변환 오류: " + e.getMessage());
            return null;
        }
    }
}
