package org.jda.example.courseman.modulesupdate.enrolmentmgmt;

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
import java.util.List;
import java.util.Set;

import org.jda.example.courseman.modulesref.student.ModuleStudent;
import org.jda.example.courseman.modulesupdate.enrolmentmgmt.model.EnrolmentMgmt;
import org.jda.example.courseman.modulesupdate.helprequest.ModuleHelpRequest;
import org.jda.example.courseman.modulesupdate.helprequest.model.HelpRequest;
import org.jda.example.courseman.modulesupdate.sclassregist.ModuleSClassRegistration;
import org.jda.example.courseman.modulesupdate.sclassregist.model.SClassRegistration;
import org.jda.example.courseman.modulesupdate.student.model.Student;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.containment.CEdge;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.containment.ScopeDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.controller.Controller;
import jda.mosa.controller.assets.datacontroller.command.manyAssoc.CreateObjectAndManyAssociatesDataControllerCommand;
import jda.mosa.controller.assets.datacontroller.command.manyAssoc.UpdateObjectAndManyAssociatesDataControllerCommand;
import jda.mosa.view.View;
import jda.mosa.view.assets.layout.TabLayoutBuilder;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;

@ModuleDescriptor(
    name="ModuleEnrolmentMgmt",
    modelDesc=@ModelDesc(
        model=EnrolmentMgmt.class
    ),
    viewDesc=@ViewDesc(
        formTitle="Manage Enrolment Management",
        domainClassLabel="Enrolment Management",    
        imageIcon="enrolment.jpg",
        view=View.class,
        viewType=RegionType.Data,
        layoutBuilderType=TabLayoutBuilder.class,
        topX=0.5,topY=0.0,//widthRatio=0.9f,heightRatio=0.9f,
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
//        ,props= {
//          @PropertyDesc(name=PropertyName.controller_dataController_new, valueIsClass=CreateAndExecActivityCommand.class, valueType=Class.class, valueAsString=MetaConstants.NullString)
//        }
    ),
    containmentTree=@CTree(
        root=EnrolmentMgmt.class,
            edges={
          // enrolmentmgmt -> student
          @CEdge(
            parent=EnrolmentMgmt.class,
            child=Student.class,
            scopeDesc=@ScopeDesc(stateScope={"id", "name", "helpRequested", "modules"})
          )
         }
    )
    ,isPrimary=true,
    childModules={ModuleStudent.class, ModuleSClassRegistration.class, ModuleHelpRequest.class }
    ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleEnrolmentMgmt_Original {
  @AttributeDesc(label="Enrolment Management")
  private String title;

  // student registration 
  @AttributeDesc(label="Student Registration",
      layoutBuilderType=TwoColumnLayoutBuilder.class
      ,controllerDesc=@ControllerDesc(
          openPolicy=OpenPolicy.I
          // support many-many association with CourseModule
          ,props={
            // custom Create object command: to create {@link Enrolment} from the course modules
            @PropertyDesc(name=PropertyName.controller_dataController_create,
                valueIsClass=CreateObjectAndManyAssociatesDataControllerCommand.class, valueAsString=CommonConstants.NullValue,
                valueType=Class.class),
            // custom Update object command: to update {@link Enrolment} from the course modules
            @PropertyDesc(name=PropertyName.controller_dataController_update,
                valueIsClass=UpdateObjectAndManyAssociatesDataControllerCommand.class, valueAsString=CommonConstants.NullValue,
                valueType=Class.class)
          })
  )
  private Set<Student> students;
  
  // help desk 
  @AttributeDesc(label="Help Request"
      ,type=DefaultPanel.class
  )
  private List<HelpRequest> helpDesks;
  
  // class registration 
  @AttributeDesc(label="Class Registration"
      ,type=DefaultPanel.class
      )
  private Collection<SClassRegistration> sclassRegists;

}
