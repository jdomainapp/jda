package org.jda.example.coursemansw.services.studentclass;

import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.coursemansw.services.studentclass.model.StudentClass;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import java.util.Collection;
import org.jda.example.coursemansw.services.student.model.Student;

@ModuleDescriptor(name = "ModuleStudentClass", modelDesc = @ModelDesc(model = StudentClass.class), viewDesc = @ViewDesc(formTitle = "Module: StudentClass", imageIcon = "StudentClass.png", domainClassLabel = "StudentClass", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleStudentClass {

    @AttributeDesc(label = "StudentClass")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "name")
    private String name;

    @AttributeDesc(label = "students")
    private Collection<Student> students;
}
