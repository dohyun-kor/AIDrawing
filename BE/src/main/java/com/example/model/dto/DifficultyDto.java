package com.example.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DifficultyDto {
    private int topicId;

    private String topic;

    private String topicEn;

    public DifficultyDto(int topicId, String topic, String topicEn) {
        this.topicId = topicId;
        this.topic = topic;
        this.topicEn = topicEn;
    }
}
