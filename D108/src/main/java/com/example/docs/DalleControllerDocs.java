package com.example.docs;

import com.example.model.dto.DalleResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface DalleControllerDocs {
    @Operation(
            summary = "DALLE API를 통해서 이미지를 생성합니다.",
            description =
                    "아래는 요청 형식 예시입니다..\n\n" +
                            "```json\n" +
                            "{\n" +
                            "    \"subject\": \"apple\",\n" +
                            "    \"size\": 1024x1024,\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<DalleResponseDto> generateImage(@RequestParam String subject, @RequestParam(required = false) String size);
}
