/**
 * 
 */
package jda.test.util.property;

import java.lang.annotation.Annotation;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.util.properties.Property;

/**
 * 
 * @overview
 * 
 * @version
 *
 * @author dmle
 *
 */
public class PropertyTest {
  public static void main(String[] args) {
    PropertyDesc pd = new PropertyDesc() {
      
      @Override
      public Class<? extends Annotation> annotationType() {
        // TODO Auto-generated method stub
        return PropertyDesc.class;
      }
      
      @Override
      public Class valueType() {
        return LAName[].class;
      }
      
      @Override
      public Class valueIsClass() {
        // TODO Auto-generated method stub
        return CommonConstants.NullType;
      }
      
      @Override
      public String valueAsString() {
        return "Help";
      }
      
      @Override
      public PropertyName name() {
        return PropertyName.controller_dataController_actions;
      }
    };
    
    Property prop = Property.createInstance(pd, null);
    
    System.out.println(prop);
  }
}
