package com.example.crm.config;

import com.example.crm.domain.Member;
import com.example.crm.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final MemberRepository memberRepository;

    @PostConstruct
    public void init() {
        // 더미 데이터 삽입 (애플리케이션 실행 시 1번만 동작)
        if (memberRepository.count() == 0) {
            Member m1 = Member.builder().name("김영진").email("young@test.com").grade("VIP").build();
            Member m2 = Member.builder().name("이다은").email("lee@test.com").grade("NORMAL").build();
            Member m3 = Member.builder().name("박성현").email("park@test.com").grade("VIP").build();
            
            memberRepository.saveAll(List.of(m1, m2, m3));
        }
    }
}