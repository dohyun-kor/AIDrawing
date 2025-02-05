package com.example.docs;

import com.example.model.dto.RankingDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface RankingControllerDocs {
    @Operation(
            summary = "상위 10명의 랭킹을 조회합니다.",
            description =
                    "상위 10명의 사용자 랭킹 데이터를 반환합니다."
    )
    public ResponseEntity<List<RankingDto>> getTopRankings();

    @Operation(
            summary = "특정 유저의 랭킹 조회",
            description =
                            "아래는 요청 형식 예시입니다:\n\n" +
                            "```json\n" +
                            "{\n" +
                            "    \"userId\": 8\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<RankingDto> getUserRanking(@PathVariable int userId);
}

