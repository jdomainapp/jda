package org.jda.eg.coursemanmsa.reconfigurer;

import jda.modules.msacommon.connections.UserContextInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jda.modules.msacommon.msatool.ServiceReconfigurerApp;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
@RefreshScope
@EnableEurekaClient
public class MyServiceReconfigurerApp {
	public static void main(String[] args) {
		SpringApplication.run(MyServiceReconfigurerApp.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate(){
		RestTemplate template = new RestTemplate();
		List interceptors = template.getInterceptors();
		if (interceptors==null){
			template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
		}
		else{
			interceptors.add(new UserContextInterceptor());
			template.setInterceptors(interceptors);
		}
		return template;
	}
}
