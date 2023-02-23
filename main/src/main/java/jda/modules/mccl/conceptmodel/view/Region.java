package jda.modules.mccl.conceptmodel.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.module.containment.ScopeDef;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentY;
import jda.mosa.view.assets.GUIToolkit;
import jda.util.properties.Property;
import jda.util.properties.PropertySet;

/**
 * @overview Represents a configuration region.
 * 
 * @author dmle
 */
@DClass(schema="app_config")
public class Region {
  
  
  
//  /**
//   * All region names related to data manipulation operations
//   * @version 2.7.2 
//   */
//  public static final RegionName[] AllDataRegionNames  = {
//    First, Last, Next, Previous, 
//    Add, New, Create, Delete, Update,
//    Actions
//  };
  
  public static final String AttributeName_name = "name";

  // v5.1:
  public static final String Assoc_hasChildren = "parent-has";
  
  @DAttr(name = "id", id = true, auto = true, type = DAttr.Type.Integer, length = 6, optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  @DAttr(name = AttributeName_name, type = DAttr.Type.String, length = 50, optional = false)
  private String name;
  @DAttr(name = "label", type = DAttr.Type.Domain, length = 255, optional = false)
  private Label label;
  
  @DAttr(name = "imageIcon", type = DAttr.Type.String, length = 50)
  private String imageIcon;

  /**derived from {@link #imageIcon}*/
  private ImageIcon icon;
  
  @DAttr(name = "width", type = DAttr.Type.Integer, length = 10)
  private Integer width;
  @DAttr(name = "height", type = DAttr.Type.Integer, length = 10)
  private Integer height;
  @DAttr(name = "type", type = DAttr.Type.Domain, length = 10)
  private RegionType type;
  @DAttr(name = "displayClass",type = DAttr.Type.String)
  private String displayClass;
  // derived
  private Class displayClassType;
  
  @DAttr(name = "defValue", type = DAttr.Type.String, length = 30)
  private String defValue;
  @DAttr(name = "enabled", type = DAttr.Type.Boolean, length = 5, defaultValue = "true")
  private Boolean enabled;
  
  @DAttr(name = "style", type = DAttr.Type.Domain, length = 6)
  private Style style;

  @DAttr(name = "children", type = DAttr.Type.Collection, serialisable = false,
      optional=false,
      filter=@Select(clazz=RegionMap.class)//,role="parent"
      )
  //@Update(add="addChildMap",delete="removeChildMap")
  @DAssoc(ascName=Assoc_hasChildren// v5.1: "parent-has"
    ,role="parent",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=RegionMap.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE))
  private List<RegionMap> children;

