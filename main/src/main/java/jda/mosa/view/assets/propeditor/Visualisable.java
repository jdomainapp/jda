/**
 *
 * A public interface that application classes must implement in order to use
 * the visualisation package 
 * 
 * @author Duc M Le  <a href="mailto:dmle@doc.ic.ac.uk"><i>dmle@doc.ic.ac.uk</i></a>
 * @version 1.0
 * Department of Computing, Imperial College
 */
package jda.mosa.view.assets.propeditor;
import java.util.Collection;


public interface Visualisable {
  /**
   * The label to be displayed next to the object
   * @return
   */
  public String getLabel();
  
  public void setProperty(String propName, String value);
  
  /**
   * Must provide a collection of {@link PropertyObject} 
   * so that properties of a selected object can be displayed on the <code>PropertyEditor</code>
   * @return
   */
  public Collection getPropertyObjects();
}
