package com.example.client.controller;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;

@RestController
public class ClientController {

    private final LoadBalancerClient loadBalancerClient;

    private final RestTemplate restTemplate;

    public ClientController(RestTemplate restTemplate, LoadBalancerClient loadBalancerClient) {
        this.restTemplate = restTemplate;
        this.loadBalancerClient = loadBalancerClient;
    }

    @RequestMapping("/get")
    public String get(Principal principal){
        ServiceInstance serviceInstance = loadBalancerClient.choose("foo_resource");
        return restTemplate.postForEntity(String.format("http://%s:%s/get", serviceInstance.getHost(), serviceInstance.getPort()), withToken(principal), String.class).getBody();
    }

    private HttpEntity<MultiValueMap<String, String>> withToken(Principal principal){
        String token = ((OAuth2AuthenticationDetails) ((OAuth2Authentication) principal).getDetails()).getTokenValue();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return new HttpEntity<>(null, headers);
    }
}
