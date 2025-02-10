package com.example.model.service;

import com.example.model.dao.RoomDao;
import com.example.model.dto.RoomDto;
import com.example.model.dto.RoomListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomDao roomDao;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String ROOM_PREFIX = "room:users:"; // Redis 키 네임

    @Autowired
    public RoomServiceImpl(RoomDao roomDao, RedisTemplate<String, String> redisTemplate) {
        this.roomDao = roomDao;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public int createRoom(RoomDto roomDto) {
        return roomDao.createRoom(roomDto);
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
    public void incrementUserCount(int roomId) {
        String key = ROOM_PREFIX + roomId;

        // 키가 없으면 초기값 0으로 설정 후 증가
        redisTemplate.opsForValue().setIfAbsent(key, "0");
        redisTemplate.opsForValue().increment(key);
    }

    @Override
    public void decrementUserCount(int roomId) {
        String key = ROOM_PREFIX + roomId;

        // 현재 인원 수 감소
        Long count = redisTemplate.opsForValue().decrement(key);

        // 방의 인원이 0명이 되면 Redis에서 삭제
        if (count <= 0) {
            redisTemplate.delete(key);
        }
        roomDao.deleteRoom(roomId);
    }

    @Override
    public int getUserCount(int roomId) {
        String count = redisTemplate.opsForValue().get(ROOM_PREFIX + roomId);
        // count가 null이거나 빈 문자열일 경우 0을 반환하고, 그렇지 않으면 count를 Integer로 반환
        return (count == null || count.isBlank()) ? 0 : Integer.parseInt(count);
    }

}
