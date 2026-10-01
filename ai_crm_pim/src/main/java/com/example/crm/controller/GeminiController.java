package com.example.crm.controller;

import com.example.crm.domain.Member;
import com.example.crm.service.CrmService;
import com.example.crm.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;
    private final CrmService CrmService; // 이 주입이 누락되었는지 확인

    @PostMapping("/ask")
    public ResponseEntity<String> askAi(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String response = geminiService.askToGemini(prompt);
        return ResponseEntity.ok(response);
    }

    // 🔥 프론트엔드가 호출하는 엔드포인트: POST /api/ai/members/search
    @PostMapping("/members/search")
    public ResponseEntity<List<Member>> searchMembers(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        List<Member> result = CrmService.searchMembersByNaturalLanguage(prompt);
        return ResponseEntity.ok(result);
    }
}