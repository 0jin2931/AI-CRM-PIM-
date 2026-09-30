package com.example.crm.service;

import com.example.crm.dto.GeminiRequest;
import com.example.crm.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiService {

    // WebClient 인스턴스 생성 (외부 API 통신용)
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent")
            .build();

    // application.yml에 등록해둔 API 키를 가져옵니다.
    @Value("${gemini.api.key}")
    private String apiKey;

    /**
     * 사용자의 텍스트를 받아 Gemini API에 전송하고 답변을 반환하는 메서드
     */
    public String askToGemini(String prompt) {
        // 1. 요청할 데이터(JSON 구조) 조립
        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
        GeminiRequest requestDto = new GeminiRequest(List.of(content));

        // 2. WebClient를 이용해 POST 요청 전송
        GeminiResponse response = webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                .bodyValue(requestDto)
                .retrieve() // 응답 받아오기
                .bodyToMono(GeminiResponse.class) // 응답을 GeminiResponse DTO로 변환 (비동기)
                .block(); // 동기적으로 결과를 기다림 (지금은 단순 조회를 위해 block 사용)

        // 3. 응답 결과에서 텍스트만 추출하여 반환
        if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
            return response.getCandidates().get(0).getContent().getParts().get(0).getText();
        }

        return "Gemini API 응답을 가져오지 못했습니다.";
    }
}