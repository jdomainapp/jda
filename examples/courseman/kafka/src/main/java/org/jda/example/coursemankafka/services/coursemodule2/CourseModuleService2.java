package org.jda.example.coursemankafka.services.coursemodule2;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jda.example.courseman.modules.coursemodule.model.CompulsoryModule;
import org.jda.example.courseman.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemankafka.services.utils.CourseManStreamConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.support.serializer.JsonSerializer;

@SpringBootApplication
public class CourseModuleService2 implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(CourseModuleService2.class);

  public static final String OUTPUT_TOPIC = CourseManStreamConfig.getStreamByService("coursemodules");

  /**
    Invoked automatically after SpringBoot application has been started
    (URL: https://stackoverflow.com/a/63229828) 
    
    @effects 
     initialise kafka-related resources 
   */
  @Override // CommandLineRunner
  public void run(String... args) throws Exception {
    log.info("(SpringBoot) CommandLineRunner.run: is executing...");
    startStreaming(args);
  }
  
  private void startStreaming(final String[] args) throws IOException {
    log.info("startStreaming()...");
    final Properties props = CourseManStreamConfig.getStreamsConfig(args);

    log.info("createOutputStream()...");

    // writes the CourseModules to output topic
    // create the producer
    props.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.RETRIES_CONFIG, 1);
    props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
    props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transaction_" + OUTPUT_TOPIC);
    
    Producer<String, CourseModule> producer = new KafkaProducer<>(props);

    producer.initTransactions();

    try {
      Random rand = new Random();
      while (true) {
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        
        producer.beginTransaction();
        String mid = Integer.toString(rand.nextInt(10000));
        CourseModule obj = new CompulsoryModule("Module:" + mid,
            rand.nextInt(10), rand.nextInt(5));
        log.info("Producer: {}", obj);

        producer.send(new ProducerRecord<String,CourseModule>(OUTPUT_TOPIC, mid, obj));

        producer.commitTransaction();
      } 
    } catch (ProducerFencedException | OutOfOrderSequenceException
        | AuthorizationException e) {
      // We can't recover from these exceptions, so our only option is to close
      // the producer and exit.
    } catch (KafkaException e) {
      // For all other exceptions, just abort the transaction and try again.
      producer.abortTransaction();
    } finally {
      producer.close();
    }
  }
  
  public static void main(String[] args) {
    // Start a Spring application    
    Class<?> appCls = CourseModuleService2.class;
    SpringApplication app = new SpringApplication(appCls);
    Properties props = new Properties();
    try {
      props.load(appCls.getClassLoader().getResourceAsStream("producer.properties"));
      app.setDefaultProperties(props);
      app.run(args);
      log.info("The {} has started...", appCls.getSimpleName());
    } catch (IOException e) { // props.load
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
