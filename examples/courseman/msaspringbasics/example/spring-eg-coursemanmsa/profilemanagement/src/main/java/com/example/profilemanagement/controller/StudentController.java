package com.example.profilemanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.profilemanagement.entity.Student;
import com.example.profilemanagement.repository.StudentRepository;

@RestController
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @RequestMapping("/student-add")
    public ModelAndView saveEmployeeProfile() {
    	ModelAndView modelAndView = new ModelAndView();
	    modelAndView.setViewName("student-add");
	    modelAndView.addObject("student", new Student());
		return modelAndView;
    }
    
    @RequestMapping("/addStudent")
	public ModelAndView  doAddStudent(@ModelAttribute("student") Student student) {
    	studentRepository.save(student);
		ModelAndView modelAndView = new ModelAndView();
	    modelAndView.setViewName("student-list");
	    modelAndView.addObject("studentList", studentRepository.findAll());
		return modelAndView;
	}

    @RequestMapping(value={"/", "/student-list"})
    public ModelAndView getAllEmployee() {
    	ModelAndView modelAndView = new ModelAndView();
	    modelAndView.setViewName("student-list");
	    modelAndView.addObject("studentList", studentRepository.findAll());
		return modelAndView;
    }
}
