package com.al7afez.al7afez.dto;

import java.time.LocalDate;
import java.util.List;

public record RecitationRequest(
        String code,
        LocalDate recitationDate,
        Long studentId,
        Integer fromSurah,
        Integer toSurah,
        Integer fromAya,
        Integer toAya,
        Integer numberOfAyat,
        Integer grade,
        String notes,
        List<RecitationMistakeRequest> mistakes
) {
}
