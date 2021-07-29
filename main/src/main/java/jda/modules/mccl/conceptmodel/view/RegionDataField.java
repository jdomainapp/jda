package jda.modules.mccl.conceptmodel.view;

import jda.modules.common.CommonConstants;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.mccl.conceptmodel.model.ModelConfig;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentY;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.util.properties.PropertySet;

/**
 * @overview The region configuration for a data field.
 * @author dmle
 */
@DClass(schema="app_config")
public class RegionDataField extends Region {
  @DAttr(name="boundAttributes",type=DAttr.Type.String,length=50)
  private String boundAttributes;

// v2.6.4.b : removed
//  @DomainConstraint(name="loadBoundValues",type=domainapp.model.meta.DomainConstraint.Type.Boolean)
//  private boolean loadBoundValues;

  // v2.6.4.b
  @DAttr(name="displayOidWithBoundValue",type=DAttr.Type.Boolean)
  private boolean displayOidWithBoundValue;

  @DAttr(name="loadOidWithBoundValue",type=DAttr.Type.Boolean)
  private boolean loadOidWithBoundValue;

  /*v3.0: moved to Region
  @DomainConstraint(name="visible",type=DomainConstraint.Type.Boolean)
  private boolean visible;
  */
  
  // v2.7.4
  @DAttr(name="labelOnly",type=DAttr.Type.Boolean)
  private boolean labelOnly;
  
  @DAttr(name="modelCfg",type=DAttr.Type.Domain)
  @DAssoc(ascName="regionField-has-modelConfig",role="regionField",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=ModelConfig.class,cardMin=1,cardMax=1))
  private ModelConfig modelCfg;
  
  // constructor used for creating objects from data source
  public RegionDataField(Integer id, String name, Label label, String imageIcon, Integer width,
      Integer height, RegionType type, 
      String displayClass,  // support display class 
      String defValue,
      Boolean enabled, Style style,
      Boolean isStateListener,
      Boolean isStateEventSource,    // v2.7.2
      Boolean editable,
      AlignmentX alignX,
      AlignmentY alignY,
      PropertySet printConfig,
      String layoutBuilderClass,  // v2.7.4
      Boolean visible   // v2.7.2
      , PropertySet properties  // v3.0
      //HelpItem helpItem,           // v2.7.4
      , String boundAttributes, //
      //Boolean loadBoundValues, //v2.6.4.b : removed
      Boolean displayOidWithBoundValue,  // v2.6.4.b
      Boolean loadOidWithBoundValue,
      Boolean labelOnly,   // v2.7.4
      ModelConfig modelCfg
      ) {
    super(id,name,label,imageIcon,width,height,type,displayClass,defValue,enabled,style,
        //null,null,null,
        isStateListener,isStateEventSource,editable, alignX, alignY, printConfig,
        //, helpItem
        layoutBuilderClass, 
        visible,
        properties
        );
    
    // the attributes are comma-separated
    this.boundAttributes=boundAttributes;
    //this.loadBoundValues=loadBoundValues;
    this.displayOidWithBoundValue=displayOidWithBoundValue;
    this.loadOidWithBoundValue = loadOidWithBoundValue;
//    this.visible = visible;
    this.labelOnly = labelOnly;
    this.modelCfg = modelCfg;
  }
  
  /*v 2.7.2:
  // this contructor apprears to not being used...????
  public RegionDataField(String name, Label label, String imageIcon, Integer width,
      Integer height, Type type, String displayClass, String defValue, Boolean enabled, Style style, 
      String boundAttributes, 
      Boolean loadBoundValues,
      Boolean displayOidWithBoundValue, 
      Boolean loadOidWithBoundValue) {
    this(null,name,label,imageIcon,width,height,type,displayClass,defValue,enabled,style,
        null,null,null,boundAttributes,
        //loadBoundValues, 
        displayOidWithBoundValue, 
        loadOidWithBoundValue);
  }
  */
  

  /**
   * Constructor used by SetUp to create objects
   * @version 2.7.4
   *  using code must also invoke {@link #setProperties(AttributeDesc)} to initialise other properties that may be 
   *  added in the future
   */
  public RegionDataField(String name, Label label, String imageIcon, String displayClass, 
      Region parent, Integer displayOrder,
      Boolean visible  // v2.7.2
      , PropertySet properties  // v3.0
      , ModelConfig modelCfg
      ) {
    super(name, label, imageIcon, displayClass, parent, displayOrder);
    setVisible(visible);
    setProperties(properties);
    this.modelCfg=modelCfg;
  }
  
  /**
   * @version 2.7.4
   *  added to support additional properties that may be added in the future
   */
  public void setProperties(AttributeDesc attribDesc) {
    setLoadOidWithBoundValue(attribDesc.loadOidWithBoundValue());
    setDisplayOidWithBoundValue(attribDesc.displayOidWithBoundValue());
    
    // v2.7.2:
    AlignmentX alignX = attribDesc.alignX();
    AlignmentY alignY = attribDesc.alignY();
    setAlignX(alignX);
    setAlignY(alignY);
    
    // v2.7.4
    boolean labelOnly = attribDesc.labelOnly();
    setLabelOnly(labelOnly);
    
    Class layoutBuilderType = attribDesc.layoutBuilderType();
    String layoutBuilderCls = null;
    
    if (layoutBuilderType != CommonConstants.NullType)
      layoutBuilderCls = layoutBuilderType.getName();
    
    setLayoutBuilderClass(layoutBuilderCls);
  }
  
  public void setLabelOnly(boolean labelOnly) {
    this.labelOnly = labelOnly;
  }

  public boolean getLabelOnly() {
    return labelOnly;
  }

  public String getBoundAttributes() {
    return boundAttributes;
  }

  public void setBoundAttributes(String attributes) {
    this.boundAttributes=attributes;
  }

//  public boolean getLoadBoundValues() {
//    return loadBoundValues;
//  }

//  public void setLoadBoundValues(boolean loadBoundValues) {
//    this.loadBoundValues = loadBoundValues;
//  }

  public boolean getDisplayOidWithBoundValue() {
    return displayOidWithBoundValue;
  }

  public void setDisplayOidWithBoundValue(boolean displayOidWithBoundValue) {
    this.displayOidWithBoundValue = displayOidWithBoundValue;
  }

  public boolean getLoadOidWithBoundValue() {
    return loadOidWithBoundValue;
  }

  public void setLoadOidWithBoundValue(boolean loadOidWithBoundValue) {
    this.loadOidWithBoundValue = loadOidWithBoundValue;
  }

//  public boolean getVisible() {
//    return visible;
//  }
//
//  public void setVisible(boolean visible) {
//    this.visible = visible;
//  }

  public ModelConfig getModelCfg() {
    return modelCfg;
  }

  public void setModelCfg(ModelConfig modelCfg) {
    this.modelCfg = modelCfg;
  }
}
