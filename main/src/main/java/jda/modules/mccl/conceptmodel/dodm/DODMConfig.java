package jda.modules.mccl.conceptmodel.dodm;

import java.io.Serializable;

import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dodm.DODM;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.dodm.osm.OSM;
import jda.modules.dodm.osm.javadb.JavaDbOSMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.OSMProtocol;

/**
 * @overview 
 *  Represent the DODM configuration settings that are included as part of an application's configuration.
 *  
 * @version 2.7.3
 * 
 * @author dmle
 */
//TODO: serialise this
@DClass(schema=DCSLConstants.CONFIG_SCHEMA)
public class DODMConfig implements Serializable {

  // v3.1
  private static final long serialVersionUID = 3383688773208590383L;
  
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, 
      optional = false, mutable = false)
  private int id;
  private static int idCounter;

  // FIXME: this is a simple solution to define OsmConfig (below) directly from the object form of this 
  // improve this if needed
  // optional
//  @DomainConstraint(name="osmConfig",type=Type.Domain,optional=true)
//  @Association(name="dodmConfig-has-osmConfig",role="dodmConfig",
//      type=AssocType.One2One,endType=AssocEndType.One,
//      associate=@AssocEnd(type=OsmConfig.class,cardMin=0,cardMax=1 
//      ))
  @DAttr(name="osmProtocol",type=Type.Domain)
  private OSMProtocol osmProtocol;
  
  @DAttr(name="protocolSpec",type=Type.String,length=100)
  private String protocolSpec;
  
  /**derived from {@link #osmProtocol} and {@link #protocolSpec} */
  private OsmConfig osmConfig;
  
  private Class<? extends DODMBasic> dodmType;  // v3.0
  private Class<? extends DOMBasic> domType;
  private Class<? extends DSMBasic> dsmType;
  private Class<? extends OSM> osmType;
  
  @DAttr(name="config",type=Type.Domain,serialisable=false)
  @DAssoc(ascName="config-has-dodmConfig",role="dodmConfig",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=Configuration.class,cardMin=1,cardMax=1,determinant=true 
      ))
  private Configuration config;
  
  // this constructor is used by other constructors
  private DODMConfig(Integer id, OSMProtocol osmProtocol, String protocolSpec,
      Configuration config) throws NotPossibleException {
    this.id = nextID(id);
    
    this.config = config;
    // use default values
    /* 20210805: v5.4.1: use DODM as the default
    dodmType = DODMBasic.class;
    dsmType = DSMBasic.class;
    domType =  DOMBasic.class;
    END: 20210805 */
    dodmType = DODM.class;
    dsmType = DSM.class;
    domType =  DOM.class;
    
    
    this.osmProtocol = osmProtocol;
    this.protocolSpec = protocolSpec;
    
    onOsmProtocolSpecChange();
  }
  
  // this constructor is to create object from data source
  public DODMConfig(Integer id, OSMProtocol osmProtocol, String protocolSpec) throws NotPossibleException {
    this(id, osmProtocol, protocolSpec, null);
  }
  
  // this constructor is to create object from object form
  public DODMConfig(OSMProtocol osmProtocol, String protocolSpec, Configuration config) {
    this(null, osmProtocol, protocolSpec, config);
  }
  
  // this constructor is used by set-up to set osm config directly
  public DODMConfig(Configuration config, OsmConfig osmConfig) {
    this(null, null, null, config);
    
    if (osmConfig != null) {  
      this.osmConfig = osmConfig;
      onOsmConfigChange();
    }
  }

  /**
   * @effects 
   *  initialise this as a memory-based config (i.e. without OSM)
   */
  public DODMConfig(Configuration config) {
    this(config, null);
  }

  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();
      
      if (num > idCounter) {
        idCounter=num;
      }   
      return currID;
    }
  }

  /**
   * @effects 
   *  if osmProtocol AND protocolSpec are not null
   *    create new OsmConfig from them
   *  else 
   *    do nothing
   */
  private void onOsmProtocolSpecChange() throws NotImplementedException, NotPossibleException {
    if (osmProtocol != null && protocolSpec != null) {
      osmConfig = new OsmConfig(osmProtocol, protocolSpec);
      if (osmConfig.isDataSourceTypeJavaDb()) { //v3.0: osmConfig.getDataSourceType().equals("derby")) {
        osmType = JavaDbOSMBasic.class;
      } else {
        /* v3.0:
        //FIXME: remove this to support other types of data sources
        throw new NotImplementedException(NotImplementedException.Code.OSM_Type_Not_Supported, 
            new Object[] {osmConfig.getDataSourceType()});
            */
      }
    }
  }

  /**
   * The reverse of {@link #onOsmProtocolSpecChange()}
   * 
   * @requires 
   *  osmConfig != null
   */
  private void onOsmConfigChange() {
    this.protocolSpec = osmConfig.getProtSpecString();
    this.osmProtocol = osmConfig.getOsmProt();
    
    if (osmConfig.isDataSourceTypeJavaDb()) { //v3.0: getDataSourceType().equals("derby")) {
      osmType = JavaDbOSMBasic.class;
    } else {
      /*v3.0: removed to allow using setter  
      //FIXME: remove this to support other types of data sources
      throw new NotImplementedException(NotImplementedException.Code.OSM_Type_Not_Supported, 
          new Object[] {osmConfig.getDataSourceType()});
          */
    }    
  }
  
  public Class<? extends OSM> getOsmType() {
    return osmType;
  }

  public void setOsmType(Class<? extends OSM> osmType) {
    this.osmType = osmType;
  }
  
  public OsmConfig getOsmConfig() {
    return osmConfig;
  }
  
  /**
   * @requires 
   *  osmConfig != null
   *  
   * @version 3.1
   */
  public void setOsmConfig(OsmConfig osmConfig) {
    if (osmConfig != null) {
      this.osmConfig = osmConfig;
      onOsmConfigChange();
    }
  }
  
  public OSMProtocol getOsmProtocol() {
    return osmProtocol;
  }

  public void setOsmProtocol(OSMProtocol osmProtocol) {
    this.osmProtocol = osmProtocol;
    onOsmProtocolSpecChange();
  }

  public String getProtocolSpec() {
    return protocolSpec;
  }

  public void setProtocolSpec(String protocolSpec) {
    this.protocolSpec = protocolSpec;
    onOsmProtocolSpecChange();
  }

  public Configuration getConfig() {
    return config;
  }

  public void setConfig(Configuration config) {
    this.config = config;
  }

  public int getId() {
    return id;
  }

  public void setDodmType(Class<? extends DODMBasic> dodmType) {
    this.dodmType = dodmType;
  }

  public Class<? extends DODMBasic> getDodmType() {
    return this.dodmType;
  }
  
  public Class<? extends DSMBasic> getDsmType() {
    //TODO: use an attribute for this
    return dsmType;
  }

  public Class<? extends DOMBasic> getDomType() {
    //TODO: use an attribute for this
    return domType;
  }

  public void setDomType(Class<? extends DOMBasic> domType) {
    this.domType = domType;
  }

  public void setDsmType(Class<? extends DSMBasic> dsmType) {
    this.dsmType = dsmType;
  }

  public boolean isObjectSerialisable() {
    //TODO: use an attribute for this (its value is derived from Osm type)
    return osmType != null;
  }
  
  public void setObjectSerialisable(boolean b) {
    osmType = null;
  }

  public String getAppName() {
    return config.getAppName();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DODMConfig other = (DODMConfig) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "DODMConfig (" + id + ", " + config + ", data-source: " +isObjectSerialisable()+")";
  }
}
