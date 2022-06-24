package org.jda.example.coursemanmsa.assessmenthub.modules.teacher.events.source;

import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.events.model.ChangeModel;
import org.jda.example.coursemanmsa.assessmenthub.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class SimpleSourceBean {
    private Source source;

    private static final Logger logger = LoggerFactory.getLogger(SimpleSourceBean.class);

    @Autowired
    public SimpleSourceBean(Source source){
        this.source = source;
    }

    public void publishChange(String action, String id){
       logger.debug("Sending Kafka message {} for Enrolment Id: {}", action, id);
        ChangeModel change =  new ChangeModel(
                ChangeModel.class.getTypeName(),
                action,
                id,
                UserContext.getCorrelationId());

        source.output().send(MessageBuilder.withPayload(change).build());
    }
}
