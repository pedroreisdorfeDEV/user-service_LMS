package com.lms.userservice.repository;

import com.lms.userservice.entity.Organization;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    List<Organization> findAllByOrderByNameAsc();
}
