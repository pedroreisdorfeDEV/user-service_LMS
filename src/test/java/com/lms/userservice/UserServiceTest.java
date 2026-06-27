package com.lms.userservice;

import com.lms.userservice.dto.UserCreateRequest;
import com.lms.userservice.dto.UserResponse;
import com.lms.userservice.entity.Organization;
import com.lms.userservice.entity.Role;
import com.lms.userservice.entity.User;
import com.lms.userservice.exception.BusinessException;
import com.lms.userservice.repository.OrganizationRepository;
import com.lms.userservice.repository.RoleRepository;
import com.lms.userservice.repository.UserRepository;
import com.lms.userservice.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserSuccessfully() {
        UUID organizationId = UUID.randomUUID();
        Organization organization = organization(organizationId);
        Role adminRole = role("ADMIN");

        UserCreateRequest request = new UserCreateRequest(
                organizationId,
                " Pedro Reisdorfer ",
                " PEDRO@EMAIL.COM ",
                "12345678",
                "51999999999",
                Set.of("ADMIN"));

        when(userRepository.existsByEmailIgnoreCase("pedro@email.com"))
                .thenReturn(false);
        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));
        when(roleRepository.findByNameIn(Set.of("ADMIN")))
                .thenReturn(List.of(adminRole));
        when(passwordEncoder.encode("12345678"))
                .thenReturn("senha-criptografada");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.create(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("Pedro Reisdorfer", savedUser.getFullName());
        assertEquals("pedro@email.com", savedUser.getEmail());
        assertEquals("senha-criptografada", savedUser.getPasswordHash());
        assertEquals("51999999999", savedUser.getPhone());
        assertEquals("ACTIVE", savedUser.getStatus());
        assertEquals(organization, savedUser.getOrganization());
        assertEquals(Set.of(adminRole), savedUser.getRoles());

        assertEquals("Pedro Reisdorfer", response.fullName());
        assertEquals("pedro@email.com", response.email());
        assertEquals("Org Teste", response.organizationName());
        assertEquals(Set.of("ADMIN"), response.roles());
    }

    @Test
    void shouldThrowBusinessExceptionWhenEmailAlreadyExists() {
        UserCreateRequest request = new UserCreateRequest(
                UUID.randomUUID(),
                "Pedro Reisdorfer",
                "pedro@email.com",
                "12345678",
                "51999999999",
                Set.of("ADMIN"));

        when(userRepository.existsByEmailIgnoreCase("pedro@email.com"))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.create(request));

        assertEquals("A user with this email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldNormalizeEmailBeforeCheckingAndSaving() {
        UUID organizationId = UUID.randomUUID();
        Organization organization = organization(organizationId);
        Role adminRole = role("ADMIN");

        UserCreateRequest request = new UserCreateRequest(
                organizationId,
                "Pedro",
                " TESTE@EMAIL.COM ",
                "12345678",
                "51999999999",
                Set.of("ADMIN"));

        when(userRepository.existsByEmailIgnoreCase("teste@email.com"))
                .thenReturn(false);
        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));
        when(roleRepository.findByNameIn(Set.of("ADMIN")))
                .thenReturn(List.of(adminRole));
        when(passwordEncoder.encode("12345678"))
                .thenReturn("hash");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.create(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertEquals("teste@email.com", userCaptor.getValue().getEmail());
    }

    @Test
    void shouldEncodePasswordBeforeSaving() {
        UUID organizationId = UUID.randomUUID();
        Organization organization = organization(organizationId);
        Role adminRole = role("ADMIN");

        UserCreateRequest request = new UserCreateRequest(
                organizationId,
                "Pedro",
                "pedro@email.com",
                "senha1234",
                "51999999999",
                Set.of("ADMIN"));



        when(userRepository.existsByEmailIgnoreCase("pedro@email.com"))
                .thenReturn(false);
        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));
        when(roleRepository.findByNameIn(Set.of("ADMIN")))
                .thenReturn(List.of(adminRole));
        when(passwordEncoder.encode("senha1234"))
                .thenReturn("senha-hash");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.create(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertNotEquals("senha1234", savedUser.getPasswordHash());
        assertEquals("senha-hash", savedUser.getPasswordHash());
    }

    @Test
    void shouldUseDefaultStudentRoleWhenRequestDoesNotProvideRoles() {
        UUID organizationId = UUID.randomUUID();
        Organization organization = organization(organizationId);
        Role studentRole = role("STUDENT");

        UserCreateRequest request = new UserCreateRequest(
                organizationId,
                "Pedro",
                "pedro@email.com",
                "senha1234",
                "51999999999",
                null);

        when(userRepository.existsByEmailIgnoreCase("pedro@email.com"))
                .thenReturn(false);
        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));
        when(roleRepository.findByNameIn(Set.of("STUDENT")))
                .thenReturn(List.of(studentRole));
        when(passwordEncoder.encode("senha1234"))
                .thenReturn("senha-hash");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.create(request);

        assertEquals(Set.of("STUDENT"), response.roles());
    }

    @Test
    void shouldThrowBusinessExceptionWhenAnyRequestedRoleDoesNotExist() {
        UUID organizationId = UUID.randomUUID();
        Organization organization = organization(organizationId);
        Role adminRole = role("ADMIN");

        UserCreateRequest request = new UserCreateRequest(
                organizationId,
                "Pedro",
                "pedro@email.com",
                "senha1234",
                "51999999999",
                Set.of("ADMIN", "MANAGER"));

        when(userRepository.existsByEmailIgnoreCase("pedro@email.com"))
                .thenReturn(false);
        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(organization));
        when(roleRepository.findByNameIn(Set.of("ADMIN", "MANAGER")))
                .thenReturn(List.of(adminRole));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.create(request));

        assertTrue(exception.getMessage().contains("Roles not found"));
        assertTrue(exception.getMessage().contains("MANAGER"));
        verify(userRepository, never()).save(any(User.class));
    }

    private Organization organization(UUID id) {
        return Organization.builder()
                .id(id)
                .name("Org Teste")
                .document("03451346052")
                .email("contato@org.com")
                .phone("519999645333")
                .build();
    }

    private Role role(String name) {
        return Role.builder()
                .id(UUID.randomUUID())
                .name(name)
                .build();
    }
}
