package com.example.model.service;

import com.example.model.dto.Friend;

import java.util.List;

public interface FriendService {


    /**사용자 친구정보를 조회한다.**/
    public List<Friend> searchFriends(int userId);

    /** 친구 요청을 보낸다.*/
    public boolean requestFriends(int userId, int friendId);
}
