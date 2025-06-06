package com.example.model.service;

import com.example.model.dto.LoginRequestDto;
import com.example.model.dto.SignUpDto;
import com.example.model.dto.UserDto;

public interface UserService {

    /**
     * 사용자 정보를 DB에 저장한다.
     *
     * @param signUpDto
     */
    public int join(SignUpDto signUpDto);

    // 로그인 인증 메서드
    public int authenticate(LoginRequestDto loginRequestDto);

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

//    /**
//     * ID로 조회하면 userId 반환
//     */
//    public int getUserIdById(String id);

    /**
     * userId로 유저 정보를 조회한다.
     * @param userId 조회할 userId
     * @return User
     */
    UserDto findByUserId(int userId);

    /**
     * userId의 유저 프로필을 ItemId로 변경한다.
     * @param userId
     * @param itemId
     * @return Boolean
     */
    public int changeProfile(int userId, int itemId);

    /**
     * nickname으로 유저 정보를 조회한다.
     * @param nickname 조회할 nickname
     * @return User
     */
    UserDto findByNickname(String nickname);

    /**
     * 회원 정보를 수정한다.
     * @param signUpDto 업데이트할 사용자 정보
     * @return 업데이트된 행의 수
     */
    public int updateUser(int userId, SignUpDto signUpDto);

    /**
     * 닉네임을 수정한다.
     *  @param userId 닉네임을 수정할 사용자의 고유 ID
     *  @param nickname 새로운 닉네임
     * @return 업데이트된 행의 수
     */
    public int updateUserNickname(int userId, String nickname);
}
