package jda.modules.mosar.test.reactjs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosarfrontend.reactjsbhd.FEReactApp;

/**
 * @overview 
 *  Similar to {@link FEReactApp} excepts that it starts each new app with the server-port 
 *  automatically incremented by 1 (from a base counter).  
 * 
 * <p>This is useful for testing multiple front-end app on Node.js. 
 *  *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class FEReactAppByCount extends FEReactApp {
  private static Logger logger = (Logger) LoggerFactory.getLogger("module.restfstool");

  private static long portCounter = 1;
  
  private long thisPortCounter;
  
  public FEReactAppByCount(RFSGenConfig config) {
    super(config);
    thisPortCounter = portCounter++;
  }

  @Override
  public String getFeProjName(RFSGenConfig config) {
    String feProjName = config.getFeProjName();
    
    feProjName += "_" + thisPortCounter;
    
    return feProjName;
  }
  
  /**
   * @modifies {@link #portCounter}
   * @effects 
   *  return super.getFeServerPort() + portCounter and 
   *  increment portCounter
   */
  @Override
  public long getFeServerPort(RFSGenConfig config) {
    long currPort = config.getFeServerPort();
    currPort += thisPortCounter;
    return currPort;
  }

  
}
