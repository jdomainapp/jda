package org.jda.example.coursemanswref.modules.address;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import org.jda.example.coursemanswref.modules.address.model.Address;
import org.jda.example.coursemanswref.modules.student.model.Student;


@ModuleDescriptor(
        name = "ModuleAddress",
        modelDesc = @ModelDesc(model = Address.class),
        viewDesc = @ViewDesc(
            formTitle = "Form: Address",
            imageIcon = "Address.png",
            domainClassLabel = "Address",
            viewType= RegionType.Data,
            view= View.class,
            parent= RegionName.Tools
        ),
        controllerDesc = @ControllerDesc(
            controller= Controller.class,
            isDataFieldStateListener=true
        )
        ,isPrimary = true
        ,setUpDesc = @SetUpDesc(postSetUp = CopyResourceFilesCommand.class)
)
public class ModuleAddress {

    @AttributeDesc(label = "Form: Address")
    private String title;

    @AttributeDesc(label = "ID")
    private int id;

    @AttributeDesc(label = "City name")
    private String name;

    @AttributeDesc(label = "Student"
        ,isStateEventSource=true)
    private Student student;
}
