package org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {
	
    @Input("inboundCoursemoduleChanges")
    SubscribableChannel coursemoduleSubscribableChannel();
}
