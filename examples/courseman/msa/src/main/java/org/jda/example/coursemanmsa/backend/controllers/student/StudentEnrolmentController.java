package org.jda.example.coursemanmsa.backend.controllers.student;

import jda.modules.mosar.backend.base.controllers.DefaultNestedRestfulController;

import javax.annotation.Generated;

import org.jda.example.coursemanmsa.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.modules.student.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import jda.modules.mosar.backend.base.models.Identifier;
import jda.modules.mosar.backend.base.models.PagingModel;
import jda.modules.mosar.backend.base.models.Page;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController()
@RequestMapping(value = "/students/{id}/enrolments")
@Generated(value = "jda.modules.mosar.software.backend.generators.SourceCodeWebControllerGenerator")
public class StudentEnrolmentController extends DefaultNestedRestfulController<org.jda.example.coursemanmsa.modules.student.model.Student, org.jda.example.coursemanmsa.modules.enrolment.model.Enrolment> {

    @org.springframework.web.bind.annotation.PostMapping()
    public Enrolment createInner(Identifier arg0, @org.springframework.web.bind.annotation.RequestBody() Enrolment arg1) {
        return super.createInner(arg0, arg1);
    }

    @org.springframework.web.bind.annotation.GetMapping()
    public Page getInnerListByOuterId(Identifier arg0, PagingModel arg1) {
        return super.getInnerListByOuterId(arg0, arg1);
    }

    @Autowired()
    public StudentEnrolmentController(jda.modules.mosar.backend.base.websockets.WebSocketHandler arg0) {
        super(arg0);
    }
}
