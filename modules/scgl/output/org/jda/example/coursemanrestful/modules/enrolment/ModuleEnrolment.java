package org.jda.example.coursemanrestful.modules.enrolment;

import java.util.Date;
import jda.modules.common.types.Null;
import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.*;
import jda.modules.mccl.syntax.InputTypes;
import jda.modules.mccl.syntax.JSValidation;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.*;
import jda.mosa.controller.*;
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.mosa.view.assets.panels.DefaultPanel;
import jda.mosa.view.View;
import org.jda.example.coursemanrestful.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanrestful.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanrestful.modules.student.model.Student;

@ModuleDescriptor(
    name = "ModuleEnrolment",
    modelDesc = @ModelDesc(
        model = Enrolment.class
    ),
    viewDesc = @ViewDesc(
        formTitle = "Manage Enrolment",
        domainClassLabel = "Enrolment",
        imageIcon = "enrolment.jpg",
        view = View.class,
        viewType = RegionType.Data,
        parent = RegionName.Tools,
        topX = -1,
        topY = -1,
        widthRatio = -1f,
        heightRatio = -1f,
        children = {
            
        },
        excludeComponents = {
            
        },
        props = {
            
        }
    ),
    controllerDesc = @ControllerDesc(
        controller = Controller.class,
        openPolicy = OpenPolicy.I,
        isDataFieldStateListener = true
    ),
    setUpDesc = @SetUpDesc(
        postSetUp = CopyResourceFilesCommand.class
    ),
    subtypes = {
        
    },
    type = ModuleType.DomainData,
    isPrimary = true
)
public class ModuleEnrolment {
            @AttributeDesc(
        label = "Id",
        alignX = AlignmentX.Center,
        jsValidation = @JSValidation(),
        inputType = InputTypes.Undefined,
        type = Null.class,
        ref = @Select(),
        loadOidWithBoundValue = false,
        displayOidWithBoundValue = false,
        width = -1,
        height = -1,
        isStateEventSource = false,
        id = "␀",
        controllerDesc = @ControllerDesc()
    )
    private int id;
            @AttributeDesc(
        label = "Course Module",
        alignX = AlignmentX.Center,
        jsValidation = @JSValidation(),
        inputType = InputTypes.Undefined,
        type = JComboField.class,
        ref = @Select(
        clazz = CourseModule.class,
        attributes = {
            "code"
        }
    ),
        loadOidWithBoundValue = false,
        displayOidWithBoundValue = false,
        width = 80,
        height = 25,
        isStateEventSource = true,
        id = "␀",
        controllerDesc = @ControllerDesc()
    )
    private CourseModule courseModule;
            @AttributeDesc(
        label = "Date range",
        alignX = AlignmentX.Left,
        jsValidation = @JSValidation(),
        inputType = InputTypes.DateRangeStart,
        type = Null.class,
        ref = @Select(),
        loadOidWithBoundValue = false,
        displayOidWithBoundValue = false,
        width = -1,
        height = -1,
        isStateEventSource = false,
        id = "date_range",
        controllerDesc = @ControllerDesc()
    )
    private Date startDate;
            @AttributeDesc(
        label = "Student",
        alignX = AlignmentX.Left,
        jsValidation = @JSValidation(),
        inputType = InputTypes.Undefined,
        type = Null.class,
        ref = @Select(),
        loadOidWithBoundValue = false,
        displayOidWithBoundValue = false,
        width = -1,
        height = -1,
        isStateEventSource = false,
        id = "␀",
        controllerDesc = @ControllerDesc()
    )
    private Student student;
            @AttributeDesc(
        label = "Date range",
        alignX = AlignmentX.Left,
        jsValidation = @JSValidation(),
        inputType = InputTypes.DateRangeEnd,
        type = Null.class,
        ref = @Select(),
        loadOidWithBoundValue = false,
        displayOidWithBoundValue = false,
        width = -1,
        height = -1,
        isStateEventSource = false,
        id = "date_range",
        controllerDesc = @ControllerDesc()
    )
    private Date endDate;
            @AttributeDesc(
        label = "Exam Mark",
        alignX = AlignmentX.Center,
        jsValidation = @JSValidation(),
        inputType = InputTypes.Undefined,
        type = Null.class,
        ref = @Select(),
        loadOidWithBoundValue = false,
        displayOidWithBoundValue = false,
        width = -1,
        height = -1,
        isStateEventSource = false,
        id = "␀",
        controllerDesc = @ControllerDesc()
    )
    private double examMark;
            @AttributeDesc(
        label = "Form: Compulsory Module",
        alignX = AlignmentX.Left,
        jsValidation = @JSValidation(),
        inputType = InputTypes.Undefined,
        type = Null.class,
        ref = @Select(),
        loadOidWithBoundValue = false,
        displayOidWithBoundValue = false,
        width = -1,
        height = -1,
        isStateEventSource = false,
        id = "␀",
        controllerDesc = @ControllerDesc()
    )
    private String title;
            @AttributeDesc(
        label = "Final Grade",
        alignX = AlignmentX.Center,
        jsValidation = @JSValidation(),
        inputType = InputTypes.Undefined,
        type = Null.class,
        ref = @Select(),
        loadOidWithBoundValue = false,
        displayOidWithBoundValue = false,
        width = -1,
        height = -1,
        isStateEventSource = false,
        id = "␀",
        controllerDesc = @ControllerDesc()
    )
    private char finalGrade;
            @AttributeDesc(
        label = "Internal Mark",
        alignX = AlignmentX.Center,
        jsValidation = @JSValidation(),
        inputType = InputTypes.Undefined,
        type = Null.class,
        ref = @Select(),
        loadOidWithBoundValue = false,
        displayOidWithBoundValue = false,
        width = -1,
        height = -1,
        isStateEventSource = false,
        id = "␀",
        controllerDesc = @ControllerDesc()
    )
    private double internalMark;
}