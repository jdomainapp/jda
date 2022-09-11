package org.jda.example.coursemanmsa.coursemgnt.events.source;

import org.jda.example.coursemanmsa.common.events.model.ChangeModel;
import org.jda.example.coursemanmsa.coursemgnt.events.CustomChannels;
import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanmsa.coursemgnt.modules.teacher.model.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@EnableBinding(CustomChannels.class)
public class CourseModuleMgntSourceBean {
	
    private CustomChannels source;

    private static final Logger logger = LoggerFactory.getLogger(CourseModuleMgntSourceBean.class);

    @Autowired
    public CourseModuleMgntSourceBean(CustomChannels source){
        this.source = source;
    }

    public void publishChange(ChangeModel change){
       logger.debug("Sending Kafka message {} for "+ change.getType()+" Id: {}", change.getAction(), change.getId());
       if(change.getType().equals(CourseModule.class.getTypeName())) {
    	   source.courseChangeOutput().send(MessageBuilder.withPayload(change).build());   
       }else if(change.getType().equals(Teacher.class.getTypeName())) {
    	   source.teacherChangeOutput().send(MessageBuilder.withPayload(change).build()); 
       }
    }
}
