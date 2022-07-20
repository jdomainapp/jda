package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodulemgmt;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.courseman.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanmsa.common.controller.ControllerRegistry;
import org.jda.example.coursemanmsa.common.controller.DefaultController;
import org.jda.example.coursemanmsa.common.controller.RedirectController;
import org.jda.example.coursemanmsa.common.model.MyResponseEntity;
import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.coursemgnt.modules.teacher.model.Teacher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

/**ducmle: renamed to match path update */
@Controller
//public class CourseModuleMgntController 
public class CModuleMgntController 
extends RedirectController{
	
  /* ducmle: to generalise
	public final static String PATH_TEACHER="/teacher";
	public final static String PATH_COURSEMODULE="/coursemodule";
	*/
  
	private final static Map<String, Class<?>> PathMap = new HashMap<>() {{
      put("/teacher", Teacher.class);
	    put("/coursemodule", CourseModule.class);
  	}};
	
	@Override
	public MyResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String pathPattern) {
		String path = req.getServletPath();
		/* ducmle: use generic code
		if(path.matches("(.*)"+PATH_COURSEMODULE+"(.*)")) {
			return handleCoursemodule(req, res);
		}else if(path.matches("(.*)"+PATH_TEACHER+"(.*)")) {
			return handleTeacher(req, res);
		}else {
			return new MyResponseEntity(ResponseEntity.ok("No method for request URL"), null);
		}
		*/
		for (Entry<String, Class<?>> e : PathMap.entrySet()) {
		  String p = e.getKey(); 
		  Class<?> cls = e.getValue();
      if(path.matches("(.*)"+p+"(.*)")) {
        // handle invocation
        DefaultController<?, Integer> controller = ControllerRegistry.getInstance().get(cls);
        Integer id= null;
        if(path.matches("(.*)"+p+"/(.+)")) {  // id available in path
          String pathVariable = path.substring(path.lastIndexOf("/")+1);
          id = Integer.parseInt(pathVariable);
        }        
        return controller.handleRequest(req, res, id);
      }
    }
		
		// invalid path
    return new MyResponseEntity(ResponseEntity.ok("Invalid path: " + path), null);
	}
	
	/* ducmle: to generalise 
	public MyResponseEntity handleCoursemodule(HttpServletRequest req, HttpServletResponse res){
		DefaultController<Coursemodule, Integer> controller = ControllerRegistry.getInstance().get(Coursemodule.class);
		String path = req.getServletPath();
		Integer id= null;
		if(path.matches("(.*)"+PATH_COURSEMODULE+"/(.+)")) {
			String pathVariable = path.substring(path.lastIndexOf("/")+1);
			id = Integer.parseInt(pathVariable);
		}
		return controller.handleRequest(req, res, id);
	}
	
	public MyResponseEntity handleTeacher(HttpServletRequest req, HttpServletResponse res){
		DefaultController<Teacher, Integer> controller = ControllerRegistry.getInstance().get(Teacher.class);
		String path = req.getServletPath();
		Integer id= null;
		if(path.matches("(.*)"+PATH_TEACHER+"/(.+)")) {
			String pathVariable = path.substring(path.lastIndexOf("/")+1);
			id = Integer.parseInt(pathVariable);
		}
		return controller.handleRequest(req, res, id);
	}
	*/
}