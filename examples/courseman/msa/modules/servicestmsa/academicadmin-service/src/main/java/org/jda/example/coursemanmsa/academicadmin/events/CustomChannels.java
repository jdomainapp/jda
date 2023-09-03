package org.jda.example.coursemanmsa.academicadmin.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {
	
	@Output("outboundCourseChanges")
	MessageChannel courseChangeOutput();
	
	@Output("outboundEnrolmentChanges")
	MessageChannel enrolmentChangeOutput();
	
	@Output("outboundStudentChanges")
	MessageChannel studentChangeOutput();
	
	@Output("outboundTeacherChanges")
	MessageChannel teacherChangeOutput();
	
    @Input("inboundAddressChanges")
    SubscribableChannel addressSubscribableChannel();
    
    @Input("inboundClassChanges")
    SubscribableChannel classSubscribableChannel();
}

