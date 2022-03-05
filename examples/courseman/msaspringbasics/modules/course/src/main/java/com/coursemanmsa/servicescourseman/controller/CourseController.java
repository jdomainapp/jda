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

import com.coursemanmsa.servicescourseman.model.Course;
import com.coursemanmsa.servicescourseman.service.CourseService;

@Controller

public class CourseController {
    @Autowired
    private CourseService service;
    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("SUBJECT",service.findAll());
        return "index";
    }
    @GetMapping("/add")
    public String addcourse(Model model){
        model.addAttribute("SUBJECT",new Course());
        return "/addcourse";
    }
    @PostMapping("/save")
    public String save(@Validated Course course, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "redirect:/";
        }
       service.save(course);
        redirectAttributes.addFlashAttribute("SUCCESS","Save a record successfully!");
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
        return "/editcourse";
    }
    @GetMapping(value = "/search",params = "searchid")
    public String search(@RequestParam("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        if (!service.existsById(id)) {
            redirectAttributes.addFlashAttribute("SUBJECT","No ID in Database");
            return "redirect:/";
        }
        model.addAttribute("SUBJECT", service.findById(id));
        return "index";
    }
}
