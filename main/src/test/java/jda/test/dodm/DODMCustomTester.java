package jda.test.dodm;

import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;

/**
 * @overview 
 *  A sub-type of {@link DODMEnhancedTester} that supports a custom {@link DODMConfig}
 *  that contains specific types of {@link DOM} and/or {@link DSM}. 
 *  
 *  <p>This is used to test the new features of the DODM.
 *  
 * @author dmle
 *
 */
public class DODMCustomTester extends DODMEnhancedTester {

  @Override
  protected Configuration initEmbeddedJavaDbConfiguration(String appName,
      String dataSourceName) {
    Configuration config = super.initEmbeddedJavaDbConfiguration(appName, dataSourceName);
    
    // update DODM component types as desired
    DODMConfig dodmConfig = config.getDodmConfig();
    
    Class<DOM> domType = DOM.class;
    Class<DSM> dsmType = DSM.class;
    
    dodmConfig.setDomType(domType);
    dodmConfig.setDsmType(dsmType);
    
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
