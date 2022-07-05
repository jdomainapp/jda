package org.jda.example.coursemanmsa.coursemgnt.modules.teacher.events;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CustomChannels {
	
	@Output("outboundTeacherChanges")
	MessageChannel output();
}

