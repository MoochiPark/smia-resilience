package com.optimagrowth.license.service;

import com.optimagrowth.license.model.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrganizationDiscoveryClient {

    private static final String SERVICE_ID = "organization-service";

    private final DiscoveryClient discoveryClient;

    public Organization getOrganization(String organizationId) {
        final RestTemplate restTemplate = new RestTemplate();
        final List<ServiceInstance> instances = discoveryClient.getInstances(SERVICE_ID);

        if (instances.isEmpty()) return null;
        String serviceUri = String.format("%s/v1/organization/%s", instances.get(0).getUri().toString(), organizationId);

        final ResponseEntity<Organization> exchange = restTemplate.exchange(serviceUri, HttpMethod.GET, null, Organization.class, organizationId);

        return exchange.getBody();
    }

}
