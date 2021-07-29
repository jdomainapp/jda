/**
 *
 *
 * @author Duc M Le  <a href="mailto:dmle@doc.ic.ac.uk"><i>dmle@doc.ic.ac.uk</i></a>
 * @version 1.0
 * Department of Computing, Imperial College
 */
package jda.mosa.view.assets.propeditor;



public interface PropertyObject {
  public String getName();
  
  public Object getValue();
  
  public void setValue(Object newValue);
  
  public boolean isEditable();
  
  public Object[] getAllowedValues();
  
  /**
   * Handle user's invocation action on the object
   * 
   * @return
   */
  public Object[] handleEvent(Visualisable source, Object data);
}
