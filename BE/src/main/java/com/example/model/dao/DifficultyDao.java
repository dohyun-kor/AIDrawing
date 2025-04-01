package com.example.model.dao;

import com.example.model.dto.DifficultyDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DifficultyDao {

    // 난이도에 따른 주제 리스트를 가져오는 메서드
    List<DifficultyDto> getEasyTopics(@Param("count") int count);
    List<DifficultyDto> getNormalTopics(@Param("count") int count);
    List<DifficultyDto> getHardTopics(@Param("count") int count);

//    // 난이도별 주제 사용 상태 업데이트
//    int updateEasyTopicUsed(@Param("topicId") int topicId);
//    int updateNormalTopicUsed(@Param("topicId") int topicId);
//    int updateHardTopicUsed(@Param("topicId") int topicId);

}
