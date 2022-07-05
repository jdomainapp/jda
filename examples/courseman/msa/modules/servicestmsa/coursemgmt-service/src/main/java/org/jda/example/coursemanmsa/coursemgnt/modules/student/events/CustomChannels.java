package org.jda.example.coursemanmsa.coursemgnt.modules.student.events;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CustomChannels {
	
	@Output("outboundStudentChanges")
	MessageChannel output();
}

