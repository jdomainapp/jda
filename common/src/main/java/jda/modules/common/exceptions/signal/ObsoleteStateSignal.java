package jda.modules.common.exceptions.signal;

import jda.modules.common.exceptions.ApplicationRuntimeException;

/**
 * @overview
 *  A 'valid' exception to signal the calling code to process 
 *  the situation that is captured in the name of the exception
 *   
 * @author dmle
 */
public class ObsoleteStateSignal extends ApplicationRuntimeException {

  public ObsoleteStateSignal() {
    super(null, null, null, null);
  }
  
  public ObsoleteStateSignal(String mesg) {
    super(null, mesg);
  }
  
//  public ObsoleteIdStateSignal(InfoCode errCode, String msg) {
//    super(errCode, msg);
//  }
//
//  public ObsoleteIdStateSignal(InfoCode errCode, String msg, Object... args) {
//    super(errCode, msg, args);
//  }

//  public ObsoleteIdStateSignal(InfoCode errCode, String msg, Throwable e,
//      Object... args) {
//    super(errCode, msg, e, args);
//  }

}
