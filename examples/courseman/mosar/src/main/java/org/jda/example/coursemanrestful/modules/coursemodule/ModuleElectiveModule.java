package org.jda.example.coursemanrestful.modules.coursemodule;

import org.jda.example.coursemanrestful.modules.coursemodule.model.ElectiveModule;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;

/**
 * @Overview
 *  Module for {@link ElectiveModule}
 * 
 * @author dmle
 */
@ModuleDescriptor(name="ModuleElectiveModule",
modelDesc=@ModelDesc(
    model= ElectiveModule.class
),
viewDesc=@ViewDesc(
    formTitle="Manage Elective Module",
    domainClassLabel="Elective Module",    
    imageIcon="coursemodule.jpg",
    viewType=RegionType.Data,
    view=View.class,
    parent=RegionName.Tools
),
controllerDesc=@ControllerDesc(controller=Controller.class),
isPrimary=true
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleElectiveModule  extends ModuleCourseModule {
  @AttributeDesc(label="Form: Elective Module")
  private String title;
  
  @AttributeDesc(label="Dept. Name", alignX=AlignmentX.Center)
  private String deptName;
}
