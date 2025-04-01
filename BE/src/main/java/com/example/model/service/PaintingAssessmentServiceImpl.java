// C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\model\service\PaintingAssessmentServiceImpl.java
package com.example.model.service;

import com.example.model.dao.PaintingAssessmentDao;
import com.example.model.dto.PaintingAssessmentDto;
import com.example.model.dto.PaintingAssessmentPutRequestDto;
import com.example.model.dto.PaintingAssessmentRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaintingAssessmentServiceImpl implements PaintingAssessmentService {

    private final PaintingAssessmentDao paintingAssessmentDao;

    @Autowired
    public PaintingAssessmentServiceImpl(PaintingAssessmentDao paintingAssessmentDao) {
        this.paintingAssessmentDao = paintingAssessmentDao;
    }

    @Override
    public int createPaintingAssessment(PaintingAssessmentRequestDto paintingAssessmentRequestDto) {
        return paintingAssessmentDao.insertPaintingAssessment(paintingAssessmentRequestDto);
    }

    @Override
    public int updatePaintingAssessment(int paintingAssessmentId, PaintingAssessmentPutRequestDto paintingAssessmentPutRequestDto) {
        return paintingAssessmentDao.updatePaintingAssessment(paintingAssessmentId, paintingAssessmentPutRequestDto);
    }

    @Override
    public int deletePaintingAssessment(int paintingAssessmentId) {
        return paintingAssessmentDao.deletePaintingAssessment(paintingAssessmentId);
    }

    @Override
    public List<PaintingAssessmentDto> getPaintingAssessmentsByUserId(int userId) {
        return paintingAssessmentDao.selectPaintingAssessmentsByUserId(userId);
    }

    @Override
    public List<PaintingAssessmentDto> getPaintingAssessmentsByPictureId(int pictureId) {
        return paintingAssessmentDao.selectPaintingAssessmentsByPictureId(pictureId);
    }
}
