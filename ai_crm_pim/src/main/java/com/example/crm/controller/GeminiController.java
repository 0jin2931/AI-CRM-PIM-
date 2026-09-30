package com.example.crm.controller;

import com.example.crm.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController // JSON 형태로 객체 데이터를 반환하는 REST API용 컨트롤러
@RequestMapping("/api/ai") // 기본 URL 경로 설정
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

    /**
     * 관리자가 AI에게 질문을 던지는 API 엔드포인트
     * [POST] /api/ai/ask
     */
    @PostMapping("/ask")
    public ResponseEntity<String> askAi(@RequestBody Map<String, String> request) {
        // 클라이언트가 보낸 JSON에서 "prompt"라는 키의 값을 꺼냅니다.
        String prompt = request.get("prompt");

        // 서비스 계층에 질문을 넘기고 답변을 받습니다.
        String response = geminiService.askToGemini(prompt);

        // HTTP 상태 코드 200(OK)과 함께 답변을 반환합니다.
        return ResponseEntity.ok(response);
    }
}