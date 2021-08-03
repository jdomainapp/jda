package jda.modules.restfstool.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.util.DClassTk;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.restfstool.RFSGen;
import jda.modules.restfstool.config.RFSGenConfig;
import jda.modules.restfstool.config.RFSGenDesc;
import jda.modules.restfstool.frontend.utils.DomainTypeRegistry;
import jda.util.ApplicationToolKit;

/**
 * @overview 
 *  Toolkit class for {@link RFSGen}.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4
 */
public class RFSGenTk {
  private RFSGenTk() {}

  /**
   * @effects 
   *  extract and return {@link RFSGenConfig} from the scc or return null if 
   *  it is not available.
   *  
   *  Throws {@link NotFoundException} if system configuration is not specified in scc.
   */
  public static RFSGenConfig parseRFSGenConfig(Class scc) throws NotFoundException {
    if (scc == null)
      return null;
    
    RFSGenDesc rfsGenDesc = (RFSGenDesc) scc.getAnnotation(RFSGenDesc.class);
    
    if (rfsGenDesc == null) {
      throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[] {RFSGenDesc.class, scc});
    }
    
    RFSGenConfig cfg = new RFSGenConfig();
    
    parseAnnotation2Config(rfsGenDesc, cfg);
    
    // the domain model
    Class[] model = ApplicationToolKit.parseDomainModel(scc).
        toArray(new Class[0]);
    
    cfg.setDomainModel(model);
    
    // TODO: remove these after updating FrontEndGen
    cfg.setSCC(scc);
    cfg.setMCCMain(ApplicationToolKit.parseModuleConfigs(scc, ModuleType.DomainMain)[0]);
    cfg.setMCCFuncs(ApplicationToolKit.parseModuleConfigs(scc, 
        ModuleType.DomainData, ModuleType.DomainReport));
    
    return cfg;
  }

  /**
   * @requires
   *  config.class has a corresponding field for each annotation element in ano
   * @effects 
   *  for each element e in ano
   *    copy its value to the corresponding f in config
   *    
   *  <p>Throws NotFoundException if config.class does not have a required corresponding field, 
   *  NotPossibleException if failed to set value of the corresponding field
   */
  public static <T extends Annotation> void parseAnnotation2Config(T ano, Object config) throws NotFoundException {
    Class<? extends Annotation> anoType = ano.annotationType();
    Class cfgCls = config.getClass();
    
    Method[] methods = anoType.getDeclaredMethods();
    
    for (Method m : methods) {
      String name = m.getName();
      Method corMethod = DClassTk.findSetterMethod(cfgCls, name);
      
      if (corMethod == null)
        throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND, 
            new Object[] {cfgCls, "set-"+name});
      
      Object val = null;
      try {
        val = m.invoke(ano);
      } catch (IllegalAccessException | IllegalArgumentException
          | InvocationTargetException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
            new Object[] {anoType, name, ""});
      }
      
      try {
        corMethod.invoke(config, val);
      } catch (IllegalAccessException | IllegalArgumentException
          | InvocationTargetException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
            new Object[] {cfgCls, "set-"+name, val});
      }
    }
  }
  
  
  /**
   * @effects 
   *  registers domain classes in <code>model</code> to the system.
   *  
   * @todo replace DomainTypeRegistry by using DSM
   */
  public static void init(Class<?>[] model) {
    DomainTypeRegistry regist = DomainTypeRegistry.getInstance();
    regist.addDomainTypes(model);
    
    // register all the enum types of the domain attributes in the domain classes
    for (Class<?> dcls : model) {
      DClassTk.getDomainEnumTypedAttribs(dcls).ifPresent(col -> {
        col.forEach(enumType -> regist.addDomainType(enumType.getType()));
      });
    }
  }
}
