package com.example.crm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AiCrmPimApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiCrmPimApplication.class, args);
	}

	// 이 부분을 추가해 주세요.
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
