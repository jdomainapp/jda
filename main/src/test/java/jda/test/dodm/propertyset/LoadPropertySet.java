package jda.test.dodm.propertyset;

import static java.lang.System.out;

import java.util.Collection;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.DODMBasic;
import jda.test.app.courseman.extended.CourseManExtendedTester;
import jda.util.properties.Property;
import jda.util.properties.PropertySet;
import jda.util.properties.PropertySetFactory;
import jda.util.properties.PropertySet.PropertySetType;

public class LoadPropertySet extends CourseManExtendedTester {
  @Test
  public void doTest() throws Exception {
    Class[] toRegisterEnums = {
        PropertySetType.class
    };
    
    Class[] toRegister = {
        PropertySet.class,
        Property.class,
    };

    registerEnumClasses(toRegisterEnums);
    
    registerDataSourceSchemas(instance.getDODM(), toRegister);
    registerClasses(toRegister);
    
    loadObjects(Property.class);
    loadObjects(PropertySet.class);
    
    //DODM schema = instance.getDomainSchema();

    System.out.println("\nPROPERTIES\n");
    Collection<Property> props = getObjects(Property.class); 
    if (props != null) {
      for (Property p : props) {
        out.printf("%d. %s: \"%s\" (%s<%s>)%n   (- container: %s%n",
            p.getId(),
            p.getPkey(), p.getValueAsString(), p.getType(), p.getValue(),
            p.getContainer()
            );
      }
    }

    
    System.out.println("\nPROPERTY SETS\n");
    Collection<PropertySet> propSets = getObjects(PropertySet.class); 
    if (propSets != null) {
      for (PropertySet propSet : propSets) {
        loadAssociatedObjects(propSet);
        //printASet(propSet,0);
        PropertySetFactory.print(propSet);
      }
    }
  }
  
//  private void printASet(PropertySet  propSet, int gapDistance) throws DataSourceException {
//    StringBuffer indent = new StringBuffer();
//    for (int i = 0; i < gapDistance;i++) indent.append(" ");
//    
//    StringBuffer subIndent = new StringBuffer(indent);
//    subIndent.append("  ");
//    
//    gapDistance = gapDistance + 4;
//
//    // add property set to data source
//    out.printf("%sProperty set (%d): %s%n", indent, propSet.getId(), propSet.getName());
//    
//    out.printf("%sProperties:%n", indent);
//    Collection<Property> props = propSet.getProps();
//    for (Property p : props) {
//      out.printf("%s%d. %s: \"%s\" (%s<%s>)%n", 
//          subIndent,p.getId(),  
//          p.getPkey(), p.getValueAsString(), p.getType(), p.getValue());
//    }
//
//    Collection<PropertySet> extents = propSet.getExtensions();
//    if (extents != null && !extents.isEmpty()) {
//      out.printf("%sExtension(s):%n", subIndent);
//      for (PropertySet pset : extents) {
//        // recursive
//        printASet(pset, gapDistance);
//      }
//    }
//  }

}
