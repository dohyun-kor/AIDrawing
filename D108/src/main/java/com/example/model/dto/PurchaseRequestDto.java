package com.example.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PurchaseRequestDto {
// Getter/Setter
    /**
 * 아이템 구매 요청 시 필요한 필드를 담는 DTO
 * userId, itemId, itemPrice 모두 하나의 JSON body로 받는다.
 */
    private int userId;      // 구매자 사용자 ID
    private int itemId;      // 구매할 아이템 ID
    private int itemPrice;   // 아이템 가격

    // 모든 필드를 받는 생성자
    public PurchaseRequestDto(int userId, int itemId, int itemPrice) {
        this.userId = userId;
        this.itemId = itemId;
        this.itemPrice = itemPrice;
    }

}
