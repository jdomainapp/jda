package jda.modules.mccl.conceptmodel.view;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;
@DClass(schema="app_config")
public class Style {
  
  public static final String A_bgColor = "bgColor";
  
  @DAttr(name="id",id=true,auto=true,type=Type.Integer,length=6,optional=false,mutable=false)
  private int id;
  @DAttr(name="name",type=Type.String,length=50)
  private String name;  
  @DAttr(name="font",type=Type.String,length=30)
  private String font;
  @DAttr(name=A_bgColor,type=Type.String,length=20)
  private String bgColor;
  @DAttr(name="fgColor",type=Type.String,length=20)
  private String fgColor;
  
  private static int idCounter = 0;

  
  /**
   * constructor method
   */
  public Style(Integer id, String name, String font, String bgColor, String fgColor) {
    this.id = nextID(id);
    this.name=name;
    this.font = font;
    this.bgColor = bgColor;
    this.fgColor = fgColor;
  }

  public Style(String name, String font, String bgColor, String fgColor) {
    this(null, name, font, bgColor, fgColor);
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public String getFont() {
    return font;
  }

  public void setFont(String font) {
    this.font = font;
  }

  public String getBgColor() {
    return bgColor;
  }

  public void setBgColor(String bgColor) {
    this.bgColor = bgColor;
  }

  public String getFgColor() {
    return fgColor;
  }

  public void setFgColor(String fgColor) {
    this.fgColor = fgColor;
  }

  public int getId() {
    return id;
  }

  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();
      
      if (num > idCounter) 
        idCounter=num;
      
      return currID;
    }
  }
  
  public String toString() {
    return "Style("+id+","+name+","+font+")";
  }

  /**
   * @effects returns a <code>Style</code> object those attribute values are the same as <code>this</code>
   * 
   */
  public Style copy() {
    Style style = new Style(this.name, this.font, this.bgColor, this.fgColor);
    return style;
  }
}
