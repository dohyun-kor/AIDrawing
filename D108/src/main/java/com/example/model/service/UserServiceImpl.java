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

    /**
     * 로그인 인증 메서드: 아이디와 비밀번호가 일치하는지 확인한다.
     * @param user (사용자가 입력한 아이디, 비밀번호)
     * @return "success" 또는 "failure"
     */
    @Override
    public String authenticate(User user) {
        // DB에서 아이디로 사용자 조회
        User foundUser = userDao.findById(user.getId());

        // 사용자 존재 여부 및 비밀번호 확인
        if (foundUser != null && foundUser.getPassword().equals(user.getPassword())) {
            return "true";
        } else {
            return "false";
        }
    }


}
