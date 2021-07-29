package jda.modules.mccl.conceptmodel.view;

import javax.swing.ImageIcon;

import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentY;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.assets.GUIToolkit;
import jda.util.properties.PropertySet;

@DClass(schema="app_config")
public class RegionGui extends Region {
  @DAttr(name = "domainClassLabel", type = DAttr.Type.Domain, length = 255, optional = true)
  private Label domainClassLabel; 

  @DAttr(name="applicationModule",type=DAttr.Type.Domain,optional=false,
      serialisable=false  // v2.7
      )
  @DAssoc(ascName="module-has-viewConfig",role="viewConfig",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=ApplicationModule.class,cardMin=1,cardMax=1,determinant=true))  // v2.7
  private ApplicationModule applicationModule;

  @DAttr(name = "rootContainerTypeName",type = DAttr.Type.String)
  private String rootContainerTypeName; // v2.7.2
  
  @DAttr(name = "topX",type = DAttr.Type.Double)
  private double topX;
  
  @DAttr(name = "topY",type = DAttr.Type.Double)
  private double topY;

  @DAttr(name = "widthRatio",type = DAttr.Type.Float)
  private float widthRatio;
  
  @DAttr(name = "heightRatio",type = DAttr.Type.Float)
  private float heightRatio;

  @DAttr(name = "resizable",type = DAttr.Type.Boolean)
  private boolean resizable;
  
  @DAttr(name = "relocatable",type = DAttr.Type.Boolean)
  private boolean relocatable;

  // v2.7.4
  @DAttr(name = "titleIcon",type = DAttr.Type.String)
  private String titleIcon;

  /** derived from {@link titleIcon} */ 
  private ImageIcon titleIconObj;
  
  // constructor for creating object from data source
  public RegionGui(Integer id, String name, Label label, String imageIcon, Integer width,
      Integer height, RegionType type, 
      String displayClass,  // support display class 
      String defValue, Boolean enabled, Style style, 
      Boolean isStateListener,
      Boolean isStateEventSource,    // v2.7.2
      Boolean editable,
      AlignmentX alignX,
      AlignmentY alignY,
      PropertySet printConfig,        //v2.7.2
      String layoutBuilderTypeName,  // v2.7.4
      Boolean visible // v3.0
      , PropertySet properties  // v3.0
      //HelpItem helpItem,           // v2.7.4
      , Label classLabel,               // v2.7.3
      String rootContainerTypeName,   // v2.7.2
      Double topX,
      Double topY,   // v2.7.2
      Float widthRatio,
      Float heightRatio,
      Boolean resizable,  // v2.7.4
      Boolean relocatable,
      String titleIcon
      ) {
    super(id,name,label,imageIcon,width,height,type,displayClass,defValue,enabled,style,
        //null,null,null,
        isStateListener,isStateEventSource,editable,null,null,printConfig,
        //,helpItem
        layoutBuilderTypeName, 
        visible,
        properties
        );
    //this.module=module;
    this.domainClassLabel = classLabel;
    this.rootContainerTypeName = rootContainerTypeName;
    this.topX = topX;
    this.topY = topY;
    this.widthRatio = widthRatio;
    this.heightRatio = heightRatio;

    this.resizable = resizable;
    this.relocatable=relocatable;
    this.titleIcon = titleIcon;
  }
  
  // this contructor apprears to not being used...????
