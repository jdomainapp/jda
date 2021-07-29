package jda.util.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.model.ModelConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;

@DClass(schema="app_config")
public class PropertySet {
  @DAttr(name="id",id=true,auto=true,type=DAttr.Type.Integer,mutable=false,optional=false)
  private int id;
  private static int idCounter;
  
  @DAttr(name="String",type=DAttr.Type.String,mutable=false,optional=false)
  private String name;
  
  @DAttr(name="type",type=DAttr.Type.Domain,mutable=false,optional=false)
  private PropertySetType type;
  
  @DAttr(name="extensionOf",type=DAttr.Type.Domain)
  @DAssoc(ascName="propSet-has-extensions",role="extensions",
  ascType=AssocType.One2Many,endType=AssocEndType.Many,
  associate=@Associate(type=PropertySet.class,cardMin=1,cardMax=1))
  private PropertySet extensionOf;

  @DAttr(name="extensions",type=DAttr.Type.Collection,serialisable=false)
  @DAssoc(ascName="propSet-has-extensions",role="propSet",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=PropertySet.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE))
  private Collection<PropertySet> extensions;

  // derived
  private int extentCount;
  
//  @DomainConstraint(name="refByProp",type=DomainConstraint.Type.Domain,serialisable=false)
//  @Association(name="prop-references-set",role="set",
//      type=AssocType.One2One,endType=AssocEndType.One,
//      associate=@AssocEnd(type=Property.class,cardMin=0,cardMax=1,determinant=true))
//  private Property refByProp; 
  
  @DAttr(name="props",type=DAttr.Type.Collection,serialisable=false)
  @DAssoc(ascName=Property.Association_WithPropertySet,role="propSet",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=Property.class,cardMin=1,cardMax=DCSLConstants.CARD_MORE))
  private List<Property> props;
  
  // derived
  private int propCount;

  // associations to other configuration types
  @DAttr(name="appModule",type=DAttr.Type.Domain,serialisable=false)
  @DAssoc(ascName="module-has-printCfg",role="printCfg",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=ApplicationModule.class,cardMin=0,cardMax=1,determinant=true))
  private ApplicationModule appModule; 

  // for print configuration of a region
  @DAttr(name="referringRegion",type=DAttr.Type.Domain,serialisable=false)
  @DAssoc(ascName="region-has-printCfg",role="printCfg",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=Region.class,cardMin=0,cardMax=1,determinant=true))
  private Region referringRegion; 

  // v3.0: region properties
  @DAttr(name="region",type=DAttr.Type.Domain,serialisable=false)
  @DAssoc(ascName="region-has-properties",role="properties",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=Region.class,cardMin=0,cardMax=1,determinant=true))
  private Region region; 
  
  // v2.7.4
  @DAttr(name="controllerCfg",type=DAttr.Type.Domain,serialisable=false)
  @DAssoc(ascName="controllerCfg-has-properties",role="properties",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=ControllerConfig.class,cardMin=0,cardMax=1,determinant=true))  
  private ControllerConfig controllerCfg; 
  
  // v3.0
  @DAttr(name="modelCfg",type=DAttr.Type.Domain,serialisable=false)
  @DAssoc(ascName="modelCfg-has-properties",role="properties",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=ModelConfig.class,cardMin=0,cardMax=1,determinant=true))  
  private ModelConfig modelCfg; 
  
//  // v2.7.4: for region properties
//  @DomainConstraint(name="region",type=DomainConstraint.Type.Domain,serialisable=false)
//  @Association(name="region-has-properties",role="properties",
//    type=AssocType.One2One,endType=AssocEndType.One,
//    associate=@AssocEnd(type=ControllerConfig.class,cardMin=0,cardMax=1,determinant=true))  
//  private Region region; 
  
  // constructor to create from data source
  public PropertySet(Integer id, String name, PropertySetType type, PropertySet extensionOf) {
    if (id == null) {
      idCounter++;
      this.id=idCounter;
    } else {
      this.id = id;
    }    
    this.name = name;
    this.type = type;
    this.extensionOf = extensionOf;
    props = new ArrayList<>();
    extentCount = 0;
    propCount = 0;
  }

  // constructor to create from application
  public PropertySet(String name, PropertySetType type, PropertySet extensionOf) {
    this(null, name, type, extensionOf);
  }

  public PropertySet(String name, PropertySetType type) {
    this(null, name, type, null);
  }
  
  public int getId() {
    return id;
  }

  public PropertySetType getType() {
    return type;
  }

  public String getName() {
    return name;
  }
  
