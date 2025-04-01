package com.example.docs;

import com.example.model.dto.RankingDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface RankingControllerDocs {
    @Operation(
            summary = "전체 랭킹에서 상위 10명의 랭킹을 조회합니다.",
            description =
                    "상위 10명의 사용자 랭킹 데이터를 반환합니다."
    )
    public ResponseEntity<List<RankingDto>> getTopRankings();

    @Operation(
            summary = "해당 유저의 랭킹을 조회합니다.",
            description =
                    "주어진 `userId`에 해당하는 유저의 랭킹을 조회합니다."
    )
    public ResponseEntity<RankingDto> getUserRanking(@PathVariable int userId);
}

