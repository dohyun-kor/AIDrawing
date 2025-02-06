package com.example.model.dao;

import com.example.model.dto.Room;
import com.example.model.dto.RoomListDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomDao {

    public int createRoom(Room room);

    public int updateRoom(@Param("roomId") int roomId,
                          @Param("room") Room room);


    public List<RoomListDto> searchRoom();
}
