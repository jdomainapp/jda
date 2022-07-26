package org.jda.example.coursemanmsa.financehub.service;

import org.jda.example.coursemanmsa.financehub.config.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class FinanceService {

	@Autowired
	MessageSource messages;

	@Autowired
	ServiceConfig config;
	

	private static final Logger logger = LoggerFactory.getLogger(FinanceService.class);
    
}
