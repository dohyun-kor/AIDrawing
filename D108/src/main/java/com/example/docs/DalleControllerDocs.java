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
                            "    \"difficulty\": \"easy\",\n" +
                            "    \"count\": \"2\",\n" +
                            "    \"size\": 1024x1024,\n" +
                            "}\n" +
                            "```\n" +
                            "아래는 응답 형식 예시입니다..\n\n" +
                            "```json\n" +
                            "{\n" +
                            "    \"data\": [\n" +
                            "        {\n" +
                            "            \"url\": \"https://example.com/image1.png\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"url\": \"https://example.com/image2.png\"\n" +
                            "        }\n" +
                            "    ]\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<DalleResponseDto> generateImage(
            @RequestParam String difficulty,
            @RequestParam int count,
            @RequestParam(required = false) String size);
}
