package org.jda.example.courseman.modules;

import java.util.Collection;

import org.jda.example.courseman.model.Address;
import org.jda.example.courseman.model.Enrolment;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;

@ModuleDescriptor(name = "ModuleStudent", 
modelDesc = @ModelDesc(
    model = org.jda.example.courseman.model.Student.class), 
viewDesc = @ViewDesc(formTitle = "Form: Student", imageIcon = "Student.png", 
domainClassLabel = "Student" 
,parent=RegionName.Tools // TODO
,viewType=RegionType.Data // TODO       
,view = jda.mosa.view.View.class), 
controllerDesc = @ControllerDesc())
public class ModuleStudent {

    @AttributeDesc(label = "Student")
    private String title;

    @AttributeDesc(label = "Mã")
    private int id;

    @AttributeDesc(label = "Tên")
    private String name;

    @AttributeDesc(label = "Địa chỉ")
    private Address address;

    @AttributeDesc(label = "Đăng ký môn")
    private Collection<Enrolment> enrolments;
}
