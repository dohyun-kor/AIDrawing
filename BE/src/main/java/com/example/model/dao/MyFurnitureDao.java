package com.example.model.dao;

import com.example.model.dto.MyFurnitureDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MyFurnitureDao {
    // 가구 전시를 위한 insert 메서드 정의
    int insertFurnitureDisplay(@Param("userId")int userId, @Param("myFurniture")MyFurnitureDto myFurnitureDto);

}
