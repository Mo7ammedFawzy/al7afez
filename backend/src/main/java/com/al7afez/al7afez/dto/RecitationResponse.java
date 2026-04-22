package com.al7afez.al7afez.dto;

import java.time.LocalDate;
import java.util.List;

public record RecitationResponse(
        Long id,
        String code,
        LocalDate recitationDate,
        EntityReference student,
        EntityReference group,
        EntityReference level,
        EntityReference sheikh,
        Integer fromSurah,
        Integer toSurah,
        Integer fromAya,
        Integer toAya,
        Integer numberOfAyat,
        Integer grade,
        int totalMistakes,
        String notes,
        List<RecitationMistakeResponse> mistakes
) {
}
