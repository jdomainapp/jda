package org.jda.example.coursemanmsa.service;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@SpringBootApplication
@RefreshScope
@EnableEurekaClient
public class ServiceApplication {
	private static final Logger logger = LoggerFactory.getLogger(ServiceApplication.class);

	@Value("${deployedAtParent}")
	private String deployedAtParent;

	private boolean isRegisterToParent = false;

	public static void main(String[] args) {
		final ServiceRegistry serviceRegistry = ServiceRegistry.getInstance();
		final ControllerRegistry controllerRegistry = ControllerRegistry.getInstance();
		final RedirectControllerRegistry redirectControllerRegistry = RedirectControllerRegistry.getInstance();
		ApplicationContext ctx = SpringApplication.run(ServiceApplication.class, args);
		ctx.getBeansOfType(PagingAndSortingRepository.class).forEach((k, v) -> {
			serviceRegistry.put(k, v);
			System.out.println("CHECK SERVICES: " + k + "_" + v);
		});
		ctx.getBeansOfType(DefaultController.class).forEach((k, v) -> {
			controllerRegistry.put(k, v);
			System.out.println("CHECK Controller: " + k + "_" + v);
		});
		ctx.getBeansOfType(RedirectController.class).forEach((k, v) -> {
			redirectControllerRegistry.put(k, v);
			System.out.println("CHECK RedirectController: " + k + "_" + v);
		});

		
	}

	/**
	 * After finishing starting, the (module) serivce registers to parent by itself
	 * 
	 * @return
	 */
	@EventListener(WebServerInitializedEvent.class)
	public void registerToParent() {
		if (!isRegisterToParent) {
			try {
				ResponseEntity<String> restExchange = getRestTemplate().exchange(deployedAtParent, HttpMethod.POST,
						null, String.class);
				if (restExchange.getStatusCode() == HttpStatus.OK) {
					isRegisterToParent = true;
					logger.info("Register child service to parent successfully!!!");
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}

	@SuppressWarnings("unchecked")
	@LoadBalanced
	@Bean
	public RestTemplate getRestTemplate() {
		RestTemplate template = new RestTemplate();
		List interceptors = template.getInterceptors();
		if (interceptors == null) {
			template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
		} else {
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
