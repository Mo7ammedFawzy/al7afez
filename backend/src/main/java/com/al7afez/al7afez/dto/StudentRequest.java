package com.al7afez.al7afez.dto;

import com.al7afez.al7afez.model.Gender;
import java.time.LocalDate;

public record StudentRequest(
        String name,
        String code,
        LocalDate birthDate,
        String phoneNumber,
        String parentPhoneNumber,
        Gender gender,
        Long recitationGroupId
) {
}
