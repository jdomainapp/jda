package org.jda.example.courseman.modules;

import org.jda.example.courseman.modules.ModuleCourseModule2;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.courseman.model2.ElectiveModule2;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;

@ModuleDescriptor(name = "ModuleElectiveModule2", modelDesc = @ModelDesc(model = ElectiveModule2.class), viewDesc = @ViewDesc(formTitle = "Module: ElectiveModule2", imageIcon = "ElectiveModule2.png", domainClassLabel = "ElectiveModule2", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleElectiveModule2 extends ModuleCourseModule2 {

    @AttributeDesc(label = "ElectiveModule2")
    private String title;

    @AttributeDesc(label = "deptName")
    private String deptName;
}
