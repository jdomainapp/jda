package org.jda.example.coursemanmsa.student.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.student.model.Student;
import org.jda.example.coursemanmsa.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="v1/student/")
public class StudentController {

	@Autowired
	private StudentService studentService;

	@RequestMapping(value="/{studentId}",method = RequestMethod.GET)
	public ResponseEntity<Student> getStudent( @PathVariable("studentId") String studentId) throws TimeoutException {

		Student student = studentService.getStudent(studentId);
		student.add( 
				linkTo(methodOn(StudentController.class).getStudent(studentId)).withSelfRel()
				);

		return ResponseEntity.ok(student);
	}

	@RequestMapping(value="/",method = RequestMethod.GET)
	public List<Student> getStudents(){
		return studentService.getStudents();
	}

}
