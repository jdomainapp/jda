package org.jda.example.courseman.modules.payment.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jda.example.courseman.modules.coursemodule.model.CourseModule;
import org.jda.example.courseman.modules.payment.model.Payment.PaymentStatus;
import org.jda.example.courseman.modules.student.model.Student;

/**
 * @overview 
 *  Represents a process of requesting a designated bank of a a {@link Student} to perform 
 *  a {@link Payment}.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class EnrolmentPaymentProcess {

  private static final int MAX_FEE_PER_MODULE = 10^6;

  private static final PaymentProfile Fixed_Payment_Profile = 
      new PaymentProfile("Bank of Vietnam", "123456789");
  
  private static EnrolmentPaymentProcess instance;
  
  private ExecutorService threadMan;

  private WaitRunnable waitRunnable;
  
  /**
   * @effects 
   *  initialise {@link #instance} is not already, and 
   *  return {@link #instance}
   */
  public static EnrolmentPaymentProcess getInstance() {
    if (instance == null) {
      instance = new EnrolmentPaymentProcess();
    }
    
    return instance;
  }

  /**
   * @effects <pre> 
   *   let amount be computed from the course modules in <tt>student.enrolments</tt>.
   *   request the designated bank of <tt>student</tt> to perform a payment for <tt>amount</tt>.
   *   return result as {@link Map}<String,Object>, whose keys are 
   *    {{@link Payment#A_payDetails}, {@link Payment#A_description}, {@link Payment#A_status}}
   *   </pre>
   */
  public Map<String, Object> execute(Student student) {
    // TODO: simulate the following payment process (e.g. showing the progress on a GUI)
    
    // (1) compute payment amount from the enrolled course modules
    Collection<CourseModule> modules = student.getModules();
    double amount = computePaymentByCourseModules(modules);
    
    // (2) look up the payment details of student
    PaymentProfile payProf = lookUpPaymentProfile(student);
    
    // (3) contact the bank to request for payment of the specified amount
    String desc = "Enrolment payment for " + student;
    PaymentStatus payResult = requestPayment(payProf, desc, amount);
    
    // (4) return result
    Map<String, Object> result = null;
    if (payResult != null) {
      result = new HashMap<>();
      result.put(Payment.A_payDetails, payProf.getDetailsAsString());
      result.put(Payment.A_description, desc);
      result.put(Payment.A_status, payResult);
    }
    
    return result;
  }

  /**
   * @effects 
   *  Request bank in <tt>payProf</tt> to perform payment for <tt>amount</tt>, with payment description 
   *  <tt>desc</tt>. 
   *  Return result as {@link PaymentStatus}.
   *    
   */
  private PaymentStatus requestPayment(PaymentProfile payProf, String desc,
      double amount) {
    // TODO perform electronic bank request here!!!
    // for now just simulate with a wait-thread and return with a random result
    
    if (threadMan == null) threadMan = Executors.newSingleThreadExecutor();
    if (waitRunnable == null) waitRunnable = new WaitRunnable(3,5);
    
    try {
      threadMan.submit(waitRunnable).get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    
    PaymentStatus result = PaymentStatus.ACCEPTED;
    
    return result;
  }

  /**
   * @requires student != null
   * 
   * @effects 
   *  retrieve and return the PaymentProfile of <tt>student</tt>
   *  
   */
  private PaymentProfile lookUpPaymentProfile(Student student) {
    // TODO store payment profile in data store and retrieve them
    // for now, assume the same profile for all students
    return Fixed_Payment_Profile;
  }

  /**
   * @requires modules != null
   * 
   * @effects 
   *  compute the sum of fees for all {@link CourseModule}s in <tt>modules</tt>,
   *  return this sum
   */
  private double computePaymentByCourseModules(Collection<CourseModule> modules) {
    // TODO: store module fees in data source and retrieve them 
    // for now, just return a random amount for a module.
    double sumFee = 0;
    double fee;
    for (CourseModule m : modules) {
      // use hashCode of m.toString 
      fee = m.toString().hashCode() % MAX_FEE_PER_MODULE;
      sumFee += fee;
    }
    
    return sumFee;
  }

}
