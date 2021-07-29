package jda.modules.dcsl.conceptmodel.constraints.feedback;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class FeedbackError extends Feedback {

  public FeedbackError(String content) {
    super(content);
  }

  @Override
  public FeedbackType getType() {
    return FeedbackType.Error;
  }

}
