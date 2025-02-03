package com.example.model.service;

import com.example.model.dao.FriendDao;
import com.example.model.dto.Friend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendServiceImpl implements FriendService{

    private final FriendDao friendDao;

    @Autowired
    public FriendServiceImpl(FriendDao friendDao) {
        this.friendDao = friendDao;
    }

    @Override
    public List<Friend> searchFriends(int userId) {
        return friendDao.searchFriends(userId);
    }

    @Override
    public int requestFriends(int userId, int friendId) {
        Friend friend = new Friend(userId, friendId);
        return friendDao.requestFriends(friend);
    }
}
