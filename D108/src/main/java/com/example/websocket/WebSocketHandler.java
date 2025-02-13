// WebSocketHandler.java
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
import java.util.concurrent.atomic.AtomicReference;
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

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
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
                rService.incrementUserCount(Integer.parseInt(roomId), userId);
                sendExistingParticipants(session, roomId);
                broadcastMessageToRoom(roomId, payload, session);
            } else if ("leave".equals(event)) {
                handleUserLeave(session, roomId);
            } else if ("draw".equals(event)) {
                broadcastMessageToRoom(roomId, payload, session);
            } else if ("cleardrawing".equals(event)) {
                broadcastMessageToRoom(roomId, payload, null);
            } else if ("chat".equals(event)) {
                correctCheck(roomId, payload);
                broadcastMessageToRoom(roomId, payload, session);
            } else if ("start".equals(event)) {
                broadcastMessageToRoom(roomId, payload, session);
                gamestart(roomId, 0,1);
            } else if ("colorchange".equals(event)) {
                broadcastMessageToRoom(roomId, payload, session);
            } else if("changeroominfo".equals(event)){
                broadcastMessageToRoom(roomId, payload, null);
            } else if ("topicselect".equals(event)){
                topicSelect(roomId, payload);
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

        //Redis Set 초기화
        redisTemplate.delete(ROOM_PREFIX + roomId + ":correctusers");

        redisTemplate.opsForHash().put(key, "maxround", room.getRounds());
        redisTemplate.opsForHash().put(key, "remaintime", room.getRoundTime());
        redisTemplate.opsForHash().put(key, "turn", nowturn);
        redisTemplate.opsForHash().put(key, "status", "play");
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
        System.out.println("선정된 주제들"+topics);

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

    public void waitTopicSelect(String roomId, int nowturn) {
        AtomicInteger remainTime = new AtomicInteger(15);
        String key = ROOM_PREFIX + roomId;

        // AtomicReference를 사용하여 future를 감싸줍니다.
        AtomicReference<ScheduledFuture<?>> futureRef = new AtomicReference<>();

        try {
            ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
                try {
                    String topic = (String) redisTemplate.opsForHash().get(key, "topic");

                    if (!"wait".equals(topic) || remainTime.get() <= 0) {
                        // AtomicReference에서 future를 가져와 취소합니다.
                        ScheduledFuture<?> future = futureRef.get();
                        if (future != null) {
                            future.cancel(true);
                        }
                        if (remainTime.get() <= 0) {
                            try {
                                endRound(roomId, nowturn);
                            } catch (IOException | InterruptedException e) {
                                System.err.println("waitTopicSelect endRound 중 오류 발생: " + e.getMessage());
                            }
                        } else {
                            Map<String, Object> messageMap = new HashMap<>();
                            messageMap.put("event", "topicselect");
                            messageMap.put("roomId", roomId);
                            try {
                                broadcastMessageToRoom(roomId, createJsonMessage(messageMap), null);
                            } catch (IOException e) {
                                System.err.println("topicselect 메시지 전송 중 오류 발생: " + e.getMessage());
                            }
                            startRoundTimer(roomId, nowturn);
                        }
                        return;
                    }

                    broadcastRemainingTime(roomId, remainTime);
                    remainTime.decrementAndGet();
                    redisTemplate.opsForHash().put(key, "remaintime", remainTime.get());

                } catch (Exception e) {
                    System.err.println("waitTopicSelect 작업 중 오류 발생: " + e.getMessage());
                    // AtomicReference에서 future를 가져와 취소합니다.
                    ScheduledFuture<?> future = futureRef.get();
                    if (future != null) {
                        future.cancel(true);
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);

            // 생성된 ScheduledFuture를 AtomicReference에 저장합니다.
            futureRef.set(scheduledFuture);
        } catch (Exception e) {
            System.err.println("waitTopicSelect 스케줄링 중 오류 발생: " + e.getMessage());
        }
    }

    // 토픽을 정했을 때
    private void topicSelect(String roomId, String message){
        Map<String, String> data = parseJson(message);
        String topic = data.get("topic");
        rService.setTopic(Integer.parseInt(roomId),topic);
    }

    private void correctCheck(String roomId, String payload) throws IOException, InterruptedException {
        if ("play".equals(rService.getRoomInfo(Integer.parseInt(roomId),"status"))) {
            Map<String, String> data = parseJson(payload);
            String answer = data.get("message");
            String userId = data.get("userId");

            if (answer.equals(rService.getRoomInfo(Integer.parseInt(roomId),"topic"))) {
                // 정답 메시지 전송
                broadcastMessageToRoom(roomId, createCorrectMessage(roomId, userId), null);

                // Redis Set에 정답 사용자 추가
                redisTemplate.opsForSet().add(ROOM_PREFIX + roomId + ":correctusers", userId);

                roundcheck(roomId);
            }
        }
    }

    private void roundcheck(String roomId) throws IOException, InterruptedException {
        // Redis Set에서 정답 사용자 수를 가져옵니다.
        Long correctCount = redisTemplate.opsForSet().size(ROOM_PREFIX + roomId + ":correctusers");
        int numbers = (int) rService.getRoomInfo(Integer.parseInt(roomId),"numbers");
        if (correctCount != null && correctCount >= numbers / 2) {
            int nowturn = (int) rService.getRoomInfo(Integer.parseInt(roomId),"turn");
            endRound(roomId, nowturn);
        }
    }

    // 라운드 시작시 Timer기능
    private void startRoundTimer(String roomId, int nowturn) {
        AtomicInteger remainingTime = new AtomicInteger((Integer) rService.getRoomInfo(Integer.parseInt(roomId),"remaintime"));

        roundTimerTask = scheduler.scheduleAtFixedRate(() -> {
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
                    System.err.println("broadcastRemainingTime 에러"+ e.getMessage());
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    // 라운드 종료
    private void endRound(String roomId, int nowturn) throws IOException, InterruptedException {
        // 타이머 종료
        cancelRoundTimer();

        // 현재 라운드와 최대 라운드 비교
        int curround = (int) rService.getRoomInfo(Integer.parseInt(roomId),"currentround");
        int maxround = (int) rService.getRoomInfo(Integer.parseInt(roomId),"maxround");
        if (curround+1 > maxround) {
            endGame(roomId); // 게임 종료 처리
            return;
        }

        int count = (int) rService.getRoomInfo(Integer.parseInt(roomId),"numbers");

        // 다음 턴 유저 계산 (현재 턴 + 1, count로 나눈 나머지)
        int nextturn = (nowturn + 1) % count;

        // 새로운 라운드 시작
        gamestart(roomId, nextturn,curround+1);
    }

    // 게임 종료
    private void endGame(String roomId) throws IOException {
        cancelRoundTimer();
        redisTemplate.opsForHash().put(ROOM_PREFIX+roomId, "status", "wait");

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "endgame");
        messageMap.put("roomId", roomId);

        // ObjectMapper를 사용해 메시지 객체를 JSON 형식으로 변환
        broadcastMessageToRoom(roomId, createJsonMessage(messageMap), null);
    }

    // 라운드 타이머 멈추기
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
            List<String> participants = rService.getParticipants(Integer.parseInt(roomId));
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("event", "participants");
            messageMap.put("participants", participants);

            sendMessageSafely(session, createJsonMessage(messageMap));
        }
    }

    private Map<String, String> parseJson(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonString, Map.class);
        } catch (IOException e) {
            System.err.println("JSON 파싱 오류: " + e.getMessage());
            return null;
        }
    }

    private String createJsonMessage(Object messageObject) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(messageObject);
    }

    private String createCorrectMessage(String roomId, String userId) throws IOException {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "correct");
        messageMap.put("userId", userId);
        messageMap.put("roomId", roomId);

        return createJsonMessage(messageMap);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = sessionUserMap.get(session.getId());
        if (userId != null) {
            // 퇴장 처리 로직 호출
            handleUserLeave(session, null);
        }
        sessions.remove(session.getId());
        System.out.println("사용자 연결 해제됨: " + session.getId() + ", 상태: " + status);
    }

    private void handleUserLeave(WebSocketSession session, String roomId) throws IOException {
        String userId = sessionUserMap.get(session.getId());
        if (roomId == null) {
            for (Map.Entry<String, Set<WebSocketSession>> entry : roomSessions.entrySet()) {
                if (entry.getValue().contains(session)) {
                    roomId = entry.getKey();
                    break;
                }
            }
        }

        if (roomId != null && userId != null) {
            removeSessionFromRoom(roomId, session);
            rService.decrementUserCount(Integer.parseInt(roomId), userId);
            broadcastLeaveMessage(roomId, userId);

            String roomHost = rService.getRoomHost(Integer.parseInt(roomId));

            // 방에 남은 유저가 없을 때 방을 삭제하는 로직
            Set<WebSocketSession> roomUsers = roomSessions.get(roomId);
            if (roomUsers == null || roomUsers.isEmpty()) {
                // Redis 및 DB에서 방 정보 삭제
                rService.deleteRoom(Integer.parseInt(roomId));
                System.out.println("방 " + roomId + " 가 삭제되었습니다.");
            } else if (roomHost != null && roomHost.equals(userId)) {
                // 방장이 나갔을 경우 방장 교체 로직
                Optional<WebSocketSession> newHostSession = roomUsers.stream().findAny();
                if (newHostSession.isPresent()) {
                    String newHostId = sessionUserMap.get(newHostSession.get().getId());
                    rService.setRoomHost(Integer.parseInt(roomId), newHostId);
                    broadcastHostChange(roomId, newHostId);
                    System.out.println("방 " + roomId + " 의 방장이 " + newHostId + " 로 변경되었습니다.");
                }
            }
        }

        sessionUserMap.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket 전송 오류: " + exception.getMessage());
    }
}
