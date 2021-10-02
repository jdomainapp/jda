package org.jda.example.courseman.swclasses.cls2.modules.enrolmentmgmt;

import java.util.Collection;

import org.jda.example.courseman.modules.enrolment.model.Enrolment;
import org.jda.example.courseman.modules.enrolmentmgmt.sequential.model.EnrolmentMgmt;
import org.jda.example.courseman.modules.sclassregist.model.SClassRegistration;
import org.jda.example.courseman.modules.student.model.Student;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.mbsl.controller.command.ExecActivityCommand;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.containment.CEdge;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.containment.ScopeDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;

@ModuleDescriptor(
    name="ModuleEnrolmentMgmt",
    modelDesc=@ModelDesc(
        model=EnrolmentMgmt.class
    ),
    viewDesc=@ViewDesc(
        formTitle="Module: EnrolmentMgmt",
        domainClassLabel="EnrolmentMgmt",    
        imageIcon="enrolmentmgmt.jpg",
        view=View.class,
        viewType=RegionType.Data,
        parent=RegionName.Tools
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
        ,edges= {
            // enrolmentmgmt -> student
            @CEdge( parent = EnrolmentMgmt.class, child = Student.class,
                scopeDesc=@ScopeDesc(stateScope={"id", "name", "enrolments"})
                )
            // student -> enrolment
            ,@CEdge( parent = Student.class, child = Enrolment.class,
                scopeDesc=@ScopeDesc(
                    controllerDesc=@ControllerDesc(openPolicy=OpenPolicy.O_A), // SW2: controller config customisation
                    stateScope={"student", "module"}
                    )
            )
            // enrolmentmgmt -> sclassRegists
            ,@CEdge( parent = EnrolmentMgmt.class, child = SClassRegistration.class,
                scopeDesc=@ScopeDesc(
                    stateScope= {"*"}
                    ,controllerDesc=@ControllerDesc(openPolicy=OpenPolicy.O)) // SW2: controller config customisation 
                )
        }        
    )
    ,isPrimary=true
//    childModules={ModuleStudent.class, ModuleSClassRegistration.class }
//    ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleEnrolmentMgmt {
  @AttributeDesc(label="EnrolmentMgmt")
  private String title;

  // student registration 
  @AttributeDesc(label="students")
  private Collection<Student> students;
  
  // class registration 
  @AttributeDesc(label="sclassRegists")
  private Collection<SClassRegistration> sclassRegists;
}
