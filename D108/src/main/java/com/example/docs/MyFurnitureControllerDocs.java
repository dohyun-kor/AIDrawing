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
                    "주어진 `userId`에 해당하는 유저의 마이룸에 가구를 전시합니다.\n\n" +
                            "아래는 요청 형식 예시입니다..\n\n" +
                            "```json\n" +
                            "{\n" +
                            "    \"furId\": 224\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<Boolean> displayFurniture(@PathVariable int userId, @RequestBody MyFurnitureDto myFurnitureDto);

}
