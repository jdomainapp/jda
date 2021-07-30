package jda.modules.backend.helidon.wsocket3way;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class Message {
  
  public static enum Action {
    Get,
  };
  
  private String action;
  private String clientId;

  /**
   * @effects 
   *
   * @version 
   */
  public Message(String action, String clientId) {
    this.action = action;
    this.clientId = clientId;
  }
  
  public boolean isAction(Action act) {
    return (act.name().equals(action));
  }
  
  /**
   * @effects return action
   */
  public String getAction() {
    return action;
  }
  /**
   * @effects set action = action
   */
  public void setAction(String action) {
    this.action = action;
  }
  /**
   * @effects return clientId
   */
  public String getClientId() {
    return clientId;
  }
  /**
   * @effects set clientId = clientId
   */
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return "Message (" + action + ", " + clientId + ")";
  }
}
