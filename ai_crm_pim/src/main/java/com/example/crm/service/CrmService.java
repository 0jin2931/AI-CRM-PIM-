package com.example.crm.service;

import com.example.crm.domain.Member;
import com.example.crm.dto.MemberSearchCondition;
import com.example.crm.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrmService {

    private final GeminiService geminiService;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper; // JSON 파싱을 위한 Jackson 라이브러리 객체

    /**
     * 관리자의 자연어 요청을 처리하여 회원 리스트를 반환
     */
    public List<Member> searchMembersByNaturalLanguage(String userInput) {
        
        // 1. AI에게 프롬프트 엔지니어링을 적용하여 명확한 지시를 내림
        String prompt = "너는 고객 관리 시스템의 검색 조건을 추출하는 AI야.\n" +
                "사용자의 입력문에서 이름(name)과 등급(grade: NORMAL 또는 VIP)을 추출해서 반드시 순수 JSON 객체 포맷으로만 응답해줘. 다른 설명은 절대 하지마.\n" +
                "값이 없으면 null로 처리해.\n" +
                "예시: {\"name\": \"홍길동\", \"grade\": \"VIP\"}\n" +
                "사용자 입력: " + userInput;

        // 2. Gemini API 호출
        String jsonResponse = geminiService.askToGemini(prompt);
        
        // 마크다운 백틱(```json)이 섞여올 경우를 대비한 클렌징 작업
        jsonResponse = jsonResponse.replace("```json", "").replace("```", "").trim();

        try {
            // 3. String 형태의 JSON을 자바 객체(MemberSearchCondition)로 변환 (역직렬화)
            MemberSearchCondition condition = objectMapper.readValue(jsonResponse, MemberSearchCondition.class);

            // 4. QueryDSL이 적용된 Repository를 호출하여 동적 검색 수행
            return memberRepository.searchMembers(condition.getName(), condition.getGrade());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("AI 응답을 JSON으로 파싱하는데 실패했습니다: " + jsonResponse);
        }
    }
}