  @DAttr(name = "parents", type = DAttr.Type.Collection, serialisable = false,
      optional=false,
      filter=@Select(clazz=RegionMap.class)//,role="child"
      )
  //@Update(add="addParentMap",delete="removeParentMap")
  @DAssoc(ascName="child-has",role="child",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=RegionMap.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE))
  private List<RegionMap> parents;

  /** the regions that are excluded from the use of this region */
  @DAttr(name = "exclusion", type = DAttr.Type.Collection, serialisable = false,
      optional=false,
      filter=@Select(clazz=ExclusionMap.class)//,role="source"
  )
  //@Update(add="addExclusionMap",delete="removeExclusionMap")
  @DAssoc(ascName="source-has",role="source",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=ExclusionMap.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE))
  private List<ExclusionMap> exclusion;
  
  @DAttr(name="isStateListener",type=DAttr.Type.Boolean)
  private boolean isStateListener;

  // v2.7.2
  @DAttr(name="isStateEventSource",type=DAttr.Type.Boolean)
  private boolean isStateEventSource;

  @DAttr(name="editable",type=DAttr.Type.Boolean)
  private boolean editable;
  
  // v2.7.2
  @DAttr(name="alignX",type=DAttr.Type.Domain,optional=false)
  private AlignmentX alignX;

  @DAttr(name="alignY",type=DAttr.Type.Domain,optional=false)
  private AlignmentY alignY;

  @DAttr(name="printConfig",type=DAttr.Type.Domain)
  @DAssoc(ascName="region-has-printCfg",role="region",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=PropertySet.class,cardMin=0,cardMax=1))
  private PropertySet printConfig;

  // v2.7.4
  @DAttr(name = "layoutBuilderClass",type = DAttr.Type.String)
  private String layoutBuilderClass;
  // derived
  private Class layoutBuilderType;
  
  /*v3.0*/
  @DAttr(name="visible",type=DAttr.Type.Boolean)
  private boolean visible;
  
  // v3.0
  @DAttr(name="properties",type=DAttr.Type.Domain)
  @DAssoc(ascName="region-has-properties",role="region",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=PropertySet.class,cardMin=0,cardMax=1))  
  private PropertySet properties;
  
  /**
   *  A non-serialisable {@link Map} which maps user modules to <b>specialised</b> {@link ScopeDef}s specified for the 
   *  this region. Only certain modules in certain applications need to have these scope definitions.
   *  
   *  <p>This mapping is populated at run-time when this region is used to create the actual 
   *  data components of the user modules.
   *  
   *  @version 3.2
   */
  private Map<ApplicationModule,ScopeDef> userModuleScopeMap;
  
  // v2.7.4
//  @DomainConstraint(name="helpItem",type=DomainConstraint.Type.Domain,length=30)
//  @Association(name="region-has-helpitem",role="region",
//  type=AssocType.One2One,endType=AssocEndType.One,
//  associate=@AssocEnd(type=HelpItem.class,cardMin=0,cardMax=1))
//  private HelpItem helpItem;
  

  /*
   * constructor methods
   */
  /**
   * This is the <b>base</b> initialisation method which is invoked by other constructors
   * 
   * <p>However, it is NOT the one that is invoked when <tt>Region</tt> objects are loaded from the data source.
   * 
   * <p>As such, this constructor <b>MAY NOT initialise all the attributes</b> of this. 
   */  
  private void initBase(Integer id, String name, Label label, String imageIcon,
      Integer width, Integer height, RegionType type, 
      String displayClass,  // support display class 
      String defValue,
      Boolean enabled, Style style, 
      List<RegionMap> children, 
      List<RegionMap> parents,
      List<ExclusionMap> exclusion, 
      Boolean isStateListener,
      Boolean editable,
      AlignmentX alignX,  // v2.7.2
      AlignmentY alignY
      ) {
    this.id = nextID(id);
    this.name = name;
    this.label = label;
    this.imageIcon = imageIcon;
    this.width = width;
    this.height = height;
    this.type = type;
    
    this.displayClass=displayClass;
    
    this.defValue = defValue;
    this.enabled = enabled;
    this.style = style;
    if (children == null) {
      this.children = new ArrayList();
    } else {
      this.children = children;
    }
    if (parents == null) {
      this.parents = new ArrayList();
    } else {
      this.parents = parents;
    }
    
    if (exclusion == null) {
      this.exclusion = new ArrayList();
    } else {
      this.exclusion=exclusion;
    }
    
    if (isStateListener != null) {
      this.isStateListener = isStateListener;
    } else {
      this.isStateListener=false;
    }
    
    if (editable != null) {
      this.editable = editable;
    } else {
      this.editable=true; // default
    }
    
    this.alignX = alignX;
    this.alignY = alignY;
  }
  
  /**
   * This is the <b>data source</b> contructor method, which is invoked when <tt>Region</tt> objects are loaded from the data source.
   * 
   * <p>Sub-types must redefine a similar constructor for their objects.
   */  
  public Region(Integer id, String name, Label label, String imageIcon,
      Integer width, Integer height, RegionType type, 
      String displayClass,  // support display class 
      String defValue, Boolean enabled, Style style, 
      Boolean isStateListener, 
      Boolean isStateEventSource, // v2.7.2
      Boolean editable, 
      AlignmentX alignX,
      AlignmentY alignY, 
      PropertySet printConfig
      //HelpItem helpItem           // v2.7.4
      , String layoutBuilderClass,
      Boolean visible   // 3.0
      , PropertySet properties  // v3.0
      ) {
    // invoke the BASE constructor
    /* v2.7.4:
    this(id, name, label, imageIcon, width, height, type, displayClass, defValue, enabled, style,
        null, null,null,isStateListener,editable, alignX, alignY);
        */
    initBase(id, name, label, imageIcon, width, height, type, displayClass, defValue, enabled, style,
        null, null,null,isStateListener,editable, alignX, alignY);
    
    if (isStateEventSource != null)
      this.isStateEventSource = isStateEventSource;
    else
      this.isStateEventSource = false;
    
    this.printConfig = printConfig;
    //this.helpItem = helpItem;
    
    this.layoutBuilderClass = layoutBuilderClass;
    
    this.visible = visible;
    
    this.properties = properties;
  }
  
