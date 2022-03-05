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

import com.coursemanmsa.servicescourseman.model.Sclass;
import com.coursemanmsa.servicescourseman.service.SclassService;

//@RestController
//@RequestMapping("/")
@Controller
public class SclassController {
    @Autowired
    SclassService service;
//  @GetMapping("class")
   @GetMapping("/")
    public String index(Model model){
        model.addAttribute("SUBJECT",service.findAll());
        return "index";
    }
    @GetMapping("/add")
    public String addsclass(Model model){
        model.addAttribute("SUBJECT",new Sclass());
        return "addsclass";
    }
    @PostMapping("/save")
    public String save(@Validated Sclass sclass, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "redirect:/";
        }
        service.save(sclass);
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
        return "/editsclass";
    }
    @GetMapping(value = "/search",params = "searchid")
    public String search(@RequestParam("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        if (!service.existsById(id)) {
            redirectAttributes.addFlashAttribute("SUBJECT","No ID in Database");
            return "redirect:/";
        }
        model.addAttribute("sclass", service.findById(id));
        return "index";
    }
}
