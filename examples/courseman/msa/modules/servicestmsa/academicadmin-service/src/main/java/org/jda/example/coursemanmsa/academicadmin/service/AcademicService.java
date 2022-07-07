package org.jda.example.coursemanmsa.academicadmin.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.academicadmin.config.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AcademicService {

	@Autowired
	MessageSource messages;

	@Autowired
	ServiceConfig config;
	

	private static final Logger logger = LoggerFactory.getLogger(AcademicService.class);
    
}
