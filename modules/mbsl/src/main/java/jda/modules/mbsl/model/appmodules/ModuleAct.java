package jda.modules.mbsl.model.appmodules;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mbsl.model.graph.Node;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.composite.MethodEvent;
import jda.mosa.controller.assets.composite.MethodListener;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MethodName;
import jda.mosa.module.ModuleService;

/**
 * @overview 
 *  Represents an operation executable by an application module.  
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ModuleAct implements MethodListener {

  private MethodName actName;
  private AppState[] endStates;
  private String[] attribNames;
  
  private Node node;
  private Object result;
  
  /** caches the currently referenced {@link ModuleService}, updated by {@link #exec(ModuleService, Object...)} */
  private ModuleService mService;
  
  /**derived from {@link #mService} and {@link #actName} */
  private Method runMethod;
  
  /**flag whether or not {@link #exec(ControllerBasic, Object...)} has completed*/
  private boolean stopped;
  

  /**
   * @effects 
   *
   * @version 
   */
  public ModuleAct(MethodName optName, AppState[] endStates, String[] attribNames, Node node) {
    this.actName = optName;
    this.endStates = endStates;
    this.attribNames = attribNames;
    this.node = node;
  }

  
  /**
   * @effects return actName
   */
  public MethodName getActName() {
    return actName;
  }


  /**
   * @effects return endState
   */
  public AppState[] getEndState() {
    return endStates;
  }

  /**
   * @effects
   *  if {@link #exec(ControllerBasic, Object...)} has completed
   *    return true
   *  else
   *    return false 
   */
  public boolean isStopped() {
    return stopped;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return "ModuleAct (" + actName + " ⟿ " + Arrays.toString(endStates) + ")";
  }


  /**
   * @requires <pre>
   *  {@link #runMethod}.parameters = [p1,...,pn] is 'compatible' to args according to ONE of the following rules:
   *  (1) {@link #attribNames} = null => pi is compatible to each args[i]
   *  (2) {@link #attribNames} != null => i = 1 /\ p0 = {@link #attribNames} /\ p1 = args
   *  </pre>
   * @effects <pre>
   *    if {@link #runMethod} is not initialised \/ mService is changed
   *      init {@link #runMethod} = method m of mService whose name is optName
   *    
   *    execute {@link #runMethod} on mService passing args as arguments.
   *    
   *  <p>If {@link #endState} is not specified (i.e. synchronous execution) then stop immediately 
   *  upon obtain result (if any) from invoking the target operation. 
   *  Otherwise (i.e. asynchronous execution), 
   *  wait until {@link #endState} is raised and {@link #methodPerformed(MethodEvent)} is called
   *  to process the result. 
   *  
   *  <p>throws NotFoundException if a method named {@link #actName} is not found in <tt>mService.class</tt>;
   *  NotPossibleException if failed to execute the method
   * </pre>
   */
  public void exec(ModuleService mService, Object...args) throws NotPossibleException, NotFoundException {
    // invoke this on the controller object of the module referenced by node
    if (isStopped()) stopped = false;
    
    // only retrieve runMethod and update this.mService when mService is changed
    if (this.mService == null || !this.mService.equals(mService)) {
      if (attribNames != null) {  // rule (1) 
        runMethod = mService.getServiceMethod(actName, new Class[] {String[].class, Object[].class});//new Class[] {Map.class});
      } else {  // rule (2): assume that args satisfy the parameters
        Class[] nullParamTypes = null;

        runMethod = mService.getServiceMethod(actName, nullParamTypes);
      }
      
      if (this.mService != null) unregisterListeners(this.mService); 
        
      this.mService = mService;
      
      ensureRegisterListeners(mService);
    } else {
      // if state is specified then re-register to listen to the state
      // because they may have been removed by #methodPerformed
      ensureRegisterListeners(mService);
    }

    Object output = invokeRunMethod(mService, runMethod, args);

    /** if endStates is not specified (i.e. synchronous execution) then stop when invocation is done 
     *  else (i.e. asynchronous execution) then must wait until #methodPerformed is invoked)  
     **/
    if (endStates == null) {
      stopped = true;
      if (runMethod != null) { 
        /** if method returns a value then assigns it to the run output */
        Class rt = runMethod.getReturnType();
        if (!rt.getName().equals("void")) {
          result = output;
        }
      }
    }
    
    postRun();
  }

  /**
   * This method is used together with {@link #methodPerformed(MethodEvent)} to carry out the invocation of 
   * a run method. {@link #methodPerformed(MethodEvent)} is invoked (at some later time) only if the run method
   * is asynchronous.  
   * 
   * @requires <pre>
   *  runMethod.parameters = [p1,...,pn] is 'compatible' to args according to ONE of the following rules:
   *  (1) {@link #attribNames} = null => pi is compatible to each args[i]
   *  (2) {@link #attribNames} != null => i = 2 /\ p0 = {@link #attribNames} /\ p1 = args
   *  </pre>
   * 
   * @effects 
   *  invoke <tt>runMethod</tt> on <tt>mService</tt> passing in <tt>args</tt> as arguments, 
   *  return invocation result or null if no result is expected.
   */
  private Object invokeRunMethod(ModuleService mService, Method runMethod, Object...args) throws NotPossibleException {
    Object output;
    Parameter[] params = runMethod.getParameters();
    if (params.length == 0) {
      // no arguments required
      Object[] nullArgs = null;
      try {
        output = runMethod.invoke(mService, nullArgs);
      } catch (Exception e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, 
            new Object[] {mService, runMethod.getName(), Arrays.toString(args)});
      }
    } else {
      // arguments required
      // invoke according to the compatibility rules stated in @requires
      if (attribNames == null) { // rule (1)
        try {
          output = runMethod.invoke(mService, args);
        } catch (Exception e) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, 
              new Object[] {mService, runMethod.getName(), Arrays.toString(args)});
        }
      } else { // rule (2)
        try {
          output = runMethod.invoke(mService, attribNames, args);
        } catch (Exception e) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, 
              new Object[] {mService, runMethod.getName(), Arrays.toString(args)});
        }
      }
