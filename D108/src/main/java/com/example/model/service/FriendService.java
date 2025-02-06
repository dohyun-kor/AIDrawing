package com.example.model.service;

import com.example.model.dto.FriendDto;
import com.example.model.dto.FriendRequestDto;

import java.util.List;

public interface FriendService {


    /**사용자 친구정보를 조회한다.**/
    public List<FriendDto> searchFriends(int userId);

    /** 친구 요청을 보낸다.*/
    public int requestFriends(FriendRequestDto friendRequestDto);

    /** 친구 요청을 처리한다.*/
    public int updateRequest(FriendDto nFriendDto);
}
