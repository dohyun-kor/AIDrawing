package com.example.model.dao;

import com.example.model.dto.Friend;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FriendDao {

    /**
     * 사용자 친구정보를 조회한다.
     *
     * @param userId
     */
    List<Friend> searchFriend(int userId);
}
