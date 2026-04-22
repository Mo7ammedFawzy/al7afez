package com.al7afez.al7afez.dto;

public record MistakeBreakdownResponse(
        Long id,
        String name,
        long count,
        double share
) {
}
