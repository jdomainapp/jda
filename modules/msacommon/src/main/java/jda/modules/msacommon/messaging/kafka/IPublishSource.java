package jda.modules.msacommon.messaging.kafka;

import jda.modules.msacommon.events.model.ChangeModel;

public interface IPublishSource {
	public void publishChange(ChangeModel change);
}
