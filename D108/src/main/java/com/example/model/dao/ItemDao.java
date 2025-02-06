// C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\model\dao\ItemDao.java
package com.example.model.dao;

import com.example.model.dto.ItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 아이템 조회/등록/수정/삭제 등 DB 접근을 위한 MyBatis Mapper 인터페이스
 */
@Mapper
public interface ItemDao {

    /**
     * 상점에 등록된 모든 아이템을 조회한다.
     *
     * @return 모든 아이템 목록
     */
    List<ItemDto> findAll();

    /**
     * 특정 아이템 상세 조회
     *
     * @param itemId 아이템 고유 ID
     * @return 해당 아이템 DTO (없으면 null)
     */
    ItemDto findById(@Param("itemId") int itemId);
}
