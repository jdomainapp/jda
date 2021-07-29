package jda.modules.printing.model;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview
 *  Represents printing
 *  
 * @author dmle
 * @todo
 *  implement this when needed
 */
@DClass(serialisable=false)
public class Printing {
  // nothing yet
  @DAttr(name="id",type=Type.Integer,id=true,auto=true,mutable=false)
  private int id;
}
