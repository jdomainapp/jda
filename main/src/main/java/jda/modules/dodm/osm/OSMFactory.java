package jda.modules.dodm.osm;

import java.lang.reflect.Constructor;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.net.ProtocolSpec;
import jda.modules.common.net.ServerProtocolSpec;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.mccl.conceptmodel.dodm.OsmClientServerConfig;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.OSMProtocol;

public class OSMFactory {

//  /**
//   * @effects 
//   *  create and return an <tt>OSM</tt> whose type is specified in <tt>config</tt>
//   */
//  public static OSM createOsmInstance(Configuration config, DOM dom) throws NotPossibleException {
//    Class<? extends OSM> osmType = config.getOsmType();
//    
//    try {
//      // invoke the constructor to create object 
//      //DSM dsm = dom.getDsm();
//      OSM instance = osmType.getConstructor(Configuration.class, DOM.class).newInstance(config, dom);
//      
//      return instance;
//    } catch (Exception e) {
//      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
//          "Không thể tạo đối tượng lớp: {0}.{1}({2})", osmType.getSimpleName(), "init", config);
//    }
//  }

  /**
   * @effects 
   *  create and return an <tt>OSM</tt> whose type is <tt>osmType</tt> and whose 
   *  connection details are specified in <tt>config</tt>
   */
  public static OSM getOsmInstance(Class<? extends OSM> osmType, OsmConfig config,
      DOMBasic dom) {
    try {
      // invoke the constructor to create object 
      // OSM instance = osmType.getConstructor(OsmConfig.class, DOMBasic.class).newInstance(config, dom);
      Constructor<? extends OSM> cons = osmType.getConstructor(OsmConfig.class, DOMBasic.class);
      
      OSM instance = cons.newInstance(config, dom);
      
      return instance;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {osmType.getSimpleName(), config});
    }
  }

  /**
   * @effects
   *  return an <tt>OsmConfig</tt> whose protocol is {@link OSMProtocol#Standard} and whose
   *  data source type is <tt>dsType</tt> and whose  
   *  data source path is <tt>dataSourcePath</tt>.
   *  
   *  <p>Throws NotPossibleException if failed to create the config.
   *  
   * @version 
   *  - 2.7.3: created <br>
   *  - 3.0: add default properties
   */
  public static OsmConfig getStandardOsmConfig(String dsType, String dataSourcePath)  
      throws NotPossibleException {
    String protSpec = dsType + ":" + dataSourcePath;
    
    //v3.0: return new OsmConfig(OSMProtocol.Standard, protSpec);
    OsmConfig config = new OsmConfig(OSMProtocol.Standard, protSpec);
    
    config.setProperty("create", "true");
    return config;
  }

  /**
   * @effects
   *  return an <tt>OsmConfig</tt> whose protocol is {@link OSMProtocol#Standard} and whose
   *  protocol specification is <tt>protSpec</tt> 
   *  
   *  <p>Throws NotPossibleException if failed to create the config.
   *  
   * @version 
   *  - 2.7.3: created <br>
   *  - 3.0: add default properties
   */
  public static OsmConfig getStandardOsmConfig(String dsType, ProtocolSpec protSpec)   
          throws NotPossibleException {
    // v3.0: return new OsmConfig(OSMProtocol.Standard, protSpec);
    OsmConfig config = new OsmConfig(OSMProtocol.Standard, protSpec);
    
    config.setProperty("create", "true");
    return config;

  }

  /**
   * @effects
   *  return an <tt>OsmClientServerConfig</tt> whose protocol is {@link OSMProtocol#Standard} and whose
   *  data source type is <tt>dsType</tt> and whose client protocol specification is <tt>clientUrl</tt>
   *  
   *  <p>Throws NotPossibleException if failed to create the config.
   *  
   * @version 2.8
   */
  public static OsmClientServerConfig getStandardOsmClientConfig(
      String dsType, String dataSourcePath) {
    String clientUrl = dsType + ":" + dataSourcePath;
    
    OsmClientServerConfig osmConfig = new OsmClientServerConfig(OSMProtocol.Standard, clientUrl);
    
    // v3.0: add default props
    osmConfig.setProperty("create", "true");
    
    return osmConfig;
  }


  /**
   * @effects
   *  return an <tt>OsmClientServerConfig</tt> whose protocol is {@link OSMProtocol#Standard} and whose
   *  data source type is <tt>dsType</tt> and whose client and server protocol specification details are 
   *  <tt>clientUrl</tt> and <tt>serverUrl</tt>, respectively 
   *  
   *  <p>Throws NotPossibleException if failed to create the config.
   *  
   * @version 2.7.3
   */
  public static OsmClientServerConfig getStandardOsmClientServerConfig(
      String dsType, String dataSourcePath, String serverUrl) {
    String clientUrl = dsType + ":" + dataSourcePath;
    
    OsmClientServerConfig osmConfig = new OsmClientServerConfig(OSMProtocol.Standard, clientUrl);
    
    // v3.0: add default props
    osmConfig.setProperty("create", "true");

    ServerProtocolSpec serverProto = getStandardClientServerProtocolSpec(dsType, serverUrl);
    
    osmConfig.setServerProtocolSpec(serverProto);
    
    return osmConfig;
    
  }
  
  /**
   * @effects
   *  return an <tt>ProtocolSpec</tt> whose 
   *  data source type is <tt>dsType</tt> and whose  
   *  data source path is <tt>dataSourcePath</tt>.
   *  
   *  <p>Throws NotPossibleException if failed to create the config.
   *  
   * @version 2.7.3
   */
  public static ProtocolSpec getStandardProtocolSpec(String dsType, String dataSourcePath) 
  throws NotPossibleException {
    String protSpec = dsType + ":" + dataSourcePath;
    return new ProtocolSpec(protSpec);
  }

  /**
   * @effects
   *  return an <tt>ServerProtocolSpec</tt> whose 
   *  data source type is <tt>dsType</tt> and whose  
   *  data source path is <tt>dataSourcePath</tt>.
   *  
   *  <p>Throws NotPossibleException if failed to create the config.
   *  
   * @version 2.7.3
   */  
  public static ServerProtocolSpec getStandardClientServerProtocolSpec(
      String dsType, String dataSourcePath) throws NotPossibleException {
    String protSpec = dsType + ":" + dataSourcePath;
    return new ServerProtocolSpec(protSpec);
  }
}