//  public Region(Integer id, String name, Label label, String imageIcon,
//      Integer width, Integer height, Type type, 
//      String displayClass,  // support display class 
//      String defValue, Boolean enabled, Style style, 
//      Boolean isStateListener, 
//      Boolean isStateEventSource, // v2.7.2
//      Boolean editable
//      ) {
//    //
//  }
  
  public Region(Integer id, String name, Label label, String imageIcon,
      Integer width, Integer height, RegionType type, String defValue,
      Boolean enabled, Style style
      //, List<RegionMap> children, List<RegionMap> parents
      ) {
    // invoke the BASE constructor
    /*v2.7.4: 
    this(id,name, label, imageIcon, width, height, type, 
        null,   // display class 
        defValue, enabled, style, 
        null, null,null,null,null,
        //alignX, alignY
        null,null
        );
    */
    initBase(id,name, label, imageIcon, width, height, type, 
        null,   // display class 
        defValue, enabled, style, 
        null, null,null,null,null,
        //alignX, alignY
        null,null
        );
  }

  
//  public Region(String name, Label label, String imageIcon, Integer width,
//      Integer height, Type type, String defValue, Boolean enabled, Style style) {
//    this(null, name, label, imageIcon, width, height, type, defValue, enabled, style,
//        null, null);
//  }

//  public Region(String name, Label label, String imageIcon, Integer width,
//      Integer height, Type type, String defValue, Boolean enabled, Style style,
//      List<RegionMap> children, List<RegionMap> parents) {
//    this(null, name, label, imageIcon, width, height, type, defValue, enabled, style,
//        children, parents);
//  }

  public Region(String name) {
    this(null, name, null, null, null, null, null, null, null, null 
        //,null, null
        );
  }

