package jda.modules.jdatool.setup;

import java.util.List;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.setup.model.SetUpConfigBasic;
import jda.modules.setup.model.SetUpGen;

/**
 * @overview
 *  A sub-type of {@link SetUpGen} that represents the application set-up class for domain application tool
 *  
 *  <br><b>Note</b>: The input domain classes must be specified from the set up class.
 *  
 * @author dmle
 * @version 3.3
 */
public class DomainAppToolSetUpGen extends SetUpGen implements DomainAppToolSetUpIntf {
  
  private DomainAppToolSetUpHelper helper;
  
  public DomainAppToolSetUpGen() {
    super();
    helper = new DomainAppToolSetUpHelper(this);
  }
  
  @Override
  public Class[] getInputModelClasses() {
    // return the model classes
    return helper.getInputModelClasses();
  }
  
  @Override
  public Class[] getModelClasses() {
    // return the model classes together with their super classes 
    return helper.getInputModelClasses();
  }
  
  @Override
  public List<List<Class>> getModuleDescriptors() {
    // return the module descriptors of the model classes (if any)
    //return moduleDescriptors;
    return helper.getModuleDescriptors();
  }
  
  
  /**
   * @modifies {@link #modelClasses}, {@link #moduleDescriptors}
   * @effects 
   *  read and load the domain class(es) whose FQN names are given in <tt>args</tt>.
   *  
   *  <p>Throws IllegalArgumentException if no valid domain classes can be loaded from <tt>args</tt>; 
   *  NotPossibleException if fails to create the application modules; 
   *  DataSourceException if fails to connect to the data source
   */
  @Override
  public void loadClasses(String[] args) throws IllegalArgumentException, NotPossibleException, DataSourceException {
    helper.loadClasses(args);
  }
  
  
  @Override
  protected void createDomainConfiguration(SetUpConfigBasic sucfg,
      boolean serialised  // v2.8
      )
      throws DataSourceException, NotFoundException {
    helper.createDomainConfiguration(sucfg, serialised);
  }
}
