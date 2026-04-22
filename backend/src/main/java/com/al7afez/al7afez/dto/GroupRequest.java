package com.al7afez.al7afez.dto;

public record GroupRequest(
        String name,
        String code,
        Long levelId,
        Long sheikhId
) {
}