//  public Region(String name, Label label) {
//    this(null, name, label, null, null, null, null, null, null, null, null,
//        null);
//  }

  public Region(String name, Label label, String imageIcon, Region parent,
      Integer displayOrder) {
    this(null, name, label, imageIcon, null, null, null, null, null, null
        //,null, null
        );
    addParent(parent, displayOrder);
  }

  public Region(String name, Label label, String imageIcon, String displayClass, Region parent,
      Integer displayOrder) {
    // invoke the BASE constructor
    /* v2.7.4:
    this(null, name, label, imageIcon, null, null, null, displayClass, null, null, null,
        null, null,null,null,null,null,null);
        */
    initBase(null, name, label, imageIcon, null, null, null, displayClass, null, null, null,
        null, null,null,null,null,null,null);
    
    addParent(parent, displayOrder);
  }

  public Region(String name, Label label, String imageIcon, RegionType type,
      String defValue, Region parent, Integer displayOrder) {
    this(null, name, label, imageIcon, null, null, type, defValue, null, null
        //,null, null
        );
    addParent(parent, displayOrder);
  }

  public Region(String name, Label label, String imageIcon, Integer width, Integer height, RegionType type,
      String defValue, Region parent, Integer displayOrder) {
    this(null, name, label, imageIcon, width, height, type, defValue, null, null
        //,null, null
        );
    addParent(parent, displayOrder);
  }

  public Region(String name, Label label, String imageIcon, RegionType type,
      String defValue, String displayClass, Region parent, Integer displayOrder) {
    // invoke the BASE constructor
    /* v2.7.4:
    this(null, name, label, imageIcon, null, null, type, displayClass, 
        defValue, null, null,null, null,null,null,null,null,null);
        */
    initBase(null, name, label, imageIcon, null, null, type, displayClass, 
        defValue, null, null,null, null,null,null,null,null,null);
    
    addParent(parent, displayOrder);
  }
  
  public Region(String name, Label label, String imageIcon) {
    this(null, name, label, imageIcon, null, null, null, null, null, null
        //,null, null
        );
  }

//  public Region(String name, Label label, String imageIcon, Type type) {
//    this(null, name, label, imageIcon, null, null, type, null, null, null,
//        null, null);
//  }

