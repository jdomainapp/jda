package org.coursemanmdsa.software.services.address.modules.address;

import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import org.coursemanmdsa.models.Address;
import org.coursemanmdsa.models.Student;


@ModuleDescriptor(
        name = "address",
        modelDesc = @ModelDesc(model = Address.class),
        viewDesc = @ViewDesc(formTitle = "Form: Address", imageIcon = "Address.png", domainClassLabel = "Address", view = jda.mosa.view.View.class), controllerDesc = @jda.modules.mccl.syntax.controller.ControllerDesc())
public class ModuleAddress {

    @AttributeDesc(label = "Form: Address")
    private String title;

    @AttributeDesc(label = "ID")
    private int id;

    @AttributeDesc(label = "City name")
    private String name;

    @AttributeDesc(label = "Student")
    private Student student;
}
