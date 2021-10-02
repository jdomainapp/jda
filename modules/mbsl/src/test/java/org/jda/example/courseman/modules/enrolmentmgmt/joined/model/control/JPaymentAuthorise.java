package org.jda.example.courseman.modules.enrolmentmgmt.joined.model.control;

import java.util.Arrays;
import java.util.Map;

import org.jda.example.courseman.modules.authorisation.model.Authorisation;
import org.jda.example.courseman.modules.enrolment.model.EnrolmentApproval;
import org.jda.example.courseman.modules.payment.model.Payment;
import org.jda.example.courseman.modules.student.model.Student;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.mbsl.exceptions.DomainMessage;
import jda.modules.mbsl.model.graph.Node;
import jda.modules.mbsl.model.util.Join;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
@DClass(serialisable=false)
public class JPaymentAuthorise implements Join {

  /* (non-Javadoc)
   * @see domainapp.modules.activity.model.util.Fork#evaluate(domainapp.modules.activity.model.graph.Node, java.lang.Object[])
   */
  /**
   * @effects 
   *  return {@link Map} of the result transformed from those in <tt>args</tt> 
   * @version 
   */
  @Override
  public Object[] transform(Node joinNode, Object[] args)
      throws NotPossibleException {
    
    Payment payment = null; Authorisation authorisation = null; Student student; Boolean approved;
    for (Object o : args) {
      if (o instanceof Payment) {
        payment = (Payment) o;
      } else if (o instanceof Authorisation) {
        authorisation  = (Authorisation) o;
      }
    }
    
    if (payment == null || authorisation == null) {
      // should not happen
      throw new NotPossibleException(DomainMessage.ERR_FAIL_TO_FILTER_JOIN_INPUT, new Object[] {joinNode, Arrays.toString(args)});
    } else {      
//      student = payment.getStudent();
//      approved = EnrolmentApproval.deriveApproved(payment, authorisation);
//      
//      Map<String,Object> attribValMap = new HashMap<>();
//      
//      attribValMap.put("student", student);
//      attribValMap.put("payment", payment);
//      attribValMap.put("authorisation", authorisation);
//      attribValMap.put("approved", approved);
//      
//      return attribValMap;
      
      student = payment.getStudent();
      approved = EnrolmentApproval.deriveApproved(payment, authorisation);
      return new Object[] {student, payment, authorisation, approved};
    }
  }

}
