package jda.modules.mccl.conceptmodel.module.containment;

import jda.modules.common.CommonConstants;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.model.ModelConfig;
import jda.modules.mccl.conceptmodel.view.Label;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionGui;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.conceptmodel.view.Style;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentY;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.util.SysConstants;
import jda.util.properties.PropertySet;

/**
 * @overview 
 *  Represents configuration for a module containment, which is set in the containment tree of a composite module. 
 *  It is a {@link Region} that capture properties of {@link AttributeDesc}.
 *  
 *  <p>{@link RegionContainment} objects are created when the containment tree is created and are 
 *  stored in the object store. The object {@link #getId()} is recorded in the containment edge so that it can 
 *  be used to retrieved {@link RegionContainment} object from the object store at run-time.     
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.1
 */
@DClass(schema="app_config")
public class RegionContainment extends Region {

  @DAttr(name="controllerCfg",type=DAttr.Type.Domain,optional=false)
  @DAssoc(ascName="regionContainment-has-controllerConfig",role="regionContainment",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=ControllerConfig.class,cardMin=1,cardMax=1))
  private ControllerConfig controllerCfg;

  @DAttr(name="modelCfg",type=DAttr.Type.Domain)
  @DAssoc(ascName="regionContainment-has-modelConfig",role="regionContainment",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=ModelConfig.class,cardMin=1,cardMax=1))
  private ModelConfig modelCfg;

  // constructor for creating object from data source
  public RegionContainment(Integer id, String name, Label label, String imageIcon, Integer width,
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
  public RegionContainment(String name, Label label, String imageIcon, String displayClass, 
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
   * This method is used by Set-up to update properties of a linked region based on those of the Gui of the referenced type's module.   
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
}
