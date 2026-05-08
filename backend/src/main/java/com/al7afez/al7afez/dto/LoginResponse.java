package com.al7afez.al7afez.dto;

public record LoginResponse(
        String token,
        Long userId,
        String name,
        String username
) {}
