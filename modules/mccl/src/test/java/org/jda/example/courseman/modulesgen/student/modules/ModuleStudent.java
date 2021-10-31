package org.jda.example.courseman.modulesgen.student.modules;

import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.courseman.modulesgen.student.model.Student;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import java.util.Collection;
import org.jda.example.courseman.modulesgen.sclass.model.SClass;
import org.jda.example.courseman.modulesgen.sclassregist.model.SClassRegistration;
import org.jda.example.courseman.modulesgen.coursemodule.model.CourseModule;
import org.jda.example.courseman.modulesgen.enrolment.model.Enrolment;
import org.jda.example.courseman.modulesgen.enrolmentmgmt.model.EnrolmentMgmt;

@ModuleDescriptor(name = "ModuleStudent", modelDesc = @ModelDesc(model = Student.class), viewDesc = @ViewDesc(formTitle = "Module: Student", imageIcon = "Student.png", domainClassLabel = "Student", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleStudent {

    @AttributeDesc(label = "Student")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "name")
    private String name;

    @AttributeDesc(label = "helpRequested")
    private boolean helpRequested;

    @AttributeDesc(label = "sclasses")
    private Collection<SClass> sclasses;

    @AttributeDesc(label = "classRegists")
    private Collection<SClassRegistration> classRegists;

    @AttributeDesc(label = "modules")
    private Collection<CourseModule> modules;

    @AttributeDesc(label = "enrolments")
    private Collection<Enrolment> enrolments;

    @AttributeDesc(label = "enrolmentMgmt2")
    private EnrolmentMgmt enrolmentMgmt2;
}
