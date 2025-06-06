package com.example.controller;

import com.example.docs.RankingControllerDocs;
import com.example.model.dto.RankingDto;
import com.example.model.service.RankingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "랭킹", description = "랭킹 관련 API")
@RestController
@RequestMapping("/ranking")
public class RankingController implements RankingControllerDocs {

    private final RankingService rankingService;

    @Autowired
    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    // 상위 10위 랭킹을 조회하는 앤드포인트
    @GetMapping("/list")
    public ResponseEntity<List<RankingDto>> getTopRankings() {
        List<RankingDto> rankings  = null;
        try {
            rankings = rankingService.getTopRankings();
            return ResponseEntity.ok(rankings);
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
