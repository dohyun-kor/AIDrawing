package com.example.model.service;


import com.example.model.dao.RankingDao;
import com.example.model.dto.RankingDto;
import com.example.model.dto.UserDto;
import com.example.model.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingServiceImpl implements RankingService {

    // RankingDao 의존성 주입
    private final RankingDao rankingDao;
    private final UserDao userDao;

    @Autowired
    public RankingServiceImpl(RankingDao rankingDao, UserDao userDao) {
        this.rankingDao = rankingDao;
        this.userDao = userDao;
    }

    // 랭킹 조회 서비스 구현
    @Override
    public List<RankingDto> getTopRankings() {
        // RankingDao의 getTopRankings 메서드를 호출하여 랭킹 정보를 반환
        return rankingDao.getTopRankings();
    }

    // 특정 유저의 랭킹 조회
    @Override
    public RankingDto getUserRanking(int userId) {
        return rankingDao.getUserRanking(userId);
    }

    // 랭킹 업데이트
    @Override
    public RankingDto updateUserRanking(int userId, RankingDto rankingDto){
        int rows = rankingDao.updateUserRanking(rankingDto);
        RankingDto updatedRanking = rankingDao.getUserRanking(rankingDto.getUserId());
        return updatedRanking;
    }


    // 매일 특정 시간에 전체 랭킹 업데이트 수행(스케쥴러 사용)
    @Scheduled(cron = "0 * * * * *")
    public void updateDailyRanking(){
        // 1. Users 테이블에서 모든 사용자 데이터를 가져옵니다.
        List<UserDto> allusers = userDao.getAllUsers();

        // 2. 사용자 데이터를 exp기준으로 내림차순 정렬합니다.
        allusers.sort((u1, u2) -> Integer.compare(u2.getExp(), u1.getExp()));

        // 3. 각 사용자에 대한 순위를 계산 및 업데이트
        for (int i = 0; i < allusers.size(); i++) {
            UserDto user = allusers.get(i);
            int rank = i + 1;

            // 4. 승률 계산
            float winRate = calculateWinRate(user);

            // 5. RankingDto 객체 생성 및 값 세팅
            RankingDto rankingDto = new RankingDto();
            rankingDto.setExp(user.getExp());
            rankingDto.setWinRate(winRate);
            rankingDto.setUserId(user.getUserId());
            rankingDto.setNickname(user.getNickname());
            rankingDto.setRankPosition(rank);

            // 6. 각 유저에 대해 랭킹 업데이트 수행
            rankingDao.updateUserRanking(rankingDto);

        }
    }

    // 승률 계산 (소수 둘째 자리까지)
    private float calculateWinRate(UserDto user) {
        // totalGames가 0일 경우 나눗셈 에러 방지
        if (user.getTotalGames() == 0) {
            return 0.0f;
        }

        // 1. float 값으로 승률 계산
        float rawRate = ((float) user.getGamesWon() / user.getTotalGames()) * 100;

        // 2. 소수 둘째 자리까지 반올림
        //    - 예) 35.6789 -> 35.68
        //    - Math.round()는 long형으로 반환하므로 float으로 캐스팅
        float twoDecimalRate = Math.round(rawRate * 100) / 100.0f;

        return twoDecimalRate;
    }
}
