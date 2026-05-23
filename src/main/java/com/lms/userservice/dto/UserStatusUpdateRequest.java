package com.lms.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserStatusUpdateRequest(
        @NotBlank(message = "Status is required")
        @Size(max = 30, message = "Status must have at most 30 characters")
        String status) {
}
