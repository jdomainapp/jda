package jda.modules.mccl.conceptmodel.view;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.help.model.HelpItem;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentY;
import jda.util.properties.PropertySet;
@DClass(schema="app_config")
public class RegionToolMenuItem extends Region {
  @DAttr(name="applicationModule",type=jda.modules.dcsl.syntax.DAttr.Type.Domain,
      //optional=false,
      serialisable=false  // v2.7
      )
  @DAssoc(ascName="module-has-menuItem",role="menuItem",
  ascType=AssocType.One2One,endType=AssocEndType.One,
  associate=@Associate(type=ApplicationModule.class,cardMin=1,cardMax=1,determinant=true))  // v2.7  
  private ApplicationModule applicationModule;
  
  // constructor used for creating objects from data source
  public RegionToolMenuItem(Integer id, String name, Label label, String imageIcon, Integer width,
      Integer height, RegionType type, 
      String displayClass,  // support display class 
      String defValue, Boolean enabled, Style style, 
      Boolean isStateListener,
      Boolean isStateEventSource, // v2.7.2
      Boolean editable,
      AlignmentX alignX,
      AlignmentY alignY,
      PropertySet printConfig,
      String layoutBuilderTypeName // v2.7.4
      , Boolean visible // v3.0
      , PropertySet properties  // v3.0
      //HelpItem helpItem           // v2.7.4
      //v2.7: ,ApplicationModule module
      ) {
    super(id,name,label,imageIcon,width,height,type,displayClass,defValue,enabled,style,
        //null,null,null,
        isStateListener,
        isStateEventSource,
        editable, alignX, alignY, printConfig//, helpItem
        ,layoutBuilderTypeName, 
        visible, 
        properties
        );
    //this.module=module;
  }
  
  // constructor used by SetUp to create objects
  public RegionToolMenuItem(String name, Label label, String imageIcon, RegionType type, 
      Boolean enabled,
      Boolean visible, // v3.0
      //v2.7: ApplicationModule module, 
      Region parent, Integer displayOrder) {
    super(null,name,label,imageIcon,null,null,type,null,enabled,null
        //,null,null
        );
    super.setVisible(visible);
    //this.module=module;
    addParent(parent, displayOrder);
  }
  
  // this contructor apprears to not being used...????
  public RegionToolMenuItem(String name, Label label, String imageIcon, Integer width,
      Integer height, RegionType type, String defValue, Boolean enabled, Style style
      //v2.7: , ApplicationModule module
      ) {
    super(null,name,label,imageIcon,width,height,type,defValue,enabled,style
        //,null,null
        );
    //this.module=module;
  }
  
  public ApplicationModule getApplicationModule() {
    return applicationModule;
  }

  public void setApplicationModule(ApplicationModule module) {
    this.applicationModule = module;
  }
}
