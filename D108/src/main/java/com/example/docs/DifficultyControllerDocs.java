package com.example.docs;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

public interface DifficultyControllerDocs {

    @Operation(
            summary = "게임에 사용되는 2개의 단어를 랜덤으로 반환합니다.",
            description = "주어진 난이도`difficulty`에 맞는 단어 2개를 반환합니다.\n\n" +
                    "허용 가능한 난이도 값:\n" +
                    "- `easy`\n" +
                    "- `normal`\n" +
                    "- `hard`\n\n" +
                    "대소문자를 구분하지 않습니다.\n\n" +
                    "아래는 응답 형식 예시입니다..\n\n" +
                    "```json\n" +
                    "{\n" +
                    "    \"data\": [\n" +
                    "        {\n" +
                    "            \"topic\": \"모자\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"topic\": \"버스\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}\n" +
                    "```\n"
    )
    public ResponseEntity<List<Map<String, String>>> getTopicsByDifficulty(@PathVariable String difficulty);

}
