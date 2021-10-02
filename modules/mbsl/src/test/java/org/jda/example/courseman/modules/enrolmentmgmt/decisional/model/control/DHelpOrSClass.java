package org.jda.example.courseman.modules.enrolmentmgmt.decisional.model.control;

import java.util.List;

import org.jda.example.courseman.modules.helprequest.model.HelpRequest;
import org.jda.example.courseman.modules.sclass.model.SClass;
import org.jda.example.courseman.modules.sclassregist.model.SClassRegistration;
import org.jda.example.courseman.modules.student.model.Student;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.mbsl.exceptions.DomainMessage;
import jda.modules.mbsl.model.graph.Edge;
import jda.modules.mbsl.model.graph.Node;
import jda.modules.mbsl.model.util.Decision;

/**
 * @overview 
 *  A {@link Decision} that realises the decision class between the following referenced domain classes: 
 *  {@link HelpRequest} and {@link SClass}. 
 *  
 *  <p>Method {@link #evaluate(List)} implements the decision logic specific to these referenced
 *  domain classes. 
 *  
 * @author Duc Minh Le (ducmle)
 * @version 4.0
 */
@DClass(serialisable=false)
public class DHelpOrSClass implements Decision {

  /* (non-Javadoc)
   * @see domainapp.modules.activity.model.util.Decision#evaluate(java.lang.Object[], java.util.List)
   */
  /**
   * @requires <tt>args</tt> contains a {@link Student} <tt>s</tt>
   * 
   * @effects 
   *  implements the following decision logic specific to the referenced
   *    domain classes:
   *  <pre>
   *    let {@link Student} s = args[0]
   *    if s.helpRequested = true
   *      return Edge e in decisionNode.out s.t. e.n2.refCls = HelpDesk
   *    else
   *      return Edge e in decisionNode.out s.t. e.n2.refCls = SClass
   *  </pre>
   */
  @Override
  public Edge evaluate(Node decisionNode, Object[] args) throws NotPossibleException {
    Student s = (Student) args[0];
    
    Class outCls;
    if (s.getHelpRequested()) {
      outCls = HelpRequest.class;
    } else {
      outCls = SClassRegistration.class;
    }

    List<Edge> outEdges = decisionNode.getOut();
    for (Edge e : outEdges) {
      if (e.getTarget().getRefCls().equals(outCls)) {
        return e;
      }
    }
    
    // no out edge is found: should not happen
    throw new NotPossibleException(DomainMessage.ERR_NO_SUITABLE_OUT_EDGE, new Object[] {decisionNode});
  }
  
}
