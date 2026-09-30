package com.example.crm.repository;

import com.example.crm.domain.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

// Q클래스 static import (코드 가독성을 위해 필수!)
import static com.example.crm.domain.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> searchMembers(String name, String grade) {
        return queryFactory
                .selectFrom(member)
                .where(
                        nameEq(name),   // 이름이 조건으로 들어오면 WHERE문에 추가
                        gradeEq(grade)  // 등급이 조건으로 들어오면 WHERE문에 추가 (AND 조건)
                )
                .fetch();
    }

    // 이름 일치 조건 (값이 없으면 null 반환 -> QueryDSL이 알아서 WHERE 조건에서 무시함)
    private BooleanExpression nameEq(String name) {
        return StringUtils.hasText(name) ? member.name.eq(name) : null;
    }

    // 등급 일치 조건
    private BooleanExpression gradeEq(String grade) {
        return StringUtils.hasText(grade) ? member.grade.eq(grade) : null;
    }
}