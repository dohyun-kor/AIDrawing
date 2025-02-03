package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "Sample Controller", description = "샘플 API를 제공하는 컨트롤러입니다.")
public class SampleController {

    @GetMapping("/hello")
    @Operation(summary = "Hello API", description = "간단한 인사말을 반환합니다.")
    public String hello() {
        return "Hello, Swagger!";
    }
}