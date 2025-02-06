// C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\model\dto\MyItemDto.java
package com.example.model.dto;

// 사용자가 실제로 구매하여 가지고 있는 아이템 정보 DTO

public class MyItemDto {

    private int purchaseId;             // 구매 고유 ID
    private int itemId;                 // 아이템 고유 ID
    private int userId;                 // 사용자 고유 ID
    private String itemName;            // 아이템 이름 (조인 결과에 따라)
    private String purchaseDate;        // 구매일 (string 또는 localDateTime 등으로 구성 가능)

    // 기본 생성자
    public MyItemDto() {

    }
    // 모든 필드를 받는 생성자
    public MyItemDto(int purchaseId, int itemId, int userId, String itemName , String purchaseDate) {
        this.purchaseId = purchaseId;
        this.itemId = itemId;
        this.userId = userId;
        this.itemName = itemName;
        this.purchaseDate = purchaseDate;
    }

    //Getter/Setter
    public int getPurchaseId() {
        return purchaseId;
    }
    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }
    public int getItemId() {
        return itemId;
    }
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getItemName(){
        return itemName;
    }
    public void setitemName(String itemName){
        this.itemName = itemName;
    }
    public String getPurchaseDate() {
        return purchaseDate;
    }
}
