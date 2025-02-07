package com.example.docs;

import com.example.model.dto.MyFurnitureDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface MyFurnitureControllerDocs {
    @Operation(
            summary = "해당 사용자의 마이룸에 가구를 전시합니다.",
            description =
                    "아래는 요청 형식 예시입니다..\n\n" +
                            "```json\n" +
                            "{\n" +
                            "    \"userId\": 224\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<Boolean> displayFurniture(@PathVariable int userId, @RequestBody MyFurnitureDto myFurnitureDto);

}
