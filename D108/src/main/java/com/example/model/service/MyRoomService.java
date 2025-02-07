package com.example.model.service;

import com.example.model.dto.MyRoomDto;

public interface MyRoomService {

    // 전시된 그림과 가구를 조회하여 MyRoomDto에 담아 반환
    public MyRoomDto getDisplayedItems(int userId);


}
