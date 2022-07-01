package org.jda.example.coursemanmsa.coursemgnt.modules.teacher.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {
	
    @Input("inboundTeacherChanges")
    SubscribableChannel teacherSubscribableChannel();
    
}
