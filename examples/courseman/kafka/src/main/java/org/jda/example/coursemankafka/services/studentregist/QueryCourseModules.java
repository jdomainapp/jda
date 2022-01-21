package org.jda.example.coursemankafka.services.studentregist;

import org.jda.example.courseman.modules.coursemodule.model.CourseModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
public class QueryCourseModules {

  public static final Logger log = LoggerFactory.getLogger(QueryCourseModules.class);
  
//  private static final Long MAX_AMOUNT = 10000L;
  private CourseModuleInputStream modulesInStream;

  @Autowired
  public QueryCourseModules(CourseModuleInputStream modulesInStream) {
    this.modulesInStream = modulesInStream;
  }

  @StreamListener(CourseModuleInputStream.APPLICATIONS_IN)
  public void listAvailableCourseModules(CourseModule module) {
    log.info("Received: {}", module);

    // todo: process module

  }

//  private static final <T> Message<T> message(T val) {
//    return MessageBuilder.withPayload(val).build();
//  }
}
