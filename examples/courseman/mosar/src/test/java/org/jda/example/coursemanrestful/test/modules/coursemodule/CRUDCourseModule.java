package org.jda.example.coursemanrestful.test.modules.coursemodule;

import jda.modules.common.exceptions.DataSourceException;
import jda.software.ddd.MicroDomSoftware;
import org.jda.example.coursemanrestful.modules.coursemodule.model.CompulsoryModule;
import org.jda.example.coursemanrestful.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanrestful.modules.coursemodule.model.ElectiveModule;
import org.jda.example.coursemanrestful.test.CRUDTestMaster;
import org.junit.Before;
import org.junit.Test;

/**
 * @version 1.0
 * @overview
 */
public class CRUDCourseModule extends CRUDTestMaster<CourseModule> {

  private MicroDomSoftware<CourseModule> ms;
  private Class<CourseModule> cls;

  @Before
  public void initMe() throws DataSourceException {
    cls = CourseModule.class;
    ms = createMicroSoftware(
        getSw(),
        cls,
        // create
        createObject(),
        // update
        updateObject()
    );

    // REQUIRED
    ms.initDom();

    ms.getSw().registerClasses(new Class[]{CompulsoryModule.class, ElectiveModule.class});
  }

  @Test
  public void run() throws DataSourceException {

    logger().info("Deleting course module tables...");
//    ms.deleteDomainModel(
//        ElectiveModule.class,
//        CompulsoryModule.class,
//        CourseModule.class
//        );

    ms.displaySource();
  }
}
