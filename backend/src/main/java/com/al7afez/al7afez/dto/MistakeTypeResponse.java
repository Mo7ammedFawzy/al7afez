package com.al7afez.al7afez.dto;

public record MistakeTypeResponse(
        Long id,
        String code,
        String name,
        EntityReference parent
) {
}
