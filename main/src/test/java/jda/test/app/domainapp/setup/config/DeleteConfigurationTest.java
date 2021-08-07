package jda.test.app.domainapp.setup.config;

import org.junit.Test;

import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.SetUpBasic;


public class DeleteConfigurationTest extends TestSetUpConfig {  
  
  @Test
  public void doTest() throws Exception { 
    SetUpBasic su = ((TestSetUpConfig) instance).getSetUp();
    
    Cmd cmd = Cmd.DeleteConfig;
    su.run(cmd, null);
  }
}
