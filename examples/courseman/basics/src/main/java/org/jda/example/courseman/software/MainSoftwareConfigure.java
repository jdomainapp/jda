package org.jda.example.courseman.software;

import jda.modules.common.exceptions.NotPossibleException;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.impl.DomSoftware;

/**
 * @overview 
 *  A class the set up the software data source. It should be run only once and 
 *  involves creating a relational model for the domain model of the software. 
 *  
 * @author Duc Minh Le (ducmle)
 */
public class MainSoftwareConfigure {
  
  public static void main(String[] args) {
    try {
      DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();
      
      sw.configure();
      
      //sw.deleteConfig();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
