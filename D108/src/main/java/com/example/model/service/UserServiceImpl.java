package com.example.model.service;

import com.example.controller.FriendController;
import com.example.model.dao.RankingDao;
import com.example.model.dao.UserDao;
import com.example.model.dto.LoginRequestDto;
import com.example.model.dto.RankingDto;
import com.example.model.dto.SignUpDto;
import com.example.model.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);

    private final UserDao userDao;
    private final RankingDao rankingDao;

    @Autowired
    public UserServiceImpl(UserDao userDao, RankingDao rankingDao) {
        this.userDao = userDao;
        this.rankingDao = rankingDao;
    }

    /**
     * 회원가입
     * @param signUpDto
     * @return
     */
    @Override
    public int join(SignUpDto signUpDto) {
        // 1. 회원가입 실행(User 테이블에 회원 정보 추가)
        int result = userDao.insert(signUpDto);

        // 2. 회원가입 성공 시, 새로 가입한 사용자의 정보를 가져온 후 Ranking 테이블에 초기 데이터 추가
        if(result > 0){
            // 가입 시 사용한 아이디를 통해 새로 등록된 사용자 정보를 조회
            UserDto newUser = userDao.findById(signUpDto.getId());

            // 초기 랭킹 데이터 생성
            RankingDto rankingDto = new RankingDto();
            rankingDto.setUserId(newUser.getUserId());
            rankingDto.setExp(newUser.getExp());
            rankingDto.setNickname(newUser.getNickname());
            rankingDto.setRankPosition(0);
            rankingDto.setWinRate(0.0f);

            // Ranking 테이블에 새 회원의 랭킹 정보를 삽입
            rankingDao.insertRanking(rankingDto);
        }
        return result;
    }
    /**
     * 로그인 인증 메서드: 아이디와 비밀번호가 일치하는지 확인한다.
     * @param loginRequestDto (사용자가 입력한 아이디, 비밀번호)
     * @return "success" 또는 "failure"
     */
    @Override
    public int authenticate(LoginRequestDto loginRequestDto) {
        // DB에서 아이디로 사용자 조회
        UserDto foundUserDto = userDao.findById(loginRequestDto.getId());

        // 사용자 존재 여부 및 비밀번호 확인
        if (foundUserDto != null && foundUserDto.getPassword().equals(loginRequestDto.getPassword())) {
            return foundUserDto.getUserId();
        } else {
            return -1;
        }
    }

    /**
     * ID 중복 확인 메서드
     *
     * @param id 확인하려는 ID
     * @return true: 중복된 ID, false: 사용 가능한 ID
     */
    @Override
    public boolean isUsedId(String id) {
        UserDto foundUserDto = userDao.findById(id);
        return foundUserDto != null; // ID가 존재하면 true, 없으면 false
    }

    @Override
    public boolean isUsedNickname(String nickname) {
        return userDao.existsByNickname(nickname);
    }

//    @Override
//    public int getUserIdById(String id) {
//        return userDao.getUserIdById(id);
//    }

    @Override
    public UserDto findByUserId(int userId) {
        return userDao.findByUserId(userId);
    }

    @Override
    public int changeProfile(int userId, int itemId) {
        return userDao.changeProfile(userId, itemId);
    }

    @Override
    public UserDto findByNickname(String nickname) {
        return userDao.findByNickname(nickname);
    }

    @Override
    public int updateUser(int userId, SignUpDto signUpDto) {
        return userDao.updateUser(userId, signUpDto);
    }
}
