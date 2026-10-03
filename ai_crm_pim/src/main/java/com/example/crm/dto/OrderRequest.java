package com.example.crm.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderRequest(
        @NotNull Long memberId,
        @NotNull Long productId,
        @NotNull @Positive Integer count) {
}
