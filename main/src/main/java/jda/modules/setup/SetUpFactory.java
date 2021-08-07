package jda.modules.setup;

import jda.modules.common.types.properties.PropertyName;
import jda.modules.setup.model.SetUp;
import jda.modules.setup.model.SetUpGen;
import jda.modules.setup.sysclasses.DefaultSystemClass;
import jda.modules.setup.sysclasses.StandardSystemClass;

/**
 * @overview 
 *  A factory for {@link SetUp}. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 */
public class SetUpFactory {
  private SetUpFactory() {}

  /**
   * @effects 
   *  create and return a {@link SetUp} object for software that stores its configuration in memory 
   *  but domain data in a pre-defined type of relational database.  
   *  
   * @version 4.0
   */
  public static SetUp createSetUpWithMemoryBasedConfig(Class systemCls) {
    SetUpGen su = new SetUpGen();
    su.setSystemProperty(PropertyName.setup_systemClass, 
        systemCls.getName());
    
    // set this property to store config in memory
    su.setSystemProperty(PropertyName.setup_SerialiseConfiguration, "false");
    
    return su;
  }
  
  /**
   * @effects 
   *  create and return a {@link SetUp} object for software that stores its entire database in the default type of relational database. 
   *  
   * @version 4.0
   */
  public static SetUp createDefaultSetUp() {
    SetUpGen su = new SetUpGen();
    su.setSystemProperty(PropertyName.setup_systemClass, 
        DefaultSystemClass.class.getName());
    
    return su;
  }
  
  /**
   * @effects 
   *  create and return a {@link SetUp} object for software that stores its database in a pre-defined type 
   *  of relational database. 
   *  
   * @version 4.0
   */
  public static SetUp createStandardSetUp() {
    SetUpGen su = new SetUpGen();
    su.setSystemProperty(PropertyName.setup_systemClass, 
        StandardSystemClass.class.getName());
    
    return su;
  }
}
