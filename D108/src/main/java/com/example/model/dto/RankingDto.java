package com.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 10등까지 전체 랭킹 조회
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RankingDto {

    private int userId; // 사용자 고유 ID

    private int exp; // 사용자 경험치

    private String nickname; // 사용자의 닉네임

    private int rankPosition; // 랭킹 순위

    private float winRate; // 랭킹 승률

}
