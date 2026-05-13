package com.al7afez.al7afez.dto;

public record RecitationSuggestionResponse(
        Integer fromSurah,
        Integer fromAya,
        Integer toSurah,
        Integer toAya,
        Integer numberOfAyat
) {
}
