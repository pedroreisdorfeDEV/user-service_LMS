package com.lms.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record UserCreateRequest(
        UUID organizationId,
        @NotBlank(message = "Full name is required")
        @Size(max = 150, message = "Full name must have at most 150 characters")
        String fullName,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 150, message = "Email must have at most 150 characters")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must have between 8 and 100 characters")
        String password,
        @Size(max = 30, message = "Phone must have at most 30 characters")
        String phone,
        Set<String> roles) {
}
