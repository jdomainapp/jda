package org.jda.example.coursemanmsa.assessmenthub.modules.address;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.assessmenthub.controller.AssessmentHubController;
import org.jda.example.coursemanmsa.assessmenthub.modules.address.model.Address;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import jda.modules.msacommon.controller.ControllerTk;
import jda.modules.msacommon.controller.DefaultController;

@Controller
public class AddressController extends DefaultController<Address, Integer>{
	@Override
	public ResponseEntity<?> handleRequest(HttpServletRequest req, HttpServletResponse res) {
		String path = req.getServletPath();
		List<Integer> ids = ControllerTk.findIntegers(path);
		return ControllerTk.isPathContainModule(AssessmentHubController.PATH_ADRESS, path)
				? super.handleRequest(req, res, ids.isEmpty() ? null : ids.get(0))
				: ResponseEntity.badRequest().build();
	}
}