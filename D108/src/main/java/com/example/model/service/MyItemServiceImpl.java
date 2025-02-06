package com.example.model.service;

import com.example.model.dao.MyItemDao;
import com.example.model.dto.MyItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyItemServiceImpl implements MyItemService {

    @Autowired
    private MyItemDao myItemDao;

    @Override
    public List<MyItemDto> findAllMyItems(int userId) {
        // MyItemDao의 findAllMyItems(userId) 호출
        return myItemDao.findAllMyItems(userId);
    }

    @Override
    public MyItemDto findMyItemById(int purchaseId) {
        // MyItemDao의 findMyItemById(purchaseId) 호출
        return myItemDao.findMyItemById(purchaseId);
    }

    @Override
    public int insertMyItem(MyItemDto myItemDto) {
        // MyItemDao의 insertMyItem() 실행
        return myItemDao.insertMyItem(myItemDto);
    }
}
