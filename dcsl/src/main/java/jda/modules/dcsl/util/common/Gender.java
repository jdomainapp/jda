package jda.modules.dcsl.util.common;

import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview
 *  Represents genders 
 *  
 * @author dmle
 */
public enum Gender {
  Male("Nam"//"\u2642"
      ),
  Female("Nữ"
      //"\u2640"
      );
  
  private String name;
  
  private Gender(String name) {
    this.name = name;
  }
  
  @DAttr(name = "name",id = true, type = DAttr.Type.String, length = 10)
  public String getName() {
    return name;
  }
  
  public boolean isType(Gender g) {
    return this == g;
  }
}
