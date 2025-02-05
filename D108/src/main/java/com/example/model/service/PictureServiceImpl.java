package com.example.model.service;


import com.example.model.dao.PictureDao;
import com.example.model.dto.PictureDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PictureServiceImpl implements PictureService {

    private final PictureDao pictureDao;

    @Autowired
    public PictureServiceImpl(PictureDao pictureDao) {
        this.pictureDao = pictureDao;
    }

    // 특정 사용자의 마이룸 그림 전체 조회
    @Override
    public List<PictureDto> getPicturesByUserId(int userId) {
        return pictureDao.getPicturesByUserId(userId);
    }

    // 마이룸 특정 그림 삭제
    @Override
    public void deletePictureById(int pictureId) {
        pictureDao.deletePictureById(pictureId);
    }


}
