package jda.test.util.annotation;

import java.lang.reflect.Method;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.helpviewer.model.print.PrintDesc;
import jda.modules.mccl.syntax.MCCLConstants.PageFormat;
import jda.modules.mccl.syntax.MCCLConstants.PaperSize;
import jda.util.properties.Property;
import jda.util.properties.PropertySetFactory;

@PrintDesc(
    pageFormat=PageFormat.Landscape, 
    paperSize=PaperSize.A4)
public class AnnotationReflectorTest {
  public static void main(String[] args) {
    Class<PrintDesc> printDescCls = PrintDesc.class;
    PrintDesc printDesc = AnnotationReflectorTest.class.getAnnotation(printDescCls);
    
    Property p; String k; Object v; Class type;

    Method[] methods = printDescCls.getDeclaredMethods();
    for (Method m : methods) {
      k = m.getName();
      try {
        v = m.invoke(printDesc, null);
        type = m.getReturnType();
        
        System.out.printf("%s : %s() = %s%n", type.getSimpleName(), k, v);

      } catch (Exception e) {
        // should not happen
        e.printStackTrace();
      }
    }
  }
  
}
