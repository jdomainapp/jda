package org.jda.example.courseman.modules.authorisation.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jda.example.courseman.modules.authorisation.model.Authorisation.AuthorzStatus;
import org.jda.example.courseman.modules.coursemodule.model.CourseModule;
import org.jda.example.courseman.modules.payment.model.WaitRunnable;
import org.jda.example.courseman.modules.student.model.Student;

/**
 * @overview 
 *  Represents the payment authorisation process. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class EnrolmentAuthorisationProcess {

  private static EnrolmentAuthorisationProcess instance;
  
  private ExecutorService threadMan;

  private WaitRunnable waitRunnable;
  
  private EnrolmentAuthorisationProcess() {
    //
  }
  
  /**
   * @effects 
   *  initialise {@link #instance} if not already, 
   *  return {@link #instance}  
   */
  public static EnrolmentAuthorisationProcess getInstance() {
    if (instance == null)
      instance = new EnrolmentAuthorisationProcess();
    
    return instance;
  }

  /**
   * @effects <pre> 
   *   request the authorisation for <tt>student.modules</tt>.
   *   return result as {@link Map}<String,Object>, whose keys are 
   *    {{@link Authorisation#A_authorDetails}, {@link Authorisation#A_description}, {@link Authorisation#A_status}}
   *   </pre>
   */
  public Map<String, Object> execute(Student student) {
    // TODO: simulate the following authorisation process (e.g. showing the progress on a GUI)
    
    // (1) the enrolled course modules
    Collection<CourseModule> modules = student.getModules();
    
    // (2) contact enrolment office to request for authorisation of the modules
    String desc = "Enrolment authorisation for " + student;
    AuthorzStatus aResult = requestAuthorisation(student, desc, modules);
    
    String details = aResult.equals(AuthorzStatus.ACCEPTED) ? "Permission granted" : "Permission denied";
    
    // (4) return result
    Map<String, Object> result = null;
    if (aResult != null) {
      result = new HashMap<>();
      result.put(Authorisation.A_authorDetails, details);
      result.put(Authorisation.A_description, desc);
      result.put(Authorisation.A_status, aResult);
    }
    
    return result;
  }

  /**
   * @effects 
   *  Request enrolment office to authorise <tt>modules</tt> of <tt>student</tt>, with description 
   *  <tt>desc</tt>. 
   *  Return result as {@link AuthorzStatus}.
   */
  private AuthorzStatus requestAuthorisation(Student student, String desc,
      Collection<CourseModule> modules) {
    // TODO perform actual request here!!!
    // for now just simulate with a wait-thread and return with a random result
    
    if (threadMan == null) threadMan = Executors.newSingleThreadExecutor();
    if (waitRunnable == null) waitRunnable = new WaitRunnable(5,7);
    
    try {
      threadMan.submit(waitRunnable).get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    
    AuthorzStatus result = AuthorzStatus.ACCEPTED;
    
    return result;
  }

}
