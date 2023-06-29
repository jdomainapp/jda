package org.jda.example.coursemanmsa.coursemgnt.modules.student;

import org.jda.example.coursemanmsa.coursemgnt.modules.student.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import jda.modules.msacommon.controller.DefaultController;

@Controller
public class StudentController extends DefaultController<Student, String>{
	
}