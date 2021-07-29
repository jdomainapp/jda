package jda.modules.javadbserver.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.apache.derby.drda.NetworkServerControl;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.net.ProtocolSpec;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;

/**
 * @overview
 *  This class represents a JavaDb server process, which is started on the local machine to listen to 
 *  JavaDb client connections from the host(s) and port that are specified as input.
 *
 *  <p>An application initialises a <tt>JavaDbServer</tt> using a {@link ProtocolSpec} object as argument.
 *  This object is typically specified in the data source configuration of the application.
 *  
 *  <p>The <tt>JavaDbServer</tt> object is then used to perform one of the following two operations:
 *  <ul>
 *    <li>starts the server at the specified host/port (if no such server process has been created)
 *    and waits for client connections... 
 *    <li>shuts down the server that is running at the specified host/port
 *  </ul>
 *  
 * @author dmle
 */
@DClass(serialisable=false)
public class JavaDbServer {
  public static final String DEFAULT_SERVER_URL = "//:1527";  // same as //localhost:1727
  
  private static final boolean debug = Toolkit.getDebug(JavaDbServer.class);
  
  //@DomainConstraint(name="serverProt",type=DomainConstraint.Type.Domain,mutable=false,auto=true)
  private ProtocolSpec serverProt;

  private NetworkServerControl serverProcess;

  //v2.8: derived attributes
  @DAttr(name="port",type=DAttr.Type.Integer,mutable=false,auto=true)
  private Integer port;
  public Integer getPort() {
    return serverProt.getPort();
  }
  
  @DAttr(name="host",id=true,type=DAttr.Type.String,mutable=false,auto=true)
  private String host;
  public String getHost() {
    if (host == null) {
      host = serverProt.getHost();
      if (host == null) {
        /*v2.8: to start on wild cart host
          host = ProtocolSpec.LOCALHOST;
          */
        host = ProtocolSpec.ANY_HOST;
      }
    }
    
    return host;
  }

  @DAttr(name="dataSourceType",type=DAttr.Type.String,mutable=false,auto=true)
  private String dataSourceType;
  public String getDataSourceType() {
    return serverProt.getDsType();
  }
  
  @DAttr(name="dataSourceName",type=DAttr.Type.String,mutable=false,auto=true)
  private String dataSourceName;
  public String getDataSourceName() {
    return serverProt.getDataSourceName();
  }
  
  @DAttr(name="status",type=DAttr.Type.String,mutable=false,auto=true)
  private String status;
  public String getStatus() {
    return isRunning() ? "on" : "off";
  }
  
  public JavaDbServer(ProtocolSpec serverProt) {
    this.serverProt = serverProt;
    //parse portNum from server protocol
    port = serverProt.getPort();
    
    if (port == null) {
      // use default port
      port = NetworkServerControl.DEFAULT_PORTNUMBER;
    }
  }

  /**
   * @requires 
   *  serverProt != null 
   *  
   * @effects
   *  Start a JavaDb server process running at the host/port specified in {@link #serverProt}
   *  
   *  <p>Throws NotPossibleException if failed. 
   */
  public void start() throws NotPossibleException {
    try {
      String host = getHost();
      InetAddress serverHost = 
          InetAddress.getByName(host);

      //TODO: set server properties
      //Properties props = serverProt.getProps();

      serverProcess = new NetworkServerControl(serverHost, port);

      serverProcess.start(null);

      // TODO: if a datasource name is specified get an embedded connection to the database
      // to speed up the performance of client connections to it
      
      if (debug)
        System.out.printf("%s...started on %s:%d%n", JavaDbServer.class.getSimpleName(), serverHost, port);
      
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_START_DB_SERVER, e, null);
    }
  }

  /**
   * @requires 
   *  serverProcess != null 
   *  
   * @effects
   *  Stop the JavaDb server process running at the host/port specified in {@link #serverProt}
   *  
   *  <p>Throws NotPossibleException if failed. 
   */
  public void stop() {
    if (serverProcess == null)
      return;
    
    try {
      serverProcess.shutdown();
      
      if (debug)
        System.out.printf("%s...stopped%n", JavaDbServer.class.getSimpleName());
      
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_START_DB_SERVER, e, null);
    }
  }
  
  /**
   * Note: this method does not check whether or not a database server is actually running on host/port specified in {@link #serverProt}
   * because such a process might have been started independently by another JVM.  
   * <p>To check this case, use {@link #isPortAvailable()} instead.
   * 
   * @effects 
   *  if {@link #serverProcess} is running
   *    return true
   *  else
   *    return false
   */
  public boolean isRunning() {
    if (serverProcess != null && keepAlive()) {
      // is running and alive
      return true;
    } else {
      // serverProcess is either dead or not running
      if (serverProcess != null)  // dead -> stop
        try {stop(); } catch (Exception e) {}
      
      return false;
    }
  }

  /**
   * @effects 
   *  if port is not being used 
   *    return true
   *  else
   *    return true
   */
  public boolean isPortAvailable() {
    // 
    ServerSocket socket = null;
    try {
      socket = new ServerSocket(port);
      return true;
    } catch (IOException e) {
      // cannot use port
      if (debug) {
        System.err.printf("%s: using the existing port. %n", e.getMessage());
        //e.printStackTrace();
      }
      
      return false;
    } finally {
      // close socket
      if (socket != null) {
        try {
          socket.close();
        } catch (IOException e) {
          // ignore
        }
      }
    }
  }
  
  /**
   * @effects 
   *  Trace the status of the server process
   *  <br>Print exception if failed
   */
  public void trace(boolean onoff) {
    try {
      serverProcess.trace(onoff);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * @effects 
   *  Try to test for a connection
   *  
   *  <br>Print exception if failed
   */
  public boolean keepAlive() {
    if (serverProcess == null)
      return false;
    
    try {
      serverProcess.ping();
      return true;
    } catch (Exception e) {
      //e.printStackTrace();
      return false;
    }
  }
}
