package com.optimagrowth.organization.service;

import com.optimagrowth.organization.model.Organization;
import com.optimagrowth.organization.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {

    private final OrganizationRepository repository;

    private OrganizationService(final OrganizationRepository repository) {
        this.repository = repository;
    }

    public Organization findById(final String organizationId) {
        final Optional<Organization> organization = repository.findById(organizationId);
        return organization.orElse(null);
    }

    public Organization create(final Organization organization) {
        organization.setId(UUID.randomUUID().toString());
        return repository.save(organization);
    }

    public void update(final Organization organization) {
        repository.save(organization);
    }

    public void delete(final String organizationId) {
        repository.deleteById(organizationId);
    }

}
