package com.example.model.service;

import com.example.model.dto.RoomDto;
import com.example.model.dto.RoomListDto;

import java.util.List;

public interface RoomService {

    /** 방을 생성한다.**/
    public int createRoom(RoomDto roomDto);

    /** roomId에 해당하는 방 정보를 변경한다.**/
    public int updateRoom(int roomId, RoomDto roomDto);

    /** 전체 방 정보를 조회합니다.**/
    public List<RoomListDto> searchRoom();

    /** 참가자 증가**/
    public void incrementUserCount(int roomId);

    /** 참가자 감소**/
    public void decrementUserCount(int roomId);

    /** 참가자 조회**/
    public int getUserCount(int roomId);
}
