package com.example.model.dao;


import com.example.model.dto.RankingDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface RankingDao {
    //    전체 랭킹 정보를 조회한다
    List<RankingDto> getTopRankings();
}

