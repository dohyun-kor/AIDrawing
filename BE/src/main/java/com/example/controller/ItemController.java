package com.example.controller;

import com.example.docs.ItemControllerDocs;
import com.example.model.dto.ItemDto;
import com.example.model.service.ItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 아이템 관련 엔드포인트를 처리하는 Controller
 *
 * <p>주요 기능:
 * <ul>
 *     <li>GET /api/items : 모든 아이템 조회</li>
 *     <li>GET /api/items/{itemId} : 특정 아이템 상세 조회</li>
 * </ul>
 */
@Tag(name = "아이템", description = "아이템 관련 API")
@RestController
@RequestMapping("/items")
public class ItemController implements ItemControllerDocs {

    @Autowired
    private ItemService itemService; // 비즈니스 로직을 담당하는 서비스

    /**
     * 상점에 등록된 모든 아이템을 조회한다.
     *
     * @return 모든 아이템 리스트
     */
    @GetMapping
    public List<ItemDto> getAllItems() {
        return itemService.findAll();
    }

    /**
     * 특정 아이템을 아이템 ID로 조회한다.
     *
     * @param itemId 조회하고자 하는 아이템의 ID
     * @return 아이템 정보
     */
    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId) {
        return itemService.findById(itemId);
    }
}
