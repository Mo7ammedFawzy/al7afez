
package com.al7afez.al7afez.dto;

public record GroupResponse(
        Long id,
        String code,
        String name,
        EntityReference level,
        EntityReference sheikh
) {
}
