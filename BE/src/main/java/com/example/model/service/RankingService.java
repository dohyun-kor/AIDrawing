package com.example.model.service;

import com.example.model.dto.RankingDto;

import java.util.List;

public interface RankingService {

    // 상위 10명 랭킹 조회
    public List<RankingDto> getTopRankings();

    // 특정 유저의 랭킹 조회
    public RankingDto getUserRanking(int userId);

    RankingDto updateUserRanking(int userId, RankingDto rankingDto);
}
