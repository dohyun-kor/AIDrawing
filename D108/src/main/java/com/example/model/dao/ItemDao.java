package com.example.model.dao;

import com.example.model.dto.ItemDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

// 상점 아이템에 대한 DB 접근 인터페이스

@Mapper
public interface ItemDao {
    // 상점에 등록된 모든 아이템 조회

    List<ItemDto> findAll();

    /**
     * 특정 아이템 상세 조회
     * @param itemId 아이템 고유 ID
     */
    ItemDto findById(int itemId);
}