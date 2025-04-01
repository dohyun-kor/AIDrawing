// C:\SSAFY\Do\gitlab_repo\D108\D108\src\main\java\com\example\model\dto\MyItemDto.java
package com.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 사용자가 실제로 구매하여 가지고 있는 아이템 정보 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyItemDto {

    private int purchaseId;             // 구매 고유 ID
    private int itemId;                 // 아이템 고유 ID
    private int userId;                 // 사용자 고유 ID
    private String itemName;            // 아이템 이름 (조인 결과에 따라)
    private String category;
    private String purchaseDate;        // 구매일 (string 또는 localDateTime 등으로 구성 가능)

}
