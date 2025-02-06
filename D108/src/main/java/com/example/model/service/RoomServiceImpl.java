package com.example.model.service;

import com.example.model.dao.RoomDao;
import com.example.model.dto.RoomDto;
import com.example.model.dto.RoomListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService{

    private final RoomDao roomDao;

    @Autowired
    public RoomServiceImpl(RoomDao roomDao) {
        this.roomDao = roomDao;
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
}
