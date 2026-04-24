package com.al7afez.al7afez.dto;

public record UserResponse(
        Long id,
        String code,
        String name,
        String username,
        EntityReference sheikh) {
}
