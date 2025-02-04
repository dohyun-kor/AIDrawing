package com.example.model.service;

import com.example.model.dao.FriendDao;
import com.example.model.dao.RoomDao;
import com.example.model.dto.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService{

    private final RoomDao roomDao;

    @Autowired
    public RoomServiceImpl(RoomDao roomDao) {
        this.roomDao = roomDao;
    }

    @Override
    public int createRoom(Room room) {
        return roomDao.createRoom(room);
    }

    @Override
    public int updateRoom(int roomId, Room room) {
        return roomDao.updateRoom(roomId, room);
    }
}
