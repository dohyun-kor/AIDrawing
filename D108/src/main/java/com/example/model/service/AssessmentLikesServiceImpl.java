package com.example.model.service;

import com.example.model.dao.AssessmentLikesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssessmentLikesServiceImpl implements AssessmentLikesService {

    private final AssessmentLikesDao assessmentLikesDao;

    @Autowired
    public AssessmentLikesServiceImpl(AssessmentLikesDao assessmentLikesDao) {
        this.assessmentLikesDao = assessmentLikesDao;
    }

    @Override
    public boolean addLike(int assessmentId, int userId) {
        return assessmentLikesDao.insertLike(assessmentId, userId) == 1;
    }

    @Override
    public boolean removeLike(int assessmentId, int userId) {
        return assessmentLikesDao.deleteLike(assessmentId, userId) == 1;
    }

    @Override
    public int getLikeCount(int assessmentId) {
        return assessmentLikesDao.countLikes(assessmentId);
    }
}
