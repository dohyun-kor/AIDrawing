package com.example.model.service;

import com.example.model.dto.Room;

public interface RoomService {

    /**방을 생성한다.**/
    public int createRoom(Room room);

    /**roomId에 해당하는 방 정보를 변경한다.**/
    public int updateRoom(int roomId, Room room);
}
