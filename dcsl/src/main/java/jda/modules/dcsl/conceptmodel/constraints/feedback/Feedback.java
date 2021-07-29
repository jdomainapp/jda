package jda.modules.dcsl.conceptmodel.constraints.feedback;

/**
 * @overview 
 *  Represents grader feedbacks.
 *  
 * @author Duc Minh Le (ducmle)
 *
 */
public abstract class Feedback {

  private String content;

  public static enum FeedbackType {
    Error,
    Warning
  }
  
  /**
   * @effects 
   *  initialise this with <tt>content</tt>
   */
  public Feedback(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }
  
  /**
   * 
   * @effects 
   *  return the feedback type of this
   */
  public abstract FeedbackType getType();
  
  /**
   * @effects 
   *  if this.getType() equals type
   *    return true
   *  else
   *    return false 
   */
  public boolean isType(FeedbackType type) {
    return type.equals(getType());
  }
  
  @Override
  public String toString() {
    return content;
  }
}
