package jda.test.db.javadb;

import org.junit.Test;

import jda.modules.common.net.ProtocolSpec;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.util.SwTk;

public class OsmClientTest extends TestJavaDB {
  
  @Test
  public void test() throws Exception {
    String spec = clientSpecs[5];
    
    System.out.println("Connecting to Java Db server at: " + spec);
    
    // create a client-side protocol configuration
    ProtocolSpec protSpec = new ProtocolSpec(spec);
    
    // test configuration
    Configuration config = SwTk.createDefaultInitApplicationConfiguration("Test", protSpec);

    // create a DODM with the connection
    DODMBasic dodm = DODMBasic.getInstance(config);
    
    System.out.println("...ok");
  }
}
