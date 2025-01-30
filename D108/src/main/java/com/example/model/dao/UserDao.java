package com.example.model.dao;

import com.example.model.dto.User;

public interface UserDao {

    /**
     * 사용자 정보를 추가한다.
     * @param user
     * @return
     */
    int insert(User user);
}
