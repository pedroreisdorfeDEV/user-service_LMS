package com.lms.userservice.service;

import com.lms.userservice.dto.UserCreateRequest;
import com.lms.userservice.dto.UserResponse;
import com.lms.userservice.dto.UserStatusUpdateRequest;
import com.lms.userservice.entity.Organization;
import com.lms.userservice.entity.Role;
import com.lms.userservice.entity.User;
import com.lms.userservice.exception.BusinessException;
import com.lms.userservice.exception.ResourceNotFoundException;
import com.lms.userservice.repository.OrganizationRepository;
import com.lms.userservice.repository.RoleRepository;
import com.lms.userservice.repository.UserRepository;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String DEFAULT_ROLE = "STUDENT";

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new BusinessException("A user with this email already exists");
        }

        Organization organization = resolveOrganization(request.organizationId());
        Set<Role> roles = resolveRoles(request.roles());

        User user = User.builder()
                .organization(organization)
                .fullName(request.fullName().trim())
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .status("ACTIVE")
                .roles(roles)
                .build();

        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        return toResponse(getUser(id));
    }

    @Transactional
    public UserResponse updateStatus(UUID id, UserStatusUpdateRequest request) {
        User user = getUser(id);
        user.setStatus(request.status().trim().toUpperCase(Locale.ROOT));
        return toResponse(user);
    }

    private User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Organization resolveOrganization(UUID organizationId) {
        if (organizationId == null) {
            return null;
        }
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
    }

    private Set<Role> resolveRoles(Set<String> requestedRoles) {
        Set<String> roleNames = requestedRoles == null || requestedRoles.isEmpty()
                ? Set.of(DEFAULT_ROLE)
                : requestedRoles.stream()
                        .map(role -> role.trim().toUpperCase(Locale.ROOT))
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Role> foundRoles = roleRepository.findByNameIn(roleNames);
        if (foundRoles.size() != roleNames.size()) {
            Set<String> foundNames = foundRoles.stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            Set<String> missing = roleNames.stream()
                    .filter(roleName -> !foundNames.contains(roleName))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            throw new BusinessException("Roles not found: " + String.join(", ", missing));
        }

        return foundRoles.stream()
                .sorted(Comparator.comparing(Role::getName))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private UserResponse toResponse(User user) {
        Organization organization = user.getOrganization();
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return new UserResponse(
                user.getId(),
                organization != null ? organization.getId() : null,
                organization != null ? organization.getName() : null,
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
