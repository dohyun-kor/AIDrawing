// C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\model\service\ItemServiceImpl.java
package com.example.model.service;

import com.example.model.dao.ItemDao;
import com.example.model.dto.ItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link ItemService}의 구현체
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemDao itemDao;

    @Override
    public List<ItemDto> findAll() {
        // 비즈니스 로직(예: 캐싱, 정렬, 예외처리 등)을 여기서 추가할 수 있음
        return itemDao.findAll();
    }

    @Override
    public ItemDto findById(int itemId) {
        // 아이템이 존재하지 않을 경우 예외 처리를 할 수도 있음
        return itemDao.findById(itemId);
    }
}
