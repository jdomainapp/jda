package org.jda.example.coursemankafka.services.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class CourseManStreamConfig {
  public static final String STREAM_ID = "streams-courseman";

  public static Properties getStreamsConfig(final String[] args) throws IOException {
    final Properties props = new Properties();
    if (args != null && args.length > 0) {
      try (final FileInputStream fis = new FileInputStream(args[0])) {
        props.load(fis);
      }
      if (args.length > 1) {
        System.out.println(
            "Warning: Some command line arguments were ignored. This demo only accepts an optional configuration file.");
      }
    }
    props.putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, STREAM_ID);
    props.putIfAbsent(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.putIfAbsent(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
    props.putIfAbsent(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
        Serdes.String().getClass().getName());
    props.putIfAbsent(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
        Serdes.String().getClass().getName());

    // setting offset reset to earliest so that we can re-run the demo code with
    // the same pre-loaded data
    // Note: To re-run the demo, you need to use the offset reset tool:
    // https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams+Application+Reset+Tool
    props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return props;
  }

  /**
   * @effects 
   * 
   */
  public static String getStreamShutdownHook() {
    return STREAM_ID+"-shutdown-hook";
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public static String getStreamByService(String serviceName) {
    return STREAM_ID+"-"+serviceName;
  }
  
  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public static String getStreamInput(String serviceName) {
    return STREAM_ID+"-"+serviceName+"-input";
  }
}
