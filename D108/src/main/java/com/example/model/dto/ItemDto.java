// C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\model\dto\ItemDto.java
package com.example.model.dto;

// 상점에 등록된 아이템 정보를 담는 DTO

public class ItemDto {
    private int itemId; // 아이템 고유 ID
    private String name; // 아이템 이름
    private String category; // 아이템 분류 (기능, 치장 등)
    private int price; // 아이템 가격
    private String description; // 아이템 설명
    private String rink;

    // 기본 생성자
    public ItemDto() {
    }

    // 모든 필드를 받는 생성자
    public ItemDto(int itemId, String name, String category, int price, String description, String rink) {
        this.itemId = itemId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.rink = rink;
    }

    // Getter/Setter
    public int getItemId() {
        return itemId;
    }
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getRink() {
        return rink;
    }
    public void setRink(String rink) {
        this.rink = rink;
    }
}
