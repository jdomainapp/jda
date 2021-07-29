package org.jda.example.courseman.modules;

import org.jda.example.courseman.modulesgen.ModuleMain;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name = "ModuleModuleMain", modelDesc = @ModelDesc(model = ModuleMain.class), viewDesc = @ViewDesc(formTitle = "Module: ModuleMain", imageIcon = "ModuleMain.png", domainClassLabel = "ModuleMain", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleModuleMain {

    @AttributeDesc(label = "ModuleMain")
    private String title;
}
