package jda.mosa.software.impl;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.jdatool.DomainAppTool;
import jda.modules.setup.model.SetUpGen;
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
 *  <p>Unlike {@link DomSoftware}, this software generates a GUI-based application
 *  for user to perform actions on the GUI.
 *  
 * @version 4.0
 */
public class UISoftware extends DomSoftware {
  
  /**
   * @effects 
   *
   */
  public UISoftware(Class scc) throws NotPossibleException {
    super(scc);
  }

  /**
   * @effects 
   *
   */
  public UISoftware(Class scc, Class<? extends SetUpGen> setUpCls) {
    super(scc, setUpCls);
  }

  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public void run(Object... args) throws Exception {
    DomainAppTool.run((Class[]) args);
  }
}
