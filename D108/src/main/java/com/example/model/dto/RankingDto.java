package com.example.model.dto;

// 10등까지 전체 랭킹 조회
public class RankingDto {
    private int rankingId; // 랭킹 고유 ID
    private int userId; // 사용자 고유 ID

    private int exp; // 사용자 경험치

    private String nickname; // 사용자의 닉네임

    private int rankPosition; // 랭킹 순위

    private float winRate; // 랭킹 승률


    // 기본 생성자
    public RankingDto() {

    }

    public RankingDto(int rankingId, int userId, int exp, String nickname, int rankPosition, float winRate) {
        this.rankingId = rankingId;
        this.userId = userId;
        this.exp = exp;
        this.nickname = nickname;
        this.rankPosition = rankPosition;
        this.winRate = winRate;
    }

    public int getRankingId() {
        return rankingId;
    }

    public void setRankingId(int rankingId) {
        this.rankingId = rankingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getRankPosition() {
        return rankPosition;
    }

    public void setRankPosition(int rankPosition) {
        this.rankPosition = rankPosition;
    }

    public float getWinRate() {
        return winRate;
    }

    public void setWinRate(float winRate) {
        this.winRate = winRate;
    }

}
