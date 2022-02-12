package jda.modules.patterndom.patterndefs;

import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.AttrRef;
import java.util.Collection;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.query.QueryDef;
import jda.modules.dcsl.syntax.query.AttribExp;
import jda.modules.common.expression.Op;

/**
 * @overview 
 *  Pattern: Data Source Attribute
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4
 */
@DClass()
class C1 {

    @DAttr(name = "aSrc", type = Type.Collection, serialisable = false, virtual = true, sourceQuery = true)
    @QueryDef(clazz = C2.class, exps = { @AttribExp(attrib = "aBoundFilter", op = Op.IN, value = "'val'") })
    private Collection<C2> aSrc;

    @DAttr(name = "aBound", type = Type.Domain, sourceAttribute = "aSrc")
    private C2 aBound;

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "aSrc")
    public Collection<C2> getSelAddrs() {
        return aSrc;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "aSrc")
    public void setSelAddrs(Collection<C2> aSrc) {
        this.aSrc = aSrc;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "aBound")
    public C2 getABound() {
        return aBound;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "aBound")
    public void setABound(C2 aBound) {
        this.aBound = aBound;
    }
}

class C2 {
  @DAttr(name = "aBoundFilter", optional=false)
  private Object aBoundFilter;
  
  @DOpt(type = DOpt.Type.Getter)
  @AttrRef(value = "aBoundFilter")
  public Object getABoundFilter() {
      return aBoundFilter;
  }
}