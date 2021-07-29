package jda.test.modules.tool;

import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.setup.model.SetUpConfig;
import jda.test.app.courseman.basic.CourseManBasicTester;

public class TestDomainAppTool extends CourseManBasicTester {

  @Override
  protected Configuration initEmbeddedJavaDbConfiguration(String appName,
      String dataSourceName) {
    Configuration config = super.initEmbeddedJavaDbConfiguration(appName, dataSourceName);
    
    // use enhanced set up config
    config.setSetUpConfigurationType(SetUpConfig.class);
    
    return config;
  }

  @Override
  protected String getDataSourceName() {
    return "data/DomainAppTool";
  }

  @Override
  protected String getAppName() {
    return "DomainAppTool";
  }
  
}
