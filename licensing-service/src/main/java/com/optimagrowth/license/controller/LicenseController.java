package com.optimagrowth.license.controller;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.service.LicenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/organization/{organizationId}/license")
public class LicenseController {

    private final LicenseService licenseService;

    public LicenseController(final LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @GetMapping
    public ResponseEntity<List<License>> getLicensesByOrganization(
            @PathVariable("organizationId") final String organizationId
    ) throws TimeoutException {
        return ResponseEntity
                .ok(
                        this.licenseService.getLicensesByOrganization(organizationId)
                );
    }

    @GetMapping("/{licenseId}")
    public ResponseEntity<License> getLicense(
            @PathVariable("organizationId") final String organizationId,
            @PathVariable("licenseId") final String licenseId
    ) {
        final License license = this.licenseService.getLicense(licenseId, organizationId);

        return ResponseEntity.ok(license);
    }

    @PostMapping
    public ResponseEntity<License> createLicense(
            @PathVariable("organizationId") final String organizationId,
            @RequestBody final License license
    ) {
        return ResponseEntity
                .status(CREATED)
                .body(this.licenseService.createLicense(license));
    }

}
