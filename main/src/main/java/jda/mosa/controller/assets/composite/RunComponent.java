package jda.mosa.controller.assets.composite;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotFoundException;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MessageCode;
import jda.mosa.controller.assets.util.MethodName;

/**
 * Represents the (start,stop) method pair of either a {@see DataController}
 * or a {@see Controller} that will be executed by a
 * <code>CompositeController</code> object.
 * 
 * @author dmle
 */
public class RunComponent implements MethodListener {
  private List<AppState> stopStates;
  //private AppState stopState;
  private ControllerBasic ctl;
  private ControllerBasic.DataController dctl;
  private Method runMethod;
  private Object runOutput;

  /**
   * whether or not this component is run once in all the executions of 
   * the run sequence that contains it.
   */
  private boolean singleRun;
  
  /** whether or not this component has been run once. This is used together with
   * {@link #singleRun} to determine whether to re-run a component. */
  private boolean executed;
  
  private Method stopMethod;

  /** whether or not the run method has completed */
  private boolean stopped;
  
  private static final boolean debug = Toolkit.getDebug(RunComponent.class);
  
  protected RunComponent() {
    //
  }
  
  private RunComponent(ControllerBasic controller,
      ControllerBasic.DataController dctl, String runMethodName,
      Class[] parameterTypes, String stopMethodName,
      Class[] stopParameterTypes, AppState...stopStates) throws NotFoundException {

    // validate arguments
    if (controller == null && dctl == null)
      throw new IllegalArgumentException(
          "At least controller or data controller must be specified");

    //this.stopState = stopState;
    this.ctl = controller;
    this.dctl = dctl;
    stopped = false;

    // get the Method objects
    Class ctlClass = null;
    Class dctlClass = null;

    if (dctl != null)
      dctlClass = dctl.getClass();

    if (controller != null)
      ctlClass = controller.getClass();

    // run method may be null
    if (runMethodName != null) {
      if (dctl != null)
        this.runMethod = Toolkit.getMethod(dctlClass, runMethodName, parameterTypes);
      else
        this.runMethod = Toolkit.getMethod(ctlClass, runMethodName, parameterTypes);
    }
    
    if (stopMethodName != null) {
      Method m = null;

      if (dctlClass != null) {
        try {
          m = Toolkit.getMethod(dctlClass, stopMethodName, stopParameterTypes);
        } catch (NotFoundException e) {
        }
      }
      
      if (m == null) {
        // try the controller class
        m = Toolkit.getMethod(ctlClass, stopMethodName,
            stopParameterTypes);
      }

      this.stopMethod = m;
    }

    // finally if stop state is specified then registers to listen to it
    // TODO: improve this: why need both ctl and dctl
    if (stopStates.length > 0) {
      this.stopStates = new ArrayList();
      Collections.addAll(this.stopStates, stopStates);

      // register to listen to each of the states
      registerListeners();
//      for (AppState state : stopStates) {
//        if (dctl != null)
//          this.dctl.addMethodListener(state, this);
//        else
//          ctl.addMethodListener(state, this);
//      }
    }
    
    // all components are not single-run by default
    singleRun = false;
  }


//  /**
//   * @effects initialises this with methods of a {@see Controller} class
//   */
//  public RunComponent(Controller controller, AppState stopState, String runMethodName,
//      Class[] parameterTypes, String stopMethodName,
//      Class[] stopParameterTypes) throws NotFoundException {
//    this(controller, null, runMethodName, parameterTypes,
//        stopMethodName, stopParameterTypes, stopState);
//  }

  public RunComponent(ControllerBasic controller, String runMethodName,
      Class[] parameterTypes) throws NotFoundException {
    this(controller, null, runMethodName, parameterTypes,
        null, null);
  }
  
  public RunComponent(ControllerBasic controller) throws NotFoundException {
    this(controller, null, MethodName.run.name(), null,
        null, null);
  }
  
  /**
   * @effects initialises this with methods of a {@see
   *          Controller.DataController} class
   */
  public RunComponent(ControllerBasic.DataController controller,
      AppState stopState, 
      String runMethodName, Class[] parameterTypes, String stopMethodName,
      Class[] stopParameterTypes) throws NotFoundException {
    this(controller.getCreator(), controller, runMethodName,
        parameterTypes, stopMethodName, stopParameterTypes, stopState);
  }

  /**
   * This is to create a virtual component that is part of a GUI-interaction sequence.
   * It simply waits for the user to finished with the GUI and a suitable event 
   * state is raised.
   * @effects 
   *  create a RunComponent with no run and stop methods.
   */
  public RunComponent(ControllerBasic.DataController controller,
      AppState...stopStates) throws NotFoundException {
    this(controller.getCreator(), controller, null,
        null, null, null,stopStates);
  }

  public RunComponent(ControllerBasic.DataController controller,
      String runMethodName, Class[] parameterTypes) throws NotFoundException {
    this(controller.getCreator(), controller, runMethodName,
        parameterTypes, null, null);
  }

  public RunComponent(ControllerBasic.DataController controller, AppState stopState,
      String runMethodName, Class[] parameterTypes) throws NotFoundException {
    this(controller.getCreator(), controller, runMethodName,
        parameterTypes, null, null,stopState);
  }
  
  /**
   * @requires 
   *  this.stopStates != null
   * @effects
   *  register this to listen to method performed events of this.stopStates
   */
  private void registerListeners() {
    // v2.7.3: register listeners if not yet already registered
    // this is to avoid the situation where a method was terminated in the middle
    // leaving its listener still registered
    for (AppState state : stopStates) {
      if (dctl != null) {
        //v2.7.3: this.dctl.addMethodListener(state, this);
        this.dctl.setMethodListener(state, this);
      } else {
        // v2.7.3: ctl.addMethodListener(state, this);
        ctl.setMethodListener(state, this);
      }
    }
  }

