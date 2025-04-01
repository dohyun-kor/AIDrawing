package com.example.model.service;

import com.example.model.dao.FriendDao;
import com.example.model.dto.FriendDto;
import com.example.model.dto.FriendRequestDto;
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
    public List<FriendDto> searchFriends(int userId) {
        return friendDao.searchFriends(userId);
    }

    @Override
    public int requestFriends(FriendRequestDto friendRequestDto) {
        return friendDao.requestFriends(friendRequestDto);
    }

    @Override
    public int updateRequest(FriendDto nFriendDto) {
        if(nFriendDto.getStatus() == FriendDto.FriendStatus.ACCEPTED){
            return friendDao.acceptFriends(nFriendDto);
        }else{
            return friendDao.denyFriends(nFriendDto);
        }
    }
}
