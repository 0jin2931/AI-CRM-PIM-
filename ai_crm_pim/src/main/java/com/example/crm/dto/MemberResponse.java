package com.example.crm.dto;

import com.example.crm.domain.Member;

import java.time.LocalDateTime;

public record MemberResponse(Long id, String name, String email, String grade, LocalDateTime createdAt) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getName(), member.getEmail(),
                member.getGrade(), member.getCreatedAt());
    }
}
