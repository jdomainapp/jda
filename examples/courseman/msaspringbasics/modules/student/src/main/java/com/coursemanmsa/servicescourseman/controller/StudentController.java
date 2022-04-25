package com.coursemanmsa.servicescourseman.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coursemanmsa.servicescourseman.model.Student;
import com.coursemanmsa.servicescourseman.service.StudentService;
@Controller
public class StudentController {
    @Autowired
    StudentService service;
//    @Autowired
//    SclassService sclassService;
    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("SUBJECT",service.findAll());
        return "index";
    }

    @GetMapping("/add")
    public String addstudent(Model model, Model model1){
//       model1.addAttribute("SCLASS",sclassService.findAll());
        model.addAttribute("SUBJECT",new Student());
        return "/addstudent";
    }
//    @ModelAttribute(name = "SCLASS")
//    public List<Sclass> getAllSclass(){
//        return sclassService.findAll();
//    }
    @PostMapping("/save")
    public String save(@Validated Student student, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "redirect:/";
        }
        service.save(student);
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
        return "/editstudent";
    }
    @GetMapping(value = "/search",params = "searchid")
    public String search(@RequestParam("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        if (!service.existsById(id)) {
            redirectAttributes.addFlashAttribute("SUBJECT","No ID in Database");
            return "redirect:/";
        }
        model.addAttribute("STUDENT", service.findById(id));
        return "index";
    }
}
