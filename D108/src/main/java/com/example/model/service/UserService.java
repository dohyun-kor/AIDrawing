package com.example.model.service;

import com.example.model.dto.User;
import org.springframework.beans.factory.annotation.Autowired;

public interface UserService {

    /**
     * 사용자 정보를 DB에 저장한다.
     *
     * @param user
     */
    public int join(User user);

    // 로그인 인증 메서드
    String authenticate(User user);
}
