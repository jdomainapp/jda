package jda.modules.common.net;

import java.io.Serializable;
import java.util.Properties;
import java.util.StringTokenizer;

import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview
 *  Represents the protocol specification of an OsmConfig. 
 *  
 *  <p>E.g. refer to {@link OsmConfig} for an syntax.
 *   
 * @author dmle
 */
public class ProtocolSpec implements Serializable {
  
  public static final String LOCAL_HOST = "localhost";

  public static final String ANY_HOST = "0.0.0.0";

  // the validate protocol string
  private String protocolSpecString;
  
  private String dsType;
  protected String dataSourcePath;

  protected String dataSourceName;
  protected String host;
  protected Integer port;
  private Properties props;
  
  /**
   * @effects 
   *  parses <tt>protSpecification</tt> into elements and initialise this with the elements.
   *  Throws NotPossibleException if failed to parse. 
   */
  public ProtocolSpec(String protSpecification) throws NotPossibleException {
    int firstDelim = protSpecification.indexOf(":");

    String rest;
    if (firstDelim < 0 || protSpecification.endsWith(";")) {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_OSM_PROTOCOL_SPECIFICATION, new Object[] {protSpecification});
    } else {
      this.dsType = protSpecification.substring(0, firstDelim);
      rest = protSpecification.substring(firstDelim+1);
    }

    int propDelim = rest.indexOf(";");
    String propStr = null;
    if (propDelim > -1) {
      propStr = rest.substring(propDelim+1);
      dataSourcePath = rest.substring(0, propDelim);
    } else {
      // no properties
      dataSourcePath = rest;
    }
    
    //v3.0
    props = new Properties();

    // parses into elements
    parseDataSourcePath();
    
    if (propStr != null) { // properties
      StringTokenizer propz = new StringTokenizer(propStr, ";");
      /*v3.0: make props required (move to above)
      props = new Properties();
      */
      String propValStr;
      String[] propVal;
      while (propz.hasMoreTokens()) {
        propValStr = propz.nextToken();
        if (propValStr.indexOf("=") > -1) {
          propVal = propValStr.split("=");
          if (propVal.length != 2) {
            throw new NotPossibleException(NotPossibleException.Code.INVALID_OSM_PROTOCOL_SPECIFICATION, new Object[] {propValStr});
          }
          
          // a property-value pair (assume that they are correct w.r.t data source type specification)
          props.setProperty(propVal[0], propVal[1]);
        } else {
          throw new NotPossibleException(NotPossibleException.Code.INVALID_OSM_PROTOCOL_SPECIFICATION, new Object[] {propValStr});          
        }
      }
    }
    
    // if we get here the the procotol spec is valid
    this.protocolSpecString = protSpecification;
  }

  /**
   * @requires 
   *  length(dataSourcePath) > 0
   */
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
      throw new NotPossibleException(NotPossibleException.Code.INVALID_OSM_PROTOCOL_SPECIFICATION, new Object[] {dataSourcePath}); 
    } else {
      String first = tokenz.nextToken();
      if (clientUrl) {
        // first token is host[:port]
        String[] hostPort = first.split(":");
        if (hostPort.length > 0) {
          host = hostPort[0];
          
          if (host.isEmpty()) {
            throw new NotPossibleException(NotPossibleException.Code.INVALID_OSM_PROTOCOL_SPECIFICATION, new Object[] {dataSourcePath}); 
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
          // error: at least host must be specified
          throw new NotPossibleException(NotPossibleException.Code.INVALID_OSM_PROTOCOL_SPECIFICATION, new Object[] {dataSourcePath}); 
        }
        
        // data source name is rest of token
        if (!tokenz.hasMoreTokens()) {
          // error: no data source
          throw new NotPossibleException(NotPossibleException.Code.INVALID_OSM_PROTOCOL_SPECIFICATION, new Object[] {dataSourcePath});
        }

        dataSourceName = tokenz.nextToken("\n");
        
        if (dataSourceName.isEmpty()) {
          // error: no data source
          throw new NotPossibleException(NotPossibleException.Code.INVALID_OSM_PROTOCOL_SPECIFICATION, new Object[] {dataSourcePath});
        }
      } else {
        // normal URL: directory path to data source
        dataSourceName = dsPath;
      }
    }
  }

  public String getDsType() {
    return dsType;
  }

  /**
   * @effects 
   *  return the data source path of the protocol (this includes an optional host[:port] element 
   *    followed by {@link #dataSourceName})
   * @example <tt>//localhost:1527/data/CourseMan</tt>
   */
  public String getDataSourcePath() {
    return dataSourcePath;
  }

  /**
   * @effects 
   *  return the data source URL (i.e. {@link #dsType}:{@link #dataSourcePath}) of the protocol spec of this (which excludes the extra 
   *  connection properties (if any). 
   *  
   * @example <tt>postgresql://localhost/data/CourseMan</tt>
   *  
   * @version 3.0
   */
  public String getDataSourceURL() {
    return dsType + ":" + dataSourcePath;
  }
  
  /**
   * @effects 
   *  return the data source name of the protocol 
   * @example <tt>data/CourseMan</tt>
   */
  public String getDataSourceName() {
    return dataSourceName;
  }

  /**
   * @effects 
   *  return the host part of {@link #dataSourcePath}
   */
  public String getHost() {
    return host;
  }
  
  /**
   * @effects 
   *  if port (of {@link #dataSourcePath}) is initialised
   *    return port
   *  else
   *    return null
   */
  public Integer getPort() {
    return port;
  }

  public Properties getProps() {
    return props;
  }

  public String getProtocolSpecString() {
    return protocolSpecString;
  }


  public void setProperty(String propName, String val) {
    props.setProperty(propName, val);
  }

  public String getProperty(String propName) {
    return props.getProperty(propName);
  }
  
  @Override
  public String toString() {
    return "ProtocolSpec (" + protocolSpecString + ")";
  }


//  public String toProtocolString() {
//    StringBuffer sb = new StringBuffer(getDsType());
//    sb.append(":").append(getDataSourcePath());
//    if (props!= null) {
//      Enumeration<String> propNames = (Enumeration<String>) props.propertyNames();
//      String propName;
//      while (propNames.hasMoreElements()) {
//        propName = propNames.nextElement();
//        sb.append(";").append(propName).append("=").
//          append(props.getProperty(propName));
//      }
//    }
//    
//    return sb.toString();
//  }
}
