package org.jda.example.coursemanmsa.studentclass.events;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CustomChannels {
	
	@Output("outboundClassChanges")
	MessageChannel classChangeOutput();
}

