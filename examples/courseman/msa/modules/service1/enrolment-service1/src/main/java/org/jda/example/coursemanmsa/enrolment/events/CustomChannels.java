package org.jda.example.coursemanmsa.enrolment.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {
	
    @Input("inboundCourseChanges")
    SubscribableChannel courseSubscribableChannel();
    
    @Input("inboundStudentChanges")
    SubscribableChannel studentSubscribableChannel();
    
}
