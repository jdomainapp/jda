package org.jda.example.courseman.modules.enrolmentmgmt.merged;

import static jda.modules.mccl.conceptmodel.view.RegionName.Chart;
import static jda.modules.mccl.conceptmodel.view.RegionName.Delete;
import static jda.modules.mccl.conceptmodel.view.RegionName.Export;
import static jda.modules.mccl.conceptmodel.view.RegionName.First;
import static jda.modules.mccl.conceptmodel.view.RegionName.Last;
import static jda.modules.mccl.conceptmodel.view.RegionName.Next;
import static jda.modules.mccl.conceptmodel.view.RegionName.ObjectScroll;
import static jda.modules.mccl.conceptmodel.view.RegionName.Open;
import static jda.modules.mccl.conceptmodel.view.RegionName.Previous;
import static jda.modules.mccl.conceptmodel.view.RegionName.Print;
import static jda.modules.mccl.conceptmodel.view.RegionName.Update;

import java.util.Collection;

import org.jda.example.courseman.modules.enrolment.ModuleEnrolmentClosure;
import org.jda.example.courseman.modules.enrolment.model.EnrolmentClosure;
import org.jda.example.courseman.modules.enrolmentmgmt.merged.model.EnrolmentMgmt;
import org.jda.example.courseman.modules.enrolmentmgmt.merged.model.control.MgEnrolmentProcessing;
import org.jda.example.courseman.modules.sclassregist.model.SClassRegistration;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.mbsl.controller.command.ExecActivityCommand;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.containment.CEdge;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.containment.ScopeDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import jda.mosa.view.assets.layout.MergedLayoutBuilder;
import jda.mosa.view.assets.layout.TabLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;

@ModuleDescriptor(
    name="ModuleEnrolmentMgmt",
    modelDesc=@ModelDesc(
        model=EnrolmentMgmt.class
    ),
    viewDesc=@ViewDesc(
        formTitle="Manage Enrolment Managements (Merged)",
        domainClassLabel="Enrolment Management",    
        imageIcon="enrolment.jpg",
        view=View.class,
        viewType=RegionType.Data,
        layoutBuilderType=MergedLayoutBuilder.class,//TabLayoutBuilder.class,
        topX=0.5,topY=0.0,//widthRatio=0.9f,heightRatio=0.8f,
        parent=RegionName.Tools,
        excludeComponents={
          // general actions
          Export, Print, Chart,
          // object-related actions
          Open, Update, Delete, //New,
          First, Previous, Next, Last, ObjectScroll,
        }
    ),
    controllerDesc=@ControllerDesc(
        controller=Controller.class
        /*customise createNew command to execute the activity model*/
        ,props= {
          @PropertyDesc(name=PropertyName.controller_dataController_new, valueIsClass=ExecActivityCommand.class, valueType=Class.class, valueAsString=CommonConstants.NullString)
        }
    ),
    containmentTree=@CTree(
        root=EnrolmentMgmt.class
        /* v5.3:
        ,subtrees={
          @SubTree1L(
            parent=EnrolmentMgmt.class,
            children={
              @Child(cname=MgEnrolmentProcessing.class,scope={"*"})
            }
          )
          ,@SubTree1L(
            parent=MgEnrolmentProcessing.class,
            children={
              @Child(cname=SClassRegistration.class,scope={},scopeDef=".ScopeDefSClassRegist")
            }
          )
         } */
        , edges = {
            @CEdge(parent=EnrolmentMgmt.class, child=MgEnrolmentProcessing.class, 
                scopeDesc = @ScopeDesc(stateScope = {"*"}))
            ,@CEdge(parent=MgEnrolmentProcessing.class, child=SClassRegistration.class, 
                scopeDesc = @ScopeDesc(stateScope = {"*"}))
        }
    ),
    isPrimary=true,
    childModules={ModuleMgEnrolmentProcessing.class, ModuleEnrolmentClosure.class}
    ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleEnrolmentMgmt {
  @AttributeDesc(label="Enrolment Management")
  private String title;

  // enrolment processing
  @AttributeDesc(label="Enrolment processing"
      ,layoutBuilderType=TabLayoutBuilder.class//TwoColumnLayoutBuilder.class  
  )
  private Collection<MgEnrolmentProcessing> procs;
  
  // enrolment closure
  @AttributeDesc(label="Enrolment closure"
      ,type=DefaultPanel.class
  )
  private Collection<EnrolmentClosure> closures;
  
}
