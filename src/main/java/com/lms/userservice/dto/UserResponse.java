package com.lms.userservice.dto;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        UUID organizationId,
        String organizationName,
        String fullName,
        String email,
        String phone,
        String status,
        Set<String> roles,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
