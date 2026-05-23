package com.lms.userservice.repository;

import com.lms.userservice.entity.Role;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    List<Role> findByNameIn(Collection<String> names);

    Optional<Role> findByName(String name);
}
