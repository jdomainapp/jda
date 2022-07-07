package org.jda.example.coursemanmsa.coursemgnt;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jda.example.coursemanmsa.common.connections.UserContextInterceptor;
import org.jda.example.coursemanmsa.common.controller.ControllerRegistry;
import org.jda.example.coursemanmsa.common.controller.DefaultController;
import org.jda.example.coursemanmsa.common.controller.RedirectController;
import org.jda.example.coursemanmsa.common.controller.RedirectControllerRegistry;
import org.jda.example.coursemanmsa.common.controller.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@SpringBootApplication
@RefreshScope
@EnableEurekaClient
public class CourseMgntServiceApplication {
	private static final Logger logger = LoggerFactory.getLogger(CourseMgntServiceApplication.class);
	
	public static void main(String[] args) {
		final ServiceRegistry serviceRegistry = ServiceRegistry.getInstance();
		final ControllerRegistry controllerRegistry = ControllerRegistry.getInstance();
		final RedirectControllerRegistry redirectControllerRegistry = RedirectControllerRegistry.getInstance();
		ApplicationContext ctx = SpringApplication.run(CourseMgntServiceApplication.class, args);
		ctx.getBeansOfType(PagingAndSortingRepository.class).forEach((k, v) -> {serviceRegistry.put(k, v);
		System.out.println("CHECK SERVICES: "+ k +"_"+v);
			});
		ctx.getBeansOfType(DefaultController.class).forEach((k, v) -> {controllerRegistry.put(k, v);
		System.out.println("CHECK Controller: "+ k +"_"+v);
			});
		ctx.getBeansOfType(RedirectController.class).forEach((k, v) -> {redirectControllerRegistry.put(k, v);
		System.out.println("CHECK RedirectController: "+ k +"_"+v);
			});
		
	}
	
	@SuppressWarnings("unchecked")
	@LoadBalanced
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

	
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.US);
		return localeResolver;
	}
	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setUseCodeAsDefaultMessage(true);
		messageSource.setBasenames("messages");
		return messageSource;
	}

}
