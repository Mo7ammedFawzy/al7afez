
package com.al7afez.al7afez.dto;

import java.util.List;

public record GroupResponse(
        Long id,
        String code,
        String name,
        EntityReference level,
        EntityReference sheikh,
        List<EntityReference> students,
        long studentCount
) {
}
