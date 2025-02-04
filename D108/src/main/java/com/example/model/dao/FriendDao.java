package com.example.model.dao;

import com.example.model.dto.Friend;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FriendDao {

    /** 사용자 친구정보를 조회한다.*/
    List<Friend> searchFriends(int userId);

    /** 친구 요청을 보낸다.*/
    int requestFriends(Friend friend);

    /** 친구 요청 승인*/
    int acceptFriends(Friend nFriend);

    /** 친구 요청 거절*/
    int denyFriends(Friend nFriend);
}
