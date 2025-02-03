package com.example.model.service;

import com.example.model.dao.FriendDao;
import com.example.model.dto.Friend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendServiceImpl implements FriendService{

    private final FriendDao friendDao;

    @Autowired
    public FriendServiceImpl(FriendDao friendDao) {
        this.friendDao = friendDao;
    }

    @Override
    public Friend searchFriends(int userId) {
        return friendDao.searchFriend(userId);
    }
}
