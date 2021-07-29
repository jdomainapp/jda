package jda.modules.mccl.conceptmodel.dodm;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.net.ProtocolSpec;
import jda.modules.common.net.ServerProtocolSpec;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dodm.osm.OSM;

/**
 * @overview
 *  Represent the configuration settings of an <tt>OSM</tt>.
 *  
 *  <p>This is to support two execution modes of the OSM: (1) standard and (2) client-only. Refers to {@link OSM} for more information on these.
 *  Basically, in the standard mode an OSM is responsible for managing the connection(s) to the underlying data source
 *  needed to perform its operations (e.g. read and write records). In the client-only mode, however, the OSM does not manage
 *  these connections but rely on another OSM whose is operating in the standard mode for such resources.    
 *  
 *  <p>In both modes, the main configuration of an OSM specifies the structure of a network connection to a target resource. This has the  
 *  following syntax (adapted from that of the JDBC connection URL) :
 *  <pre>
 *  <i>osm_protocol</i>:<i>ds_type</i>:<i>ds_path</i>[;<i>prop1=val1</i>[;...]]
 *  </pre>
 *  where:
 *  <ul>
 *    <li><i>osm_protocol</i> (variable): is the name of the OSM protocol used under each mode, which must be: 
 *          <tt>jdbc</tt> (standard mode) and <tt>peer</tt> (client-only mode)</li>
 *    <li><i>ds_type</i> (variable): must be set to the target data source type (e.g. derby, postgres, etc.)</li>
 *    <li><i>ds_path</i> (variable): is a {@link DataSourcePath} which specifies the path to the actual data source used</li>
 *    <li>[;<i>prop1=val1</i>[;...]] (optional properties): is a semi-colon-separated list of optional property-value pairs. If specified, this list must 
 *    be preceded by a semi-colon (<tt>;</tt>)</li>
 *  </ul>
 *  
 *  <p>Sub-types of <tt>OSM</tt> need to specialise this class to provide their own config values.
 *  
 * @examples
 * The followings are examples of the main configuration of an {@link OSMConfig} for a derby-typed data source (a.k.a JavaDB) named <tt>CourseMan</tt>.
 * <br>All examples have <tt><i>ds_type</i> = derby</tt>. Other data source types only differ in the value of this element (e.g. Postgres data source would have 
 * <tt><i>ds_type</i> = postgres</tt> and so on.) 
 *   
 *  <h3>Derby embedded configurations:</h3>
 *  <ol>
 *    <li><tt>jdbc:derby:CourseMan</tt>: without specifying any properties 
 *    <li><tt>jdbc:derby:CourseMan;user=duc;password=duc;create=true</tt>: with three properties 
 *            <tt>user,password,create</tt>
 *  </ol>
 *  
 *  <h3>Derby client/server configurations:</h3>
 *  This is divided into <b>client-side configuration</b> and <b>server-side configuration</b>. 
 *  
 *  <br><b>Note</b>: the client and server data source properties generally differ, so please refer to the data source manual for details 
 *  of these.
 *  
 *  <h4>Server-side configurations:</h4>
 *  <ol>
 *    <li><tt>jdbc:derby://localhost:1527</tt>: start a server that listens at port <tt>1527</tt> for connections from the <tt>loopback</tt> address, without specifying any properties 
 *    <li><tt>jdbc:derby://0.0.0.0:1110</tt>: start a the server that listens at port <tt>1110</tt> for connections from <tt>ANY</tt> IPv4 hosts, without specifying any properties
 *    <li><tt>jdbc:derby://:::1110</tt>: start a the server that listens at port <tt>1110</tt> for connections from <tt>ANY</tt> <b>IPv6</b> hosts, without specifying any properties
 *  </ol>
 *  
 *  <h4>Client-side configurations:</h4>
 *  <ol>
 *    <li><tt>jdbc:derby://localhost:1527/CourseMan</tt>: connect to the default server, without specifying any properties 
 *    <li><tt>jdbc:derby://192.168.1.1:1110/CourseMan;create=true</tt>: connect to the server at <tt>192.168.1.1</tt>, port <tt>1110</tt> with one property <tt>create=true</tt>
 *  </ol>
 *  
 *  <h3>Client-only configurations:</h3>
 *  This is similar to the client-side configuration of the client-server configuration above.
 *   
 *  <ol>
 *    <li><tt>peer:derby://localhost:3000/CourseMan</tt>: connect to the <b>OSM peer</b> who is listening on port <tt>3000</tt> at <tt>localhost</tt> (without specifying any properties), 
 *        and using the OSM configuration <i>selected</i> by this peer for the <tt>derby</tt> database named <tt>CourseMan</tt> 
 *    <li><tt>peer:derby://192.168.1.254:3000/CourseMan</tt>: connect to the OSM peer who is listening on port <tt>3000</tt> at <tt>192.168.1.1</tt> (without specifying any properties), 
 *        and using the OSM configuration <i>selected</i> by this peer for the <tt>derby</tt> database named <tt>CourseMan</tt> 
 *  </ol>
 *  
 * @author dmle
 * 
 * @version 2.7.3 
 */
@DClass(schema=DCSLConstants.CONFIG_SCHEMA,
serialisable=false)
public class OsmClientServerConfig extends OsmConfig {
  
  private ServerProtocolSpec serverProtocolSpec;

  /**
   * This constructor is to create an OSMConfig from the code.
   *
   * @requires 
   * <tt>protSpecification</tt> contains the elements after the {@link OSMProtocol}. 

   * <br>Refer to {@link OSM} for further details.
   *  
   * @effects 
   *  parses <tt>protSpecification</tt> into elements and initialise this with the elements.
   *  
   *  Throws NotPossibleException if failed to parse.
   */
  public OsmClientServerConfig(OSMProtocol osmPrototocol, String protSpecification) throws NotPossibleException {
    super(osmPrototocol, new ProtocolSpec(protSpecification));
  }

  public OsmClientServerConfig(OSMProtocol osmPrototocol, ProtocolSpec protSpec)  throws NotPossibleException {
    super(osmPrototocol, protSpec); 
  }

  public void setServerProtocolSpec(ServerProtocolSpec serverProto) {
    this.serverProtocolSpec = serverProto;
  }
  
  public ServerProtocolSpec getServerProtocolSpec() {
    return serverProtocolSpec;
  }
}
