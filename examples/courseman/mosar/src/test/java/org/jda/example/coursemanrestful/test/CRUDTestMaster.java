package org.jda.example.coursemanrestful.test;

import ch.qos.logback.classic.Logger;
import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.expression.Op;
import jda.modules.common.io.ToolkitIO;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.impl.DomSoftware;
import jda.software.ddd.MicroDomSoftware;
import org.courseman.software.config.SCCCourseManDerby;
import org.jda.example.coursemanrestful.modules.address.model.Address;
import org.jda.example.coursemanrestful.modules.coursemodule.model.CompulsoryModule;
import org.jda.example.coursemanrestful.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanrestful.modules.coursemodule.model.ElectiveModule;
import org.jda.example.coursemanrestful.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanrestful.modules.student.model.Student;
import org.jda.example.coursemanrestful.software.config.SCCCourseMan;
import org.jda.example.coursemanrestful.utils.DToolkit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @version 1.0
 * @overview The master test app for CRUD micro-domain software.
 */
public class CRUDTestMaster<T> {

  private final Logger logger = (Logger) LoggerFactory.getLogger(getTestContext());

  private DomSoftware sw;

  private DomSoftware swUI;
  protected String getTestContext() {
    return this.getClass().getSimpleName();
  }

  @Before
  public void init() {
    Optional<String> appType = DToolkit.getEnvProp("app.type");
    if (appType.isEmpty() || appType.get().equals("standard")) {
      logger().info("Creating a standard DOM Software (using PostgreSQL database)...");
      sw = SoftwareFactory.createStandardDomSoftware(SCCCourseMan.class);
    } else {
      logger().info("Creating a default DOM Software (with embedded Derby database)...");
      sw = SoftwareFactory.createStandardDomSoftware(SCCCourseManDerby.class);
    }

    sw.init();
  }

  protected DomSoftware getSw() {return sw; }

  protected MicroDomSoftware<T> createMicroSoftware(DomSoftware sw,
                                                        Class<T> domClass
  ) {
    return createMicroSoftware(sw, domClass, null, null);
  }

  protected MicroDomSoftware<T> createMicroSoftware(DomSoftware sw,
                                                        Class<T> domClass,
                                                        Supplier createObject
  ) {
    return createMicroSoftware(sw, domClass, createObject, null);
  }

  protected MicroDomSoftware<T> createMicroSoftware(DomSoftware sw,
                                                      Class<T> domClass,
                                                        Supplier createObject,
                                                        Supplier updateObject
                                                      ) {
    return new MicroDomSoftware<T>(sw, domClass) {
      @Override
      public MicroDomSoftware run() throws DataSourceException {
        return initDom()
            .loadAndDisplay()
            .displaySource()
            ;
      }

      @Override
      public MicroDomSoftware create() throws DataSourceException {
        if (createObject != null) {
          Object result = createObject.get();
        }
        return this;
      }

      @Override
      protected void doUpdate(T obj, Object id) throws DataSourceException {
        if (updateObject != null) {
          Object result = updateObject.get();
        }
      }
    };
  }

  protected Supplier createObject() {
    return () -> {
      logger().info("Create object: TODO");
      return null;
    };
  }

  protected Supplier updateObject() {
    return () -> {
      logger().info("Update object: TODO");
      return null;
    };
  }

  /**
   *
   * @effects
   *  return a random object of the domain class <tt>cls</tt>, retrieved from the data source
   * @version 1.0
   */
  protected <T> T any(Class<T> cls) throws DataSourceException {
    // todo: random?
    /*
    * derby random select:
    select * from courseman.address
    order by random()
    fetch first 1 rows only;
    * */
    getSw().openMetadata(cls);

    return getSw().retrieveObject(cls, "id", Op.EQ, "1");
  }

  protected void runUI() {
    // 2. create UI software
    if (swUI == null) {
      swUI = SoftwareFactory.createUIDomSoftware();
      // create in memory configuration
      System.setProperty("domainapp.setup.SerialiseConfiguration", "false");
    }

    // 3. run it
    Class[] model = {
        Address.class,
        Student.class,
        CourseModule.class,
        CompulsoryModule.class,
        ElectiveModule.class,
        Enrolment.class
    };

    try {
      swUI.run(model);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  protected Logger logger() { return logger; }

  @Test
  public void run() throws DataSourceException{
    logger().info("run(): do nothing!");
  }

  public static void main(String[] args) throws DataSourceException {
    new CRUDTestMaster().runUI();
  }
}
