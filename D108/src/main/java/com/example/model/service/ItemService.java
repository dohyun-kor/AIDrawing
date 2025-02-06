// C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\model\service\ItemService.java
package com.example.model.service;

import com.example.model.dto.ItemDto;

import java.util.List;

/**
 * 아이템 비즈니스 로직을 담당하는 Service 인터페이스
 */
public interface ItemService {

    /**
     * 상점에 등록된 모든 아이템을 조회한다.
     *
     * @return 아이템 목록
     */
    List<ItemDto> findAll();

    /**
     * 특정 아이템을 조회한다.
     *
     * @param itemId 아이템 고유 ID
     * @return 아이템 정보
     */
    ItemDto findById(int itemId);
}
