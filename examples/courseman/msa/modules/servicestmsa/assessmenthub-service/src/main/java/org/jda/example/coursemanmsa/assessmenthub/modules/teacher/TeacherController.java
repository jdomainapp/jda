package org.jda.example.coursemanmsa.assessmenthub.modules.teacher;

import org.jda.example.coursemanmsa.assessmenthub.modules.teacher.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import jda.modules.msacommon.controller.DefaultController;

@Controller
public class TeacherController extends DefaultController<Teacher, Integer>{

}
