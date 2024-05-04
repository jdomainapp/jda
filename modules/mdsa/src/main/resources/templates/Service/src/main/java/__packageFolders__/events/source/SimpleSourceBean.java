package __outputPackage__.events.source;

import __outputPackage__.events.CustomChannels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import jda.modules.msacommon.events.model.ChangeModel;
import jda.modules.msacommon.messaging.kafka.IPublishSource;

__foreach(entityModule : entityModules)
import __entityModule.model.outputPackage__.__entityModule.model.Name__;
__endforeach

@Component
@EnableBinding(CustomChannels.class)
public class SimpleSourceBean implements IPublishSource{
    private CustomChannels source;

    private static final Logger logger = LoggerFactory.getLogger(SimpleSourceBean.class);

    @Autowired
    public SimpleSourceBean(CustomChannels source){
        this.source = source;
    }

    public void publishChange(ChangeModel change){
        logger.debug("Sending Kafka message {} for "+ change.getType()+" Id: {}", change.getAction(), change.getId());

        __foreach (entityModule : entityModules)
        if(change.getType().equals(__entityModule.model.Name__.class.getTypeName())) {
            source.__entityModule.model.name__ChangeOutput().send(MessageBuilder.withPayload(change).build());
        }
        __endforeach

    }
    
}
