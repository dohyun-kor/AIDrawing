//C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\model\service\MyItemServiceImpl.java
package com.example.model.service;

import com.example.model.dao.MyItemDao;
import com.example.model.dao.UserDao;
import com.example.model.dto.MyItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MyItemServiceImpl implements MyItemService {

    @Autowired
    private MyItemDao myItemDao;

    @Autowired
    public UserDao userDao;

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

    @Override
    public int findPointByUserId(int userId) {
        return userDao.findPointByUserId(userId);
    }

    @Override
    @Transactional
    public int purchaseItem(MyItemDto myItemDto, int itemPrice) {
        // 1) 포인트 조회
        int currentPoint = userDao.findPointByUserId(myItemDto.getUserId());

        // 2) 포인트 부족 체크
        if (currentPoint < itemPrice) {
            throw new RuntimeException("포인트가 부족합니다.");
        }

        // 3) 포인트 차감
        userDao.updatePoint(myItemDto.getUserId(), currentPoint - itemPrice);

        // 4) MyItem INSERT
        int result = myItemDao.insertMyItem(myItemDto);
        if (result != 1) {
            throw new RuntimeException("구매 INSERT 에 실패했습니다.");
        }

        return result; // 1
    }
}
