package com.optimagrowth.license.service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class LicenseService {

    private final MessageSource messageSource;
    private final LicenseRepository licenseRepository;
    private final ServiceConfig config;

    private final OrganizationDiscoveryClient organizationDiscoveryClient;

    public License getLicense(final String licenseId, final String organizationId) {
        final License license = licenseRepository
                .findByOrganizationIdAndLicenseId(organizationId, licenseId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                String.format("Unable to find license with License id %s and Organization id %s",
                                        licenseId, organizationId)
                        )
                );

        final Organization organization = organizationDiscoveryClient.getOrganization(organizationId);
        if (organization != null) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }

        return license.withComment(config.getProperty());
    }

    public License createLicense(final License license) {
        license.setLicenseId(randomUUID().toString());
        return licenseRepository.save(license.withComment(config.getProperty()));
    }

}
