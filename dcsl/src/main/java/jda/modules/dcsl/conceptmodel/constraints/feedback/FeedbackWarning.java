package jda.modules.dcsl.conceptmodel.constraints.feedback;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class FeedbackWarning extends Feedback {

  public FeedbackWarning(String content) {
    super(content);
  }

  @Override
  public FeedbackType getType() {
    return FeedbackType.Warning;
  }
}
