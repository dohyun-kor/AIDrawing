package com.example.model.service;

import com.example.model.dao.DifficultyDao;
import com.example.model.dto.DifficultyDto;
import com.example.model.dto.MyFurnitureDto;

import java.util.List;

public interface DifficultyService {
    /**
     * 난이도와 개수에 따라 주제를 가져옵니다.
     * @param difficulty 난이도 (easy, normal, hard)
     * @param count 가져올 주제 개수
     * @return 난이도에 따른 주제 리스트
     */
    List<DifficultyDto> getTopicsByDifficulty(String difficulty, int count);

}
