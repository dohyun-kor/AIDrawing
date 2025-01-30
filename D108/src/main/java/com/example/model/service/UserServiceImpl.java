package com.example.model.service;

import com.example.model.dao.UserDao;
import com.example.model.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public int join(User user) {
        return userDao.insert(user);
    }
}
