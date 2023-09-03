package org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.coursemodulemgmt;

import jda.modules.msacommon.controller.RedirectController;
import org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.coursemodulemgmt.modules.coursemodule.model.CompulsoryModule;
import org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.coursemodulemgmt.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.coursemodulemgmt.modules.coursemodule.model.ElectiveModule;
import org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.coursemodulemgmt.modules.teacher.model.Teacher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ducmle: renamed to match path update
 */
@Controller
//public class CourseModuleMgntController 
public class CModuleMgntController<ID> extends RedirectController<ID> {

  public CModuleMgntController() {
    super();
    getPathmap().put("/teacher", Teacher.class);
    getPathmap().put("/coursemodule", CourseModule.class);
    getPathmap().put("/electivemodule", ElectiveModule.class);
    getPathmap().put("/compulsorymodule", CompulsoryModule.class);
  }

  @Override
  public ResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String parentElement) {
    return super.handleRequest(req, res, parentElement);
  }
}