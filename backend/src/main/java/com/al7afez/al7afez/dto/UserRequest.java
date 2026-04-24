package com.al7afez.al7afez.dto;

public record UserRequest(
        String name,
        String code,
        String username,
        String password,
        Long sheikhId) {
}
