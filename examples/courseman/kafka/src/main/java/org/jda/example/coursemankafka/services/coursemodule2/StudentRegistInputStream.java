package org.jda.example.coursemankafka.services.coursemodule2;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

@Component
public interface StudentRegistInputStream {

  String APPLICATIONS_IN = "registOutput";
  String RETURNED_OUT = "returned";
//  String DECLINED_OUT = "declined";

  @Input(APPLICATIONS_IN)
  SubscribableChannel dataInStream();

  @Output(RETURNED_OUT)
  MessageChannel returned();
//
//  @Output(DECLINED_OUT)
//  MessageChannel declined();

}
