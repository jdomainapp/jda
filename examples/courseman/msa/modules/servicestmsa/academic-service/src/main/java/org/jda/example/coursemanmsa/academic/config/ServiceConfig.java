package org.jda.example.coursemanmsa.academic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "academic")
@Getter @Setter
public class ServiceConfig{

  private String property;
    
}