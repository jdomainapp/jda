package jda.modules.mccl.conceptmodel.model;

import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.RegionDataField;
import jda.modules.mccl.conceptmodel.view.RegionLinking;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.util.properties.PropertySet;

@DClass(schema="app_config")
public class ModelConfig {
  
  public static final String AttributeName_domainClass = "domainClass";

  @DAttr(name="id",type=Type.Long,id=true,mutable=false,optional=false)
  private long id;
  
  @DAttr(name=AttributeName_domainClass,type=Type.String,length=255,mutable=false,optional=false)
  private String domainClass;
  // derived
  @DAttr(name="domainClassCls",auto=true,serialisable=false)
  private Class domainClassCls;

  // v2.7.2
  @DAttr(name="dataSourceType",type=Type.String,optional=false)
  private String dataSourceType;

  /** derived from {@link #getDataSourceCls()}*/
  private Class dataSourceCls;

  @DAttr(name="editable",type=Type.Boolean,optional=false)
  private boolean editable;

  @DAttr(name="indexable",type=Type.Boolean,optional=false)
  private boolean indexable;
  
  // v3.0: support additional properties
  @DAttr(name="properties",type=DAttr.Type.Domain)
  @DAssoc(ascName="modelCfg-has-properties",role="modelCfg",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=PropertySet.class,cardMin=0,cardMax=1))  
  private PropertySet properties;
  
  @DAttr(name="applicationModule",type=Type.Domain,serialisable=false)
  @DAssoc(ascName="module-has-modelConfig",role="modelConfig",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=ApplicationModule.class,cardMin=1,cardMax=1,determinant=true))
  private ApplicationModule applicationModule;
  
  @DAttr(name="linkedRegion",type=Type.Domain,serialisable=false)
  @DAssoc(ascName="regionLinking-has-modelConfig",role="modelConfig",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=RegionLinking.class,cardMin=1,cardMax=1,determinant=true))
  private RegionLinking linkedRegion;
  
