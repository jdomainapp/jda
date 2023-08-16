package org.jda.eg.coursemanmsa.reconfigurer.controller;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.msacommon.msatool.ServiceReconfigurerController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(value="/")
public class MyServiceReconfigurerController extends ServiceReconfigurerController {

  @Autowired
  RestTemplate restTemplate;

  @Autowired
  private Environment environment;

  /**
   * application property values
   */
  @Value("#{${listOfServices}}")
  private List<String> listOfServices;

  @Value("#{${listOfReconfigurers}}")
  private List<String> listOfReconfigurers;

//  @Value("${path.service.deploy}")
//  private String serviceDeployPath;

  @RequestMapping("/")
  public ResponseEntity<?> main(HttpServletRequest req, HttpServletResponse res) throws Exception {
    return ResponseEntity.ok(MyServiceReconfigurerController.class.getSimpleName() + ": main: success");
  }

  @Override
  protected String lookUpReconfigurer(String sourceServ)  throws NotFoundException {
    // todo: request sourceServ to return the URL of its service-reconfigurer
    // for now, assume there is a pre-defined mapping of the application
    for (int i = 0; i < listOfServices.size(); i++) {
      if (listOfServices.get(i).equals(sourceServ))
        return listOfReconfigurers.get(i);
    }

    // Error: not found
    throw new NotFoundException(NotFoundException.Code.MODULE_NOT_FOUND, new String[] {sourceServ});
  }

  @Override
  protected Environment getApplicationContextEnv() throws NotPossibleException {
    if (environment == null)
      throw new NotPossibleException(NotPossibleException.Code.NULL_POINTER_EXCEPTION, new String[] {Environment.class.getSimpleName(), "Not available"});

    return environment;
  }
}
