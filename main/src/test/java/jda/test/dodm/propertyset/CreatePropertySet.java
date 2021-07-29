package jda.test.dodm.propertyset;

import static java.lang.System.out;

import java.util.Collection;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dodm.DODMBasic;
import jda.test.app.courseman.extended.CourseManExtendedTester;
import jda.test.model.modules.ModuleA;
import jda.util.properties.Property;
import jda.util.properties.PropertySet;
import jda.util.properties.PropertySetFactory;
import jda.util.properties.PropertySet.PropertySetType;

public class CreatePropertySet extends CourseManExtendedTester {
  @Test
  public void doTest() throws Exception {
    Class[] toRegisterEnums = {
        PropertySetType.class
    };
    
    Class[] toRegister = {
        PropertySet.class,
        Property.class,
    };

//    instance.removeClassAndDeleteFromDB(PropertySet.class);
//    instance.removeClassAndDeleteFromDB(Property.class);
    registerEnumClasses(toRegisterEnums);
    
    registerDataSourceSchemas(instance.getDODM(), toRegister);
    addClasses(toRegister);
    
    Class printableModuleCls = ModuleA.class;
    
    DODMBasic schema = instance.getDODM();
    
    String name = "ModuleA";
    PropertySet propSet = PropertySetFactory.createPrintConfigPropertySet(
        schema, 
        name, 
        printableModuleCls);
    
    out.printf("Module: %s%n", name);

    // add property set to db
    createPropertySet(propSet, 0);
  }
  
  private void createPropertySet(PropertySet  propSet, int gapDistance) throws DataSourceException {
    StringBuffer indent = new StringBuffer();
    for (int i = 0; i < gapDistance;i++) indent.append(" ");
    
    StringBuffer subIndent = new StringBuffer(indent);
    subIndent.append("  ");
    
    gapDistance = gapDistance + 4;

    // add property set to data source
    out.printf("%sProperty set: %s%n", indent, propSet.getName());
    createObject(PropertySet.class, propSet);
    
    out.printf("%sProperties:%n", indent);
    Collection<Property> props = propSet.getProps();
    for (Property p : props) {
      out.printf("%s%s: \"%s\" (%s<%s>)%n", subIndent, 
          p.getPkey(), p.getValueAsString(), p.getType(), p.getValue());
      
      // add property set to data source
      createObject(Property.class, p);
    }

    Collection<PropertySet> extents = propSet.getExtensions();
    if (extents != null && !extents.isEmpty()) {
      out.printf("%sExtension(s):%n", subIndent);
      for (PropertySet pset : extents) {
        // recursive
        createPropertySet(pset, gapDistance);
      }
    }
  }

}
