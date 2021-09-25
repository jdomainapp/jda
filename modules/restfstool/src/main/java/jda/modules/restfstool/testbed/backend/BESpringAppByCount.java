package jda.modules.restfstool.testbed.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import jda.modules.restfstool.backend.spring.BESpringApp;
import jda.modules.restfstool.config.RFSGenConfig;

/**
 * @overview 
 *  Similar to {@link BESpringApp} excepts that it starts each new app with the server-port 
 *  automatically incremented by 1 (from a base counter).  
 * 
 * <p>This is useful for testing multiple {@link SpringBootApplication}s. 
 * 
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.1
 */
@SpringBootApplication
@ComponentScan(basePackages = { 
    "jda.modules.restfstool.backend", // system beans
    "${domainBasePackages}",  // domain beans
})
public class BESpringAppByCount extends BESpringApp {
  private static Logger logger = (Logger) LoggerFactory.getLogger("module.restfstool");

  private static long portCounter = 1;
  
  public BESpringAppByCount() {
    // for Spring
    super();
  }
  
  public BESpringAppByCount(RFSGenConfig cfg) {
    super(cfg);
  }
  
  @Override
  public long getBeServerPort() {
    long beServerPort = super.getBeServerPort();
    beServerPort += portCounter++;
    
    return beServerPort;
  }
}
