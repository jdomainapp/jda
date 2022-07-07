package org.jda.example.coursemanmsa.coursemgnt.events;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CustomChannels {
	
	@Output("outboundCourseChanges")
	MessageChannel courseChangeOutput();
	
	@Output("outboundEnrolmentChanges")
	MessageChannel enrolmentChangeOutput();
	
	@Output("outboundStudentChanges")
	MessageChannel studentChangeOutput();
	
	@Output("outboundTeacherChanges")
	MessageChannel teacherChangeOutput();
}

