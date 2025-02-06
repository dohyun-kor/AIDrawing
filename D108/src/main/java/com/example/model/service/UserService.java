package com.example.model.service;

import com.example.model.dto.UserDto;

public interface UserService {

    /**
     * 사용자 정보를 DB에 저장한다.
     *
     * @param userDto
     */
    public int join(UserDto userDto);

    // 로그인 인증 메서드
    public int authenticate(UserDto userDto);


    /**
     * ID 중복 확인 메서드
     *
     * @param id 확인하려는 ID
     * @return true: 중복된 ID, false: 사용 가능한 ID
     */
    public boolean isUsedId(String id);

    /**
     * 닉네임 중복 여부 확인
     * @param nickname 확인할 닉네임
     * @return 중복이면 true, 아니면 false
     */
    public boolean isUsedNickname(String nickname);

    /**
     * ID로 조회하면 userId 반환
     */
    public int getUserIdById(String id);

    /**
     * userId로 유저 정보를 조회한다.
     * @param userId 조회할 userId
     * @return User
     */
    UserDto findByUserId(int userId);


}
