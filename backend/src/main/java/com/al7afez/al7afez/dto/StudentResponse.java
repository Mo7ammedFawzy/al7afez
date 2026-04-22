package com.al7afez.al7afez.dto;

import com.al7afez.al7afez.model.Gender;
import java.time.LocalDate;

public record StudentResponse(
        Long id,
        String code,
        String name,
        LocalDate birthDate,
        String phoneNumber,
        String parentPhoneNumber,
        Gender gender,
        EntityReference recitationGroup,
        EntityReference level
) {
}
