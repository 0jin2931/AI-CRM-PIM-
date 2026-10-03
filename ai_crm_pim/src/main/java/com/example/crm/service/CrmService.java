package com.example.crm.service;

import com.example.crm.domain.Member;
import com.example.crm.dto.MemberSearchCondition;
import com.example.crm.exception.AiResponseException;
import com.example.crm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrmService {

    private final GeminiService geminiService;
    private final MemberRepository memberRepository;
    private final JsonMapper jsonMapper; // Spring Boot가 자동 등록하는 Jackson 3 JSON 매퍼

    /**
     * 관리자의 자연어 요청을 처리하여 회원 리스트를 반환
     * 외부 API(Gemini) 호출 동안 DB 커넥션을 점유하지 않도록 이 메서드에는 트랜잭션을 걸지 않음.
     * (DB 조회 트랜잭션은 MemberRepositoryImpl.searchMembers에서 시작)
     */
    public List<Member> searchMembersByNaturalLanguage(String userInput) {
        if (!StringUtils.hasText(userInput)) {
            throw new IllegalArgumentException("검색어를 입력해 주세요.");
        }

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

        // 3. String 형태의 JSON을 자바 객체(MemberSearchCondition)로 변환 (역직렬화)
        MemberSearchCondition condition;
        try {
            condition = jsonMapper.readValue(jsonResponse, MemberSearchCondition.class);
        } catch (JacksonException e) {
            throw new AiResponseException("AI 응답을 JSON으로 파싱하는데 실패했습니다: " + jsonResponse, e);
        }

        // 4. QueryDSL이 적용된 Repository를 호출하여 동적 검색 수행
        return memberRepository.searchMembers(condition.getName(), condition.getGrade());
    }
}