//  public Property getRefByProp() {
//    return refByProp;
//  }
//
//  public void setRefByProp(Property refByProp) {
//    this.refByProp = refByProp;
//  }

  
  public PropertySet getExtensionOf() {
    return extensionOf;
  }

  /**
   * @effects 
   *  if exists a PropertySet pset in this.extensions s.t. pset.name = name
   *    return pset
   *  else
   *    return null 
   */
  public PropertySet getExtension(String name) {
    if (extensions != null) {
      for (PropertySet pset : extensions) {
        if (pset.getName().equals(name))
          return pset;
      }
    }
    
    return null;
  }

  public Collection<PropertySet> getExtensions() {
    return extensions;
  }

  public boolean hasExtensions() {
    return extensions != null;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value="extensions")
  public boolean addExtension(PropertySet o) {
    if (extensions == null) extensions = new ArrayList<>();
    
    if (!extensions.contains(o)) {
      extensions.add(o);
    
      extentCount++;
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value="extensions")
  public boolean addExtensions(List<PropertySet> extensions) {
    if (this.extensions == null) this.extensions = new ArrayList<>();
    
    for (PropertySet o : extensions) {
      if (!this.extensions.contains(o)) {
        this.extensions.add(o);
        extentCount++;
      }
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkRemover)
  @AttrRef(value="extensions")
  public boolean removeExtension(PropertySet o) {
    extensions.remove(o);
    
    if (extentCount > 0)
      extentCount--;
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getExtensionsCount() {
    return extentCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setExtensionsCount(int count) {
    extentCount = count;
  }
  
  public Collection<Property> getProps() {
    return props;
  }

  /**
   * This method is used to retrieve a typed value of this property.
   *  
   * @effects 
   *  if exists Property p s.t. p.pkey = pKey
   *    if expectedType is not null AND expectedType is either the same as or a super-type of the p.type
   *      return p.value as expectedType
   *    else
   *      return null
   *  else
   *    return null
   */
  public <T> T getPropertyValue(String pKey, Class<T> expectedType) {
//    for (Property p : props) {
//      if (p.getPkey().equals(pKey)) {
//        return p.getValuez(expectedType);
//      }
//    }
//    
//    return null;
    return getPropertyValue(pKey, expectedType, null);
  }
  

  /**
   * This method is used to retrieve a typed value of this property.
   *  
   * @effects 
   *  if exists Property p s.t. p.pkey = pKey
   *    if expectedType is not null AND expectedType is either the same as or a super-type of the p.type
   *      return p.value as expectedType
   *    else
   *      return null
   *  else
   *    return defaultVal
   */
  public <T> T getPropertyValue(String pKey, Class<T> expectedType, T defaultVal) {
    for (Property p : props) {
      if (p.getPkey().equals(pKey)) {
        return p.getValuez(expectedType);
      }
    }
    
    return defaultVal;
  }  
  
  /**
   * 
   * @requires 
   *  propName != null
   * @effects 
   *  if exists property whose key is <tt>propName.name()</tt> of <tt>this</tt> 
   *    return its value
   *  else
   *    return <tt>defaultVal</tt>
   *     
   * @version 2.7.4
   */  
  public Object getPropertyValue(PropertyName propName, Object defaultVal) {
    String pKey = propName.name();
    for (Property p : props) {
      if (p.getPkey().equals(pKey)) {
        return p.getValue();
      }
    }
    
    return defaultVal;
  }

  /**
   * 
   * @requires 
   *  propName != null
   * @effects 
   *  if exists property whose key is <tt>propName.name()</tt> of <tt>this</tt> 
   *    return its value (casted to <tt>expectedType</tt>)
   *  else
   *    return <tt>defaultVal</tt>
   *     
   * @version 3.0
   */  
  public <T> T getPropertyValue(PropertyName propName, Class<T> expectedType, T defaultVal) {
    String pKey = propName.name();
    for (Property p : props) {
      if (p.getPkey().equals(pKey)) {
        return p.getValuez(expectedType);
      }
    }
    
    return defaultVal;
  }

  /**
   * @effects 
   *  if exists {@link Property}s in this whose keys have the prefix <tt>namePrefix</tt>
   *  and whose value type = <tt>valueType</tt>
   *    return a {@link Map} of the property keys and values
   *  else
   *    return <tt>null</tt>
   */
  public <T> Map<PropertyName, T> getPropertyValuesByKeyPrefix(PropertyName namePrefix, Class<T> valueType, T defaultVal) {
    Map<PropertyName, T> propValMap = null;

    // look up the matching property names
    Collection<PropertyName> propNames = PropertyName.lookUpBySysPropNamePrefix(namePrefix);
    
    if (propNames != null) {
      propValMap = new HashMap();
      T val;
      for (PropertyName propName : propNames) {
        val = getPropertyValue(propName, valueType, defaultVal);
        propValMap.put(propName, val);
      }
      
      return (propValMap.isEmpty()) ? null : propValMap;
    } else {
      // no matching property names
      return null;
    }
  }

  /**
   * @effects 
   *  if exists Property p s.t. p.pkey = pKey
   *    return true
   *  else
   *    return false
   */
  public boolean containsProperty(String pKey) {
    for (Property p : props) {
      if (p.getPkey().equals(pKey)) {
        return true;
      }
    }
    
    return false;
  }
  

  /**
   * 
   * @requires 
   *  propName != null /\ val != null
   *  
   * @effects 
   *  if exists property whose key is <tt>propName.name()</tt> whose value equal to <tt>val</tt>
   *    return true
   *  else
   *    return false
   *     
   * @version 2.7.4
   */
  public boolean containsProperty(PropertyName propName, Object val) {
    for (Property p : props) {
      if (p.getPkey().equals(propName.name())) {
        if (p.getValue().equals(val))
          return true;
      }
    }
    
    return false;
  }

  /**
   * @effects 
   *  sets value of property whose key is <tt>pKey</tt> to <tt>val</tt> (overwriting if it already exists); 
   *  return the old value of the property.
   *  
   *  <p>If the property with the specified <tt>pKey</tt> does not exist then it is created with 
   *  value being set to <tt>val</tt>.
   *   
   * @version 2.8
   */
  public Object setProperty(String pKey, Object val) {
    Property prop = getProperty(pKey);
    if (prop == null) {
      prop = Property.createInstance(pKey, val, this);
      addProperty(prop);
      
      return val;
    } else {
      // change property value
      return prop.setValue(val);
    }
  }
  
  /**
   * @effects 
   *  sets value of property whose key is <tt>propName.name</tt> to <tt>val</tt> (overwriting if it already exists); 
   *  return the old value of the property
   *   
   *  <p>If the property with the specified <tt>propName.name</tt> does not exist then it is created with 
   *  value being set to <tt>val</tt>.
   *   
   * @version 3.1
   */
  public Object setProperty(PropertyName propName, Object val) {
    String pkey = propName.name();
    Property prop = getProperty(pkey);
    if (prop == null) {
      prop = Property.createInstance(pkey, val, this);
      addProperty(prop);
      
      return val;
    } else {
      // change property value
      return prop.setValue(val);
    }
  }
  
  /**
   * @effects 
   *  if exists Property whose key is <tt>propName</tt> return it; else return <tt>null</tt>
   */
  private Property getProperty(String propName) {
    for (Property p : props) {
      if (p.getPkey().equals(propName)) {
        // found
        return p;
      }
    }
    
    return null;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value="props")    
  public boolean addProperty(Property o) {
    if (!props.contains(o)) {
      props.add(o);
    
      propCount++;
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value="props")  
  public boolean addProperty(List<Property> properties) {
    for (Property o : properties) {
      if (!props.contains(o)) {
        props.add(o);
        propCount++;
      }
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkRemover)
  @AttrRef(value="props")    
  public boolean removeProperty(Property o) {
    props.remove(o);
    
    if (propCount > 0)
      propCount--;
    
    // no other attributes changed
    return false; 
  }

  /**
   * @requires 
   *  containsProperty(pKey) = true
   * @modifies this
   * 
   * @effects
   *  replace Property p in this (p.pkey = pKey) by <tt>replacement</tt> 
   */
  public boolean replaceProperty(String pKey, Property replacement) {
    Property p;
    int index = -1;
    for (int i = 0; i < props.size(); i++) {
      p = props.get(i);
      if (p.getPkey().equals(pKey)) {
        index = i; break;
      }
    }
    
    if (index > -1) {
      props.set(index, replacement);
      return true;
    } else {
      return false;
    }
  }
  
  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getPropsCount() {
    return propCount ;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setPropsCount(int count) {
    propCount = count;
  }
  
  public ApplicationModule getAppModule() {
    return appModule;
  }

  public void setAppModule(ApplicationModule appModule) {
    this.appModule = appModule;
  }

  public Region getReferringRegion() {
    return referringRegion;
  }

  public void setReferringRegion(Region refRegion) {
    this.referringRegion = refRegion;
  }

  public Region getRegion() {
    return region;
  }

  public void setRegion(Region region) {
    this.region = region;
  }

  public void setProps(List<Property> props) {
    this.props = props;
  }

  public ControllerConfig getControllerCfg() {
    return controllerCfg;
  }

  public void setControllerCfg(ControllerConfig controllerCfg) {
    this.controllerCfg = controllerCfg;
  }

  public ModelConfig getModelCfg() {
    return modelCfg;
  }

  public void setModelCfg(ModelConfig modelCfg) {
    this.modelCfg = modelCfg;
  }

  /**
   * @requires
   *  other != null
   *  
   * @effects 
   *  merge this.props with other.props as follows
   *  <pre>
   *    for each Property p in other.props
   *      if exists property p' s.t. equals(p'.pkey,p.pkey)
   *        if overwrite = true
   *          replace p' by p
   *      else
   *        add p to this.props
   *  </pre>
   */
  public void mergeProperties(PropertySet other, boolean overwrite) {
    if (other == null) return;
    
    Collection<Property> otherProps = other.getProps();
    boolean contains;
    for (Property p : otherProps) {
      contains = containsProperty(p.getPkey());
      if (contains && overwrite) {
        replaceProperty(p.getPkey(), p);
      } else if (!contains) {
        addProperty(p);
      }
    }
  }

  /**
   * @requires 
   *  other != null /\ other.extensions != null
   * 
   * @modifies this
   * 
   * @effects 
   *  merge other.extensions with this.extensions as follows:
   *  <pre>
   *    for each PropertySet f in other.extensions
   *      if exists PropertySet e in this.extensions s.t. e.name = f.name
   *        for each Property p in f
   *          if p is in e (by key) /\ overwrite = true
   *            replace e.p by f.p
   *          else if p not in e
   *            add p to e
   *      else
   *        add f to this.extensions
   *  </pre>
   */
  public void mergeExtent(PropertySet other, boolean overwrite) {
    if (other.hasExtensions()) {
      Collection<PropertySet> otherExtents = other.getExtensions();
      PropertySet e;
      Collection<Property> props;
      boolean contains;
      for (PropertySet f : otherExtents) {
        e = getExtension(f.getName());
        if (e != null) {  // exists e
          props = f.getProps();
          for (Property p : props) {
            contains = e.containsProperty(p.getPkey());
            if (contains && overwrite) {
              e.replaceProperty(p.getPkey(), p);
            } else if (!contains) {
              e.addProperty(p);
            }
          }
        } else { // e not exists
          addExtension(f);
        }
      }
    }
  }

  /**
   * @effects 
   *  return <tt>PropertySet c</tt> containing a copy of <tt>this</tt> such that:
   *  <pre>
   *    c.props = this.props.clone()
   *    c.extensions = this.extensions.clone() s.t |c.extensions| = |this.extensions| /\
   *      for each PropertSet e in this.extensions
   *        e.clone() in c.extensions
   *      
   *    c.a = this.a for all other attribute x of this
   *  </pre>
   */
  public PropertySet clone() {
    PropertySet c = new PropertySet(this.id, this.name, this.type, this.extensionOf);
    
    // c.props = this.props.clone()
    for (Property p : props) {
      c.addProperty(p);
    }

    /*    c.extensions = this.extensions.clone() s.t |c.extensions| = |this.extensions| /\
     *      for each PropertSet e in this.extensions
     *        e.clone() in c.extensions
     */
    if (extensions != null) {
      for (PropertySet e : extensions) {
        c.addExtension(e.clone());
      }
    }
    
    /* c.a = this.a for all other attribute x of this */
    c.appModule = this.appModule;
    c.referringRegion = this.referringRegion;
    c.region = this.region;
    c.controllerCfg = this.controllerCfg;
    c.modelCfg = this.modelCfg;
    
    return c;
  }

  /**
   * @requires 
   *  minVal != null /\ maxVal != null
   * @effects 
   *  update the auto-generated value of attribute <tt>attrib</tt>, specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
   */
  @DOpt(type=DOpt.Type.AutoAttributeValueSynchroniser)
  public static void updateAutoGeneratedValue(
      DAttr attrib,
      Tuple derivingValue, 
      Object minVal, 
      Object maxVal) throws ConstraintViolationException {    
    if (minVal != null && maxVal != null) {
      // check the right attribute
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)  
          idCounter = maxIdVal;
      } 
      // TODO add support for other attributes here 
    }
  }
  
  @Override
  public String toString() {
    return "PropertySet (" + id + "," + name+")";
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
    PropertySet other = (PropertySet) obj;
    if (id != other.id)
      return false;
    return true;
  }

  /**
   * Defines the available (and extensible) property set names.
   * 
   * @author dmle
   */
  public static enum PropertySetType {
    /**Property set for a module's view */
    ViewConfig, 
    /**Property set for each domain attribute of a module*/
    AttributeViewConfig,
    /**Property set for a module's print configuration*/
    PrintConfig,
    /**Property set for the print configuration of each domain attribute of a module*/
    PrintFieldConfig, 
    /**Property set created from the attributes of an annotation*/
    Annotation,
    /**Property set not belonging to one of the other types*/
    Other,
    ;
    // add more types here
    
    @DAttr(name="name",id=true,type=Type.String,length=50,mutable=false,optional=false)
    public String getName() {
      return name();
    }
  }
}
