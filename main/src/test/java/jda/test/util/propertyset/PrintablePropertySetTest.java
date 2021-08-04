package jda.test.util.propertyset;

import static java.lang.System.out;

import java.util.Collection;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dodm.DODMBasic;
import jda.modules.exportdoc.controller.DocumentBuilder;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.test.model.modules.ModuleWithBuilderType;
import jda.util.SwTk;
import jda.util.properties.Property;
import jda.util.properties.PropertySet;
import jda.util.properties.PropertySetFactory;

public class PrintablePropertySetTest {
  public static void main(String[] args) throws DataSourceException {
    Class printableModuleCls = ModuleWithBuilderType.class; //ModuleA.class;
    
    Configuration config = SwTk.createMemoryBasedConfiguration("test");
    DODMBasic schema = DODMBasic.getInstance(config);
    
    String name = "ModuleA";
    PropertySet propSet = PropertySetFactory.createPrintConfigPropertySet(
        schema, 
        name, 
        printableModuleCls);
    
    out.printf("Module: %s%n", name);

    // print property set
    printASet(propSet, 0);
    
    // print some special attributes
    Class<? extends DocumentBuilder> builderType = propSet.getPropertyValue("docBuilderType", Class.class);
    out.printf("builderType: %s%n", builderType);
  }
  
  private static void printASet(PropertySet  propSet, int gapDistance) {
    StringBuffer indent = new StringBuffer();
    for (int i = 0; i < gapDistance;i++) indent.append(" ");
    
    StringBuffer subIndent = new StringBuffer(indent);
    subIndent.append("  ");
    
    gapDistance = gapDistance + 4;
    
    out.printf("%sProperty set: %s%n", indent, propSet.getName());

    out.printf("%sProperties:%n", indent);
    Collection<Property> props = propSet.getProps();
    for (Property p : props) {
      out.printf("%s%s: \"%s\" (%s<%s>)%n", subIndent, 
          p.getPkey(), p.getValueAsString(), p.getType(), p.getValue());
    }

    Collection<PropertySet> extents = propSet.getExtensions();
    if (extents != null && !extents.isEmpty()) {
      out.printf("%sExtension(s):%n", subIndent);
      for (PropertySet pset : extents) {
        printASet(pset, gapDistance);
      }
    }
  }
}
