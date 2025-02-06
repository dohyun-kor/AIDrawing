package com.example.model.service;

import com.example.model.dto.MyItemDto;
import java.util.List;

/**
 * MyItem(사용자가 실제로 구매·보유한 아이템) 관련 비즈니스 로직
 */
public interface MyItemService {

    /**
     * 특정 user가 가진 모든 MyItem 조회
     */
    List<MyItemDto> findAllMyItems(int userId);

    /**
     * purchaseId로 MyItem 단일 조회
     */
    MyItemDto findMyItemById(int purchaseId);

    /**
     * 새 MyItem 등록
     */
    int insertMyItem(MyItemDto myItemDto);


    int findPointByUserId(int userId);

    // 아이템 구매(포인트 차감)
    int purchaseItem(MyItemDto myItemDto, int itemPrice);
}
