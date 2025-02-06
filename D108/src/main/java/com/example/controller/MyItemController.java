package com.example.controller;

import com.example.docs.MyItemControllerDocs;
import com.example.model.dto.MyItemDto;
import com.example.model.service.MyItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자가 실제로 구매한 아이템(MyItem)에 대한 Controller
 */
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
     * 새로 MyItem(아이템 구매 기록) 등록
     * @param myItemDto 구매 정보
     * @return insert 결과(성공 시 1, 실패 시 0 등)
     */
    @PostMapping
    public int insertMyItem(@RequestBody MyItemDto myItemDto) {
        // POST /my-items
        // Body 예시:
        // {
        //   "itemId": 10,
        //   "userId": 224
        // }
        return myItemService.insertMyItem(myItemDto);
    }
}
