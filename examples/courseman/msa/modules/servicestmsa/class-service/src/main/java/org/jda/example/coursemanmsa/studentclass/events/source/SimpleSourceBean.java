package org.jda.example.coursemanmsa.studentclass.events.source;
import org.jda.example.coursemanmsa.studentclass.events.CustomChannels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import jda.modules.msacommon.events.model.ChangeModel;
import jda.modules.msacommon.messaging.kafka.IPublishSource;

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
       logger.debug("Sending Kafka message {} for Class Id: {}", change.getAction(), change.getId());
       
        source.classChangeOutput().send(MessageBuilder.withPayload(change).build());
    }
}
