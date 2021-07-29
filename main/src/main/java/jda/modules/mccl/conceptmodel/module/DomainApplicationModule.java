package jda.modules.mccl.conceptmodel.module;

import java.util.Collection;

import javax.swing.ImageIcon;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.types.tree.Tree;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.model.ModelConfig;
import jda.modules.mccl.conceptmodel.view.RegionGui;
import jda.modules.mccl.conceptmodel.view.RegionToolMenuItem;
import jda.util.properties.PropertySet;

/**
 * @overview
 *  Represents a domain-specific application module.
 *  
 * @author dmle
 */
@DClass(schema="app_config")
public class DomainApplicationModule extends ApplicationModule {
  
  private static final boolean debug = Toolkit.getDebug(DomainApplicationModule.class);
  
  /** derived from {@link #viewCfg} */
  @DAttr(name="imageIcon",type=DAttr.Type.Image,
      length=5000000, // 5MB
      mutable=false,optional=true,
      derivedFrom={"viewCfg"})
  private ImageIcon imageIcon;
  
  /**
   * This constructor is used to create object from the module configuration.
   */
  public DomainApplicationModule(String name, 
                                Configuration config,
                                ModelConfig modelCfg,
                                RegionGui viewCfg,
                                RegionToolMenuItem toolMenuItemCfg,
                                ControllerConfig controllerCfg,
                                ModuleType type,
                                Boolean viewer, 
                                Boolean primary,
                                PropertySet printConfig,
                                ApplicationModule[] childModules   // v2.7.2: used for composite controllers
                                , Tree contTreeObj   // v3.0
                                , PropertySet props // v5.2
                                ) throws NotFoundException {
    super(name, config, modelCfg, viewCfg, toolMenuItemCfg, controllerCfg, type, viewer, primary, 
        printConfig, childModules
        , contTreeObj,
        props // v5.2
        );
    if (viewCfg != null)
      this.imageIcon = viewCfg.getImageIconObject();
  }

  /**
   * This constructor is used to create object from data source
   */
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public DomainApplicationModule(Long id, String name, 
      Configuration config,
      ModelConfig modelCfg,
      RegionGui viewCfg,
      RegionToolMenuItem toolMenuItemCfg,
      ControllerConfig controllerCfg,
      ModuleType type,
      Boolean viewer, 
      Boolean primary,
      PropertySet printConfig, 
      String contTree   // v3.0
      ,PropertySet props // v5.2
      ,ImageIcon imgIcon) {
    super(id, name, config, modelCfg, viewCfg, toolMenuItemCfg, controllerCfg, type, 
        viewer, primary, printConfig 
        ,contTree
        , props  // v5.2
        );
    this.imageIcon = imgIcon;
    
    // this is needed because imageIcon.description was not saved with the image to the data source
    if (imageIcon != null && imageIcon.getDescription() == null) {
      imageIcon.setDescription(getLabelAsString());
    }
    
    // debug
    if (debug)
      System.out.printf("...Domain module: %s (%s)%n", name, 
        (viewCfg != null) ? "image: " + viewCfg.getImageIcon() : "viewCfg: null");
  }

//  private ImageIcon readImageIcon(RegionGui viewCfg) throws NotFoundException {
//    String imgIconFileName = viewCfg.getImageIcon();
//    if (imgIconFileName != null) {
//      String labelStr = getLabelAsString();
//      return GUIToolkit.getImageIcon(imgIconFileName, labelStr);
//    } else
//      return null;
//  }

  public ImageIcon getImageIcon() {
    return imageIcon;
  }

  /**
   * @overview
   *  A wrapper class that is used to represent {@link DomainApplicationModule} as a model in the module configuration.  
   *  
   *  <p>This acts as a 'kind-of' logical data source on which to retrieve the attribute values of 
   *  {@link DomainApplicationModule} objects and feed these values into the bounded data fields.
   *   
   * @author dmle
   */
  @DClass(serialisable=false,wrapperOf=DomainApplicationModule.class)
  public static class DomainApplicationModuleWrapper {
      @DAttr(name="id",type=DAttr.Type.Long,id=true,mutable=false,optional=false)
      private long id;

      @DAttr(name="domainModule",type=DAttr.Type.Domain,optional=false)
      private DomainApplicationModule domainModule;
      
      public DomainApplicationModuleWrapper(DomainApplicationModule module) {
        id = System.currentTimeMillis();
        this.domainModule = module;
      }
      
      public long getId() {
        return id;
      }

      @Override
      public String toString() {
        return "DomainApplicationModuleWrapper(" + domainModule.getName() + ")";
      }

      public DomainApplicationModule getDomainModule() {
        return domainModule;
      }
      
  } // end DomainApplicationModuleWrapper
  
} // end DomainApplicationModule

