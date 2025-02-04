package com.example.controller;

import com.example.model.dto.RankingDto;
import com.example.model.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ranking")
public class RankingRestController {

    private final RankingService rankingService;

    @Autowired
    public RankingRestController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    // 상위 10위 랭킹을 조회하는 앤드포인트
    @GetMapping("/list")
    public ResponseEntity<List<RankingDto>> getTopRankings() {
        List<RankingDto> rankings  = null;
        try {
            rankings = rankingService.getTopRankings();
            if (rankings != null && !rankings.isEmpty()) {
                return ResponseEntity.ok(rankings);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 특정 유저의 랭킹 조회
    @GetMapping("/{userId}")
    public ResponseEntity<RankingDto> getUserRanking(@PathVariable int userId) {
        try {
           RankingDto ranking = rankingService.getUserRanking(userId);
           return ResponseEntity.ok(ranking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
