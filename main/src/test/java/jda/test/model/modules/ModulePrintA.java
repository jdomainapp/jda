package jda.test.model.modules;

import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.mosa.view.assets.tables.JObjectTable;

/**
 * @overview 
 *  Defines the print configuration for Person.
 * 
 * @author dmle
 */
@ModuleDescriptor(
name="ModulePrintA")
public class ModulePrintA {
  @AttributeDesc(label="Hộ khẩu",
  type=JObjectTable.class,
  ref=@Select(clazz=Object.class,
    attributes={"number", "issuedDate"})
  )
  private Object familyRegister;

  
  @AttributeDesc(label="CMND",type=JObjectTable.class,
          ref=@Select(clazz=Object.class,
          attributes={"number", "issuedDate", "expiryDate"})
      )
  private Object nationalId;

  @AttributeDesc(label="Địa chỉ <br>nguyên quán",
      ref=@Select(clazz=Object.class, attributes={"city"}))
  private Object address;
}
