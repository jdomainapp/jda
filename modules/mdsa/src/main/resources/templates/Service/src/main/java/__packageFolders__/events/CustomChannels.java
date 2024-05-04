package __outputPackage__.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {
	__foreach (entityModule : entityModules)
	@Output("outbound__entityModule.model.Name__Changes")
	MessageChannel __entityModule.model.name__ChangeOutput();

	@Input("inbound__entityModule.model.Name__Changes")
	SubscribableChannel __entityModule.model.name__SubscribableChannel();
	__endforeach
}