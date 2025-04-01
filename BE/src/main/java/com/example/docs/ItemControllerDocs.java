// src/main/java/com/example/docs/ItemControllerDocs.java

package com.example.docs;

import com.example.model.dto.ItemDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * ItemController에 대한 Swagger/OpenAPI 문서 인터페이스
 *
 * <p>스프링 문서화 관례상, Controller가 구현해야 할 메서드를 인터페이스로 분리하고,
 * 여기에 Swagger 어노테이션(@Operation 등)을 달아 문서화를 진행할 수 있습니다.
 */
public interface ItemControllerDocs {

    @Operation(
            summary = "모든 아이템 조회",
            description = "상점에 등록된 모든 아이템을 조회합니다.\n\n" +
                    "예시 응답:\n" +
                    "```json\n" +
                    "[\n" +
                    "  {\n" +
                    "    \"itemId\": 1,\n" +
                    "    \"name\": \"아이템 이름\",\n" +
                    "    \"category\": \"카테고리\",\n" +
                    "    \"price\": 1000,\n" +
                    "    \"description\": \"아이템 설명\",\n" +
                    "    \"link\": \"이미지 경로\"\n" +
                    "  }\n" +
                    "]\n" +
                    "```"
    )
    List<ItemDto> getAllItems();

    @Operation(
            summary = "특정 아이템 상세 조회",
            description = "아이템 고유 ID를 이용해 특정 아이템의 상세 정보를 조회합니다.\n\n" +
                    "예시 응답:\n" +
                    "```json\n" +
                    "{\n" +
                    "  \"itemId\": 10,\n" +
                    "  \"name\": \"특정 아이템\",\n" +
                    "  \"category\": \"장비\",\n" +
                    "  \"price\": 3000,\n" +
                    "  \"description\": \"특정 아이템 설명\",\n" +
                    "  \"link\": \"이미지 경로\"\n" +
                    "}\n" +
                    "```"
    )
    ItemDto getItemById(@PathVariable int itemId);
}
