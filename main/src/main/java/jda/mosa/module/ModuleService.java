package jda.mosa.module;

import java.lang.reflect.Method;

import jda.modules.common.exceptions.NotFoundException;
import jda.mosa.controller.assets.composite.MethodListener;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MethodName;

/**
 * @overview 
 *  Represents a generic interface for components wishing to offer some module's functionality (i.e. service). 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 3.4c
 */
public interface ModuleService {

  /**
   * This method is used with {@link #addMethodListener(AppState, MethodListener)} and {@link #removeMethodListener(AppState, MethodListener)}. 
   * 
   * @effects if l already registered to <tt>state</tt> in this do nothing
   *          else register l to listen to <tt>state</tt>
   */
  void setMethodListener(AppState state, MethodListener l);
  
  /**
   * This method is paired with {@link #removeMethodListener(AppState, MethodListener)}.
   * 
   * @effects registers l to listen to the state change event that causes the
   *          state <tt>state</tt> raised by this
   */
  void addMethodListener(AppState state, MethodListener l);
  
  /**
   * This method is paired with {@link #addMethodListener(AppState, MethodListener)}.
   * 
   * @effects unregister <tt>l</tt> from listening to the state change event
   *          that causes the state <tt>state</tt> raised by this
   *  
   */
  void removeMethodListener(AppState state, MethodListener l);

  /**
   * @effects 
   *  returns {@link Context} surrounding this module service 
   */
  Context getContext();

  /**
   * @effects 
   *  if <tt>serviceCls</tt> is a data controller service
   *    return <tt>true</tt> 
   *  else
   *    return <tt>false</tt>
   */
  boolean isDataService(Class serviceCls);

  /**
   * @effects 
   *  returns the {@link Module} of this module service 
   */
  Module getModule();

  /**
   * @effects 
   *  if exists {@link Method} m declared in this.class whose name is <tt>methodName</tt> and 
   *  parameter types (if specified) are <tt>paramTypes</tt>
   *    return m
   *  else
   *    throws NotFoundException
   */
  Method getServiceMethod(MethodName methodName, Class[] paramTypes) throws NotFoundException;

  /**
   * @effects 
   *  if this provides a user interface
   *    return true
   *  else
   *    return false 
   */
  boolean hasView();

  /**
   * @effects 
   *  if {@link #hasView()}
   *    activate the user interface of this service
   *  else
   *    do nothing 
   */
  void activateView();
  
  /**
   * @effects 
   *  if exists a parent of this 
   *    return it
   *  else
   *    return null
   * @version 5.6 
   *
   */
  ModuleService getParent();

}
