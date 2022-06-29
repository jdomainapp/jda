package org.jda.example.coursemanmsa.academicadmin.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {
	
    @Input("inboundEnrolmentChanges")
    SubscribableChannel enrolmentSubscribableChannel();
    @Input("inboundCourseChanges")
    SubscribableChannel courseSubscribableChannel();
    
    @Input("inboundStudentChanges")
    SubscribableChannel studentSubscribableChannel();
}
