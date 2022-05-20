package org.jda.example.coursemanmsa.student.events.handler;

import java.util.List;

import org.jda.example.coursemanmsa.student.events.CustomChannels;
import org.jda.example.coursemanmsa.student.events.model.SinkChangeModel;
import org.jda.example.coursemanmsa.student.model.Address;
import org.jda.example.coursemanmsa.student.model.Student;
import org.jda.example.coursemanmsa.student.model.StudentClass;
import org.jda.example.coursemanmsa.student.service.AddressService;
import org.jda.example.coursemanmsa.student.service.StudentClassService;
import org.jda.example.coursemanmsa.student.service.StudentService;
import org.jda.example.coursemanmsa.student.service.client.AddressRestTemplateClient;
import org.jda.example.coursemanmsa.student.service.client.ClassRestTemplateClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(CustomChannels.class)
public class ChangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(ChangeHandler.class);

	@Autowired
	private AddressService addressService;

	@Autowired
	AddressRestTemplateClient addressRestClient;
	
	@Autowired
	private StudentClassService classService;
	
	@Autowired
	private StudentService studentService;

	@Autowired
	ClassRestTemplateClient classRestClient;

	public enum ActionEnum {
		GET, CREATED, UPDATED, DELETED
	}

	@StreamListener("inboundAddressChanges")
	public void processAddressChanges(SinkChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			logger.debug("Received a SAVE event from the address service for address id {}", model.getId());
			Address createdObj = addressRestClient.getDataByREST(model.getId());
			addressService.createEntity(createdObj);
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			logger.debug("Received a UPDATE event from the address service for address id {}", model.getId());
			Address updatedObj = addressRestClient.getDataByREST(model.getId());
			addressService.updateEntity(model.getId(), updatedObj);
			List<Student> changedObjs = studentService.findByAddressId(model.getId());
			for(Student c : changedObjs) {
				studentService.updateTopic(c);
			}
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			logger.debug("Received a DELETE event from the address service for address id {}", model.getId());
			addressService.deleteEntityById(model.getId());
			List<Student> changedObjs = studentService.findByAddressId(model.getId());
			for(Student c : changedObjs) {
				c.setAddressId(0);
				studentService.updateEntity(c.getId(), c);
			}
		} else {
			logger.error("Received an UNKNOWN event from the address service of type {}", model.getType());
		}
		
	}
	
	@StreamListener("inboundClassChanges")
	public void processClassChanges(SinkChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			logger.debug("Received a SAVE event from the class service for class id {}", model.getId());
			StudentClass createdObj = classRestClient.getDataByREST(model.getId());
			classService.createEntity(createdObj);
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			logger.debug("Received a UPDATE event from the class service for class id {}", model.getId());
			StudentClass updatedObj = classRestClient.getDataByREST(model.getId());
			classService.updateEntity(model.getId(), updatedObj);
			List<Student> changedObjs = studentService.findByStudentclassId(model.getId());
			for(Student c : changedObjs) {
				studentService.updateTopic(c);
			}
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			logger.debug("Received a DELETE event from the class service for class id {}", model.getId());
			classService.deleteEntityById(model.getId());
			List<Student> changedObjs = studentService.findByStudentclassId(model.getId());
			for(Student c : changedObjs) {
				c.setStudentclassId(0);
				studentService.updateEntity(c.getId(), c);
			}
		} else {
			logger.error("Received an UNKNOWN event from the class service of type {}", model.getType());
		}
	}

}
