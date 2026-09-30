package com.example.crm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String grade; // NORMAL, VIP 등 (향후 Enum으로 변경해도 좋습니다)

    private LocalDateTime createdAt;

    @Builder
    public Member(String name, String email, String grade) {
        this.name = name;
        this.email = email;
        this.grade = grade;
        this.createdAt = LocalDateTime.now();
    }
}