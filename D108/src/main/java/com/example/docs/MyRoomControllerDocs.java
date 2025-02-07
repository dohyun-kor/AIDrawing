package com.example.docs;

import com.example.model.dto.MyRoomDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface MyRoomControllerDocs {
    @Operation(
            summary = "마이룸에 전시된 그림과 가구를 조회합니다.",
            description =
                    "아래는 요청 형식 예시입니다..\n\n" +
                            "```json\n" +
                            "{\n" +
                            "    \"userId\": 224\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<MyRoomDto> getDisplayedItems(@PathVariable int userId);
}
