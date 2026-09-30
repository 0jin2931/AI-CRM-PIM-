package com.example.crm.repository;

import com.example.crm.domain.Member;
import java.util.List;

public interface MemberRepositoryCustom {
    // 이름, 등급이 파라미터로 넘어올 수도 있고(Null 아님), 안 넘어올 수도(Null) 있습니다.
    List<Member> searchMembers(String name, String grade);
}