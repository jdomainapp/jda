package org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.events.source;

import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.events.CustomChannels;
import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.events.model.ChangeModel;
import org.jda.example.coursemanmsa.coursemgnt.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(CustomChannels.class)
public class EnrolmentSourceBean {
    private CustomChannels source;

    private static final Logger logger = LoggerFactory.getLogger(EnrolmentSourceBean.class);

    @Autowired
    public EnrolmentSourceBean(CustomChannels source){
        this.source = source;
    }

    public void publishChange(String action, int id){
       logger.debug("Sending Kafka message {} for Enrolment Id: {}", action, id);
        ChangeModel change =  new ChangeModel(
                ChangeModel.class.getTypeName(),
                action,
                id,
                UserContext.getCorrelationId());

        source.output().send(MessageBuilder.withPayload(change).build());
    }
}
