/**
 * 
 */
package jda.mosa.view.assets.util.function.value;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DomainValueDesc;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.view.assets.DataContainerToolkit;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.util.function.ViewFunction;

/**
 * 
 * @overview
 *  A function that computes a value from that of a data field of the target domain attribute in a {@link DomainValueDesc}.
 *  The computation is based on the specification of this {@link DomainValueDesc}.
 *  
 * @version 3.2c
 *
 * @author dmle
 */
public class DataFieldValueFunction extends ViewFunction {

  private DSMBasic dsm;
  private JDataContainer rootContainer;
  private DomainValueDesc valueDesc;
  
  // derived fields (for performance)
  private JDataContainer targetContainer;
  private DAttr attrib;
  
  public DataFieldValueFunction(DSMBasic dsm, JDataContainer rootContainer, DomainValueDesc valueDesc) {
    this.dsm = dsm;
    this.rootContainer = rootContainer;
    this.valueDesc = valueDesc;
  }

  /* (non-Javadoc)
   * @see domainapp.basics.view.function.value.ViewFunction#eval()
   */
  /**
   * @effects 
   *    evaluates this against the data field in the containment hierarchy of <tt>rootContainer</tt> that is specified by {@link valueDesc}
   *    and return the result
   */
  @Override
  public Object eval() throws ApplicationRuntimeException {
    Op op = valueDesc.op();
    
    if (targetContainer == null) {
      // first time: find and cache the target data field for subsequent use
      Class targetCls = valueDesc.clazz();
      String attribName = valueDesc.attribute();
      
      // retrieve from containment hierarchy of rootContainer the data field of the target attribute
      attrib = dsm.getDomainConstraint(targetCls, attribName); // the target attribute
      targetContainer = DataContainerToolkit.getDescendantContainer(rootContainer, targetCls);
      
      if (targetContainer == null) {
        throw new NotFoundException(NotFoundException.Code.CHILD_DATA_CONTAINER_NOT_FOUND, new Object[] {targetCls, rootContainer});
      }
    }
    
    // get the value of the target data field
    Object val = DataContainerToolkit.getDataFieldActualValue(targetContainer, attrib); 
      
    Object result = null;
    
    if (val != null) {
      // evaluate the value against op
      if (op.equals(Op.EQ)) {
        result = val;
      }  
      // add other cases here
      else {
        throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, new Object[] {"Operator: " + op});
      }
    }
    
    // return the result
    return result;
  }

}
