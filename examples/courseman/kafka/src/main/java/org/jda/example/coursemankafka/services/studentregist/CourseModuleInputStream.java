package org.jda.example.coursemankafka.services.studentregist;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

@Component
public interface CourseModuleInputStream {

  String APPLICATIONS_IN = "output";
//  String APPROVED_OUT = "approved";
//  String DECLINED_OUT = "declined";

  @Input(APPLICATIONS_IN)
  SubscribableChannel dataInStream();

//  @Output(APPROVED_OUT)
//  MessageChannel approved();
//
//  @Output(DECLINED_OUT)
//  MessageChannel declined();

}
