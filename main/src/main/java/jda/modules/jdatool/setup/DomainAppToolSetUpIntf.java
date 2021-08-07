package jda.modules.jdatool.setup;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview
 *
 * @author dmle
 *
 * @version 3.3
 */
public interface DomainAppToolSetUpIntf {

  /**
   * @effects 
   *  read and load the domain class(es) whose FQN names are given in <tt>args</tt>.
   *  
   *  <p>Throws IllegalArgumentException if no valid domain classes can be loaded from <tt>args</tt>; 
   *  NotPossibleException if fails to create the application modules; 
   *  DataSourceException if fails to connect to the data source
   */
  void loadClasses(String[] args) throws IllegalArgumentException,
      NotPossibleException, DataSourceException;

}
