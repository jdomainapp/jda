package jda.modules.iexport.model.dodm;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.osm.CsvOSM;
import jda.modules.dodm.osm.OSM;

/**
 * @overview
 *  Represent a <tt>Class<{@link OSM}></tt> with additional information (e.g. class label) 
 *  
 *  <p>This is used as a domain class to allow an application to manipulate information about
 *  the <tt>OSM</tt>s 
 *  
 * @author dmle
 */
@DClass(schema="app_config",serialisable=false)
public class OSMType<T extends OSM> {
  
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, 
      optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;
  
  private Class<T> cls;
  
  /**derived fom {@link #cls}*/
  @DAttr(name="simpleName",type=Type.String,length=100,mutable=false,auto=true,serialisable=false)
  private String simpleName;
  
  /**computed fom {@link #cls}*/
  @DAttr(name="classLabel",type=Type.String,length=100)
  private String classLabel;

  //// pre-defined objects 
  //not yet supported: 
  // public static final OSMType<RelationalOSM> RelationalOSMType = new OSMType<>(RelationalOSM.class, "CSDL Quan Hệ");
  public static final OSMType<CsvOSM> CsvOSMType = new OSMType<>(CsvOSM.class, "CSV (Excel)");
  
  ///////////////////////////
  
  public OSMType(Class<T> cls, String classLabel) {
    this.id = nextID(null);

    this.cls = cls;
    this.simpleName = cls.getSimpleName();
    this.classLabel = classLabel;
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
  
  public int getId() {
    return id;
  }

  public String getClassLabel() {
    return classLabel;
  }

  public void setClassLabel(String classLabel) {
    this.classLabel = classLabel;
  }

  public Class<T> getCls() {
    return cls;
  }

  public String getSimpleName() {
    return simpleName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cls == null) ? 0 : cls.getName().hashCode());
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
    
    OSMType other = (OSMType) obj;
    if (cls == null) {
      if (other.cls != null)
        return false;
    } else if (cls != other.cls)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "OSMType (" + simpleName + ")";
  }
  
//  /**
//   * @overview
//   *  A wrapper class that is used to represent {@link OSMType} as a model.  
//   *  
//   *  <p>This acts as a 'kind-of' logical data source on which to retrieve the attribute values of 
//   *  {@link OSMType} objects and feed these values into the bounded data fields.
//   *   
//   * @author dmle
//   */
//  @DomainClass(serialisable=false,wrapperOf=OSMType.class)
//  public static class OSMTypeWrapper {
//      @DomainConstraint(name="id",type=DomainConstraint.Type.Long,id=true,mutable=false,optional=false)
//      private long id;
//
//      @DomainConstraint(name="osmType",type=DomainConstraint.Type.Domain,optional=false)
//      private OSMType osmType;
//      
//      public OSMTypeWrapper(OSMType osmType) {
//        id = System.currentTimeMillis();
//        this.osmType = osmType;
//      }
//      
//      public long getId() {
//        return id;
//      }
//
//      @Override
//      public String toString() {
//        return OSMTypeWrapper.class.getSimpleName()+ "(" + osmType.getSimpleName() + ")";
//      }
//
//      public OSMType getOsmType() {
//        return osmType;
//      }
//  } // end OSMTypeWrapper
}
