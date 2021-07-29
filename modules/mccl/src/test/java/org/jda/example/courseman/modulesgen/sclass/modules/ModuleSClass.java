package org.jda.example.courseman.modulesgen.sclass.modules;

import java.util.Collection;

import org.jda.example.courseman.modulesgen.sclass.model.SClass;
import org.jda.example.courseman.modulesgen.sclassregist.model.SClassRegistration;
import org.jda.example.courseman.modulesgen.student.model.Student;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name = "ModuleSClass", modelDesc = @ModelDesc(model = SClass.class), viewDesc = @ViewDesc(formTitle = "Module: SClass", imageIcon = "SClass.png", domainClassLabel = "SClass", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleSClass {

    @AttributeDesc(label = "SClass")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "name")
    private String name;

    @AttributeDesc(label = "students")
    private Collection<Student> students;

    @AttributeDesc(label = "classRegists")
    private Collection<SClassRegistration> classRegists;
}
