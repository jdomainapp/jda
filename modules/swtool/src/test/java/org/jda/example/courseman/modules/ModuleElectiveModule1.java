package org.jda.example.courseman.modules;

import org.jda.example.courseman.modules.ModuleCourseModule1;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.courseman.model2.ElectiveModule1;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;

@ModuleDescriptor(name = "ModuleElectiveModule1", modelDesc = @ModelDesc(model = ElectiveModule1.class), viewDesc = @ViewDesc(formTitle = "Module: ElectiveModule1", imageIcon = "ElectiveModule1.png", domainClassLabel = "ElectiveModule1", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleElectiveModule1 extends ModuleCourseModule1 {

    @AttributeDesc(label = "ElectiveModule1")
    private String title;

    @AttributeDesc(label = "deptName")
    private String deptName;
}
