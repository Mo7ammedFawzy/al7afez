package com.al7afez.al7afez.dto;

public record RecitationMistakeRequest(
        Long mistakeTypeId,
        Integer count,
        String note
) {
}
