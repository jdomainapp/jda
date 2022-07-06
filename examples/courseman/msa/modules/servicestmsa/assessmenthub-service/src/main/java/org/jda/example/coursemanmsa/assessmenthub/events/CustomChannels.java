package org.jda.example.coursemanmsa.assessmenthub.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {
	
    @Input("inboundCoursemoduleChanges")
    SubscribableChannel coursemoduleSubscribableChannel();
    
    @Input("inboundEnrolmentChanges")
    SubscribableChannel enrolmentSubscribableChannel();
    
    @Input("inboundStudentChanges")
    SubscribableChannel studentSubscribableChannel();
    
    @Input("inboundTeacherChanges")
    SubscribableChannel teacherSubscribableChannel();
}