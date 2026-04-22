 package com.al7afez.al7afez.dto;

public record LevelRequest(
        String name,
        String code,
        int fromSurah,
        int toSurah,
        int fromAya,
        int toAya,
        int numberOfAyatPerSession
) {
}
