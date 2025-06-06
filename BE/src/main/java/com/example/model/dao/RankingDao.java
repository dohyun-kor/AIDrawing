package com.example.model.dao;


import com.example.model.dto.RankingDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface RankingDao {
    //    전체 랭킹 정보를 조회한다
    List<RankingDto> getTopRankings();

    // 특정 유저의 랭킹 조회한다.
    RankingDto getUserRanking(int userId);

    // 랭킹 업데이트
    int updateUserRanking(RankingDto rankingDto);

    void insertRanking(RankingDto rankingDto);
}

