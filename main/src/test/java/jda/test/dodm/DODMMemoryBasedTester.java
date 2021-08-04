package jda.test.dodm;

import jda.modules.mccl.conceptmodel.Configuration;
import jda.util.SwTk;

/**
 * @overview 
 *  A sub-type of {@link DODMBasicTester} that supports memory-based DODM.
 *  
 *  <p>This is used to quickly test application features that do not need to use a data source.
 *  
 * @author dmle
 * 
 * @version 3.2
 *
 */
public class DODMMemoryBasedTester extends DODMBasicTester {

  @Override
  protected Configuration initEmbeddedJavaDbConfiguration(String appName,
      String dataSourceName) {
    printf("Using memory-based config...%n");

    Configuration config = SwTk.createMemoryBasedConfiguration(getAppName());
    return config;
  }

  @Override
  protected void initClasses() {
    // for sub-types to use if needed
  }

  @Override
  protected void defaultInitData() {
    // for sub-types to use if needed
  }
}
