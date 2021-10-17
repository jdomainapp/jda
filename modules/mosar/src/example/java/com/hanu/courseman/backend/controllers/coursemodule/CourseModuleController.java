package com.hanu.courseman.backend.controllers.coursemodule;

import jda.modules.mosar.backend.base.controllers.DefaultRestfulWithInheritanceController;
import com.hanu.courseman.modules.coursemodule.model.CourseModule;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import jda.modules.mosar.backend.base.models.Identifier;
import jda.modules.mosar.backend.base.models.PagingModel;
import jda.modules.mosar.backend.base.models.Page;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController()
@RequestMapping(value = "/course-modules")
@Generated(value = "jda.modules.mosar.software.backend.generators.SourceCodeWebControllerGenerator")
public class CourseModuleController extends DefaultRestfulWithInheritanceController<com.hanu.courseman.modules.coursemodule.model.CourseModule> {

    public Page getEntityListByPage(PagingModel arg0) {
        return super.getEntityListByPage(arg0);
    }

    @org.springframework.web.bind.annotation.GetMapping()
    public Page getEntityListByTypeAndPage(@org.springframework.web.bind.annotation.RequestParam(value = "type", required = false) String arg0, PagingModel arg1) {
        return super.getEntityListByTypeAndPage(arg0, arg1);
    }

    @org.springframework.web.bind.annotation.PostMapping()
    public CourseModule createEntity(@org.springframework.web.bind.annotation.RequestBody() CourseModule arg0) {
        return super.createEntity(arg0);
    }

    @org.springframework.web.bind.annotation.GetMapping(value = "/{id}")
    public CourseModule getEntityById(Identifier arg0) {
        return super.getEntityById(arg0);
    }

    @org.springframework.web.bind.annotation.PatchMapping(value = "/{id}")
    public CourseModule updateEntity(Identifier arg0, @org.springframework.web.bind.annotation.RequestBody() CourseModule arg1) {
        return super.updateEntity(arg0, arg1);
    }

    @org.springframework.web.bind.annotation.DeleteMapping(value = "/{id}")
    public void deleteEntityById(Identifier arg0) {
        super.deleteEntityById(arg0);
    }

    @Autowired()
    public CourseModuleController(jda.modules.mosar.backend.base.websockets.WebSocketHandler arg0) {
        super(arg0);
    }
}
