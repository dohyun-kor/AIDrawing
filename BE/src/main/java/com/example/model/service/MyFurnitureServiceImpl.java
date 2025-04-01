package com.example.model.service;

import com.example.model.dao.MyFurnitureDao;
import com.example.model.dto.MyFurnitureDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyFurnitureServiceImpl implements MyFurnitureService {

    private final MyFurnitureDao myFurnitureDao;

    @Autowired
    public MyFurnitureServiceImpl(MyFurnitureDao myFurnitureDao) {
        this.myFurnitureDao = myFurnitureDao;
    }

    @Override
    public int displayFurniture(int userId, MyFurnitureDto myFurnitureDto) {
        return myFurnitureDao.insertFurnitureDisplay(userId, myFurnitureDto);
    }
}
