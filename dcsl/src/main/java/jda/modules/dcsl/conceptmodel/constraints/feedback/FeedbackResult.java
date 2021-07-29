package jda.modules.dcsl.conceptmodel.constraints.feedback;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import jda.modules.dcsl.conceptmodel.constraints.Constraint;
import jda.modules.dcsl.conceptmodel.constraints.feedback.Feedback.FeedbackType;

/**
 * @overview 
 *  Record the {@link Feedback}s of all the {@link Constraint}s 
 *  which were applied to a compilation unit (e.g. Student.java). 
 *   
 * @author Duc Minh Le (ducmle)
 *
 */
public class FeedbackResult {

  private Map<Constraint, Collection<Feedback>> result;
  
  public FeedbackResult() {
    result = new LinkedHashMap<>();
  }
  
  /**
   * @effects 
   *  add (rule, feedbacks) to this
   */
  public void add(Constraint rule, Collection<Feedback> feedbacks) {
    result.put(rule, feedbacks);
  }

  /**
   * @effects 
   *  if this is empty
   *    return true
   *  else
   *    return false
   */
  public boolean isEmpty() {
    return result.isEmpty();
  }

  public void forEach(BiConsumer<Constraint, Collection<Feedback>> action) {
    result.forEach(action);
  }
  
  @Override
  public String toString() {
    if (result.isEmpty()) {
      return "(empty)";
    } else {
//      StringBuilder sb = new StringBuilder();
      StringBuilder sbErr = new StringBuilder(), sbWarn = new StringBuilder();
      result.forEach((r, fbc) -> {
//        Rule r = e.getKey();
//        Collection<Feedback> fbs = e.getValue();
//        sb.append("\n").append(r.toString());
//        fbs.forEach(fb -> {
//          sb.append("\n  ").append(fb.toString());
//        });
        String r2Str = r.toString();
        int[] counts = new int[2];
        fbc.forEach(f -> {
          if (f.isType(FeedbackType.Error)) {
            counts[0]++;
            if (counts[0] == 1) sbErr.append("\n").append(r2Str);
            sbErr.append("\n  ").append(f.toString());
          } else if (f.isType(FeedbackType.Warning)) {
            counts[1]++;
            if (counts[1] == 1) sbWarn.append("\n").append(r2Str);
            sbWarn.append("\n  ").append(f.toString());
          }
        });
      });
      
      StringBuilder sb = new StringBuilder();
      if (sbWarn.length() > 0) {
        sb.append("\nWARNINGS:\n----------").append(sbWarn);
      }

      if (sbErr.length() > 0) {
        sb.append("\n\nERRORS:\n----------").append(sbErr).append("\n");
      }
      
      return sb.toString();
    }
    
  }
}
