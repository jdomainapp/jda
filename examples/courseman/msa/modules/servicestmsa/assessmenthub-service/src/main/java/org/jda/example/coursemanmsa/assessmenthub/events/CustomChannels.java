package org.jda.example.coursemanmsa.assessmenthub.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {
	
    @Input("inboundCourseChanges")
    SubscribableChannel coursemoduleSubscribableChannel();
    
    @Input("inboundEnrolmentChanges")
    SubscribableChannel enrolmentSubscribableChannel();
    
    @Input("inboundStudentChanges")
    SubscribableChannel studentSubscribableChannel();
    
    @Input("inboundTeacherChanges")
    SubscribableChannel teacherSubscribableChannel();
    
    @Input("inboundAddressChanges")
    SubscribableChannel addressSubscribableChannel();
    
    @Input("inboundClassChanges")
    SubscribableChannel classSubscribableChannel();
}
