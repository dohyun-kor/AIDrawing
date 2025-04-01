// C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\model\dto\ItemDto.java
package com.example.model.dto;

// 상점에 등록된 아이템 정보를 담는 DTO

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ItemDto {
    // Getter/Setter
    private int itemId; // 아이템 고유 ID
    private String name; // 아이템 이름
    private String category; // 아이템 분류 (기능, 치장 등)
    private int price; // 아이템 가격
    private String description; // 아이템 설명
    private String link;

    // 모든 필드를 받는 생성자
    public ItemDto(int itemId, String name, String category, int price, String description, String link) {
        this.itemId = itemId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.link = link;
    }

}
