package org.jda.example.coursemanmsa.service.events;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CustomChannels {
	
	@Output("outboundCourseChanges")
	MessageChannel courseChangeOutput();
	
	@Output("outboundTeacherChanges")
	MessageChannel teacherChangeOutput();
}

