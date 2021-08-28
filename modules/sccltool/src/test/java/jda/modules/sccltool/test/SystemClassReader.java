package jda.modules.sccltool.test;

import java.util.Arrays;

import org.junit.Test;

import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.modules.sccltool.test.model.SC1;
import jda.util.SwTk;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class SystemClassReader {
  @Test
  public void run() {
    Class sysClass = SC1.class;
    
    // initial config
    Configuration initCfg = SwTk.parseInitApplicationConfiguration(sysClass);
    System.out.printf("Initial config: %n%s%n", initCfg);
    
    // data source config
    DODMConfig dodmCfg = initCfg.getDodmConfig();
    System.out.printf("%nData source config: %n%s%nJDBC URL: %s%nData source: %s%n", 
        dodmCfg,
        dodmCfg.getProtocolSpec(),
        dodmCfg.getOsmConfig().getDataSourceName() // more specific JDBC properties
        );
    
    // more complete config
    Configuration config = SwTk.parseApplicationConfiguration(sysClass);
    System.out.printf("%nConfig: %n%s%n", config);
    System.out.printf("%nOrganisation: %n%s%n", config.getOrganisation());
    
    Class[] modules = SwTk.parseMCCs(sysClass);
    System.out.printf("%nModules: %s%n", Arrays.toString(modules));
  }
}
