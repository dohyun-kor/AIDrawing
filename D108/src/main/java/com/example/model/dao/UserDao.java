package com.example.model.dao;

import com.example.model.dto.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper  // MyBatis Mapper 설정
public interface UserDao {

    /**
     * 사용자 정보를 추가한다.
     * @param user
     * @return
     */

    // 회원가입 : 사용자의 정보를 DB에 삽입
    int insert(User user);

    /**
     * ID로 사용자 정보를 조회한다.
     * @param id
     * @return
     */
    User findById(String id);

    /**
     * 닉네임 중복 여부를 확인한다.
     * @param nickname
     * @return 중복되면 true, 아니면 false
     */
    boolean existsByNickname(String nickname);

    /**
     * ID로 userId를 조회한다.
     * @param id
     * @return userId
     */
    int getUserIdById(String id);


    /**
     * userId로 유저 정보를 조회한다.
     * @param userId 조회할 userId
     * @return User
     */
    User findByUserId(int userId);

}
