package com.example.profilemanagement.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/config")
public class ConfigController {
	@Value("${spring.application.name}")
    private String serviceName;
    @Value("${server.port}")
    private String servicePort;
    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @GetMapping
    public String config() {
        return "serviceName: " + serviceName + "<br/>servicePort: " + servicePort + "<br/>databaseUrl: " + databaseUrl;
    }
}
