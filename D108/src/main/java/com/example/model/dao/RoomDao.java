package com.example.model.dao;

import com.example.model.dto.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoomDao {

    public int createRoom(Room room);

    public int updateRoom(@Param("roomId") int roomId,
                          @Param("room") Room room);
}
