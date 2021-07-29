package jda.modules.common;

import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview 
 *  Represents an interface for modules that wish to make themselves available for use by client 
 *  applications.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 * - 4.0: created<br>
 * - 5.2: changed exec()
 */
public interface ModuleToolable {

  // void exec() throws NotPossibleException;
  Object exec(Object...args) throws NotPossibleException;
}
