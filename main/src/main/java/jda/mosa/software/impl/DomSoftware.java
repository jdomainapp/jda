package jda.mosa.software.impl;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.setup.model.SetUpGen;
import jda.modules.setup.sysclasses.DefaultSystemClass;
import jda.mosa.software.aio.SoftwareAio;

/**
 * @overview 
 *  An implementation of {@link SoftwareImpl} that represents 
 *  <b>domain-driven software</b>. That is a software that directly uses {@link SoftwareAio} and the domain model API
 *  to manipulate (CRUD) the domain model and its instances.
 *  A domain model instance consists of a set of domain models and its links.
 *  
 *  <p>This software uses the default relational database (usually JavaDb)
 *  for storing the domain model and its objects.
 *  
 * @version 4.0
 */
public class DomSoftware extends SoftwareImpl {
  final static Class SysCls = DefaultSystemClass.class;
  
  public DomSoftware() throws NotPossibleException {
    this(// system class with JavaDb
        SysCls);
  }
  
  public DomSoftware(Class scc) throws NotPossibleException {
    super(scc);
  }
  
  /**
   * @effects 
   */
  public DomSoftware(Class scc, Class<? extends SetUpGen> setUpCls) {
    super(scc == null ? SysCls : scc, setUpCls);
  }

  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public void run(Object... args) throws Exception {
    System.out.printf("Welcome to %s%n", DomSoftware.class.getSimpleName());
  }
}
