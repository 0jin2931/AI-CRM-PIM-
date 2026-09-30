package com.example.crm.repository;

import com.example.crm.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

// 기본적인 CRUD는 JpaRepository가, 복잡한 동적 검색은 MemberRepositoryCustom이 담당!
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
}