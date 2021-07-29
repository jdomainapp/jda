package jda.modules.mccl.conceptmodel.view;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * Represents a language-aware label. 
 * 
 * @author dmle
 *
 */
@DClass(schema="app_config")
public abstract class Label {
  @DAttr(name="id",id=true,auto=true,type=Type.Integer,mutable=false,optional=false,length=6)
  private int id;

  @DAttr(name="typeId",type=Type.Integer,optional=false,length=6)
  private int typeId;

  @DAttr(name="value",type=Type.String,optional=false,length=255)
  private String value;
    
  // v2.7
  @DAttr(name = "style", type = DAttr.Type.Domain, length = 6)
  private Style style;
  
  private static int idCounter;

  public Label(Integer id, Integer typeId, String value,
      Style style // v2.7
      ) {
    this.id = nextID(id);
    this.typeId = nextTypeId(typeId);
    this.value=value;
    this.style=style;
  }
  
  public Label(String value) {
    this(null,null,value,null);
  }
  
  /**
   * @effects 
   *  create and return an instance of <tt>T</tt> from <tt>labelStr</tt>
   * @version 3.0
   */
  public static <T extends Label> T createInstance(Class<T> labelCls, String labelStr) {
    try {
      // invoke the single-arg constructor to create object 
      T instance = labelCls.getConstructor(String.class).newInstance(labelStr);
      return instance;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {labelCls.getSimpleName(), labelStr});
    }
  }
  
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setTypeId(int typeId) {
    this.typeId = typeId;
  }

  public int getTypeId() {
    return typeId;
  }

  public Style getStyle() {
    return style;
  }

  public void setStyle(Style style) {
    this.style = style;
  }

  private int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();

      if (num > idCounter)
        idCounter = num;

      return currID;
    }
  }
  
  protected abstract int nextTypeId(Integer currID);

  @Override
  public String toString() {
    return "Label<"+id+","+typeId+","+value+">";
  }
  
  @Override
  public boolean equals(Object o) {
    return (o != null && o instanceof Label && 
        ((Label)o).value.equals(this.value));
  }
}
