package org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {
	
    @Input("inboundEnrolmentChanges")
    SubscribableChannel enrolmentSubscribableChannel();
    
}
