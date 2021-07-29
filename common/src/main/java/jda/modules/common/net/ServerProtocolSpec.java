package jda.modules.common.net;

import java.util.StringTokenizer;

import jda.modules.common.exceptions.NotPossibleException;

public class ServerProtocolSpec extends ProtocolSpec {

  public ServerProtocolSpec(String protSpecification)
      throws NotPossibleException {
    super(protSpecification);
  }

  /**
   * This differs from <tt>{@link ProtocolSpec#parseDataSourcePath()} in that it allows host and/or port
   * to be omitted.
   * 
   * @requires 
   *  length(dataSourcePath) > 0
   */
  @Override
  protected void parseDataSourcePath() throws NotPossibleException {
    String dsPath = dataSourcePath;
    
    // remove the "//" prefix for client connection URL
    boolean clientUrl = false;
    if (dsPath.startsWith("//")) { 
      clientUrl = true;
      dsPath = dsPath.substring(2);
    }
    
    // now parse this 
    StringTokenizer tokenz = new StringTokenizer(dsPath, "/");
    if (!tokenz.hasMoreTokens()) {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_OSM_PROTOCOL_SPECIFICATION, new Object[] {dsPath}); 
    } else {
      String first = tokenz.nextToken();
      if (clientUrl) {
        // first token is host[:port]
        String[] hostPort = first.split(":");
        if (hostPort.length > 0) {
          host = hostPort[0];
          if (host.isEmpty()) {
            // default host
            host = null;
          }
          
          if (hostPort.length > 1) {
            try {
              port = Integer.parseInt(hostPort[1]);
            } catch (NumberFormatException e) {
              throw new NotPossibleException(NotPossibleException.Code.INVALID_PORT, e, new Object[] {hostPort[1]});             
            }
          } else {
            // default port
          }
        } else {
          // default host & port
        }
      } else {
        // normal URL: directory path to data source
        dataSourceName = dsPath;
      }
    }
  }
}