//  public Region(String name, Label label, String imageIcon, Type type,
//      String defValue) {
//    this(null, name, label, imageIcon, null, null, type, defValue, null, null,
//        null, null);
//  }

  public Region(String name, Label label, Integer width, Integer height,
      RegionType type, 
      String displayClass, 
      Boolean enabled, Style style, Region parent, int displayOrder) {
    // invoke the BASE constructor
    /* v2.7.4:
    this(null, name, label, null, width, height, type, 
        displayClass, null, enabled, style,null, null,null,null,null,null,null);
        */
    initBase(null, name, label, null, width, height, type, 
        displayClass, null, enabled, style,null, null,null,null,null,null,null);
    
    addParent(parent, displayOrder);
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Label getLabel() {
    return label;
  }
  
  /**
   * @effects (derived from {@link #label}) 
   *  if label != null
   *    return label.value
   *  else
   *    return null
   */
  public String getLabelAsString() {
    if (label != null)
      return label.getValue();
    else
      return null;
  }

  public void setLabel(Label label) {
    this.label = label;
  }

  public String getImageIcon() {
    return imageIcon;
  }

  public void setImageIcon(String imageIcon) {
    this.imageIcon = imageIcon;
  }
  
  public ImageIcon getImageIconObject() {
    if (imageIcon != null && !imageIcon.equals("")) {
      if (icon == null) {
        String labelStr = getLabelAsString();
        try {
          icon = GUIToolkit.getImageIcon(imageIcon, labelStr);
        } catch (NotFoundException e) {
          // ignore
        }
      }
      return icon;
    } else {
      return null;
    }
  }
  
  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public RegionType getType() {
    return type;
  }

  
  public void setType(RegionType type) {
    this.type = type;
  }

  public void setDisplayClass(String displayClass) {
    this.displayClass = displayClass;
    // reset
    displayClassType=null;
    getDisplayClassType();
  }

  public String getDisplayClass() {
    return this.displayClass;
  }
  
  public Class getDisplayClassType() throws NotPossibleException {
    if (displayClassType == null && displayClass != null) {
      try {
        displayClassType = Class.forName(displayClass);
      } catch (ClassNotFoundException e) {
        throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, 
            e, new Object[] {displayClass});
      }
    } 
    
    return displayClassType;
  }
  
  public String getDefValue() {
    return defValue;
  }

  public void setDefValue(String defValue) {
    this.defValue = defValue;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public Style getStyle() {
    return style;
  }

  public void setStyle(Style style) {
    this.style = style;
  }

  /**
   * @effects
   *  if this has child {@link Region}s specified in {@link #children}
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>  
   * @version 5.1 
   */
  public boolean hasChildren() {
    return getChildrenCount() > 0;
  }

  /**
   * @effects 
   *  if exists a child {@link Region} of this whose name equals <tt>name</tt>
   *    return that {@link Region}
   *  else
   *    return <tt>null</tt>
   * @version 5.1
   */
  public Region getChildRegion(final String name) {
    for (RegionMap childMap : children) {
      Region child = childMap.getChild(); 
      if (child.getName().equals(name)) {
        return child;
      }
    }
    
    // not found
    return null;
  }
  

  /**
   * @effects 
   *  if <tt>child</tt> in {@link #getChildRegions()} 
   *    return  {@link RegionMap} of <tt>child</tt> in this
   *  else
   *    return null
   * @version 5.2
   */
  public RegionMap getChildRegionMap(Region child) {
    for (RegionMap childMap : children) {
      Region c = childMap.getChild(); 
      if (child.equals(c)) {
        return childMap;
      }
    }
    
    // not found
    return null;
  }
  
  public List<RegionMap> getChildren() {
    return (children.isEmpty()) ? null : children;
  }

  public Integer getChildrenCount() {
    return children.size();
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value="children")
  public void addChildMap(RegionMap child) {
    children.add(child);
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value="children")
  public void addChildMap(List<RegionMap> childs) {
    children.addAll(childs);
  }

  @DOpt(type=DOpt.Type.LinkUpdater)
  @AttrRef(value="children")
  public void removeChildMap(RegionMap child) {
    children.remove(child);
  }

  public RegionMap addChild(Region child, Integer displayOrder) {
    if (child != null) {
      RegionMap parentMap = new RegionMap(this, child, displayOrder);
      children.add(parentMap);
      child.parents.add(parentMap);

      return parentMap;      
    } else 
      return null;
  }

  public void setChildren(List<RegionMap> children) {
    this.children = children;
  }

  /**
   * @effects 
   *  if this has child regions
   *    return List<Region> containing them
   *  else
   *    return null
   */
  public List<Region> getChildRegions() {
    List<Region> childs = null;
    if (!children.isEmpty()) {
      childs = new ArrayList();
      for (RegionMap childMap: children) {
        childs.add(childMap.getChild());
      }
    }
    
    return childs;
  }
  
  public List<RegionMap> getParents() {
    return (parents.isEmpty()) ? null : parents;
  }

  public Integer getParentsCount() {
    return parents.size();
  }
  
  public void setParents(List<RegionMap> parents) {
    this.parents = parents;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value="parents")
  public void addParentMap(RegionMap parent) {
    parents.add(parent);
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value="parents")
  public void addParentMap(List<RegionMap> parents) {
    this.parents.addAll(parents);
  }

  @DOpt(type=DOpt.Type.LinkRemover)
  @AttrRef(value="parents")
  public void removeParentMap(RegionMap parent) {
    parents.remove(parent);
  }

  public RegionMap addParent(Region parent, Integer displayOrder) {
    if (parent != null) {
      RegionMap childMap = new RegionMap(parent, this, displayOrder);
      parents.add(childMap);
      parent.children.add(childMap);

      return childMap;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if this has parent regions
   *    return List<Region> containing them
   *  else
   *    return null
   */
  public List<Region> getParentRegions() {
    List<Region> parentRegs = null;
    if (!parents.isEmpty()) {
      parentRegs = new ArrayList();
      for (RegionMap m: parents) {
        parentRegs.add(m.getParent());
      }
    }
    
    return parentRegs;
  }
  
  public List<ExclusionMap> getExclusion() {
    return (exclusion.isEmpty()) ? null : exclusion;
  }

  public Integer getExclusionCount() {
    return exclusion.size();
  }
  
  /**
   * @effects 
   *  return a list of the excluded regions; or <tt>null</tt> if no such regions exist. 
   */
  public List<Region> getExcludedRegions() {
    List<Region> excluded = null;
    if (!exclusion.isEmpty()) {
      excluded = new ArrayList();
      for (ExclusionMap emap : exclusion) 
        excluded.add(emap.getTarget());
    }
    
    return excluded;
  }
  
  public void setExclusion(List<ExclusionMap> exclusion) {
    this.exclusion = exclusion;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value="exclusion")
  public void addExclusionMap(ExclusionMap emap) {
    exclusion.add(emap);
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value="exclusion")  
  public void addExclusionMap(List<ExclusionMap> emaps) {
    exclusion.addAll(emaps);
  }
  
  @DOpt(type=DOpt.Type.LinkRemover)
  @AttrRef(value="exclusion")  
  public void removeExclusionMap(ExclusionMap emap) {
    exclusion.remove(emap);
  }

  /**
   * @effects 
   *  add <tt>excluded</tt> to the list of regions that are excluded 
   *  by this (i.e. <tt>this</tt> is the source)
   */
  public ExclusionMap addExclusion(Region excluded) {
    ExclusionMap emap = null;
    if (excluded != null) {
      emap = new ExclusionMap(this, excluded);
      exclusion.add(emap);
    }
    
    return emap;
  }
  
//  public RegionStyle addStyle(Style s) {
//    RegionStyle rs = new RegionStyle(this, s);
//    styles.add(rs);
//    return rs;
//  }

  public boolean getIsStateListener() {
    return isStateListener;
  }

  public void setIsStateListener(boolean isStateListener) {
    this.isStateListener = isStateListener;
  }

  public boolean getIsStateEventSource() {
    return isStateEventSource;
  }

  public void setIsStateEventSource(boolean isStateEventSource) {
    this.isStateEventSource = isStateEventSource;
  }

  /**
   * @effects 
   *  if this is editable
   *    return true
   *  else
   *    return false
   */
  public boolean getEditable() {
    return editable;
  }

  /**
   * This differs from {@link #getEditable()} in that it processes only the scope definition 
   * of a specified user module. 
   * 
   * @effects 
   *  if exists in {@link #userModuleScopeMap} a {@link ScopeDef} for <tt>module</tt>
   *    return its editability setting
   *  else 
   *    return null
   *  @version 3.2
   */
  public Boolean getEditable(ApplicationModule module) {
    ScopeDef scopeDef = lookUpUserModuleScope(module);
//    if (scopeDef != null) {
//      Boolean scopeEditable = scopeDef.isEditable();
//      if (scopeEditable != null && scopeEditable == false)
//        return false;
//      else  
//        return getEditable();
//    } else {
//      return getEditable();
//    }

    if (scopeDef != null) {
      return scopeDef.isEditable();
    } else {
      return null;
    }
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public AlignmentX getAlignX() {
    return alignX;
  }

  public void setAlignX(AlignmentX alignX) {
    this.alignX = alignX;
  }

  public AlignmentY getAlignY() {
    return alignY;
  }

  public void setAlignY(AlignmentY alignY) {
    this.alignY = alignY;
  }

  public PropertySet getPrintConfig() {
    return printConfig;
  }

  public void setPrintConfig(PropertySet printConfig) {
    this.printConfig = printConfig;
  }
  

  public String getLayoutBuilderClass() {
    return layoutBuilderClass;
  }

  public void setLayoutBuilderClass(String layoutBuilderClass) {
    this.layoutBuilderClass = layoutBuilderClass;
  }

  public Class getLayoutBuilderType() {
    if (layoutBuilderType == null && layoutBuilderClass != null) {
      try {
        layoutBuilderType = Class.forName(layoutBuilderClass);
      } catch (ClassNotFoundException e) {
        throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, 
            e, new Object[] {layoutBuilderClass});
      }
    } 
    
    return layoutBuilderType;
  }

  
  /**
   * <b>IMPORTANT</b>: this differs from {@link #isDisplayVisible()} in that it determines whether the 
   * associated component should be displayed visible or hidden. 
   * The other method determines whether 
   * the component is included in the view. 
   * 
   * @effects 
   *  return this.visible
   */
  public boolean getVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }
  
  /**
   * <b>IMPORTANT</b>: this differs from {@link #getVisible()} in that it determines whether 
   * the associated component is included in the view. The other method determines whether the 
   * component should be displayed visible or hidden. 
   * 
   * @effects 
   *  if {@link #properties} is not null and that contains {@link Property}({@link PropertyName#view_objectForm_dataField_visible},true)
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public boolean isDisplayVisible() {
    return getProperty(PropertyName.view_objectForm_dataField_visible, Boolean.class, Boolean.TRUE);
  }
  
  public PropertySet getProperties() {
    return properties;
  }

  /**
   * @requires 
   *  propName != null
   * @effects 
   *  if exists property <tt>propName</tt> of <tt>this</tt> whose value type is assignable to <tt>valueType</tt> 
   *    return its value
   *  else
   *    return <tt>defaultVal</tt>
   *     
   * @version 3.0
   */  
  public <T> T getProperty(PropertyName propName,
      Class<T> valueType, T defaultVal) {
    T val = null;
    if (properties != null) {
      val = properties.getPropertyValue(propName, valueType, defaultVal);
    } else {
      val = defaultVal;
    }
    
    return val;
  }
  
  /**
   * @effects 
   *  if this.{@link #properties} has a property named {@link PropertyName#tag}, whose value 
   *  is <tt>value</tt>
   *    return true
   *  else 
   *    return false
   * @version 5.6
   * 
   */
  public boolean hasPropertyTagValue(String value) {
    String val = getProperty(PropertyName.tag, String.class, null);
    
    if (val != null && val.equals(value)) {
      return true;
    } else {
      return false;
    }
  }
  
  public void setProperties(PropertySet properties) {
    this.properties = properties;
  }

  /**
   * @requires 
   *  propName != null /\ exists a property in <tt>this</tt> whose name is <tt>propName</tt>
   * @effects 
   *  if exists property <tt>propName</tt> of <tt>this</tt> whose value type is assignable to <tt>valueType</tt> 
   *    set its value to <tt>value</tt>
   *    return the old value 
   *  else
   *    do nothing
   *    return <tt>null</tt>
   *     
   * @version 3.1
   */
  public Object setProperty(PropertyName propName, Object value) {
    Object oldVal = null;
    if (properties != null) {
      oldVal = properties.setProperty(propName, value);
    }
    
    return oldVal;
  }
  
  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();

      if (num > idCounter)
        idCounter = num;

      return currID;
    }
  }

  public String toString() {
    return this.getClass().getSimpleName() + "(" + id + "," + name + ")";
  }
  
  public boolean equals(Object o) {
    return (o != null && o instanceof Region && 
        ((Region)o).id==this.id);
  }

  public int hashCode() {
    return this.id;
  }
  
  public boolean equalsByName(Region r) {
    return (r != null && 
        r.name.equals(this.name));
  }

  /**
   * @effects
   *  if this.name = type.getName()
   *    return true
   *  else
   *    return false
   */
  public boolean isType(RegionType type) {
    return getName().equals(type.getName());
  }
  
  public boolean isSizeConfigured() {
    // either (width,height) OR (widthRatio,heightRatio) is specified
    return (width != null && width  > -1 && height != null && height > -1);
  }

