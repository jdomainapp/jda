package jda.test.modules.help;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.test.model.basic.City;

/**
 * A domain class whose objects are city names. This class is used as 
 * the <code>allowedValues</code> of the domain attributes of 
 * other domain classes (e.g. Student.address).  
 * 
 * <p>Method <code>toString</code> overrides <code>Object.toString</code> to 
 * return the string representation of a city name which is expected by 
 * the application. 
 * 
 * @author dmle
 *
 */
@ModuleDescriptor(name="ModuleCity",
modelDesc=@ModelDesc(
    model=City.class
),
viewDesc=@ViewDesc(
    formTitle="Quản lí địa chỉ",
    domainClassLabel="Địa chỉ",
    imageIcon="address.jpg",
    viewType=RegionType.Data,
    view=View.class,
    parent=RegionName.Tools
),
isPrimary=true
)
public class ModuleCity {
  @AttributeDesc(label="Nhập thông tin các thành phố")
  private String title;

  @AttributeDesc(label="Tên thành phố")
  private String name;
}
