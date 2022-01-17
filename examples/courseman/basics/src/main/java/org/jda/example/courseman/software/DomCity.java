package org.jda.example.courseman.software;

import org.jda.example.courseman.services.student.model.City;

import jda.modules.common.exceptions.DataSourceException;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.impl.DomSoftware;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class DomCity {
  
  public static void main(String[] args) {
    DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();
    
    // this should be run subsequent times
    sw.init();
    
    // create classes
    try {
      sw.addClass(City.class);
      
      // get objects
      sw.loadAndPrintObjects(City.class);

      // create some objects
      createCity(sw);
      
      // check that a new object is in the object pool
      sw.printObjectPool(City.class);

      // check that object is in the database by printing data in the database
      sw.printObjectDB(City.class);
      
    } catch (DataSourceException e) {
      e.printStackTrace();
    }
  }

  /**
   * @effects 
   * 
   */
  private static void createCity(DomSoftware sw) throws DataSourceException {
    City obj = new
//    City(6, "Hoa Binh");
//      City(5, "Thai Nguyen");
//    City(4, "Hue");
//      City(3, "Danang");
//      City(2, "HCM");
      City(1, "Hanoi");
      sw.addObject(City.class, obj);    
  }
}
