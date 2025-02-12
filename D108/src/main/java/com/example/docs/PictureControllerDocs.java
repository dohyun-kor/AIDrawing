package com.example.docs;

import com.example.model.dto.PictureDisplayRequestDto;
import com.example.model.dto.PictureDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface PictureControllerDocs {
    @Operation(
            summary = "해당 사용자의 마이룸에 있는 그림을 전체 조회합니다.",
            description = "주어진 `userId`에 해당하는 사용자가 업로드한 모든 그림을 반환합니다.\n\n"+
                    "요청 형식 예시:\n\n" +
                    "```json\n" +
                    "{\n" +
                    "    \"userId\": 8\n" +
                    "}\n" +
                    "```"

    )
    public ResponseEntity<List<PictureDto>> getPicturesByUserId(@RequestParam int userId);

    @Operation(
            summary = "마이룸에 있는 해당 그림을 삭제합니다.",
            description = "주어진 `pictureId`에 해당하는 그림을 삭제합니다."
    )
    public void deletePicture(@PathVariable int pictureId);

    @Operation(
            summary = "해당 사용자의 마이룸에 그림을 전시합니다.",
            description =
                    "주어진 `userId`에 해당하는 사용자의 마이룸에 그림 전시 정보를 저장합니다.\n\n" +
                            "요청 형식 예시:\n\n" +
                            "```json\n" +
                            "[\n" +
                            "    {\n" +
                            "        \"pictureId\": \"1\",\n" +
                            "        \"rotation\": \"90\",\n" +
                            "        \"x_val\": \"6.7689\",\n" +
                            "        \"y_val\": \"39.7524\"\n" +
                            "    },\n" +
                            "    {\n" +
                            "        \"pictureId\": \"2\",\n" +
                            "        \"rotation\": \"0\",\n" +
                            "        \"x_val\": \"19.2345\",\n" +
                            "        \"y_val\": \"49.8646\"\n" +
                            "    }\n" +
                            "]\n" +
                            "```\n"
    )
    public ResponseEntity<Boolean> updatePictureDisplay(@PathVariable int userId, @RequestBody List<PictureDisplayRequestDto> pictureDisplayRequestDto);
}
