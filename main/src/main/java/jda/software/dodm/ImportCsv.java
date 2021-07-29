package jda.software.dodm;

import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.dodm.DODM;
import jda.modules.dodm.DODMBasic;
import jda.modules.ds.SimpleDataFileLoader;
import jda.modules.setup.model.SetUpBasic;

/**
 * @overview 
 *  A command-line program that acts as the interface for the function {@link DODM#importObjectsFromCsvFile(Class, String)}.  
 *  
 * @author dmle
 */
public class ImportCsv {
  public static void main(String[] args) {
    
    // get arguments from properties
    try {
      if (args == null || args.length < 2) {
        throw new IllegalArgumentException("Needs 2 arguments: (1) setup-class; (2) data-loader-class");
      }

      Class appSetUpCls = Class.forName(args[0]);
      Class<SimpleDataFileLoader> dataCls = (Class<SimpleDataFileLoader>) Class.forName(args[1]);
      
      SetUpBasic su = SetUpBasic.createInstance(appSetUpCls);
      
      DODMBasic dodm = su.initDODM();
      
      if (!(dodm instanceof DODM)) {
        throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, 
            dodm.getClass().getName()+": Import CSV");
      }
      
      SimpleDataFileLoader dataClassObject = dataCls.newInstance();
      Class domainCls = dataClassObject.getDomainClass();
      
      System.out.printf("Importing CSV: %n   Domain class: %s%n   Data class: %s", 
          domainCls.getSimpleName(), 
          dataCls.getSimpleName());
      
      //addClass(c);
      if (!dodm.isRegistered(domainCls)) {
        dodm.registerClass(domainCls);
      }
      
      ((DODM)dodm).importObjectsFromCsvFile(domainCls, dataClassObject.getFilePath());
      
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
  }
}
