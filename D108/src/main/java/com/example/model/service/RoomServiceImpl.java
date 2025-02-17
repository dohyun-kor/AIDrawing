package com.example.model.service;

import com.example.model.dao.RoomDao;
import com.example.model.dto.RoomDto;
import com.example.model.dto.RoomListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
        redisTemplate.opsForHash().put(key, "turn", 0);

        // 게임 인원 수 초기화
        redisTemplate.opsForHash().put(key, "numbers", 1);
        redisTemplate.opsForHash().put(key, "host", roomDto.getHostId()+"");

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

        // 참가자 리스트 가져오기
        List<String> participants = (List<String>) redisTemplate.opsForHash().get(key, "participants");

        if (participants == null) {
            participants = new ArrayList<>();
        }

        // CopyOnWriteArrayList로 변환 후 사용
        CopyOnWriteArrayList<String> safeParticipants = new CopyOnWriteArrayList<>(participants);

        if (!safeParticipants.contains(userId)) {
            safeParticipants.add(userId);
            redisTemplate.opsForHash().put(key, "participants", safeParticipants); // ArrayList로 저장
            redisTemplate.opsForHash().increment(key, "numbers", 1);
        }
    }

    @Override
    public void decrementUserCount(int roomId, String userId) {
        String key = ROOM_PREFIX + roomId;

        List<String> participants = (List<String>) redisTemplate.opsForHash().get(key, "participants");

        if (participants != null) {
            // CopyOnWriteArrayList로 변환 후 사용
            CopyOnWriteArrayList<String> safeParticipants = new CopyOnWriteArrayList<>(participants);

            if (safeParticipants.contains(userId)) {
                safeParticipants.remove(userId);
                redisTemplate.opsForHash().put(key, "participants", safeParticipants); // 다시 저장할 땐 ArrayList로
                redisTemplate.opsForHash().increment(key, "numbers", -1);
            }
        }

        // 방의 인원이 0명이 되면 Redis에서 삭제 및 DB 삭제
        Integer count = (Integer) redisTemplate.opsForHash().get(key, "numbers");
        if (count == null || count <= 0) {
            redisTemplate.delete(key);
            roomDao.deleteRoom(roomId);
        }
    }


    @Override
    public int getUserCount(int roomId) {
        String key = ROOM_PREFIX + roomId;
        Integer count = (Integer) redisTemplate.opsForHash().get(key, "numbers");
        return count == null ? 0 : count;
    }

    public List<String> getParticipants(int roomId) {
        String key = ROOM_PREFIX + roomId;
        return (List<String>) redisTemplate.opsForHash().get(key, "participants");
    }

    public String getRoomHost(int roomId){
        String key = ROOM_PREFIX + roomId;
        return String.valueOf(redisTemplate.opsForHash().get(key, "host"));
    }

    @Override
    public void setRoomHost(int roomId, String newHostId) {
        String key = ROOM_PREFIX + roomId;
        redisTemplate.opsForHash().put(key, "host", newHostId);
    }
}
