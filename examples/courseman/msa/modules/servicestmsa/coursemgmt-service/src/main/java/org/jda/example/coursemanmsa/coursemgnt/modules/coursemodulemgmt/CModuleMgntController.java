package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodulemgmt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.CompulsoryModule;
import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.ElectiveModule;
import org.jda.example.coursemanmsa.coursemgnt.modules.teacher.model.Teacher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import jda.modules.msacommon.controller.RedirectController;

/**ducmle: renamed to match path update */
@Controller
//public class CourseModuleMgntController 
public class CModuleMgntController<ID> extends RedirectController<ID>{
	
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