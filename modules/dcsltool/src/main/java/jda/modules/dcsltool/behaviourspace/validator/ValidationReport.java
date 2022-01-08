package jda.modules.dcsltool.behaviourspace.validator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @overview Capture detailed result of performing behaviour space validation on a domain class.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2
 */
public class ValidationReport {

  private List<String> errors;
  
  /**
   * @effects  initialise this as empty
   */
  public ValidationReport() {
    errors = new LinkedList<>();
  }

  /**
   * @effects 
   *  add to this an error-typed entry, whose content is <tt>errorMsg</tt>
   */
  public void addError(String errorMsg) {
    errors.add(errorMsg);
  }

  /**
   * @effects 
   */
  public void clear() {
    errors.clear();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    StringBuilder strb = new StringBuilder("Validation Report: \n");
    
    strb.append("ERRORS: ");
    if (!errors.isEmpty()) {
      strb.append("\n");
      int numErrors = errors.size();
      int idx = 0;
      for (String s : errors) {
        strb.append(s);
        if (idx < numErrors - 1) strb.append("\n");
      }
    } else {
      strb.append("(Nil)");
    }
    
    return strb.toString();
  }

  /**
   * @effects 
   *  if {@link #errors} is not empty
   *    return an {@link Iterator} of it
   *  else
   *    return null
   */
  public Iterator<String> getErrorIterator() {
    if (errors != null) {
      return errors.iterator();
    } else {
      return null;
    }
  }
}
