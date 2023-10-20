package org.jda.example.courseman.modules;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import org.jda.example.courseman.model.Address;
import org.jda.example.courseman.model.Student;

@ModuleDescriptor(name = "ModuleAddress", 
modelDesc = @ModelDesc(
    model = Address.class),
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

  @AttributeDesc(label = "Id")
  private int id;

  @AttributeDesc(label = "City")
  private String cityName;

  @AttributeDesc(label = "Student")
  private Student student;
}
