package com.example.websocket;

import com.example.model.dao.UserDao;
import com.example.model.dto.DifficultyDto;
import com.example.model.dto.RoomDto;
import com.example.model.service.DalleService;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    UserService uService;

    @Autowired
    RoomService rService;

    @Autowired
    UserDao uDao;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    DifficultyService difficultyService;

    @Autowired
    DalleService dalleService;

    private static final String ROOM_PREFIX = "room:";

    // 세션 ID ↔ userId 매핑
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    // 전체 클라이언트 세션 (세션 ID -> 세션 객체)
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 방 별 세션 관리 (방 ID -> 해당 방에 접속한 세션 Set)
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    // 방 별 점수 저장
    private final Map<String, Map<String, Integer>> roomScores = new ConcurrentHashMap<>();

    private final Map<String, ScheduledFuture<?>> roomTimerTasks = new ConcurrentHashMap<>();

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
                broadcastJoinChatMessage(roomId, userId);
            } else if ("leave".equals(event)) {
                handleUserLeave(session);
            } else if ("draw".equals(event)) {
                broadcastMessageToRoom(roomId, payload, session);
            } else if ("cleardrawing".equals(event)) {
                broadcastMessageToRoom(roomId, payload, null);
            } else if ("chat".equals(event)) {
                correctCheck(roomId, payload, null);
            } else if ("correctchat".equals(event)) {
                broadcastCorrectChatMessageToRoom(roomId, payload, null);
            } else if ("start".equals(event)) {
                broadcastMessageToRoom(roomId, payload, session);
                gamestart(roomId, 0, 1);
            } else if ("colorchange".equals(event)) {
                broadcastMessageToRoom(roomId, payload, session);
            } else if ("changeroominfo".equals(event)) {
                broadcastMessageToRoom(roomId, payload, null);
            } else if ("topicselect".equals(event)) {
                topicSelect(roomId, payload);
            } else if ("resultscore".equals(event)) {
                scoreupdate(data);
            }
        } catch (Exception e) {
            System.err.println("WebSocket 메시지 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    private void gamestart(String roomId, int nowturn, int currentround) throws IOException {
        RoomDto room = rService.selectRoom(Integer.parseInt(roomId));
        String key = ROOM_PREFIX + roomId;

        ArrayList<String> correctuser = new ArrayList<>();
        redisTemplate.opsForHash().put(key, "currentround", currentround);
        redisTemplate.opsForHash().put(key, "maxround", room.getRounds());
        redisTemplate.opsForHash().put(key, "remaintime", room.getRoundTime());
        redisTemplate.opsForHash().put(key, "correctuser", correctuser);
        redisTemplate.opsForHash().put(key, "turn", nowturn);
        redisTemplate.opsForHash().put(key, "mode", room.getMode());
        redisTemplate.opsForHash().put(key, "status", "play");
        redisTemplate.opsForHash().put(key, "topic", "wait");
        redisTemplate.opsForHash().put(key, "score", 100);

        rService.setRoomStatus(Integer.parseInt(roomId), "PLAY");

        // 참여자 목록 가져오기
        ArrayList<String> participants = (ArrayList<String>) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "participants");
        String currentPlayer = participants.get(nowturn);
        System.out.println("현재차례 : " + nowturn + " 현재 턴인 유저 : " + currentPlayer);
        String currentNickName = uDao.findByUserId(Integer.parseInt(currentPlayer)).getNickname();

        // 넥스트 라운드로 보낼 메시지 내용 생성
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "nextround");
        messageMap.put("round", currentround); // 현재 라운드 번호
        messageMap.put("turn", currentPlayer); // 현재 턴을 가진 유저

        // 방의 모든 유저에게 메시지 전송
        broadcastMessageToRoom(roomId, createJsonMessage(messageMap), null);

        if ("AI".equals(redisTemplate.opsForHash().get(key, "mode"))) {
            // AI 모드일 때 난이도를 가져오는 로직
            List<DifficultyDto> topics = difficultyService.getTopicsByDifficulty(room.getLevel().toString(), 1);
            String subject = topics.get(0).getTopicEn();
            String aitopic = topics.get(0).getTopic();
            redisTemplate.opsForHash().put(key, "topic", aitopic);

            CompletableFuture.supplyAsync(() -> dalleService.generateImage(subject, "512x512", 1))
                    .thenAccept(responseDto -> {
                                if (responseDto != null && responseDto.getData() != null && !responseDto.getData().isEmpty()) {
                                    String imageUrl = responseDto.getData().get(0).getUrl();
//                                    String imageUrl = "https://placehold.co/1024x1024";

                                    Map<String, Object> resultMessage = new HashMap<>();
                                    resultMessage.put("event", "aidrawing");
                                    resultMessage.put("images", imageUrl);

                                    try {
                                        System.out.println("Dalle Response: " + createJsonMessage(resultMessage));
                                        broadcastMessageToRoom(roomId, createJsonMessage(resultMessage), null);
                                        startRoundTimer(roomId, nowturn);
                                    } catch (IOException e) {
                                        System.err.println("JSON 변환 오류: " + e.getMessage());
                                    }
                                }
                            }
                    );
        } else {
            List<DifficultyDto> topics = difficultyService.getTopicsByDifficulty(room.getLevel().toString(), 2);

            // 원하는 형태로 topic을 가공
            List<String> topicList = topics.stream()
                    .map(DifficultyDto::getTopic)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("event", "topic");
            response.put("topic", topicList);
            response.put("nickname", currentNickName);
            // JSON으로 변환하여 사용하거나, 다른 형태로 활용 가능
            try {
                String jsonResponse = createJsonMessage(response);
                // jsonResponse를 broadcast하거나, 다른 용도로 사용
                System.out.println("topic Response: " + jsonResponse);  // 예시: 콘솔에 출력
                broadcastMessageToRoom(roomId, jsonResponse, null); // 추가: 클라이언트에 전송
            } catch (IOException e) {
                System.err.println("JSON 변환 오류: " + e.getMessage());
            }

            waitTopicSelect(roomId, nowturn, currentNickName);
        }
    }


    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    private final Map<String, AtomicBoolean> roomRunningStatus = new ConcurrentHashMap<>();

    public void waitTopicSelect(String roomId, int nowturn, String nickname) {
        AtomicInteger remainTime = new AtomicInteger(15);
        AtomicBoolean isRunning = roomRunningStatus.computeIfAbsent(roomId, k -> new AtomicBoolean(true));

        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (!isRunning.get()) {
                    return; // 게임이 종료되었으면 작업 중단
                }
                String topic = (String) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "topic");
                if (!"wait".equals(topic) || remainTime.get() <= 0) {
                    // 타이머 작업 취소
                    cancelRoundTimer(roomId);

                    if (remainTime.get() <= 0) {
                        try {
                            endRound(roomId, nowturn);
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        System.out.println("토픽 선택됐음");
                        Map<String, Object> messageMap = new HashMap<>();
                        messageMap.put("event", "topicselect");
                        messageMap.put("roomId", roomId);
                        messageMap.put("topic", topic);
                        messageMap.put("nickname", nickname);
                        try {
                            broadcastMessageToRoom(roomId, createJsonMessage(messageMap), null);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        startRoundTimer(roomId, nowturn);
                    }
                    return;
                }

                try {
                    broadcastRemainingTime(roomId, remainTime);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                remainTime.decrementAndGet();

                // 다음 1초 후 실행
                if (isRunning.get()) {
                    scheduler.schedule(this, 1, TimeUnit.SECONDS);
                }
            }
        };

        // 기존 타이머 작업 취소
        cancelRoundTimer(roomId);

        // 새 타이머 작업 생성 및 저장
        ScheduledFuture<?> newTask = scheduler.schedule(() -> {
            try {
                task.run();
            } catch (Exception e) {
                System.out.println("스케줄러 실행 중 예외 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, TimeUnit.SECONDS);

        roomTimerTasks.put(roomId, newTask);
    }


    // 토픽을 정했을 때
    private void topicSelect(String roomId, String message) {
        Map<String, String> data = parseJson(message);
        String topic = data.get("topic");
        redisTemplate.opsForHash().put(ROOM_PREFIX + roomId, "topic", topic);
    }

    private void correctCheck(String roomId, String payload, WebSocketSession session) throws IOException, InterruptedException {
        if ("play".equals(redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "status"))) {
            Map<String, String> data = parseJson(payload);
            String answer = data.get("message");
            String userId = data.get("userId");

            if (answer.equals(redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "topic"))) {
                // 정답 메시지 전송
                broadcastMessageToRoom(roomId, createCorrectMessage(roomId, userId), null);
                broadcastMessageToRoom(roomId, createScoreMessage(roomId, userId), null);
                redisTemplate.opsForHash().increment(ROOM_PREFIX + roomId, "score", -10);

                // 정답자 리스트 가져오기
                List<String> correctuser = (List<String>) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "correctuser");

                if (correctuser == null) {
                    correctuser = new ArrayList<>();
                }

                // CopyOnWriteArrayList로 변환 후 사용
                CopyOnWriteArrayList<String> safeCorrectuse = new CopyOnWriteArrayList<>(correctuser);

                if (!safeCorrectuse.contains(userId)) {
                    safeCorrectuse.add(userId);
                    redisTemplate.opsForHash().put(ROOM_PREFIX + roomId, "correctuser", safeCorrectuse); // ArrayList로 저장
                }

                roundcheck(roomId);
            } else {
                broadcastChatMessageToRoom(roomId, payload, session);
            }
        } else {
            broadcastChatMessageToRoom(roomId, payload, session);
        }
    }


    // 라운드 시작시 Timer기능 (수정됨)
    private void startRoundTimer(String roomId, int nowturn) {
        cancelRoundTimer(roomId);

        AtomicInteger remainingTime = new AtomicInteger((Integer) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "remaintime"));

        ScheduledFuture<?> newTask = scheduler.scheduleAtFixedRate(() -> {
            if (remainingTime.get() <= 0) {
                try {
                    System.out.println("라운드 넘어갔음");
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

        roomTimerTasks.put(roomId, newTask);
    }


    // 라운드 종료
    private void endRound(String roomId, int nowturn) throws IOException, InterruptedException {
        // 타이머 종료
        cancelRoundTimer(roomId);

        redisTemplate.opsForHash().put(ROOM_PREFIX + roomId, "score", 100);

        // 현재 라운드와 최대 라운드 비교
        Object curRoundObj = redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "currentround");
        int curround = curRoundObj != null ? Integer.parseInt(curRoundObj.toString()) : 0;

        Object curMaxRoundObj = redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "maxround");
        int maxround = curMaxRoundObj != null ? Integer.parseInt(curMaxRoundObj.toString()) : 0;
        System.out.println("현재 라운드 : " + curround + " 최대 라운드 : " + maxround);

        String answer = (String) redisTemplate.opsForHash().get(ROOM_PREFIX+roomId, "topic");
        if(!answer.equals("wait")){
            String finalAnswer = answer; // 람다식에서 사용하기 위해 final 변수로 복사
            scheduler.schedule(() -> {
                try {
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("event", "answerchat");
                    messageMap.put("roomId", roomId);
                    messageMap.put("message", "🎉 정답 공개 🎉\n✨ \"" + finalAnswer + "\" ✨");
                    broadcastMessageToRoom(roomId, createJsonMessage(messageMap), null);
                } catch (IOException e) {
                    System.err.println("정답 메시지 전송 중 오류: " + e.getMessage());
                }
            }, 100, TimeUnit.MILLISECONDS);
        }

        if (curround + 1 > maxround) {
            System.out.println("겜 끝");
            endGame(roomId); // 게임 종료 처리
            return;
        }

        Object curCountObj = redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "numbers");
        int count = curCountObj != null ? Integer.parseInt(curCountObj.toString()) : 0;
        System.out.println("현재 그 방안에 있는 사람 수 : " + count);

        // 다음 턴 유저 계산 (현재 턴 + 1, count로 나눈 나머지)
        int nextturn = (nowturn + 1) % count;

        System.out.println("라운드 시작합니다.");
        System.out.println("현재 라운드 : " + curround + " 최대 라운드 : " + maxround + " 현재 차례 : " + nowturn + " 다음 차례 : " + nextturn);


        // 새로운 라운드 시작
        gamestart(roomId, nextturn, curround + 1);
    }

    // 게임 종료
    private void endGame(String roomId) throws IOException {
        AtomicBoolean isRunning = roomRunningStatus.get(roomId);
        if (isRunning != null) {
            isRunning.set(false);
        }
        cancelRoundTimer(roomId);
        roomRunningStatus.remove(roomId); // 게임 종료 후 상태 제거
        redisTemplate.opsForHash().put(ROOM_PREFIX + roomId, "status", "wait");

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "endgame");
        messageMap.put("roomId", roomId);

        // ObjectMapper를 사용해 메시지 객체를 JSON 형식으로 변환
        broadcastMessageToRoom(roomId, createJsonMessage(messageMap), null);
        rService.setRoomStatus(Integer.parseInt(roomId), "WAIT");

    }

    // 라운드 타이머 멈추기 (수정됨)
    private void cancelRoundTimer(String roomId) {
        // 해당 방에 스케줄된 작업이 있는지 확인하고 취소
        if (roomTimerTasks.containsKey(roomId)) {
            ScheduledFuture<?> task = roomTimerTasks.get(roomId);
            if (task != null && !task.isCancelled()) {
                task.cancel(false);
            }
            roomTimerTasks.remove(roomId); // 작업 제거
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

    private void broadcastChatMessageToRoom(String roomId, String payload, WebSocketSession session) throws IOException {
        Map<String, String> data = parseJson(payload);
        Map<String, Object> chatMessage = new HashMap<>();
        chatMessage.put("roomId", roomId);
        chatMessage.put("event", "chat");
        chatMessage.put("message", data.get("nickname") + " : " + data.get("message"));
        chatMessage.put("userId", data.get("userId"));
        broadcastMessageToRoom(roomId, createJsonMessage(chatMessage), null);
    }

    private void broadcastLeaveChatMessage(String roomId, String userId) throws IOException {
        Map<String, Object> chatMassage = new HashMap<>();
        chatMassage.put("roomId", roomId);
        chatMassage.put("event", "leavemessage");
        String userNickName = uDao.findByUserId(Integer.parseInt(userId)).getNickname();
        chatMassage.put("message", userNickName + " 님이 퇴장하셨습니다.");
        broadcastMessageToRoom(roomId, createJsonMessage(chatMassage), null);
    }

    private void broadcastJoinChatMessage(String roomId, String userId) throws IOException {
        Map<String, Object> chatMassage = new HashMap<>();
        chatMassage.put("roomId", roomId);
        chatMassage.put("event", "joinmessage");
        String userNickName = uDao.findByUserId(Integer.parseInt(userId)).getNickname();
        chatMassage.put("message", userNickName + " 님이 입장하셨습니다.");
        broadcastMessageToRoom(roomId, createJsonMessage(chatMassage), null);
    }

    private void broadcastCorrectChatMessageToRoom(String roomId, String payload, WebSocketSession session) throws IOException {
        Map<String, String> data = parseJson(payload);
        Map<String, Object> chatMassage = new HashMap<>();
        chatMassage.put("event", "correctchat");
        chatMassage.put("message", data.get("message"));
        broadcastMessageToRoom(roomId, createJsonMessage(chatMassage), null);
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

    private void sendExistingParticipants(WebSocketSession session, String roomId) throws IOException {
        System.out.println(redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "numbers"));
        if (rService.getUserCount(Integer.parseInt(roomId)) >= 1) {
            List<String> existingParticipants = rService.getParticipants(Integer.parseInt(roomId));
            String hostId = rService.getRoomHost(Integer.parseInt(roomId));

            // JSON 형태의 메시지 생성
            String message = createExistingUserMessage(existingParticipants, hostId);

            // 클라이언트에게 전송
            sendMessageSafely(session, message);
        }
        if (rService.getUserCount(Integer.parseInt(roomId)) >= 2) {
            broadcastMessageToRoom(roomId, createGameCanStartMessage(roomId), null);
        }
    }

    private String createGameCanStartMessage(String roomId) {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "gamecanstart");
        messageMap.put("roomId", roomId);
        return createJsonMessage(messageMap);
    }

    private String createGameCantStartMessage(String roomId) {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "gamecantstart");
        messageMap.put("roomId", roomId);
        return createJsonMessage(messageMap);
    }

    private String createCorrectMessage(String roomId, String userId) {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "correct");
        messageMap.put("roomId", roomId);
        messageMap.put("userId", userId);
        return createJsonMessage(messageMap);
    }

    private String createScoreMessage(String roomId, String userId) {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("event", "score");
        messageMap.put("roomId", roomId);
        messageMap.put("userId", userId);
        messageMap.put("score", redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "score").toString());
        return createJsonMessage(messageMap);
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
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException, InterruptedException {
        handleUserLeave(session);
        System.out.println("사용자 연결 종료: " + session.getId() + " 상태: " + status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws IOException, InterruptedException {
        System.err.println("WebSocket 오류 발생: " + exception.toString());
        handleUserLeave(session);
        if (session.isOpen()) {
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException e) {
                System.err.println("소켓 종료 오류: " + e.getMessage());
            }
        }
    }

    private void handleUserLeave(WebSocketSession session) throws IOException, InterruptedException {
        String userId = sessionUserMap.get(session.getId());
        if (userId == null) return;

        String roomId = null;
        for (Map.Entry<String, Set<WebSocketSession>> entry : roomSessions.entrySet()) {
            if (entry.getValue().contains(session)) {
                roomId = entry.getKey();
                break;
            }
        }
        if (roomId == null) return;

        ArrayList<String> participants = (ArrayList<String>) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "participants");
        if (participants == null || participants.isEmpty()) return;

        Integer currentTurn = (Integer) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "turn");
        participants = (ArrayList<String>) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "participants");
        String currentPlayer = participants == null || participants.isEmpty() ? null : participants.get(currentTurn);

        // 턴 조정 로직
        int leaverIndex = participants.indexOf(userId);
        if (leaverIndex != -1 && currentTurn != null) {
            if (leaverIndex <= currentTurn) {
                currentTurn = (currentTurn - 1 + participants.size()) % participants.size() - 1;
                if (currentTurn < 0) currentTurn = 0;
                redisTemplate.opsForHash().put(ROOM_PREFIX + roomId, "turn", currentTurn);
            }
        }

        rService.decrementUserCount(Integer.parseInt(roomId), userId);
        participants = (ArrayList<String>) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "participants");


        String currentHostId = rService.getRoomHost(Integer.parseInt(roomId));

        broadcastLeaveMessage(roomId, userId);
        broadcastLeaveChatMessage(roomId, userId);

        // 사용자가 방장이라면 방장 변경
        if (userId.equals(currentHostId)) {
            if (!participants.isEmpty()) {
                String newHostId = participants.get(0);
                rService.setRoomHost(Integer.parseInt(roomId), newHostId);
                broadcastHostChange(roomId, newHostId);
            }
        }

        // 정답자 목록에서 떠난 유저 제거
        removeUserFromCorrectUsers(roomId, userId);

        // 유저 카운트 감소 및 세션 정보 제거
        sessionUserMap.remove(session.getId());
        removeSessionFromRoom(roomId, session);

        int userCount = rService.getUserCount(Integer.parseInt(roomId));
        if (userCount < 2) {
            if (userCount == 1) {
                broadcastMessageToRoom(roomId, createGameCantStartMessage(roomId), null);
                if ("play".equals(redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "status"))) {
                    endGame(roomId);
                }
            }
        } else if ("play".equals(redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "status"))) {
            System.out.println(userId + " 가 나감." + " 현재 플레이어 : " + currentPlayer);
            if (userId.equals(currentPlayer)) {
                System.out.println("endRound 호출");
                endRound(roomId, currentTurn);
            } else {
                roundcheck(roomId);
            }
        }

        sessions.remove(session.getId());
    }


    /**
     * 정답자 목록에서 나간 유저 제거
     */
    private void removeUserFromCorrectUsers(String roomId, String userId) throws IOException, InterruptedException {
        List<String> correctuser = (List<String>) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "correctuser");
        if (correctuser != null) {
            if (correctuser.contains(userId)) {
                correctuser.remove(userId);
                redisTemplate.opsForHash().put(ROOM_PREFIX + roomId, "correctuse", correctuser);
            }
        }
        roundcheck(roomId);
    }

    private void roundcheck(String roomId) throws IOException, InterruptedException {
        // 최신 참가자 목록 가져오기 (방을 나간 유저 제외)
        List<String> participants = (List<String>) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "participants");
        List<String> correctusers = (List<String>) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "correctuser");
        String mode = (String) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "status");

        // null 체크 후 빈 리스트로 초기화
        if (participants == null) {
            participants = new ArrayList<>();
        }
        if (correctusers == null) {
            correctusers = new ArrayList<>();
        }
        if( mode == null ){
            return;
        }

        if("USER".equals(redisTemplate.opsForHash().get(ROOM_PREFIX+roomId, "mode"))){
            // 정답자 수와 참가자 수 비교
            if (correctusers.size() == participants.size() - 1 && mode.equals("play")) {
                endRound(roomId, (int) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "turn"));
            }
        }else{
            // AI 모드 시 정답자 수 == 참가자 수 일 때 다음라운드 진행
            if (correctusers.size() == participants.size() && mode.equals("play")) {
                endRound(roomId, (int) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "turn"));
            }
        }
    }

    private void scoreupdate(Map<String, String> data) throws IOException {
        String userId = data.get("userId");
        int score = Integer.parseInt(data.get("score"));
        String roomId = data.get("roomId");
        int intuserId = Integer.parseInt(data.get("userId"));

        // 룸별 점수 맵이 없다면 생성
        roomScores.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>());

        // 유저 점수 업데이트
        roomScores.get(roomId).put(userId, score);
        if (roomScores.get(roomId).size() == (Integer) redisTemplate.opsForHash().get(ROOM_PREFIX + roomId, "numbers")) {
            calculateAndBroadcastResults(roomId);
            roomScores.remove(roomId);
        }

        uDao.updateEXP(intuserId, (int) (score * 1.7));
        uDao.updatePoint(intuserId, (int) (score * 0.2));
    }

    private void calculateAndBroadcastResults(String roomId) throws IOException {
        Map<String, Integer> scores = roomScores.get(roomId);
        if (scores == null || scores.isEmpty()) {
            return;
        }

        String winnerId = null;
        int maxScore = Integer.MIN_VALUE;

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            String userId = entry.getKey();
            int score = entry.getValue();
            if (score > maxScore) {
                maxScore = score;
                winnerId = userId;
            }
        }

        // 결과 메시지 생성
        Map<String, Object> resultMessage = new HashMap<>();
        resultMessage.put("event", "winner");
        resultMessage.put("userId", winnerId != null ? winnerId : -1);
        resultMessage.put("score", maxScore != Integer.MIN_VALUE ? maxScore : 0);

        if (winnerId != null) {
            try {
                uDao.updateWinner(Integer.parseInt(winnerId));
            } catch (Exception e) {
                e.printStackTrace();
                ;
            }
        }

        // 결과 방송
        broadcastMessageToRoom(roomId, createJsonMessage(resultMessage), null);
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
