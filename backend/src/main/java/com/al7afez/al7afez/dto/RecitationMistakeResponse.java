package com.al7afez.al7afez.dto;

public record RecitationMistakeResponse(
        Long id,
        EntityReference mistakeType,
        Integer surahNumber,
        Integer ayaNumber,
        Integer wordIndex
) {
}
