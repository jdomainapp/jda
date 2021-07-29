/**
 * @overview
 *
 * @author dmle
 */
package jda.modules.mccl.syntax.controller;

import java.lang.annotation.Annotation;

import jda.modules.common.types.Null;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.mosa.controller.ControllerBasic;

/**
 * @overview
 *  An adapter for {@link ControllerDesc} that makes it easier for creating anonymous sub-types. 
 *  
 * @author dmle
 * @version 3.2
 */
@Deprecated
public class ControllerDescAdapter implements ControllerDesc {

  private static final PropertyDesc[] EmptyPropertyDescArr = {};
  
  private PropertyDesc[] props;

  /**
   * @effects 
   * 
   */
  public ControllerDescAdapter(PropertyDesc[] props) {
    this.props = props;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see java.lang.annotation.Annotation#annotationType()
   */
  @Override
  public Class<? extends Annotation> annotationType() {
    return ControllerDesc.class;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.config.controller.ControllerDesc#on()
   */
  @Override
  public boolean on() {
    return true;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.config.controller.ControllerDesc#controller()
   */
  @Override
  public Class controller() {
    return ControllerBasic.class;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.config.controller.ControllerDesc#dataController()
   */
  @Override
  public Class dataController() {
    return Null.class;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.config.controller.ControllerDesc#objectBrowser()
   */
  @Override
  public Class objectBrowser() {
    return Null.class;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.config.controller.ControllerDesc#defaultCommand()
   */
  @Override
  public LAName defaultCommand() {
    return LAName.Null;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.config.controller.ControllerDesc#isStateListener()
   */
  @Override
  public boolean isStateListener() {
    return false;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.config.controller.ControllerDesc#isDataFieldStateListener()
   */
  @Override
  public boolean isDataFieldStateListener() {
    return false;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.config.controller.ControllerDesc#openPolicy()
   */
  @Override
  public OpenPolicy openPolicy() {
    return OpenPolicy.I;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.config.controller.ControllerDesc#startAfter()
   */
  @Override
  public long startAfter() {
    return 0;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.config.controller.ControllerDesc#runTime()
   */
  @Override
  public long runTime() {
    return -1;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.config.controller.ControllerDesc#props()
   */
  @Override
  public PropertyDesc[] props() {
    if (props != null)
      return props;
    else
      return EmptyPropertyDescArr;
  }
}
