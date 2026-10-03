package com.example.crm.controller;

import com.example.crm.dto.MemberResponse;
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
    private final CrmService crmService;

    @PostMapping("/ask")
    public ResponseEntity<String> askAi(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String response = geminiService.askToGemini(prompt);
        return ResponseEntity.ok(response);
    }

    // 🔥 프론트엔드가 호출하는 엔드포인트: POST /api/ai/members/search
    @PostMapping("/members/search")
    public ResponseEntity<List<MemberResponse>> searchMembers(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        List<MemberResponse> result = crmService.searchMembersByNaturalLanguage(prompt).stream()
                .map(MemberResponse::from)
                .toList();
        return ResponseEntity.ok(result);
    }
}
