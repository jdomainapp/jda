package jda.test.app.domainapp;

import jda.mosa.software.ApplicationLauncher;
import jda.test.app.domainapp.setup.DomainAppSetUp;

public class DomainApp {
  // application entry point
  public static void main(String[] args) {
    String lang = null;
    if (args.length > 0) {
      lang = args[0];
    }
  
    String dbName = "data/DomainAppTest";
    
    DomainAppSetUp su = new DomainAppSetUp(dbName);
    
    try {
      ApplicationLauncher.run(su, lang);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
