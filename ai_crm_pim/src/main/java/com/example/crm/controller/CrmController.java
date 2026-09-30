package com.example.crm.controller;

import com.example.crm.domain.Member;
import com.example.crm.service.CrmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crm")
@RequiredArgsConstructor
public class CrmController {

    private final CrmService crmService;

    @PostMapping("/search")
    public ResponseEntity<List<Member>> search(@RequestBody Map<String, String> request) {
        String userInput = request.get("userInput");
        List<Member> members = crmService.searchMembersByNaturalLanguage(userInput);
        return ResponseEntity.ok(members);
    }
}