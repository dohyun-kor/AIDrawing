package com.example.model.service;

import com.example.model.dto.Room;
import com.example.model.dto.RoomListDto;

import java.util.List;

public interface RoomService {

    /** 방을 생성한다.**/
    public int createRoom(Room room);

    /** roomId에 해당하는 방 정보를 변경한다.**/
    public int updateRoom(int roomId, Room room);

    /** 전체 방 정보를 조회합니다.**/
    public List<RoomListDto> searchRoom();
}
