package org.jda.example.courseman.modulesupdate.sclassregist;

import org.jda.example.courseman.modulesupdate.sclass.ModuleSClass;
import org.jda.example.courseman.modulesupdate.sclass.model.SClass;
import org.jda.example.courseman.modulesupdate.sclassregist.model.SClassRegistration;
import org.jda.example.courseman.modulesupdate.student.ModuleStudent;
import org.jda.example.courseman.modulesupdate.student.model.Student;

import jda.modules.dcsl.syntax.Select;
import jda.modules.ds.viewable.JFlexiDataSource;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.JTextField;
import jda.mosa.view.assets.datafields.list.JComboField;

/**
 * @Overview
 *  Module for {@link SClassRegistration}
 * 
 * @author dmle
 */
@ModuleDescriptor(name="ModuleSClassRegistration",
modelDesc=@ModelDesc(
    model=SClassRegistration.class
),
viewDesc=@ViewDesc(
    formTitle="Manage class registration",
    domainClassLabel="SClass Registration"
    //,imageIcon="-"
    ,imageIcon="sclassRegistration.jpg",
    viewType=RegionType.Data,
    //parent=RegionName.Tools,
    view=View.class
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    isDataFieldStateListener=true
),
isPrimary=true
,childModules={ModuleStudent.class, ModuleSClass.class}
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleSClassRegistration  {

  @AttributeDesc(label="Class Registration")
  private String title;
  
  // attributes
  @AttributeDesc(label="Id", alignX=AlignmentX.Center)
  private int id;
  
  @AttributeDesc(label="Student",
      /* use this config if this module is displayed on menu Tools
      type=JComboField.class,
      // use this if this field is displayed in a JObjectTable
      //width=150,height=25,
      ref=@Select(clazz=Student.class,attributes={"name"}),
      loadOidWithBoundValue=true,  // this must be set to true if displayOidWithBoundValue = true
      displayOidWithBoundValue=true
      */
      /* use this config if this module is NOT displayed on menu Tools AND to be used only as 
       * part of an activity flow where Student is known before-hand.
       * */
      type=JTextField.class
      , editable=false
      ,modelDesc=@ModelDesc(model=Student.class, dataSourceType=JFlexiDataSource.class)
      ,ref=@Select(clazz=Student.class,attributes={"name"})
      )
  private Student student;
  
  @AttributeDesc(label="Class", 
      type=JComboField.class,
      // this is is needed for JObjectTable 
      //width=80,height=25,
      ref=@Select(clazz=SClass.class,attributes={"name"}),
      isStateEventSource=true,      
      alignX=AlignmentX.Center)
  private SClass sClass;  
}
