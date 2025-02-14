package com.example.model.dao;

import com.example.model.dto.SignUpDto;
import com.example.model.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper  // MyBatis Mapper 설정
public interface UserDao {

    /**
     * 사용자 정보를 추가한다.
     * @param userDto
     * @return
     */

    // 회원가입 : 사용자의 정보를 DB에 삽입
    int insert(SignUpDto userDto);

    /**
     * ID로 사용자 정보를 조회한다.
     * @param id
     * @return
     */
    UserDto findById(String id);

    /**
     * 닉네임 중복 여부를 확인한다.
     * @param nickname
     * @return 중복되면 true, 아니면 false
     */
    boolean existsByNickname(String nickname);


//    /**
//     * ID로 userId를 조회한다.
//     * @param id
//     * @return userId
//     */
//    int getUserIdById(String id);



    /**
     * userId로 유저 정보를 조회한다.
     * @param userId 조회할 userId
     * @return User
     */
    UserDto findByUserId(int userId);


    /**
     * nickname으로 유저 정보를 조회한다.
     * @param nickname 조회할 nickname
     * @return User
     */
    UserDto findByNickname(String nickname);


    /**
     * userId로 유저 정보 중 point를 조회한다.
     * @param userId 조회할 아이디 userId
     * @return points
     */
    int findPointByUserId(int userId);


    /**
     *
     * @param userId
     * @param points
     * @return
     */
    int updatePoint(@Param("userId") int userId, @Param("points") int points);


    int changeProfile(@Param("userId") int userId, @Param("itemId") int itemId);


    /**
     * 사용자 정보를 업데이트한다.
     * @param signUpDto 업데이트할 사용자 정보
     * @return 업데이트된 행의 수
     */
    public int updateUser(@Param("userId")int userId, @Param("signUpDto") SignUpDto signUpDto);

    /**
     * 모든 사용자 정보를 가져온다.
     * @return
     */
    List<UserDto> getAllUsers();
}
