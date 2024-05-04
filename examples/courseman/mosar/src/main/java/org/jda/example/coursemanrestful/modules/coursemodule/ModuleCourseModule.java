package org.jda.example.coursemanrestful.modules.coursemodule;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.InputTypes;
import jda.modules.mccl.syntax.JSValidation;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.mosarfrontend.common.utils.RegexUtils;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import org.jda.example.coursemanrestful.modules.coursemodule.model.CourseModule;

/**
 * @author dmle
 * @Overview Module for {@link CourseModule}
 */
@ModuleDescriptor(name = "ModuleCourseModule",
        subtypes = {
                ModuleCompulsoryModule.class,
                ModuleElectiveModule.class
        },
        modelDesc = @ModelDesc(
                model = CourseModule.class
        ),
        viewDesc = @ViewDesc(
                formTitle = "Manage Course Modules",
                domainClassLabel = "Course Module",
                imageIcon = "coursemodule.jpg",
                viewType = RegionType.Data,
                view = View.class,
                parent = RegionName.Tools
        ),
        controllerDesc = @ControllerDesc(controller = Controller.class),
        isPrimary = true
        , setUpDesc = @SetUpDesc(postSetUp = CopyResourceFilesCommand.class)
)
public class ModuleCourseModule {
    @AttributeDesc(label = "Form: Course Module")
    private String title;

    // attributes
    @AttributeDesc(label = "Id", alignX = AlignmentX.Center)
    private int id;

    @AttributeDesc(label = "Code", alignX = AlignmentX.Center)
    private String code;

    @AttributeDesc(label = "Name", jsValidation = @JSValidation(regex = "/^S\\d+$/", invalidMsg = "Name must start with 'S' and followed by one or more numbers!"))
    private String name;

    @AttributeDesc(label = "Description", inputType = InputTypes.TextArea, jsValidation = @JSValidation(optional = true,regex = "/^[A-Za-z\\s]$/", invalidMsg = "Description must only include characters!"))
    private String description;

    @AttributeDesc(label = "Semester", alignX = AlignmentX.Center)
    private int semester;

    @AttributeDesc(label = "Cost", inputType = InputTypes.Slider)
    private int cost;

    @AttributeDesc(label = "Credits", alignX = AlignmentX.Center, jsValidation = @JSValidation(regex = "/^\\d+$/", invalidMsg = "Name must be a number or a float number!"))
    private int credits;

    @AttributeDesc(label = "Rating", inputType = InputTypes.Rating)
    private int rating;

}