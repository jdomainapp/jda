package org.jda.example.coursemanmsa.academic.service;

import java.util.List;
import org.jda.example.coursemanmsa.academic.config.ServiceConfig;
import org.jda.example.coursemanmsa.academic.model.Enrolment;
import org.jda.example.coursemanmsa.academic.service.client.EnrolmentRestTemplateClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class AcademicService {

	@Autowired
	MessageSource messages;

	@Autowired
	ServiceConfig config;
	
	@Autowired
	EnrolmentRestTemplateClient restClient;

	private static final Logger logger = LoggerFactory.getLogger(AcademicService.class);

	
	public List<Enrolment> getEntityList(int id){
		return restClient.getDataByREST(id);
	}

    public Enrolment updateEntity(int arg0, Enrolment arg1) {
        return restClient.updateDataByREST(arg0, arg1);
    }
    
}
