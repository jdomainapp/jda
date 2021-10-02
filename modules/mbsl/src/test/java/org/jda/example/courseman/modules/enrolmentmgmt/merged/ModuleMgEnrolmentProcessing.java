package org.jda.example.courseman.modules.enrolmentmgmt.merged;

import java.util.Collection;

import org.jda.example.courseman.modules.enrolmentmgmt.merged.model.control.MgEnrolmentProcessing;
import org.jda.example.courseman.modules.orientation.ModuleOrientation;
import org.jda.example.courseman.modules.orientation.model.Orientation;
import org.jda.example.courseman.modules.sclassregist.ModuleSClassRegistration;
import org.jda.example.courseman.modules.sclassregist.model.SClassRegistration;

import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.view.assets.panels.DefaultPanel;

@ModuleDescriptor(
    name="ModuleFEnrolmentProcessing",
    modelDesc=@ModelDesc(
        model=MgEnrolmentProcessing.class
    ),
    viewDesc=@ViewDesc(
        formTitle="-",
        domainClassLabel="MgEnrolmentProcessing",    
        imageIcon="enrolmentProc.jpg"
        //view=View.class,
        //viewType=Region.Type.Data,
        //layoutBuilderType=TabLayoutBuilder.class
        //topX=0.5,topY=0.0,//widthRatio=0.9f,heightRatio=0.9f,
        //parent=RegionName.Tools,
    )
    ,isPrimary=true,
    childModules={ModuleSClassRegistration.class, ModuleOrientation.class}
    ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleMgEnrolmentProcessing {
  @AttributeDesc(label="Enrolment Processing")
  private String title;

  // payment 
  @AttributeDesc(label="Class Registration"
      ,type=DefaultPanel.class
      ,props={
        @PropertyDesc(name=PropertyName.view_objectForm_autoActivate, valueType=Boolean.class, valueAsString="true")
      }
  )
  private Collection<SClassRegistration> sclassRegists;
  
  // authorisation 
  @AttributeDesc(label="Orientation"
      ,type=DefaultPanel.class
      ,props={
        @PropertyDesc(name=PropertyName.view_objectForm_autoActivate, valueType=Boolean.class, valueAsString="true")
      }
  )
  private Collection<Orientation> orientations;
  
}
