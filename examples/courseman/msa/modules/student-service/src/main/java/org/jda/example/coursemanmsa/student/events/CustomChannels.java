package org.jda.example.coursemanmsa.student.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {
	
    @Input("inboundAddressChanges")
    SubscribableChannel addressSubscribableChannel();
    
    @Input("inboundClassChanges")
    SubscribableChannel classSubscribableChannel();
    
}
