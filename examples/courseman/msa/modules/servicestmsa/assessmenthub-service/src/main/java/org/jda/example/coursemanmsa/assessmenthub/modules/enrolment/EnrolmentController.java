package org.jda.example.coursemanmsa.assessmenthub.modules.enrolment;

import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model.Enrolment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import jda.modules.msacommon.controller.DefaultController;

@Controller
public class EnrolmentController extends DefaultController<Enrolment, Integer>{

}
