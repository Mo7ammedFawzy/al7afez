package com.al7afez.al7afez.dto;

import java.util.List;

public record GroupRequest(
        String name,
        String code,
        Long levelId,
        Long sheikhId,
        List<Long> studentIds
) {
}
