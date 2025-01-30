package com.example.model.service;

import com.example.model.dto.User;

public interface UserService {

    /**
     * 사용자 정보를 DB에 저장한다.
     *
     * @param user
     */
    public int join(User user);

}
