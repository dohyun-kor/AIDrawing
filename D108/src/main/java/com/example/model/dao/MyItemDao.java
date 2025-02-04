package com.example.model.dao;

import com.example.model.dto.MyItemDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

// 사용자가 실제로 구매한 아이템 정보에 대한 DB 접근 인터페이스

@Mapper
public interface MyItemDao {
    /**
     * 내가 가진 모든 아이템 조회
     * @param userId 사용자 ID
     */
    List<MyItemDto> findAllMyItems(int userId);

    /**
     * 내가 가진 아이템 단일 조회
     * @param purchaseId 구매(보유) 고유 ID
     */
    MyItemDto findMyItemById(int purchaseId);

    /**
     * 아이템 구매 (MyItem 테이블에 데이터 Insert
     */
    int insertMyItem(MyItemDto myItemDto);
}