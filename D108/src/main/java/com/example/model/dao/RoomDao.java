package com.example.model.dao;

import com.example.model.dto.RoomDto;
import com.example.model.dto.RoomListDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomDao {

    public int createRoom(RoomDto roomDto);

    public int updateRoom(@Param("roomId") int roomId,
                          @Param("room") RoomDto roomDto);


    public List<RoomListDto> searchRoom();

    public void deleteRoom(int roomId);

    public RoomDto selectRoom(int roodId);
}
