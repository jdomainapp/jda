package jda.modules.report.model.stats;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview
 *  Represents a <b>non-serialisable</b> statistics counter.
 *  
 * @author dmle
 */
@DClass(serialisable=false)
public class StatCount {
  @DAttr(name="name",type=Type.String,length=15,optional=false,mutable=false)
  private String name;
  
  @DAttr(name="value",type=Type.Integer,optional=false,mutable=false)
  private Integer value;

  protected StatCount() {
    // only used by sub-types
  }
  
  public StatCount(String name, Integer val) {
    super();
    this.name = name;
    this.value = val;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getValue() {
    return value;
  }

  public void setValue(Integer val) {
    this.value = val;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()+"(" + name + "," + value + ")";
  }
}
