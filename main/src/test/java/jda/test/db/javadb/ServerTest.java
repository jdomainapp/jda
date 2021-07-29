package jda.test.db.javadb;

import org.junit.Test;

import jda.modules.common.net.ServerProtocolSpec;
import jda.modules.javadbserver.model.JavaDbServer;

public class ServerTest extends TestJavaDB {
  
  public void startServer(String protSpec) throws Exception {
    System.out.println("Starting Java Db server: " + protSpec);
    
    // start a JavaDBServer
    ServerProtocolSpec serverProt = new ServerProtocolSpec(protSpec);
    JavaDbServer dbServer = new JavaDbServer(serverProt);
    
    if (dbServer.isPortAvailable()) {
      dbServer.start();
      
      System.out.println("...ok");
      
      // wait to exit
      waitForEnterKey();      
    } else {
      System.err.printf("Server port is in use%n");
    }
  }
  
  @Test
  public void test() throws Exception {
    String spec = serverSpecs[4];
    
    startServer(spec);
  }
}
