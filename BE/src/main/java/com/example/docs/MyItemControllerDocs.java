package com.example.docs;

import com.example.model.dto.MyItemDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface MyItemControllerDocs {

    @Operation(
            summary = "특정 사용자(userId)의 모든 MyItem 조회",
            description = "예: GET /my-items?userId=224"
    )
    List<MyItemDto> getAllMyItems(@RequestParam int userId);

    @Operation(
            summary = "구매(PK) ID로 단일 MyItem 조회",
            description = "예: GET /my-items/{purchaseId}"
    )
    MyItemDto getMyItemById(@PathVariable int purchaseId);

}
