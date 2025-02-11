package com.example.model.dao;

import com.example.model.dto.DifficultyDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DifficultyDao {
    List<DifficultyDto> getEasyTopics(@Param("count") int count);
    List<DifficultyDto> getNormalTopics(@Param("count") int count);
    List<DifficultyDto> getHardTopics(@Param("count") int count);
}
