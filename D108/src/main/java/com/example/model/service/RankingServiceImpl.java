package com.example.model.service;


import com.example.model.dao.RankingDao;
import com.example.model.dto.RankingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingServiceImpl implements RankingService {

    // RankingDao 의존성 주입
    private final RankingDao rankingDao;

    @Autowired
    public RankingServiceImpl(RankingDao rankingDao) {
        this.rankingDao = rankingDao;
    }

    // 랭킹 조회 서비스 구현
    @Override
    public List<RankingDto> getTopRankings() {
        // RankingDao의 getTopRankings 메서드를 호출하여 랭킹 정보를 반환
        return rankingDao.getTopRankings();
    }
}
