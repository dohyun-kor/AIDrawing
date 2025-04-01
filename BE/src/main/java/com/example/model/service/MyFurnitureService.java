package com.example.model.service;

import com.example.model.dto.MyFurnitureDto;

public interface MyFurnitureService {

    // 마이룸에 가구 전시를 요청한다.
    public int displayFurniture(int userId, MyFurnitureDto myFurnitureDto);



}
