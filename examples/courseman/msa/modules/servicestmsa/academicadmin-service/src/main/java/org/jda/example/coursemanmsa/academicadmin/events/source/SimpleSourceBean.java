package org.jda.example.coursemanmsa.academicadmin.events.source;

import jda.modules.msacommon.events.model.ChangeModel;
import jda.modules.msacommon.messaging.kafka.IPublishSource;
import org.jda.example.coursemanmsa.academicadmin.events.CustomChannels;
import org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.coursemodulemgmt.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.coursemodulemgmt.modules.teacher.model.Teacher;
import org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.studentenrolment.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.studentenrolment.modules.student.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@EnableBinding(CustomChannels.class)
public class SimpleSourceBean implements IPublishSource{
	
    private final CustomChannels source;

    private static final Logger logger = LoggerFactory.getLogger(SimpleSourceBean.class);

    @Autowired
    public SimpleSourceBean(CustomChannels source){
        this.source = source;
    }

    public void publishChange(ChangeModel change){
       logger.debug("Sending Kafka message {} for "+ change.getType()+" Id: {}", change.getAction(), change.getId());
       if(change.getType().equals(CourseModule.class.getTypeName())) {
    	   source.courseChangeOutput().send(MessageBuilder.withPayload(change).build());   
       }else if(change.getType().equals(Enrolment.class.getTypeName())) {
    	   source.enrolmentChangeOutput().send(MessageBuilder.withPayload(change).build());  
       }else if(change.getType().equals(Student.class.getTypeName())) {
    	   source.studentChangeOutput().send(MessageBuilder.withPayload(change).build());  
       }else if(change.getType().equals(Teacher.class.getTypeName())) {
    	   source.teacherChangeOutput().send(MessageBuilder.withPayload(change).build()); 
       }
    }
}
