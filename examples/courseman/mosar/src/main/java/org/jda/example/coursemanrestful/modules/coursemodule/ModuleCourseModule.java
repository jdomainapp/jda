package org.jda.example.coursemanrestful.modules.coursemodule;

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
import jda.mosa.view.View;
import org.jda.example.coursemanrestful.modules.coursemodule.model.CourseModule;

@ModuleDescriptor(
    name = "ModuleCourseModule",
    modelDesc = @ModelDesc(
        model = CourseModule.class
    ),
    viewDesc = @ViewDesc(
        formTitle = "Manage Course Modules",
        domainClassLabel = "Course Module",
        imageIcon = "coursemodule.jpg",
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
        isDataFieldStateListener = false
    ),
    setUpDesc = @SetUpDesc(
        postSetUp = CopyResourceFilesCommand.class
    ),
    subtypes = {
        ModuleCompulsoryModule.class,
		ModuleElectiveModule.class
    },
    type = ModuleType.DomainData,
    isPrimary = true
)
public class ModuleCourseModule {
            @AttributeDesc(
        label = "Name",
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
    private String name;
            @AttributeDesc(
        label = "Rating",
        alignX = AlignmentX.Left,
        jsValidation = @JSValidation(),
        inputType = InputTypes.Rating,
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
    private int rating;
            @AttributeDesc(
        label = "Semester",
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
    private int semester;
            @AttributeDesc(
        label = "Credits",
        alignX = AlignmentX.Center,
        jsValidation = @JSValidation(
        optional = false,
        regex = "/^\\d+$/",
        invalidMsg = "Credits must be a number or a float number!"
    ),
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
    private int credits;
            @AttributeDesc(
        label = "Code",
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
    private String code;
            @AttributeDesc(
        label = "Cost",
        alignX = AlignmentX.Left,
        jsValidation = @JSValidation(),
        inputType = InputTypes.Slider,
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
    private int cost;
            @AttributeDesc(
        label = "Description",
        alignX = AlignmentX.Left,
        jsValidation = @JSValidation(
        optional = true,
        regex = "/^[A-Za-z\\s]$/",
        invalidMsg = "Description must only include characters!"
    ),
        inputType = InputTypes.TextArea,
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
    private String description;
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
        label = "Student Class",
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
}