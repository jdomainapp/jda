package jda.modules.ds;

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * @overview
 *  A data file loader used to load test data (objects) from file. 
 *  
 * @author dmle
 *
 */
public abstract class SimpleDataFileLoader {

  /**
   * @effects 
   *  return the absolute file path
   */
  public abstract String getFilePath() throws FileNotFoundException;

  /**
   * @effects 
   *  return the domain class whose objects are to be loaded by this
   */
  public abstract Class getDomainClass();

  protected String getFilePath(Class loaderCls, String fileName) throws FileNotFoundException {
  
    URL fileURL = loaderCls.getResource(fileName);
    
    if (fileURL == null)
      throw new FileNotFoundException("File: " + fileName + " (relative to " + loaderCls + ")");
    
    return fileURL.getPath();
  }
  


}
