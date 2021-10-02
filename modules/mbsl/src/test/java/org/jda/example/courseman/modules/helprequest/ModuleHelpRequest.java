package org.jda.example.courseman.modules.helprequest;

import org.jda.example.courseman.modules.helprequest.model.HelpRequest;
import org.jda.example.courseman.modules.student.model.Student;

import jda.modules.dcsl.syntax.Select;
import jda.modules.ds.viewable.JFlexiDataSource;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.JTextField;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;

/**
 * @overview
 *  Module for {@link HelpRequest} (HelpRequest)
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleHelpRequest",
modelDesc=@ModelDesc(
    model=HelpRequest.class
),
viewDesc=@ViewDesc(
    domainClassLabel="Help Request",
    formTitle="Help Request", 
    imageIcon="helprequest.jpg",
    viewType=RegionType.Data,
    //parent=RegionName.Tools,
    view=View.class,
    layoutBuilderType=TwoColumnLayoutBuilder.class,
    topX=0.5,topY=0.0,widthRatio=0.5f,heightRatio=0.9f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
    // openPolicy=OpenPolicy.I_C
    //,isDataFieldStateListener=true  // listens to state change event of list field
),
type=ModuleType.DomainData,
isPrimary=true
)
public class ModuleHelpRequest {
  @AttributeDesc(label="Help Request")
  private String title;

  @AttributeDesc(label="Id",alignX=AlignmentX.Center)
  private int id;

  @AttributeDesc(label="Student"
      ,type=JTextField.class
      ,editable=false
      ,ref=@Select(clazz=Student.class,attributes={Student.A_name})
      ,modelDesc=@ModelDesc(model=Student.class,dataSourceType=JFlexiDataSource.class)
      )
  private Student student;

  @AttributeDesc(label="Content")
  private String content;

}