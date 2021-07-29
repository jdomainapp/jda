package jda.modules.ds.viewable;

import java.lang.reflect.Constructor;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;

public class JDataSourceFactory {
  /**
   * This is a producer method
   * 
   * @effects 
   *  create a return a <tt>JDataSource</tt> instance of type <tt>dsType</tt>
   * @version 
   * 3.1: added mainCtl parameter 
   */
  public static <T extends JDataSource> T createInstance(Class<T> dsType,
      ControllerBasic mainCtl, // v3.1
      DODMBasic dodm, Class domainClass) throws NotPossibleException {
    
    try {
      Constructor<T> cons = dsType.getConstructor(ControllerBasic.class, DODMBasic.class, Class.class);
    
      T dataSource = cons.newInstance(mainCtl, dodm, domainClass);
      
      // v3.1
      // register data source as listener for changes in objects of the domain
      // class
      dodm.getDom().addChangeListener(domainClass, dataSource);

      return dataSource;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e,  
          new Object[] {dsType.getName(), dodm+","+domainClass});
    }
  }

  /**
   * Unlike other data source, the data source returned by this method contains run-time objects that are 
   * derived from a source attribute.  
   * 
   * @effects 
   *  create and return a special data source whose source objects are of type <tt>domainClass</tt> AND 
   *  are extracted (at run-time) from the value of <tt>sourceAttrib</tt> of the domain class managed by <tt>dataController</tt>
   *  
   *  <p>throws NotPossibleException if failed to create the instance
   */
  public static JDataSource createAttributeDerivedInstance(
      DataController dataController, DAttr sourceAttrib, Class domainClass
      ) throws NotPossibleException {
    DODMBasic dodm = dataController.getCreator().getDodm();
    
    JAttributeDerivedDataSource ds = new JAttributeDerivedDataSource(dataController, 
        dodm, sourceAttrib, domainClass);
    
    // v3.1
    // register data source as listener for changes in objects of the domain
    // class
    dodm.getDom().addChangeListener(domainClass, ds);
    
    return ds;
  }
}
