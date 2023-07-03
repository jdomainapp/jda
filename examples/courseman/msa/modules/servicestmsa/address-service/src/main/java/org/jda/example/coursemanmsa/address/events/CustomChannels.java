package org.jda.example.coursemanmsa.address.events;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CustomChannels {
	
	@Output("outboundAddressChanges")
	MessageChannel addressChangeOutput();
}

