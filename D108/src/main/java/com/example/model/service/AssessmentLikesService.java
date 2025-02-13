package com.example.model.service;

public interface AssessmentLikesService {
    boolean addLike(int assessmentId, int userId);
    boolean removeLike(int assessmentId, int userId);
    int getLikeCount(int assessmentId);
}
