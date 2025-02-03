package com.example.model.service;

import com.example.model.dto.Friend;

import java.util.List;

public interface FriendService {

    /**
     * 사용자 친구정보를 조회한다.
     *
     * @param userId
     */
    public List<Friend> searchFriends(int userId);
}