//  public HelpItem getHelpItem() {
//    return helpItem;
//  }
//
//  public void setHelpItem(HelpItem helpItem) {
//    this.helpItem = helpItem;
//  }
  
  /**
   * @effects 
   *  creates a <b>shallow</b> copy of this, i.e. all primitive-typed attributes are 
   *  copied, while all object-typed attributes have their references copied.
   * @version 2.8 
   */
  public Region clone() {
    Region r = new Region(
        id,name, label, imageIcon, width, height, type, displayClass, defValue, enabled, style, 
        isStateListener, isStateEventSource, editable, alignX, alignY, printConfig, 
        layoutBuilderClass, visible
        , properties);
    
    r.label=label;
    r.displayClassType=displayClassType;
    r.icon=icon;
    r.layoutBuilderType=layoutBuilderType;

    r.exclusion=this.exclusion;
    r.children=this.children;
    r.parents=this.parents;
    
    
    return r;
  }

  /**
   *  @requires 
   *  children != null
   * @effects 
   *  replace the child mapping of <tt>this</tt> that is to <tt>currentChild</tt> by <tt>newChild</tt>
   *  
   *  @version 2.8
   */
  private void replaceChild(Region currentChild, Region newChild) {
    if (children == null)
      return;
    
    for (RegionMap childMap : children) {
      if (childMap.getChild() == currentChild) {
        childMap.setChild(newChild);
        
        break;
      }
    }
  }

  /**
   * @modifies this(.children,parents,exclusion)
   * @effects 
   * <pre>
   *  clone each RegionMap in this as a new map m as follows:
   *    if exists r in regions s.t. equals(r,m.parent)
   *      set m.parent = r
   *      
   *    if exists r in regions s.t. equals(r,m.child)
   *      set m.child = r
   *  </pre>      
   * <pre>
   *  clone each ExclusionMap in this as a new map xm as follows:
   *    if exists r in regions s.t. equals(r,xm.parent)
   *      set xm.parent = r
   *      
   *    if exists r in regions s.t. equals(r,xm.child)
   *      set xm.child = r
   *  </pre>
   * @version 2.8      
   */
  public void cloneMappings(Collection<Region> regions) {
    List<RegionMap> newMaps;
    RegionMap m;
    if (children != null) {
      newMaps = new ArrayList();
      for (RegionMap childMap : children) {
        m = childMap.clone();
        newMaps.add(m);
        m.setParent(this);
        for (Region r : regions) {
          if (r.equals(m.getChild())) {
            m.setChild(r);
            break;
          }
        }
      }
      
      children = newMaps;
    }
    
    if (parents != null) {
      newMaps = new ArrayList();
      for (RegionMap parentMap : parents) {
        m = parentMap.clone();
        newMaps.add(m);
        
        m.setChild(this);
        for (Region r : regions) {
          if (r.equals(m.getParent())) {
            m.setParent(r);
            break;
          }
        }
      }
      
      parents = newMaps;
    }

    if (exclusion != null) {
      List<ExclusionMap> newXMaps = new ArrayList();
      ExclusionMap xm;
      for (ExclusionMap xMap : exclusion) {
        xm = xMap.clone();
        newXMaps.add(xm);
        xm.setSource(this); // same semantics as method addExclusion
        for (Region r : regions) {
          if (r.equals(xm.getTarget())) {
            xm.setTarget(r);
            break;
          }
        }
      }
      
      exclusion = newXMaps;
    }
  }
  
  /**
   * @effects 
   *  if exists in {@link #userModuleScopeMap} a {@link ScopeDef} for <tt>module</tt>
   *    return it
   *  else
   *    return <tt>null</tt>
   *  @version 3.2
   */
  public ScopeDef lookUpUserModuleScope(ApplicationModule module) {
    if (userModuleScopeMap != null) {
      return userModuleScopeMap.get(module);
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  add mapping <tt>(module, scopeDef)</tt> to {@link #userModuleScopeMap}
   * @version 3.2
   */
  public void addUserModuleScope(ApplicationModule module,
      ScopeDef scopeDef) {
    if (userModuleScopeMap == null) {
      userModuleScopeMap = new HashMap();
    }
    
    userModuleScopeMap.put(module, scopeDef);
  }


}
