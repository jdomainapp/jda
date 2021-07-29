package jda.modules.dcsl.parser.behaviourspace;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.type.Type;

import jda.modules.dcsl.syntax.DOpt;

/**
 * @overview Represents the standard method signatures of the pre-defined domain methods of DCSL's behaviour space.
 *  Each signature is mapped to (and identified by) its {@link DOpt.Type}.
 * 
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2
 */
public class MethodSig {

  private Type returnType;
  private String name;
  private Type[] paramTypes;
  private String[] paramNames;
  private Class[] throwables;
  private Modifier[] modifiers;

  /**signature table*/
  private static Map<DOpt.Type, MethodSig> sigs = new HashMap<>();

  private MethodSig() {
    //
  }
  
  
  /**
   * @effects 
   *  initialise this
   */
  private MethodSig(Type returnType, String name, Type[] paramTypes,
      String[] paramNames, Class[] exceptions, Modifier...modifier) {
    this.returnType = returnType;
    this.name = name;
    this.paramTypes = paramTypes;
    this.paramNames = paramNames;
    this.throwables = exceptions;
    this.modifiers = modifier;
  }


  /**
   * @effects 
   *  if this contains a {@link MethodSig} matching the specified <tt>optType</tt>
   *    return true
   *  else
   *    return false
   */
  public static MethodSig getInstance(DOpt.Type optType) {
    return sigs.get(optType);
  }

  /**
   * @param optType: required
   * @param returnType: required
   * @param paramTypes: required
   * @param modifier: required
   * @param name: optional
   * @param paramNames: optional
   * @param exceptions: optional
   * 
   * @effects 
   *  create and return a {@link MethodSig} whose opt-type is <tt>optType</tt> and whose header 
   *  is defined by other parameters
   */
  public static MethodSig createInstance(DOpt.Type optType,
      Type returnType,
      String name, Type[] paramTypes, String[] paramNames,
      Class[] exceptions, Modifier...modifier) {
    MethodSig sig = new MethodSig(returnType, name, paramTypes, paramNames, exceptions, modifier);
    
    sigs.put(optType, sig);
    
    return sig;
  }


  /**
   * @effects return returnType
   */
  public Type getReturnType() {
    return returnType;
  }


  /**
   * @effects return name
   */
  public String getName() {
    return name;
  }


  /**
   * @effects return paramTypes
   */
  public Type[] getParamTypes() {
    return paramTypes;
  }


  /**
   * @effects return paramNames
   */
  public String[] getParamNames() {
    return paramNames;
  }


  /**
   * @effects return throwables
   */
  public Class[] getThrowables() {
    return throwables;
  }


  /**
   * @effects return modifiers
   */
  public Modifier[] getModifiers() {
    return modifiers;
  }


  /**
   * @param mname: optional
   * @param mparamNames: optional
   * @param mthrowables: optional
   * 
   * @effects
   *  if this equals to the signature consisting of the parameters
   *    return true
   *  else
   *    return false 
   */
  public boolean equals(Type mreturnType, String mname,
      String[] mparamNames, Type[] mparamTypes, 
      Class[] mthrowables,
      Modifier... mmodifiers) {
    return MethodSig.equals(
        returnType, name, paramNames, paramTypes, throwables, modifiers, 
        mreturnType, mname, mparamNames, mparamTypes, mthrowables, mmodifiers);
  }
  
  /**
   * @param mname: optional
   * @param mparamNames: optional
   * @param mthrowables: optional
   * 
   * @effects
   *  if this equals to the signature consisting of the parameters
   *    return true
   *  else
   *    return false 
   */
  public static boolean equals(
      // signature 1
      Type returnType, String name,
      String[] paramNames, Type[] paramTypes, 
      Class[] throwables,
      Modifier[] modifiers,
      // signature 2
      Type mreturnType, String mname,
      String[] mparamNames, Type[] mparamTypes, 
      Class[] mthrowables,
      Modifier[] mmodifiers) {
    // check return type
    if (returnType != null && !returnType.equals(mreturnType))
      return false;
    
    // check name
    if (name != null && ! name.equals(mname)) 
      return false;

    // skip: check param names
    
    // check param-types
    if (paramTypes != null) {
      if (mparamTypes == null) 
        return false;
      
      if (!Arrays.equals(paramTypes, mparamTypes)) {
        return false;
      }
    }
    
    // check throwables (if any)
    if (throwables != null) {
      if (mthrowables == null) 
        return false;

      if (!Arrays.equals(throwables, mthrowables)) {
        return false;
      }
    }
    
    // check modifiers
    if (!Arrays.equals(modifiers, mmodifiers))
      return false;
    
    // equal!
    return true;
  }
}