//  // v5.1
//  @DAttr(name="containRegion",type=Type.Domain,serialisable=false)
//  @DAssoc(ascName="regionContainment-has-modelConfig",role="modelConfig",
//      ascType=AssocType.One2One,endType=AssocEndType.One,
//      associate=@Associate(type=RegionContainment.class,cardMin=1,cardMax=1,determinant=true))
//  private RegionContainment containRegion;
  
  @DAttr(name="regionField",type=Type.Domain,serialisable=false)
  @DAssoc(ascName="regionField-has-modelConfig",role="modelConfig",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=RegionDataField.class,cardMin=1,cardMax=1,determinant=true))
  private RegionDataField regionField;
  
  // constructor to create objects from the configuration
  public ModelConfig(Class domainClass, 
      Class dataSourceType,   // v2.7.2 
      Boolean editable, Boolean indexable
      , PropertySet properties // v3.0
      ) {
    this.id = nextId(null);
    
    if (domainClass != null)
      this.domainClass = domainClass.getName();    
    
    this.domainClassCls=domainClass;
    
    if (dataSourceType != null)
      this.dataSourceType = dataSourceType.getName();
    
    this.dataSourceCls = dataSourceType;
    
    this.editable = editable;
    
    this.indexable = indexable;
    
    this.properties = properties;
  }
  
  // constructors to create objects from the data source
  public ModelConfig(Long id, String domainClass, 
      String dataSourceType,    // v2,7.2
      Boolean editable, Boolean indexable
      , PropertySet properties // v3.0
      ) {
    this.id = nextId(id);
    this.domainClass = domainClass;
    try {
      if (domainClass != null)
        this.domainClassCls=Class.forName(domainClass);
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e, new Object[] {domainClass});
    }

    this.dataSourceType = dataSourceType;
    try {
      if (dataSourceType != null)
        this.dataSourceCls =Class.forName(dataSourceType);
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e, new Object[] {domainClass});
    }

    this.editable = editable;
    this.indexable = indexable;
    
    this.properties = properties;
  }
  
  private long nextId(Long currId) {
    // use current time as id
    if (currId == null)
      return System.nanoTime();
    else
      return currId;
  }

  public long getId() {
    return id;
  }

  public String getDomainClass() {
    return domainClass;
  }

  public Class getDomainClassCls() {
    return this.domainClassCls;
  }

  public void setDomainClass(String domainClass) {
    this.domainClass = domainClass;
  }
  
  public void setDomainClassCls(Class cls) {
    this.domainClassCls=cls;
    if (cls != null)
      this.domainClass=cls.getName();
  }
  
  public String getDataSourceType() {
    return dataSourceType;
  }

  public void setDataSourceType(String dataSourceType) {
    this.dataSourceType = dataSourceType;
  }

  public Class getDataSourceCls() {
    return dataSourceCls;
  }

  public void setDataSourceCls(Class dataSourceCls) {
    this.dataSourceCls = dataSourceCls;
    if (dataSourceCls != null)
      dataSourceType = dataSourceCls.getName();
  }

  public boolean getEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public boolean getIndexable() {
    return indexable;
  }

  public void setIndexable(boolean indexable) {
    this.indexable = indexable;
  }

  public void setProperties(PropertySet pset) {
    this.properties = pset;
  }
  
  public PropertySet getProperties() {
    return this.properties;
  }

  /**
   * 
   * @requires 
   *  propName != null /\ val != null
   * @effects 
   *  if property <tt>propName</tt> of <tt>this</tt> has value equal to <tt>val</tt>
   *    return true
   *  else
   *    return false
   */
  public boolean hasProperty(PropertyName propName, Object val) {
    boolean hasIt = false;
    if (properties != null) {
      hasIt = properties.containsProperty(propName, val);
    }
    
    return hasIt;
  }


  /**
   * 
   * @requires 
   *  propName != null
   * @effects 
   *  if exists property <tt>propName</tt> of <tt>this</tt> 
   *    return its value
   *  else
   *    return <tt>defaultVal</tt>
   */  
  public Object getProperty(PropertyName propName, Object defaultVal) {
    Object val = null;
    if (properties != null) {
      val = properties.getPropertyValue(propName, defaultVal);
    } else {
      val = defaultVal;
    }
    
    return val;
  }
  
  public ApplicationModule getApplicationModule() {
    return applicationModule;
  }

  public void setApplicationModule(ApplicationModule applicationModule) {
    this.applicationModule = applicationModule;
  }

  
  public RegionLinking getLinkedRegion() {
    return linkedRegion;
  }

  public void setLinkedRegion(RegionLinking linkedRegion) {
    this.linkedRegion = linkedRegion;
  }

//  /**
//   * @effects return containRegion
//   */
//  public RegionContainment getContainRegion() {
//    return containRegion;
//  }
//
//  /**
//   * @effects set containRegion = containRegion
//   */
//  public void setContainRegion(RegionContainment containRegion) {
//    this.containRegion = containRegion;
//  }

  public RegionDataField getRegionField() {
    return regionField;
  }

  public void setRegionField(RegionDataField regionField) {
    this.regionField = regionField;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
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
    ModelConfig other = (ModelConfig) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ModelConfig (" + id + ", " + domainClass + ")";
  }

  public static ModelConfig createInstance(ModelDesc modelDesc, 
      PropertySet properties  // v3.0
      ) {
    Class domainCls = modelDesc.model();
    if (domainCls == CommonConstants.NullType) {
      domainCls = null;
    }

    Class dataSourceCls = modelDesc.dataSourceType();
    if (dataSourceCls == CommonConstants.NullType) {
      dataSourceCls = null;
    }
    
    
    return new ModelConfig(domainCls, 
        dataSourceCls,  // v2.7.2
        modelDesc.editable(), modelDesc.indexable()
        , properties
        );
  }

  /**
   * @effects 
   *  merge values of <tt>otherCfg</tt> in and taking precendence over this 
   * @version 5.2b
   * TODO: complete this merge for all attributes!
   */
  public void merge(ModelConfig otherCfg) {
    if (otherCfg == null) return;
    
    if (otherCfg.domainClass != null)
      setDomainClass(otherCfg.domainClass);;
    
  }
}
