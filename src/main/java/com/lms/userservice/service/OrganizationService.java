package com.lms.userservice.service;

import com.lms.userservice.dto.OrganizationResponse;
import com.lms.userservice.repository.OrganizationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Transactional(readOnly = true)
    public List<OrganizationResponse> findAll() {
        List<OrganizationResponse> listaOrganizacoes = organizationRepository.findAllByOrderByNameAsc().stream()
                .map(organization -> new OrganizationResponse(
                        organization.getId(),
                        organization.getName(),
                        organization.getDocument(),
                        organization.getEmail(),
                        organization.getPhone(),
                        organization.getActive()))
                .toList();

        return listaOrganizacoes;

    }

}
