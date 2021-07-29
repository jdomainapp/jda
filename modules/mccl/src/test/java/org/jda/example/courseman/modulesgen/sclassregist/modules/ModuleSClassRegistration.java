package org.jda.example.courseman.modulesgen.sclassregist.modules;

import org.jda.example.courseman.modulesgen.enrolmentmgmt.model.EnrolmentMgmt;
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

@ModuleDescriptor(name = "ModuleSClassRegistration", modelDesc = @ModelDesc(model = SClassRegistration.class), viewDesc = @ViewDesc(formTitle = "Module: SClassRegistration", imageIcon = "SClassRegistration.png", domainClassLabel = "SClassRegistration", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleSClassRegistration {

    @AttributeDesc(label = "SClassRegistration")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "student")
    private Student student;

    @AttributeDesc(label = "sClass")
    private SClass sClass;

    @AttributeDesc(label = "enrolmentMgmt2")
    private EnrolmentMgmt enrolmentMgmt2;
}
