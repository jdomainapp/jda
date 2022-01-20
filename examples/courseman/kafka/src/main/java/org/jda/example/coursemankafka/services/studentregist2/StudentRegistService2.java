package org.jda.example.coursemankafka.services.studentregist2;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jda.example.courseman.modules.coursemodule.model.CompulsoryModule;
import org.jda.example.courseman.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemankafka.services.utils.CourseManStreamConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@SpringBootApplication
public class StudentRegistService2 implements CommandLineRunner {

  public static final Logger log = LoggerFactory.getLogger(StudentRegistService2.class);

  public static final String INPUT_TOPIC = CourseManStreamConfig.getStreamByService("coursemodules");

  private static final Object CONSUMER_GROUP_ID = "Consumer-1";
  
//  public static final String OUTPUT_TOPIC = "streams-wordcount-output";

  /**
   * Invoked automatically after SpringBoot application has been started
   * (URL: https://stackoverflow.com/a/63229828) 
   * 
   * @effects 
   *  initialise kafka-related resources 
   */
  @Override  // CommandLineRunner
  public void run(String... args) throws Exception {
    log.info("SpringBoot.CommandLineRunner.run()...");

    startStreaming(args);
  }
  
  private void startStreaming(final String[] args) throws IOException {
    log.info("startStreaming()...");

    final Properties props = CourseManStreamConfig.getStreamsConfig(args);

    // create the consumer
    props.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
    props.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.putIfAbsent(JsonDeserializer.VALUE_DEFAULT_TYPE, CourseModule.class.getName());
    
    KafkaConsumer<String,CourseModule> consumer = new KafkaConsumer<>(props);
    
    consumer.subscribe(Arrays.asList(INPUT_TOPIC));
    
    // process data
    try {
      while (true) {
        ConsumerRecords<String, CourseModule> records = consumer.poll(Duration.ofSeconds(1));
        records.forEach(record -> {
    //      CourseModule m = record.value();
          CourseModule val = record.value();
          System.out.printf("%n*** received : %s%n", val);
          doService(val);
        });
      }
    } finally {
      consumer.close();
    }
  }
  

  public StudentRegist doService(CourseModule m){
    log.info("doService()...");

    Random rand = new Random();
    // todo: get the available Modules from inputstream
    StudentRegist obj = new StudentRegist("Student:"+rand.nextInt(10000), 
        m.toString());
    
    log.info("   {}({} {})", StudentRegist.class.getSimpleName(), obj.getStudent(), obj.getCourseModule());
    return obj;

  }
  
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
}
