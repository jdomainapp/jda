/**
 * 
 */
package jda.modules.sccl.syntax;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.osm.javadb.JavaDbOSM;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;

/**
 * @overview 
 *  An annotation that is used to specify details about a data source that is used by an application. 
 *  
 * @author dmle
 *
 * @version 3.3
 * 
 * @todo
 * - support other data source configuration modes: client/server, embedded, etc.
 * (the currentd design only supports client mode)
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
@Documented
public @interface DSDesc {
  /**
   * data source type name (e.g. postgresql, mysql, derby, etc.), which is the name that is used 
   * in the first part of the JDBC connection URL.
   */
  public String type();
  
  /**
   * login user name
   */
  public String user();
  
  /**
   * login password associated with {@link #user()}
   */
  public String password();
  
  /**
   * Data source's URL, i.e. the part of the JDBC connection url that immediately follows the data source type; 
   * e.g. myserver.com:5432/myDS
   */
  public String dsUrl();

  /**
   * (Optional) The DSM type to use
   * <br>Default: {@link DSM} 
   */
  Class dsmType() default DSM.class;

  /**
   * (Optional) The OSM type to use
   * <br>Default: {@link JavaDbOSM} 
   */
  Class osmType() default JavaDbOSM.class;

  /**
   * (Optional) The DOM type to use
   * <br>Default: {@link DOM} 
   */
  Class domType() default DOM.class;

  /**
   * @effects 
   *  The type of connection to the underlying data source that is specified by {@link #osmType()}
   *  
   *  <br>Default: {@link OsmConfig.ConnectionType#Embedded}
   * @version 3.3
   */
  ConnectionType connType();
}
