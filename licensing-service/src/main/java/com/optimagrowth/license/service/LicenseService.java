package com.optimagrowth.license.service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class LicenseService {

    private final MessageSource messageSource;
    private final LicenseRepository licenseRepository;
    private final ServiceConfig config;

    private final OrganizationDiscoveryClient organizationDiscoveryClient;

    private void sleep() throws TimeoutException {
        try {
            Thread.sleep(3000);
            throw new TimeoutException("시간 초과");
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    private void randomlyRunLong() throws TimeoutException {
        final Random random = new Random();
        final int randomNum = random.nextInt(3) + 1;
        if (randomNum == 3) sleep();
    }

    @Retry(name = "retryLicenseService",
            fallbackMethod = "buildFallbackLicenseList")
    @CircuitBreaker(name = "licenseService")
    @RateLimiter(name = "licenseService")
    @Bulkhead(name = "bulkheadLicenseService")
    public List<License> getLicensesByOrganization(
            final String organizationId) throws TimeoutException {
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    private List<License> buildFallbackLicenseList(
            final String organizationId,
            Throwable t
    ) {
        final License license = new License();
        license.setLicenseId("0000-00-000");
        license.setOrganizationId(organizationId);
        license.setProductName(
                "Sorry no licensing information currently available"
        );
        return List.of(license);
    }

    @CircuitBreaker(name = "licenseService")
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
