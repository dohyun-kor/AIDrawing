package com.example.model.service;

import com.example.model.dao.DifficultyDao;
import com.example.model.dto.DifficultyDto;
import com.example.model.dto.MyFurnitureDto;

import java.util.List;

public interface DifficultyService {
    /**
     * 난이도와 개수에 따라 주제를 가져옵니다.
     *
     * @param difficulty 난이도 (easy, normal, hard)
     * @param count      가져올 주제 개수
     * @return 난이도에 따른 주제 리스트
     */
    List<DifficultyDto> getTopicsByDifficulty(String difficulty, int count);

    /**
     * 난이도에 따라 주제의 사용 상태를 업데이트합니다.
     *
     * @param difficulty 난이도 (easy, normal, hard)
     * @param topicId    사용 상태를 업데이트할 주제 ID
     * @return 업데이트된 레코드의 수
     */
    int updateTopicUsed(String difficulty, int topicId);
}

