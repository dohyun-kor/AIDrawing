package com.example.model.service;

import com.example.model.dao.MyRoomDao;
import com.example.model.dto.MyFurnitureDto;
import com.example.model.dto.MyRoomDto;
import com.example.model.dto.PictureDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyRoomServiceImpl implements MyRoomService {
    private final MyRoomDao myRoomDao;

    @Autowired
    public MyRoomServiceImpl(MyRoomDao myRoomDao) {
        this.myRoomDao = myRoomDao;
    }

    @Override
    public MyRoomDto getDisplayedItems(int userId) {
        List<PictureDto> displayedPictures = myRoomDao.getDisplayedPictures();

        List<MyFurnitureDto> displayedFurniture = myRoomDao.getDisplayedFurniture(userId);

        return new MyRoomDto(displayedPictures, displayedFurniture);
    }
}
