package com.example.model.service;

import com.example.model.dao.UserDao;
import com.example.model.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public int join(User user) {
        return userDao.insert(user);
    }

    @Override
    public String authenticate(User user) {
        return null;
    }


}
