package org.jda.example.coursemankafka.services.coursemodule2;

import java.util.Random;

import org.jda.example.courseman.modules.coursemodule.model.CompulsoryModule;
import org.jda.example.courseman.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemankafka.services.studentregist2.StudentRegist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class CourseModuleQueryHandler {

  public static final Logger log = LoggerFactory.getLogger(CourseModuleQueryHandler.class);
  
//  private static final Long MAX_AMOUNT = 10000L;
  private StudentRegistInputStream stdRegistInStream;

  @Autowired
  public CourseModuleQueryHandler(StudentRegistInputStream stdRegistInStream) {
    this.stdRegistInStream = stdRegistInStream;
  }

  @StreamListener(StudentRegistInputStream.APPLICATIONS_IN)
  public void listAvailableCourseModules(StudentRegist regist) {
    log.info("Received: {}", regist);

    // todo: process regist
    Random rand = new Random();
    CourseModule obj = new CompulsoryModule(regist.getCourseModule(), rand.nextInt(10), rand.nextInt(5));
    stdRegistInStream.returned().send(message(obj));
    
    log.info("Returned: {}", obj);

  }

  private static final <T> Message<T> message(T val) {
    return MessageBuilder.withPayload(val).build();
  }
}
