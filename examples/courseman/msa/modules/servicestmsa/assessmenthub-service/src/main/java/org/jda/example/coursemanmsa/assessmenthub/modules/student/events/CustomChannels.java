package org.jda.example.coursemanmsa.assessmenthub.modules.student.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {
    
    @Input("inboundStudentChanges")
    SubscribableChannel studentSubscribableChannel();
    
}
