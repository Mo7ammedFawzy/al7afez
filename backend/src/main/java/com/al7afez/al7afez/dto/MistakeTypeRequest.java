package com.al7afez.al7afez.dto;

public record MistakeTypeRequest(
        String name,
        String code,
        Long parentId
) {
}
