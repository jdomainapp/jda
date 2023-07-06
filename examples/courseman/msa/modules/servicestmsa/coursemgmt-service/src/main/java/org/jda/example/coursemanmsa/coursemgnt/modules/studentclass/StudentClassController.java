package org.jda.example.coursemanmsa.coursemgnt.modules.studentclass;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.coursemgnt.controller.CourseMgntController;
import org.jda.example.coursemanmsa.coursemgnt.modules.studentclass.model.StudentClass;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import jda.modules.msacommon.controller.ControllerTk;
import jda.modules.msacommon.controller.DefaultController;

@Controller
public class StudentClassController extends DefaultController<StudentClass, Integer>{
	@Override
	public ResponseEntity<?> handleRequest(HttpServletRequest req, HttpServletResponse res) {
		String path = req.getServletPath();
		List<Integer> ids = ControllerTk.findIntegers(path);
		return ControllerTk.isPathContainModule(CourseMgntController.PATH_CLASS, path)
				? super.handleRequest(req, res, ids.isEmpty() ? null : ids.get(0))
				: ResponseEntity.badRequest().build();
	}
}
