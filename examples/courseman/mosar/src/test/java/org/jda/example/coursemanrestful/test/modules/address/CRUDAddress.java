package org.jda.example.coursemanrestful.test.modules.address;

import jda.modules.common.exceptions.DataSourceException;
import jda.software.ddd.MicroDomSoftware;
import org.jda.example.coursemanrestful.modules.address.model.Address;
import org.jda.example.coursemanrestful.test.CRUDTestMaster;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Supplier;

import static org.jda.example.coursemanrestful.utils.DToolkit.*;

/**
 * @version 1.0
 * @overview
 */
public class CRUDAddress extends CRUDTestMaster<Address> {

  private MicroDomSoftware<Address> ms;
  private Class<Address> cls;

  @Before
  public void initMe() throws DataSourceException {
    cls = Address.class;
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
  }

  @Test
  public void run() throws DataSourceException {

    logger().info("BEFORE...");
    ms.loadAndDisplay()
        .displaySource()
    ;

    // create
    ms.create();

    logger().info("AFTER CREATE...");
    ms.displaySource();

    // update
    // delete
    // ms.delete(1);
  }

  @Override
  protected Supplier createObject() {
    return () -> {
      logger().info("Creating object Address objects...");

      try {

        // todo: loop to create many random objects (e.g. using the format `value + counter`)
        Address[] objs = {
            new Address(randCity()),
            new Address(randCity()),
            new Address(randCity()),
            new Address(randCity()),
            new Address(randCity()),
        };

        getSw().createObjects(Address.class, Arrays.asList(objs));
      } catch (Exception e) {
        e.printStackTrace();
      }

      return null;
    };
  }

}
