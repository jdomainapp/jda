package org.jda.example.courseman.modulesgen.helprequest.modules;

import org.jda.example.courseman.modulesgen.enrolmentmgmt.model.EnrolmentMgmt;
import org.jda.example.courseman.modulesgen.helprequest.model.HelpRequest;
import org.jda.example.courseman.modulesgen.student.model.Student;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name = "ModuleHelpRequest", modelDesc = @ModelDesc(model = HelpRequest.class), viewDesc = @ViewDesc(formTitle = "Module: HelpRequest", imageIcon = "HelpRequest.png", domainClassLabel = "HelpRequest", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleHelpRequest {

    @AttributeDesc(label = "HelpRequest")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "student")
    private Student student;

    @AttributeDesc(label = "content")
    private String content;

    @AttributeDesc(label = "enrolmentMgmt")
    private EnrolmentMgmt enrolmentMgmt;
}
