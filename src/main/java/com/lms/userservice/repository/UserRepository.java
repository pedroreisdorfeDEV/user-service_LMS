package com.lms.userservice.repository;

import com.lms.userservice.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"organization", "roles"})
    List<User> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"organization", "roles"})
    Optional<User> findById(UUID id);
}
