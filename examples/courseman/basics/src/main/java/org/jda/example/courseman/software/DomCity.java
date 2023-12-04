package org.jda.example.courseman.software;

import ch.qos.logback.classic.Logger;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.impl.DomSoftware;
import jda.software.ddd.MicroDomSoftware;
import org.jda.example.courseman.services.student.model.City;
import org.slf4j.LoggerFactory;
/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class DomCity extends MicroDomSoftware<City> {

  private static final Logger logger = (Logger) LoggerFactory.getLogger(DomCity.class.getSimpleName());

  public static void main(String[] args) {
    DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();

    // this should be run subsequent times
    sw.init();

    try {
      new DomCity(sw).run();
    } catch (DataSourceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public DomCity run() throws DataSourceException {
    return (DomCity) initDom()
        .loadAndDisplay()
        .create()
        .displaySource();
  }

  /**
   * @effects
   * @version
   */
  public DomCity(DomSoftware sw) {
    super(sw, City.class);
  }

  @Override
  public DomCity create() throws DataSourceException {
    // create some objects
    City obj = new
//        City(6, "Hoa Binh");
//          City(5, "Thai Nguyen");
//        City(4, "Hue");
//          City(3, "Danang");
          City(2, "HCM");
//        City(1, "Hanoi");
    getSw().addObject(City.class, obj);

    return this;
  }

  @Override
  protected void doUpdate(City obj, Object id) throws DataSourceException {
    throw new NotImplementedException("Not implemented");
  }

}

//public class DomCity {
//
//  public static void main(String[] args) {
//    DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();
//
//    // this should be run subsequent times
//    sw.init();
//
//    // create classes
//    try {
//      sw.addClass(City.class);
//
//      // get objects
//      sw.loadAndPrintObjects(City.class);
//
//      // create some objects
//      createCity(sw);
//
//      // check that a new object is in the object pool
//      sw.printObjectPool(City.class);
//
//      // check that object is in the database by printing data in the database
//      sw.printObjectDB(City.class);
//
//    } catch (DataSourceException e) {
//      e.printStackTrace();
//    }
//  }
//
//  /**
//   * @effects
//   *
//   */
//  private static void createCity(DomSoftware sw) throws DataSourceException {
//    City obj = new
////    City(6, "Hoa Binh");
////      City(5, "Thai Nguyen");
////    City(4, "Hue");
////      City(3, "Danang");
////      City(2, "HCM");
//      City(1, "Hanoi");
//      sw.addObject(City.class, obj);
//  }
//}
