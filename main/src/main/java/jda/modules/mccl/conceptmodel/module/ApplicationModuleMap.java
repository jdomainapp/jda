package jda.modules.mccl.conceptmodel.module;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

@DClass(schema="app_config")
public class ApplicationModuleMap {
  @DAttr(name="id",id=true,auto=true,type=Type.Long,optional=false,mutable=false)
  private long id;
  
  @DAttr(name="parentModule",type=DAttr.Type.Domain)
  @DAssoc(ascName="parent-has",role="childmodule",
  ascType=AssocType.One2Many,endType=AssocEndType.Many,
  associate=@Associate(type=ApplicationModule.class,cardMin=1,cardMax=1))
  private ApplicationModule parentModule;

  @DAttr(name="childModule",type=DAttr.Type.Domain)
  @DAssoc(ascName="child-has",role="parentmodule",
  ascType=AssocType.One2Many,endType=AssocEndType.Many,
  associate=@Associate(type=ApplicationModule.class,cardMin=1,cardMax=1))
  private ApplicationModule childModule;

  /**
   * This constructor is used to create objects from the data source
   */
  public ApplicationModuleMap(Long id, ApplicationModule parent, ApplicationModule child) {
    this.id = nextID(id);
    this.parentModule = parent;
    this.childModule = child;
  }

  public ApplicationModuleMap(ApplicationModule parent, ApplicationModule child) {
    this(null, parent, child);
  }

  private long nextID(Long id) {
    if (id == null)
      return System.nanoTime();
    else
      return id;
  }

  public long getId() {
    return id;
  }

  public ApplicationModule getParentModule() {
    return parentModule;
  }

  public void setParentModule(ApplicationModule parentModule) {
    this.parentModule = parentModule;
  }

  public ApplicationModule getChildModule() {
    return childModule;
  }

  public void setChildModule(ApplicationModule childModule) {
    this.childModule = childModule;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ApplicationModuleMap other = (ApplicationModuleMap) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ApplicationModuleMap (" + id + ", parent:" + parentModule.getName() + ", child:"
        + childModule.getName() + ")";
  }
}
