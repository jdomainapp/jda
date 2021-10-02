package org.jda.example.courseman.modules.enrolment;

import java.util.Date;

import org.jda.example.courseman.modules.enrolment.model.EnrolmentClosure;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.MCCLConstants;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.assets.datafields.text.JFormattedField;

/**
 * @overview
 *  Module for {@link EnrolmentClosure} 
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleEnrolmentClosure",
modelDesc=@ModelDesc(
    model=EnrolmentClosure.class
),
viewDesc=@ViewDesc(
    formTitle="-", 
    domainClassLabel="Enrolment Closure",
    imageIcon="enrolmentClosure.jpg"
    //viewType=Type.Data,
    //parent=RegionName.Tools,
    //view=View.class,
    //layoutBuilderType=TwoColumnLayoutBuilder.class,
    //topX=0.5,topY=0.0,widthRatio=0.5f,heightRatio=0.9f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
    // openPolicy=OpenPolicy.I_C
    //,isDataFieldStateListener=true  // listens to state change event of list field
),
type=ModuleType.DomainData,
isPrimary=true
)
public class ModuleEnrolmentClosure {
  @AttributeDesc(label="Thank you for your enrolment!")
  private String title;
  
  @AttributeDesc(label="Id",alignX=AlignmentX.Center)
  private int id;

  @AttributeDesc(label="Date",
      type=JFormattedField.class,
      alignX=AlignmentX.Center)
  private Date closureDate;
//  @AttributeDesc(label="Class registration"
//      ,type=JTextField.class
//      ,editable=false
//      ,modelDesc=@ModelDesc(model=SClassRegistration.class,dataSourceType=JFlexiDataSource.class)
//      )
//  private SClassRegistration sclassRegist;
//
//  @AttributeDesc(label="Orientation"
//      ,type=JTextField.class
//      ,editable=false
//      ,modelDesc=@ModelDesc(model=Orientation.class,dataSourceType=JFlexiDataSource.class)
//      )
//  private Orientation orient;
  
  @AttributeDesc(label="Note" // &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      //, width=30, height=MetaConstants.STANDARD_FIELD_HEIGHT
      )
  private String note;

}