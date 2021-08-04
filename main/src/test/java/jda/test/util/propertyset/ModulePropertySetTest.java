package jda.test.util.propertyset;

import static java.lang.System.out;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.test.model.modules.ModuleWithBuilderType;
import jda.util.SwTk;
import jda.util.properties.Property;
import jda.util.properties.PropertySet;
import jda.util.properties.PropertySetFactory;

public class ModulePropertySetTest {
  public static void main(String[] args) throws DataSourceException {
    Class ModuleCls = ModuleWithBuilderType.class; //ModuleA.class;
    
    Configuration config = SwTk.createMemoryBasedConfiguration("test");
    DODMBasic dodm = DODMBasic.getInstance(config);
    
    dodm.registerClasses(new Class[] {
        Property.class,
        PropertySet.class
    });
    
    String name = "ModuleA";
    PropertySet propSet = PropertySetFactory.createViewConfigPropertySet(
        dodm, 
        name, 
        ModuleCls, false);
    
    out.printf("Module: %s%n", name);

    // print property set
    PropertySetFactory.print(propSet);
    
    // print some special attributes
//    Class<? extends DocumentBuilder> builderType = propSet.getPropertyValue("docBuilderType", Class.class);
//    out.printf("builderType: %s%n", builderType);
  }
}
