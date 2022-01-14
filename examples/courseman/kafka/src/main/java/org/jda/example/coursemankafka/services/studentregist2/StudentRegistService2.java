package org.jda.example.coursemankafka.services.studentregist2;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.function.Supplier;

import org.jda.example.courseman.modules.coursemodule.model.CompulsoryModule;
import org.jda.example.courseman.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemankafka.services.studentregist2.StudentRegist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBinding(CourseModuleInputStream2.class)
public class StudentRegistService2 {

  public static final Logger log = LoggerFactory.getLogger(StudentRegistService2.class);

  public static void main(String[] args) {
    Class<?> appCls = StudentRegistService2.class;
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
  
  @Bean
  public Supplier<StudentRegist> queryData(){

    Supplier<StudentRegist> supplier = () -> {
      Random rand = new Random();
      StudentRegist obj = new StudentRegist("Student:"+rand.nextInt(10000), "Module:"+rand.nextInt(10000));
      log.info("Query for: {}({} {})", StudentRegist.class.getSimpleName(), obj.getStudent(), obj.getCourseModule());
      return obj;
    };

    return supplier;
  }
}
