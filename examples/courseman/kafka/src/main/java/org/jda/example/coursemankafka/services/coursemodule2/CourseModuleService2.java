package org.jda.example.coursemankafka.services.coursemodule2;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.function.Supplier;

import org.jda.example.courseman.modules.coursemodule.model.CompulsoryModule;
import org.jda.example.courseman.modules.coursemodule.model.CourseModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBinding(StudentRegistInputStream.class)
public class CourseModuleService2 {

  private static final Logger log = LoggerFactory.getLogger(CourseModuleService2.class);

  public static void main(String[] args) {
    Class<?> appCls = CourseModuleService2.class;
    SpringApplication app = new SpringApplication(appCls);
    Properties props = new Properties();
    try {
      props.load(appCls.getClassLoader().getResourceAsStream("producer.properties"));
      app.setDefaultProperties(props);
      app.run(args);
      log.info("The "+appCls.getSimpleName()+" has started...");
    } catch (IOException e) { // props.load
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

//  @Bean
//  public Supplier<CourseModule> supplyData(){
//
//    Supplier<CourseModule> supplier = () -> {
//      Random rand = new Random();
//      CourseModule obj = new CompulsoryModule("Module:"+rand.nextInt(10000), rand.nextInt(10), rand.nextInt(5));
//      log.info("{} {} for {} for {}", obj.getId(), obj.getCode(), obj.getName(), obj.getCredits());
//      return obj;
//    };
//
//    return supplier;
//  }
}
