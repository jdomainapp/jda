package jda.modules.mccl.conceptmodel.view;

import jda.modules.dcsl.syntax.DAttr;

/** Region types*/
public enum RegionType {
  Root, 
  Main, // main  
  Data, // data
  // v3.2: DataAuto,  
  DataLogin,
  /**Read-only, informational GUI*/
  //Info,
  // v3.2 (not used): Report,  // report
  // v3.2 (not used): Composite, // composite data  
  ChoiceMenu, Check, Menu, //
  // v3.2 (not used): Export, 
  Text, Label, 
  Null;

  @DAttr(name = "name", id = true, type = DAttr.Type.String, length = 10)
  public String getName() {
    return name();
  }
}