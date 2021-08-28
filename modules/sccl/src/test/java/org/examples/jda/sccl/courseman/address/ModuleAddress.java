package org.examples.jda.sccl.courseman.address;

import org.examples.jda.sccl.courseman.student.model.Student;

import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;

@ModuleDescriptor(name = "ModuleAddress", modelDesc = @jda.modules.mccl.syntax.model.ModelDesc(model = org.examples.jda.sccl.courseman.address.model.Address.class), viewDesc = @jda.modules.mccl.syntax.view.ViewDesc(formTitle = "Form: Address", imageIcon = "Address.png", domainClassLabel = "Address", view = jda.mosa.view.View.class), controllerDesc = @jda.modules.mccl.syntax.controller.ControllerDesc())
public class ModuleAddress {

    @AttributeDesc(label = "title")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "cityName")
    private String cityName;

    @AttributeDesc(label = "student")
    private Student student;
}