//      try {
//        output = runMethod.invoke(mService, args);
//      } catch (Exception e) {
//        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, 
//            new Object[] {mService, runMethod.getName(), Arrays.toString(args)});
//      }
    }
    
    return output;
  }


  /**
   * This method is the opposite of {@link #ensureRegisterListeners(ModuleService)}.
   * 
   * @requires 
   *  this.{@link #endStates} != null /\ mService != null
   *  
   * @effects 
   *  unregister this from listening to events of this.{@link #endStates} that are raised by <tt>mService</tt>
   */
  private void unregisterListeners(ModuleService mService) {
    if (mService != null && endStates != null) {
      for (AppState state : endStates) {
        this.mService.removeMethodListener(state, this);
      }
    }
  }


  /**
   * @requires 
   *  this.{@link #endStates} != null /\ mService != null
   * @effects
   *  register this to listen to method events of this.{@link #endStates} that are raised by <tt>mService</tt> 
   */
  private void ensureRegisterListeners(ModuleService mService) {
    // register listeners if not yet already registered
    // this is to avoid the situation where a method was terminated in the middle
    // leaving its listener still registered
    if (mService != null && endStates != null) {
      for (AppState state : endStates) {
        this.mService.setMethodListener(state, this);
      }
    }
  }
  
  /**
   * This method is invoked only for asynchronous execution of {@link #invokeRunMethod(ModuleService, Method, Object...)}.
   * @effects <pre>
   *    if e.source eq {@link #mService}
   *      set {@link #result} = e.value
   *      set {@link #stopped} = true
   *      perform post-run tasks
   *    else
   *      do nothing
   *  </pre>
   *   
   */
  @Override // MethodListener
  public void methodPerformed(MethodEvent e) {
    /**
     * if event source is the data controller of the active component then
     * carries on with the execution
     */
    Object src = e.getSource();

    if (src != null && src instanceof DataController) {
      DataController sctl = (DataController) src;
      if (sctl == mService) {
        // TODO: do we need to also check the state of the controller to make
        // sure that
        // it performed successfully?
        
        // debug
        //System.out.printf("%s.methodPerformed: state = %s%n", this, sctl.getCurrentState());
        
        
        // remove listeners
        unregisterListeners(mService);

        // get the output
        result = e.getValue();
        
        // stop the controller if the stop method is specified
        stop();
        
        stopped = true;
        
        postRun();
      }
    }
  }

  /**
   * @effects 
   * 
   * @todo (if needed)
   */
  private void postRun() {
    // TODO Auto-generated method stub
  }

//  /**
//   * @effects 
//   *  reset state of this before a new run of {@link #exec(ModuleService, Object...)}
//   *  
//   */
//  public void reset() {
//    // TODO: do we need this?
//    // stop(); // make sure that this is stopped
//    
//    stopped = false;
//  }
  
  /**
   * @effects 
   * 
   * @todo (if needed)
   */
  public void stop() {
    // TODO Auto-generated method stub
  }


  /**
   * @effects 
   *  return the current execution result, i.e. that was set by the last invocation of {@link #exec(ControllerBasic, Object...)} 
   */
  public Object getResult() {
    return result;
  }

}
