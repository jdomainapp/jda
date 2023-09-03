package jda.modules.msacommon.msatool;

/**
 * @overview
 *  represents all the service monitoring actions, including health, shutdown, restart and so on.
 */
public enum MonitorAction {
  health("GET"),
  shutdown("POST"),
  restart("POST"),
  refresh("POST");

  private final String httpMethod;

  MonitorAction(String httpMethod) {
    this.httpMethod = httpMethod;
  }

  public String httpMethod() {
    return httpMethod;
  }
}
