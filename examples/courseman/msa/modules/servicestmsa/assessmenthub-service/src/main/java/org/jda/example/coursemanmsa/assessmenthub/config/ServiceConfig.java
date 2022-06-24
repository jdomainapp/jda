package org.jda.example.coursemanmsa.assessmenthub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "student")
@Getter @Setter
public class ServiceConfig{

  private String property;
    
}