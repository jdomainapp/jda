package org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.events;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CustomChannels {
	
	@Output("outboundEnrolmentChanges")
	MessageChannel output();
}

