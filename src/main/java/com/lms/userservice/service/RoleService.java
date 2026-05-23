package com.lms.userservice.service;

import com.lms.userservice.dto.RoleResponse;
import com.lms.userservice.repository.RoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleResponse(role.getId(), role.getName(), role.getDescription()))
                .toList();
    }
}
