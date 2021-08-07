package com.hanu.courseman.backend.controllers.enrolment;

import jda.modules.restfstool.backend.base.controllers.DefaultRestfulController;
import com.hanu.courseman.modules.enrolment.model.Enrolment;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import jda.modules.restfstool.backend.base.models.Identifier;
import jda.modules.restfstool.backend.base.models.PagingModel;
import jda.modules.restfstool.backend.base.models.Page;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController()
@RequestMapping(value = "/enrolments")
@Generated(value = "jda.modules.restfstool.backend.generators.SourceCodeWebControllerGenerator")
public class EnrolmentController extends DefaultRestfulController<com.hanu.courseman.modules.enrolment.model.Enrolment> {

    @org.springframework.web.bind.annotation.PostMapping()
    public Enrolment createEntity(@org.springframework.web.bind.annotation.RequestBody() Enrolment arg0) {
        return super.createEntity(arg0);
    }

    @org.springframework.web.bind.annotation.GetMapping()
    public Page getEntityListByPage(PagingModel arg0) {
        return super.getEntityListByPage(arg0);
    }

    @org.springframework.web.bind.annotation.GetMapping(value = "/{id}")
    public Enrolment getEntityById(Identifier arg0) {
        return super.getEntityById(arg0);
    }

    @org.springframework.web.bind.annotation.PatchMapping(value = "/{id}")
    public Enrolment updateEntity(Identifier arg0, @org.springframework.web.bind.annotation.RequestBody() Enrolment arg1) {
        return super.updateEntity(arg0, arg1);
    }

    @org.springframework.web.bind.annotation.DeleteMapping(value = "/{id}")
    public void deleteEntityById(Identifier arg0) {
        super.deleteEntityById(arg0);
    }

    @Autowired()
    public EnrolmentController(jda.modules.restfstool.backend.base.websockets.WebSocketHandler arg0) {
        super(arg0);
    }
}
