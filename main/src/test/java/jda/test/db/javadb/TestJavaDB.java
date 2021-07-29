package jda.test.db.javadb;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public abstract class TestJavaDB {
  
  // dbName must be prefix with '/' if specified (e.g. /CourseMan or /data/CourseMan)
  protected static String dsName = "/data/DomainAppTest"; //getDataSourceName();
  protected static String protPrefix = "derby:"; // must not contain the prefix "jdbc:" 
  
  protected static String[] serverSpecs = {
      //INVALID
      protPrefix+"//localhost:xyz",
      // VALID
      protPrefix+"//:1527",                    // default host
      protPrefix+"//localhost",                // default port
      protPrefix+"//:",                         // default host & port
      // with a db name: strictly speaking this is not necessary b/c the server can serve
      // many different databases
      protPrefix+"//localhost:1527"+ dsName,   // local host with db name 
      protPrefix+"//localhost:1234"+ dsName,   // 
  };

  protected static String[] clientSpecs = {
    //INVALID
    protPrefix+"//localhost:xyz",            // wrong port
    protPrefix+"//:1527",                    // no host
    protPrefix+"//:1234",                    // invalid port
    protPrefix+"//:",                        // no host, port
    protPrefix+"//localhost",                // no database name
    // VALID
    protPrefix+"//localhost"+dsName,         // default port
    protPrefix+"//localhost:1527"+ dsName,   // host & port 
  };

  /**
   * This method waits until the user hits enter
   */
  protected static void waitForEnterKey() throws Exception {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Press [Enter] to stop Server");
    in.readLine();
  }
}
