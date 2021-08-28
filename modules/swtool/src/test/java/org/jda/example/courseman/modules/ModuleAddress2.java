package org.jda.example.courseman.modules;

import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.courseman.model2.Address2;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import org.jda.example.courseman.model2.Student2;

@ModuleDescriptor(name = "ModuleAddress2", modelDesc = @ModelDesc(model = Address2.class), viewDesc = @ViewDesc(formTitle = "Module: Address2", imageIcon = "Address2.png", domainClassLabel = "Address2", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleAddress2 {

    @AttributeDesc(label = "Address2")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "cityName")
    private String cityName;

    @AttributeDesc(label = "student")
    private Student2 student;
}
