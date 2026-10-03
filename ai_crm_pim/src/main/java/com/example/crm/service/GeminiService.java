package com.example.crm.service;

import com.example.crm.dto.GeminiRequest;
import com.example.crm.dto.GeminiResponse;
import com.example.crm.exception.AiResponseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.time.Duration;
import java.util.List;

@Service
public class GeminiService {

    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    // WebClient 인스턴스 생성 (외부 API 통신용)
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent")
            .build();

    // application.yml에 등록해둔 API 키를 가져옵니다.
    @Value("${gemini.api.key}")
    private String apiKey;

    /**
     * 사용자의 텍스트를 받아 Gemini API에 전송하고 답변을 반환하는 메서드
     * 호출 실패 또는 비어 있는 응답이면 AiResponseException 발생
     */
    public String askToGemini(String prompt) {
        // 1. 요청할 데이터(JSON 구조) 조립
        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
        GeminiRequest requestDto = new GeminiRequest(List.of(content));

        // 2. WebClient를 이용해 POST 요청 전송
        GeminiResponse response;
        try {
            response = webClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .bodyValue(requestDto)
                    .retrieve() // 응답 받아오기
                    .bodyToMono(GeminiResponse.class) // 응답을 GeminiResponse DTO로 변환 (비동기)
                    .block(TIMEOUT); // 동기적으로 결과를 기다림 (최대 30초)
        } catch (WebClientException | IllegalStateException e) {
            // WebClientException: 4xx/5xx·네트워크 오류, IllegalStateException: 타임아웃
            throw new AiResponseException("Gemini API 호출에 실패했습니다.", e);
        }

        // 3. 응답 결과에서 텍스트만 추출하여 반환 (중간 값이 비어 있을 수 있으므로 단계별 null 체크)
        if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
            throw new AiResponseException("Gemini API 응답이 비어 있습니다.");
        }
        GeminiResponse.Content resContent = response.getCandidates().get(0).getContent();
        if (resContent == null || resContent.getParts() == null || resContent.getParts().isEmpty()
                || resContent.getParts().get(0).getText() == null) {
            throw new AiResponseException("Gemini API 응답에 텍스트가 없습니다.");
        }
        return resContent.getParts().get(0).getText();
    }
}
