package org.jda.example.coursemanrestful.backend.controllers.student;

import jda.modules.mosar.backend.base.controllers.DefaultNestedRestfulController;
import org.jda.example.coursemanrestful.modules.student.model.Student;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import jda.modules.mosar.backend.base.models.Identifier;
import jda.modules.mosar.backend.base.models.PagingModel;
import org.jda.example.coursemanrestful.modules.enrolment.model.Enrolment;
import jda.modules.mosar.backend.base.models.Page;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController()
@RequestMapping(value = "/students/{id}/enrolments")
@Generated(value = "jda.modules.mosar.software.backend.generators.SourceCodeWebControllerGenerator")
public class StudentEnrolmentController extends DefaultNestedRestfulController<org.jda.example.coursemanrestful.modules.student.model.Student, org.jda.example.coursemanrestful.modules.enrolment.model.Enrolment> {

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
