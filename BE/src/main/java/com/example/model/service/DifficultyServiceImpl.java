package com.example.model.service;

import com.example.model.dao.DifficultyDao;
import com.example.model.dto.DifficultyDto;
import org.apache.commons.lang3.builder.Diff;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DifficultyServiceImpl implements DifficultyService{

    private final DifficultyDao difficultyDao;

    public DifficultyServiceImpl (DifficultyDao difficultyDao) {
        this.difficultyDao = difficultyDao;
    }
    /**
     * 난이도와 개수에 따라 주제를 가져옵니다.
     * @param difficulty 난이도 (easy, normal, hard)
     * @param count 가져올 주제 개수
     * @return 난이도에 따른 주제 리스트
     */
    @Override
    public List<DifficultyDto> getTopicsByDifficulty(String difficulty, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }

        switch (difficulty.toLowerCase()) {
            case "easy":
                return difficultyDao.getEasyTopics(count);
            case "normal":
                return difficultyDao.getNormalTopics(count);
            case "hard":
                return difficultyDao.getHardTopics(count);
            default:
                throw new IllegalArgumentException("Invalid difficulty level: " + difficulty);
        }
    }

//    /**
//     * 난이도별로 주제의 사용 상태를 업데이트합니다.
//     *
//     * @param difficulty 난이도 (easy, normal, hard)
//     * @param topicId    사용 상태를 업데이트할 주제 ID
//     * @return 업데이트된 레코드의 수
//     */
//    @Override
//    public int updateTopicUsed(String difficulty, int topicId) {
//        switch (difficulty.toLowerCase()) {
//            case "easy":
//                return difficultyDao.updateEasyTopicUsed(topicId);
//            case "normal":
//                return difficultyDao.updateNormalTopicUsed(topicId);
//            case "hard":
//                return difficultyDao.updateHardTopicUsed(topicId);
//            default:
//                throw new IllegalArgumentException("Invalid difficulty level: " + difficulty);
//        }
//    }

}
