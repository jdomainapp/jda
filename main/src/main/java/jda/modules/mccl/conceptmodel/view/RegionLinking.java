package jda.modules.mccl.conceptmodel.view;

import jda.modules.common.CommonConstants;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.model.ModelConfig;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentY;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.util.SysConstants;
import jda.util.properties.PropertySet;

/**
 * @overview 
 *  Represents a linking region, through which the component region of the subview of a child module becomes a child of the region of a composite module's view region.
 *  This allows us to reuse the child module's view configuration to create the subview.  
 *  
 *  <p>We also use {@link RegionLinking} to represent the customised configuration of a descendant module of a composite module.   
 *  
 *  @example <pre>
 *    Given:
 *      Composite module Mp = Module<EnrolmentMgmt>
 *      Child module Mc = Module<Student>
 *    Then:
 *      {@link RegionLinking} R is defined with the following:
 *        parent = Mp.view.compRegion
 *        child = Mc.view.compRegion
 *      also
 *        if the associative domain field of Mp.model has sub-types then 
 *          make R the parent of the compRegions of the views of the modules of these sub-types
 *  </pre>
 * @author dmle
 * @version 
 * - 2.6.4b <br>
 * - 5.1: improved (note) to use for customised configuration.
 */
@DClass(schema="app_config")
public class RegionLinking extends Region {
  
  @DAttr(name="controllerCfg",type=DAttr.Type.Domain,optional=false)
  @DAssoc(ascName="regionLinking-has-controllerConfig",role="regionLinking",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=ControllerConfig.class,cardMin=1,cardMax=1))
  private ControllerConfig controllerCfg;

  @DAttr(name="modelCfg",type=DAttr.Type.Domain)
  @DAssoc(ascName="regionLinking-has-modelConfig",role="regionLinking",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=ModelConfig.class,cardMin=1,cardMax=1))
  private ModelConfig modelCfg;

  // constructor for creating object from data source
  public RegionLinking(Integer id, String name, Label label, String imageIcon, Integer width,
      Integer height, RegionType type, 
      String displayClass,  // support display class 
      String defValue,
      Boolean enabled, Style style,
      Boolean isStateListener,
      Boolean isStateEventSource,   // v2.7.2
      Boolean editable,
      AlignmentX alignX,
      AlignmentY alignY,
      PropertySet printConfig,
      //HelpItem helpItem,           // v2.7.4 
      String layoutBuilderTypeName,  // v2.7.4
      Boolean visible // v3.0
      , PropertySet properties  // v3.0
      , ControllerConfig controllerCfg,
      ModelConfig modelCfg
      ) {
    super(id,name,label,imageIcon,width,height,type,displayClass,defValue,enabled,style,
        isStateListener,isStateEventSource,editable,alignX,alignY, printConfig,//, helpItem
        layoutBuilderTypeName, visible, properties
        );
    this.controllerCfg = controllerCfg;
    this.modelCfg = modelCfg;
  }
  
  /**
   * Constructor used by SetUp to create objects
   * @version 
   *  - 2.7.4
   *  using code must also invoke {@link #setProperties(AttributeDesc)} to initialise other properties that may be 
   *  added in the future
   *  - 3.0 support extension properties
   */
  public RegionLinking(String name, Label label, String imageIcon, String displayClass, 
      Region parent,
      Integer displayOrder, 
      ControllerConfig controllerCfg,
      ModelConfig modelCfg, 
      Boolean visible // v3.0
      , PropertySet properties  // v3.0
      ) {
    super(name, label, imageIcon, displayClass, parent, displayOrder);
    setVisible(visible);
    setProperties(properties);
    this.controllerCfg = controllerCfg;
    this.modelCfg = modelCfg;
  }

  /**
   * @version 2.7.4
   *  added to support additional properties that may be added in the future
   */
  public void setProperties(AttributeDesc attribDesc, RegionGui regionGui) {
    boolean editable = attribDesc.editable();
    setEditable(editable);

    AlignmentX alignX = attribDesc.alignX();
    setAlignX(alignX);
    AlignmentY alignY = attribDesc.alignY();
    setAlignY(alignY);
    
    setLayoutBuilderClass(attribDesc, regionGui);
  }
  
  /**
   * This method is used by Set-ip to update properties of a linked region based on those of the Gui of the referenced type's module.   
   * 
   * @effects 
   *  if <tt>regionGui != null</tt> AND exist shared properties of <tt>regionGui</tt> and <tt>this</tt>, whose values differ from the default
   *    update those properties in <tt>this</tt> using those values
   *  
   * @version 2.7.4
   */
  private void setLayoutBuilderClass(AttributeDesc attribDesc, RegionGui regionGui) {
    // the builder type specified by the attribute 
    Class layoutBuilderType = attribDesc.layoutBuilderType();
    String layoutBuilderCls_1 = null;
    
    if (layoutBuilderType != CommonConstants.NullType)
      layoutBuilderCls_1 = layoutBuilderType.getName();

    String myLayoutBuilderCls = layoutBuilderCls_1;

    // the builder type specified by the referenced GUI (if specified)
    String layoutBuilderCls_2 = null;
    String defBuilder = SysConstants.DEFAULT_LAYOUT_BUILDER.getName();
    if (regionGui != null) {
      layoutBuilderCls_2 = regionGui.getLayoutBuilderClass();
    }
    
    if (layoutBuilderCls_2 != null && !layoutBuilderCls_2.equals(defBuilder) ) {
      if (layoutBuilderCls_1 != null && layoutBuilderCls_1.equals(defBuilder)) {
        // update builder type
        myLayoutBuilderCls = layoutBuilderCls_2;
      }
    }

    setLayoutBuilderClass(myLayoutBuilderCls);
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
   * @effects 
   *  return {@link RegionLinking}<tt>::id</tt>
   * @version 5.1
   */
  public String getFormalObjId() {
    return RegionLinking.class.getSimpleName()+"::"+getId();
  }
}
