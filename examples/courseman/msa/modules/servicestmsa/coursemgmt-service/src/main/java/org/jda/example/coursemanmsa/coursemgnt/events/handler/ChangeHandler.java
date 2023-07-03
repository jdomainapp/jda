package org.jda.example.coursemanmsa.coursemgnt.events.handler;

import org.jda.example.coursemanmsa.coursemgnt.events.CustomChannels;
import org.jda.example.coursemanmsa.coursemgnt.modules.address.model.Address;
import org.jda.example.coursemanmsa.coursemgnt.modules.studentclass.model.StudentClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import jda.modules.msacommon.controller.ControllerRegistry;
import jda.modules.msacommon.controller.DefaultController;
import jda.modules.msacommon.events.model.ChangeModel;

@EnableBinding(CustomChannels.class)
public class ChangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(ChangeHandler.class);
	
	@StreamListener("inboundAddressChanges")
	public void processAddressChanges(ChangeModel<Integer> model) {
		logger.debug("Received Kafka message {} for "+ model.getType()+" Id: {}", model.getAction(), model.getId());
	    DefaultController<Address, Integer> controller = ControllerRegistry.getInstance().get(Address.class);
		controller.executeReceivedEvent(model.getAction(), model.getId(), model.getPath());
	}
	
	@StreamListener("inboundClassChanges")
	public void processClassChanges(ChangeModel<Integer> model) {
		logger.debug("Received Kafka message {} for "+ model.getType()+" Id: {}", model.getAction(), model.getId());
	    DefaultController<StudentClass, Integer> controller = ControllerRegistry.getInstance().get(StudentClass.class);
		controller.executeReceivedEvent(model.getAction(), model.getId(), model.getPath());
	}

}
