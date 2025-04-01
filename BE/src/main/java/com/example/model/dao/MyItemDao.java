// C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\model\dao\MyItemDao.java
package com.example.model.dao;

import com.example.model.dto.MyItemDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

// 사용자가 실제로 구매한 아이템 정보에 대한 DB 접근 인터페이스

@Mapper
public interface MyItemDao {
    /**
     * 특정 userId의 모든 MyItem 조회
     */
    List<MyItemDto> findAllMyItems(int userId);

    /**
     * 내가 가진 아이템 단일 조회
     * @param purchaseId 구매(보유) 고유 ID
     */
    MyItemDto findMyItemById(int purchaseId);

    /**
     * 아이템 구매 MyItem 테이블에 데이터 Insert
     */
    int insertMyItem(MyItemDto myItemDto);
}