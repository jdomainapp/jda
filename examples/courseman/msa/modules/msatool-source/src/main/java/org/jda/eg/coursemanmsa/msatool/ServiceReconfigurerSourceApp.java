package org.jda.eg.coursemanmsa.msatool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jda.modules.msacommon.msatool.ServiceReconfigurerApp;

@SpringBootApplication
public class ServiceReconfigurerSourceApp extends ServiceReconfigurerApp{
	public static void main(String[] args) {
		SpringApplication.run(ServiceReconfigurerApp.class, args);
	}
}
