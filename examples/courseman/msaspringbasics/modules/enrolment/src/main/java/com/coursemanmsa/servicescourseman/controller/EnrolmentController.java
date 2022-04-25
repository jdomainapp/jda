package com.coursemanmsa.servicescourseman.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coursemanmsa.servicescourseman.model.Erolment;
import com.coursemanmsa.servicescourseman.service.EnrolmentService;

@Controller

public class EnrolmentController {
    @Autowired
    EnrolmentService service;
 //   @Autowired
//    StudentService studentService;
//    @Autowired
//    CourseService courseService;
    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("SUBJECT",service.findAll());
        return "index";
    }

    @GetMapping("/add")
    public String addenrolment(Model model, Model model1){
//       model1.addAttribute("SCLASS",sclassService.findAll());
        model.addAttribute("SUBJECT",new Erolment());
        return "/addenrolment";
    }
//    @ModelAttribute(name = "COURSE")
//    public List<Course> getAllCourse(){
//        return courseService.findAll();
//    }
//    @ModelAttribute(name = "STUDENT")
//    public List<Student> getAllStudent(){
//        return studentService.findAll();
//    }
    @PostMapping("/save")
    public String save(@Validated Erolment erolment, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "redirect:/";
        }
        service.save(erolment);
        redirectAttributes.addFlashAttribute("SUBJECT","Save a record successfully!");
        return "redirect:/";
    }
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes){
        service.deleteById(id.intValue());
        redirectAttributes.addFlashAttribute("SUBJECT","Delete a record succsessfully!");
        return "redirect:/";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable int id, Model model) {
        model.addAttribute("SUBJECT", service.findById(id));
        return "/editenrolment";
    }
    @GetMapping("/search")
    public String search(Model model){

//                         @RequestParam(name = "name",required = false)String s) {
        List<Erolment> list =null;
//        if(StringUtils.hasText(s)){
//            list=service.findErolmentByIdstudentContaining(s);
//            list=service.findErolmentByIdstudent(s);
//            list =service.findByIdstudentLike(s);
//        }
//        else {
            list=service.findAll();

//        }
        model.addAttribute("SUBJECT", list);
        return "index";
    }

}
