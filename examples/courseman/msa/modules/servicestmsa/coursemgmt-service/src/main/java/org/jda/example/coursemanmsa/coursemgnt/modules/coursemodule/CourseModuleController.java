package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule;

import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.CourseModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import jda.modules.msacommon.controller.DefaultController;

@Controller
public class CourseModuleController extends DefaultController<CourseModule, Integer>{

}