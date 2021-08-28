package org.jda.example.courseman.modules;

import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.courseman.model2.Address1;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import org.jda.example.courseman.model2.Student1;

@ModuleDescriptor(name = "ModuleAddress1", modelDesc = @ModelDesc(model = Address1.class), viewDesc = @ViewDesc(formTitle = "Module: Address1", imageIcon = "Address1.png", domainClassLabel = "Address1", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleAddress1 {

    @AttributeDesc(label = "Address1")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "cityName")
    private String cityName;

    @AttributeDesc(label = "student")
    private Student1 student;
}
