package com.hanu.courseman.backend.controllers.studentclass;

import jda.modules.mosar.backend.base.controllers.DefaultNestedRestfulController;
import com.hanu.courseman.modules.studentclass.model.StudentClass;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import jda.modules.mosar.backend.base.models.Identifier;
import jda.modules.mosar.backend.base.models.PagingModel;
import com.hanu.courseman.modules.student.model.Student;
import jda.modules.mosar.backend.base.models.Page;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController()
@RequestMapping(value = "/student-classes/{id}/students")
@Generated(value = "jda.modules.mosar.software.backend.generators.SourceCodeWebControllerGenerator")
public class StudentClassStudentController extends DefaultNestedRestfulController<com.hanu.courseman.modules.studentclass.model.StudentClass, com.hanu.courseman.modules.student.model.Student> {

    @org.springframework.web.bind.annotation.PostMapping()
    public Student createInner(Identifier arg0, @org.springframework.web.bind.annotation.RequestBody() Student arg1) {
        return super.createInner(arg0, arg1);
    }

    @org.springframework.web.bind.annotation.GetMapping()
    public Page getInnerListByOuterId(Identifier arg0, PagingModel arg1) {
        return super.getInnerListByOuterId(arg0, arg1);
    }

    @Autowired()
    public StudentClassStudentController(jda.modules.mosar.backend.base.websockets.WebSocketHandler arg0) {
        super(arg0);
    }
}
