// RoomServiceImpl.java
package com.example.model.service;

import com.example.model.dao.RoomDao;
import com.example.model.dto.RoomDto;
import com.example.model.dto.RoomListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomDao roomDao;
    private final RedisTemplate<String, Object> redisTemplate; // Object로 변경하여 리스트도 저장 가능
    private static final String ROOM_PREFIX = "room:"; // Redis 키 네임

    @Autowired
    public RoomServiceImpl(RoomDao roomDao, DifficultyService difficultyService, RedisTemplate<String, Object> redisTemplate) {
        this.roomDao = roomDao;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public int createRoom(RoomDto roomDto) {
        // 방 생성은 DB에만 저장
        roomDao.createRoom(roomDto);
        String key = ROOM_PREFIX + roomDto.getRoomId();
        // 참가자 리스트 초기화 (방장만 있는 상태로 시작)
        ArrayList<String> participants = new ArrayList<>();
        participants.add(String.valueOf(roomDto.getHostId()));
        redisTemplate.opsForHash().put(key, "participants", participants);
        redisTemplate.opsForHash().put(key, "status", "wait");

        // 게임 인원 수 초기화
        redisTemplate.opsForHash().put(key, "numbers", 1);
        redisTemplate.opsForHash().put(key, "host", roomDto.getHostId()+"");
        redisTemplate.opsForHash().put(key, "currentround", 0);
        redisTemplate.opsForHash().put(key, "maxround", roomDto.getRounds());

        return roomDto.getRoomId();
    }

    @Override
    public int updateRoom(int roomId, RoomDto roomDto) {
        return roomDao.updateRoom(roomId, roomDto);
    }

    @Override
    public List<RoomListDto> searchRoom() {
        return roomDao.searchRoom();
    }

    @Override
    public RoomDto selectRoom(int roomId) {
        return roomDao.selectRoom(roomId);
    }

    @Override
    public void incrementUserCount(int roomId, String userId) {
        String key = ROOM_PREFIX + roomId;

        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public  Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(key);
                List<String> participants = (List<String>) operations.opsForHash().get(key, "participants");
                operations.multi();

                if (participants == null) {
                    participants = new ArrayList<>();
                }

                // CopyOnWriteArrayList로 변환 후 사용
                CopyOnWriteArrayList<String> safeParticipants = new CopyOnWriteArrayList<>(participants);

                if (!safeParticipants.contains(userId)) {
                    safeParticipants.add(userId);
                    operations.opsForHash().put(key, "participants", safeParticipants); // ArrayList로 저장
                    operations.opsForHash().increment(key, "numbers", 1);
                }
                return operations.exec();
            }
        });
    }

    @Override
    public void decrementUserCount(int roomId, String userId) {
        String key = ROOM_PREFIX + roomId;
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public  Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(key);
                List<String> participants = (List<String>) operations.opsForHash().get(key, "participants");
                Integer count = (Integer) operations.opsForHash().get(key, "numbers");
                operations.multi();

                if (participants != null) {
                    // CopyOnWriteArrayList로 변환 후 사용
                    CopyOnWriteArrayList<String> safeParticipants = new CopyOnWriteArrayList<>(participants);

                    if (safeParticipants.contains(userId)) {
                        safeParticipants.remove(userId);
                        operations.opsForHash().put(key, "participants", safeParticipants); // 다시 저장할 땐 ArrayList로
                        operations.opsForHash().increment(key, "numbers", -1);
                    }
                }

                // 방의 인원이 0명이 되면 Redis에서 삭제 및 DB 삭제
                if (count == null || count <= 0) {
                    operations.delete(key);
                    roomDao.deleteRoom(roomId);
                }
                return operations.exec();
            }
        });
    }


    @Override
    public int getUserCount(int roomId) {
        String key = ROOM_PREFIX + roomId;
        Integer count = (Integer) redisTemplate.opsForHash().get(key, "numbers");
        return count == null ? 0 : count;
    }

    @Override
    public List<String> getParticipants(int roomId) {
        String key = ROOM_PREFIX + roomId;
        return (List<String>) redisTemplate.opsForHash().get(key, "participants");
    }

    @Override
    public String getRoomHost(int roomId){
        String key = ROOM_PREFIX + roomId;
        return String.valueOf(redisTemplate.opsForHash().get(key, "host"));
    }

    @Override
    public void setRoomHost(int roomId, String newHostId) {
        String key = ROOM_PREFIX + roomId;
        redisTemplate.opsForHash().put(key, "host", newHostId);
    }

    @Override
    // Redis에서 방 정보 삭제 및 DB에서 방 삭제
    public void deleteRoom(int roomId) {
        String key = ROOM_PREFIX + roomId;
        redisTemplate.delete(key);
        roomDao.deleteRoom(roomId);
    }

    @Override
    public void setTopic(int roomId, String topic) {
        String key = ROOM_PREFIX + roomId;
        redisTemplate.opsForHash().put(key, "topic", topic);
    }

    @Override
    public void setCurrentRound(int roomId, int currentRound) {
        String key = ROOM_PREFIX + roomId;
        redisTemplate.opsForHash().put(key, "currentround", currentRound);
    }

    @Override
    public Object getRoomInfo(int roomId, String key){
        String roomKey = ROOM_PREFIX + roomId;
        return redisTemplate.opsForHash().get(roomKey, key);
    }
}
