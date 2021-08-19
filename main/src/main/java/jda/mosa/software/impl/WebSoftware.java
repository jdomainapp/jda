package jda.mosa.software.impl;

import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.jdatool.DomainAppTool;
import jda.modules.setup.model.SetUpGen;
import jda.mosa.software.aio.SoftwareAio;

/**
 * @overview 
 *  An implementation of {@link SoftwareImpl} that represents 
 *  <b>web-based domain-driven software</b>. This is a software that directly uses 
 *  {@link SoftwareAio} and the domain model API
 *  to manipulate (CRUD) the domain model and its instances.
 *  A domain model instance consists of a set of domain models and its links.
 *  
 *  <p>As a <b>web-based</b> software, it can support different front-end and back-end 
 *  technology platforms. The back end's service layer uses a {@link DomSoftware} to 
 *  perform CRUD operations on the objects.
 *  
 * @version 4.0
 */
public class WebSoftware extends DomSoftware {
  


  /**
   * @effects 
   *
   */
  public WebSoftware(Class scc, Class<? extends SetUpGen> setUpCls) {
    super(scc, setUpCls);
  }

  /**
   * @effects 
   *
   */
  public WebSoftware(Class scc) throws NotPossibleException {
    super(scc);
  }

  /**
   * @requires
   *  <ul>
   *    <li>index.fe<li> (fe = js, jsp, html, etc.)
   *  </ul>
   * @effects 
   *  generates backend from the modules specified in this.setup
   *  execute a web server that serves index.fe
   */
  @Override
  public void run(Object... args) throws Exception {
    // TODO
    /* 
     * 1. generates backend from the modules specified in this.setup
     * 2. start web server that serves index.fe
     */
    throw new NotImplementedException("Not yet implemented");
  }
}
