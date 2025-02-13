package com.example.model.dao;

import com.example.model.dto.MyFurnitureDto;
import com.example.model.dto.PictureDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyRoomDao {

    // 전시된 그림을 조회
    List<PictureDto> getDisplayedPictures(@Param("userId")int userId);
}