  /**
   * @requires 
   *  this.stopStates != null
   * @effects
   *  remove this from the list of listeners for method-performed events of this.stopStates
   */
  private void removeListeners() {
    for (AppState state : stopStates) {
      if (dctl != null)
        this.dctl.removeMethodListener(state, this);
      else
        ctl.removeMethodListener(state, this);
    }
  }

  public void setSingleRun(boolean b) {
    this.singleRun = b;
  }

  public boolean getSingleRun() {
    return this.singleRun;
  }
  
  
  public boolean isCompleted() {
    return stopped;
  }

  /**
   * @effects  
   *  if this component has been run once
   *    return true
   *  else
   *    return false
   */
  public boolean isExecuted() {
    return executed;
  }
  
  public void stop() {
    if (stopMethod != null) {
      try {
        Class stopClass = stopMethod.getDeclaringClass();
        
        Object runObj = getRunObject(stopMethod);

        stopMethod.invoke(runObj, null);
      } catch (Exception e) {
        ctl.displayErrorFromCode(MessageCode.ERROR_PERFORM_METHOD, 
            e, stopMethod.getName());
      }
    }
  }

//  /**
//   * @effects 
//   *  prepare for run (e.g. register to listen to method event if stop state is specified)
//   */
//  private void prepareState() {
//    if (stopState != null) {
//      if (dctl != null)
//        this.dctl.addMethodListener(stopState, this);
//      else
//        ctl.addMethodListener(stopState, this);
//
//    }
//  }
  
  public void run(Object arg) throws Exception {
    //if (debug)
    //  ctl.displayConsoleMessage("Executing component " + this);

    if (stopped) {
      // already run, reset first
      reset();
    }
    // v 2.5.4
//    Object runObj = getRunObject(runMethod);     
//    
//    Object output;
//    if (runMethod.getParameterTypes().length == 0)
//      output = runMethod.invoke(runObj, new Object[0]);
//    else
//      output = runMethod.invoke(runObj, arg);
//
//    /** if no stopState is specified then stop when invocation is done */
//    if (stopState == null) {
//      stopped = true;
//    }
//
//    if (stopped) { // applies to synchronous call only 
//      /** if method returns a value then assigns it to the run output */
//      Class rt = runMethod.getReturnType();
//      if (!rt.getName().equals("void")) {
//        runOutput = output;
//      }
//    }

    Object output = null;

    if (runMethod != null) {
      Object runObj = getRunObject(runMethod);     
      
      if (runMethod.getParameterTypes().length == 0)
        output = runMethod.invoke(runObj, new Object[0]);
      else
        output = runMethod.invoke(runObj, arg);
    }

    /** if no stopState is specified then stop when invocation is done */
    if (stopStates == null) {
      stopped = true;
    }

    if (stopped && runMethod != null) { // applies to synchronous call only 
      /** if method returns a value then assigns it to the run output */
      Class rt = runMethod.getReturnType();
      if (!rt.getName().equals("void")) {
        runOutput = output;
      }
    }

    postRun();
  }
  
  // MethodListener
  /**
   * This method is invoked only for asynchronous execution of this component
   * @see #run(Object)
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
      if (sctl == dctl) {
        // TODO: do we need to also check the state of the controller to make
        // sure that
        // it performed successfully?
        
        // debug
        //System.out.printf("%s.methodPerformed: state = %s%n", this, sctl.getCurrentState());
        
        
        // remove listeners
        removeListeners();
        //ctl.removeMethodListener(stopState, this);

        // get the output
        runOutput = e.getValue();
        
        // stop the controller if the stop method is specified
        stop();
        
        stopped = true;
        
        postRun();
      }
    }
  }

  private void postRun() {
    if (!executed)
      executed = true;
  }
  
  /**
   * @effects returns either <code>this.dctl</code> or <code>this.ctl</code> depending on 
   *          which object is associated to the method <code>m</code>
   */
  private Object getRunObject(Method m) {
    Object runObj;
    Class runClass = m.getDeclaringClass();
    
    try {
      if (dctl != null && dctl.getClass().asSubclass(runClass) != null)
        runObj = dctl;
      else
        runObj = ctl;
    } catch (ClassCastException e) {
      // no the type
      runObj = ctl;
    }
    
    return runObj;
  }
  
  public Object getOutput() {
    return runOutput;
  }
  
  /**
   * @effects 
   *  if data controller dctl != null
   *    return the creator of the data controller
   *  else
   *    return the controller 
   */
  public ControllerBasic getController() {
    if (dctl != null) {
      return dctl.getCreator();
    } else {
      return ctl;
    }
  }

  /**
   * @effects 
   *  if runMethod is specified
   *    return the name of this method
   *  else
   *    return <tt>null</tt> 
   */
  public String getMethodName() {
    if (runMethod != null) {
      return runMethod.getName();
    } else {
      return null;
    }
  }
  
  public void reset() {
    runOutput = null;
    stop();
    stopped = false;
    // if state is specified then re-register to listen to the state
    if (stopStates != null) {
      registerListeners();
      //ctl.addMethodListener(stopState, this);
    }
  }

  public String toString() {
    return "Component(" + ctl + "." + 
          ((runMethod != null) ? runMethod.getName() : "?") + ")";
  }
} // end Component