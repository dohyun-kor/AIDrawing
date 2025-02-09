package com.example.controller;

import com.example.docs.MyItemControllerDocs;
import com.example.model.dto.MyItemDto;
import com.example.model.dto.PurchaseRequestDto;
import com.example.model.service.MyItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자가 실제로 구매한 아이템(MyItem)에 대한 Controller
 */
@Tag(name = "사용자가 구매한 아이템", description = "사용자가 구매한 아이템 관련 API")
@RestController
@RequestMapping("/my-items")
public class MyItemController implements MyItemControllerDocs {

    @Autowired
    private MyItemService myItemService;

    /**
     * 특정 user가 가진 모든 아이템을 조회
     * @param userId 사용자 고유 ID
     * @return MyItemDto 리스트
     */
    @GetMapping
    public List<MyItemDto> getAllMyItems(@RequestParam int userId) {
        // GET /my-items?userId=123
        return myItemService.findAllMyItems(userId);
    }

    /**
     * 구매 고유 ID를 이용해 단일 MyItem 조회
     * @param purchaseId 구매(PK) 고유 ID
     * @return MyItemDto
     */
    @GetMapping("/{purchaseId}")
    public MyItemDto getMyItemById(@PathVariable int purchaseId) {
        return myItemService.findMyItemById(purchaseId);
    }

    /**
     * 아이템 구매 (포인트 차감 + MyItem Insert)
     * @param purchaseRequestDto {"userId": ..., "itemId": ..., "itemPrice": ...}
     * @return true: 성공, false: 실패
     */

    @PostMapping
    public boolean purchaseItem(@RequestBody PurchaseRequestDto purchaseRequestDto) {
        // 1) PurchaseRequestDto에서 userId, itemId, itemPrice 추출
        int userId = purchaseRequestDto.getUserId();
        int itemId = purchaseRequestDto.getItemId();
        int itemPrice = purchaseRequestDto.getItemPrice();

        // 2) MyItemDto 생성 (userId, itemId 설정)
        MyItemDto myItemDto = new MyItemDto();
        myItemDto.setUserId(userId);
        myItemDto.setItemId(itemId);
        // 필요하다면 purchaseDate 등 다른 필드도 세팅할 수 있음

        // 3) Service를 통해 구매 로직 실행
        try {
            int result = myItemService.purchaseItem(myItemDto, itemPrice);
            // Service에서 int result = 1이면 성공
            return (result == 1);
        } catch (Exception e) {
            // 포인트 부족 등 예외 발생 시 false 반환(또는 적절한 에러 응답)
            return false;
        }
    }

}