//  public RegionGui(String name, Label label, String imageIcon, Integer width,
//      Integer height, Type type, String defValue, Boolean enabled, Style style,
//      //v2.7:, ApplicationModule module
//      String rootContainerTypeName,   // v2.7.2
//      Double topX,
//      Double topY
//      ) {
//    super(null,name,label,imageIcon,width,height,type,defValue,enabled,style
//        //,null,null
//        );
//    this.rootContainerTypeName = rootContainerTypeName;
//    this.topX = topX;
//    this.topY = topY;    
//  }

  
  // constructor for creating object from the module configuration
  private RegionGui(String name, Label label, 
      String imageIcon, 
      Integer width,
      Integer height,   // v2.7.2
      RegionType type, String displayClass, 
      //v2.7: ApplicationModule module
      Label domainClassLabel, // v2.7.3
      String rootContainerTypeName,   // v2.7.2
      Double topX,
      Double topY,
      Float widthRatio,
      Float heightRatio,
      Boolean resizable,  // v2.7.4
      Boolean relocatable,
      String titleIcon,
      String layoutBuilderTypeName,
      Boolean visible // v3.0
      , PropertySet properties  // v3.0
      ) {
    super(null,name,label,imageIcon,width,height,type,displayClass,
        null,null,null,null,null,null,null,null,null//,null
        ,layoutBuilderTypeName, visible, properties
        );
    this.domainClassLabel = domainClassLabel;    
    this.rootContainerTypeName = rootContainerTypeName;
    this.topX = topX;
    this.topY = topY;;
    this.widthRatio = widthRatio;
    this.heightRatio = heightRatio;
    this.resizable = resizable;
    this.relocatable=relocatable;
    this.titleIcon=titleIcon;
  }
  
  // v2.7.2
  public static RegionGui createInstance(String name, Label titleLabel, 
      Label domainClassLabel, 
      PropertySet properties, // v3.0 
      ViewDesc viewDesc // v2.7.4
      ) {
    Class guiClass = viewDesc.view();
    String guiClassName = null;
    if (guiClass != CommonConstants.NullType) {
      guiClassName = guiClass.getName();
    }

    String layoutBuilderTypeName = null;
    Class layoutBuilderType = viewDesc.layoutBuilderType();
    
    if (layoutBuilderType != CommonConstants.NullType) {
      layoutBuilderTypeName = layoutBuilderType.getName();
    }
    
    boolean visible = true;
    return new RegionGui(name, 
        titleLabel, 
        viewDesc.imageIcon(), 
        viewDesc.width(),
        viewDesc.height(),
        viewDesc.viewType(),
        //viewDesc.view().getName(),
        guiClassName,
        domainClassLabel, 
        viewDesc.topContainerType().getName(),
        viewDesc.topX(),
        viewDesc.topY(),
        viewDesc.widthRatio(),
        viewDesc.heightRatio(),
        viewDesc.resizable(), // v2.7.4
        viewDesc.relocatable(),
        viewDesc.formTitleIcon(),
        layoutBuilderTypeName, //  b2.7.4
        visible // v3.0
        , properties
        );    
  }
  
  public Label getDomainClassLabel() {
    return domainClassLabel;
  }

  public void setDomainClassLabel(Label classLabel) {
    this.domainClassLabel = classLabel;
  }

  /**
   * @effects (derived from {@link #domainClassLabel}) 
   *  if classLabel != null
   *    return classLabel.value
   *  else
   *    return null
   */
  public String getDomainClassLabelAsString() {
    if (domainClassLabel != null) {
      return domainClassLabel.getValue();
    } else {
      return null;
    }
  }
  
  public ApplicationModule getApplicationModule() {
    return applicationModule;
  }

  public void setApplicationModule(ApplicationModule module) {
    this.applicationModule = module;
  }

  public String getRootContainerTypeName() {
    return rootContainerTypeName;
  }

  public void setRootContainerTypeName(String rootContainerTypeName) {
    this.rootContainerTypeName = rootContainerTypeName;
  }

  public Class getRootContainerType() throws NotFoundException {
    if (rootContainerTypeName != null) {
      try {
        return Class.forName(rootContainerTypeName);
      } catch (ClassNotFoundException e) {
        throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e,
            "Không tìm thấy lớp {0}", rootContainerTypeName);
      }
    } else {
      return null;
    }
  }
  
  public double getTopX() {
    return topX;
  }

  public void setTopX(double topX) {
    this.topX = topX;
  }

  public double getTopY() {
    return topY;
  }

  public void setTopY(double topY) {
    this.topY = topY;
  }

  public float getWidthRatio() {
    return widthRatio;
  }

  public void setWidthRatio(float widthRatio) {
    this.widthRatio = widthRatio;
  }

  public float getHeightRatio() {
    return heightRatio;
  }

  public void setHeightRatio(float heightRatio) {
    this.heightRatio = heightRatio;
  }

  @Override
  public boolean isSizeConfigured() {
    // either (width,height) OR (widthRatio,heightRatio) is specified
    return (super.isSizeConfigured() ||
        (widthRatio > -1 && heightRatio > -1));
  }

  public boolean getResizable() {
    return resizable;
  }

  public void setResizable(boolean resizable) {
    this.resizable = resizable;
  }

  public boolean getRelocatable() {
    return relocatable;
  }

  public void setRelocatable(boolean relocatable) {
    this.relocatable = relocatable;
  }

  /**
   * @effects 
   *  if exists the icon that is used next to the title label of this
   *    return it
   *  else
   *    return null
   */
  public String getTitleIcon() {
    return titleIcon;
  }
  
  public void setTitleIcon(String titleIcon) {
    this.titleIcon = titleIcon;
  }

  public ImageIcon getTitleIconObject() {
    if (titleIcon != null) {
      if (titleIconObj == null) {
        try {
          titleIconObj = GUIToolkit.getImageIcon(titleIcon, "");
        } catch (NotFoundException e) {
          // ignore
        }
      }
      return titleIconObj;
    } else {
      return null;
    }
  }
}
