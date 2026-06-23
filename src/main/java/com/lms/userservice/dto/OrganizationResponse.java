package com.lms.userservice.dto;

import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        String document,
        String email,
        String phone,
        Boolean active) {
}
