package org.jda.example.courseman.modules;

import org.jda.example.courseman.model.Student;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;

@ModuleDescriptor(name = "ModuleAddress", 
modelDesc = @ModelDesc(
    model = org.jda.example.courseman.model.Address.class), 
viewDesc = @ViewDesc(
    formTitle = "Form: Address", imageIcon = "Address.png", 
    domainClassLabel = "Address"  
    ,parent=RegionName.Tools // TODO
    ,viewType=RegionType.Data // TODO 
    ,view = jda.mosa.view.View.class), 
controllerDesc = @ControllerDesc())
public class ModuleAddress {
  // TODO
  @AttributeDesc(label = "Address")
  private String title;

  @AttributeDesc(label = "Mã")
  private int id;

  @AttributeDesc(label = "Thành phố")
  private String cityName;

  @AttributeDesc(label = "Student")
  private Student student;
}
