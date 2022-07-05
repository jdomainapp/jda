package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.events;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CustomChannels {
	
	@Output("outboundCourseChanges")
	MessageChannel output();
}

