package org.jda.example.coursemankafka.services.studentregist;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding(CourseModuleInputStream.class)
public class StudentRegistService {

  public static final Logger log = LoggerFactory.getLogger(StudentRegistService.class);

  public static void main(String[] args) {
    Class<?> appCls = StudentRegistService.class;
    SpringApplication app = new SpringApplication(appCls);
    Properties props = new Properties();
    try {
      props.load(appCls.getClassLoader().getResourceAsStream("consumer.properties"));
      app.setDefaultProperties(props);
      app.run(args);
      log.info("The "+appCls.getSimpleName()+" has started...");
    } catch (IOException e) { // props.load
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
