package jda.modules.dodm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview 
 *  A generic factory class to create domain objects. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4
 */
public class ObjectFactory {
  private ObjectFactory() {}
  
  /**
   * @version 2.7.4
   * @effects if could not execute the <b>default
   *          constructor</b> of <tt>c</tt> throws <code>NotPossibleException</code>, else returns
   *          a new instance of <code>c</code>
   */
  public static <T> T createObject(Class<T> c) throws NotPossibleException {
    try {
      return c.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {c.getSimpleName(), ""});
    }
  }
  
  /**
   * @requires 
   *  valsMap != null /\ !valsMap.isEmpty()
   *  
   * @effects if no constructors of the domain class <code>c</code> exist for
   *          the arguments <code>attributeVals</code> throws
   *          <code>NotFoundException</code>, else if could not execute the
   *          constructor throws <code>NotPossibleException</code>, else returns
   *          a new instance of <code>c</code>, the values of whose domain
   *          attributes are initialised to the elements of
   *          <code>attributeVals</code>;
   * @version 
   * - 5.0: redesigned to be more flexible (makes use of {@link AttrRef})
   */
  public static <T> T createObject(Class<T> c, Map<DAttr, Object> valsMap) 
    throws NotFoundException, NotPossibleException {

    // first look in c for the constructor whose parameters are annotated with AttrRefs to the attributes 
    // in valsMap
    Constructor<T> co = findConstructorMethod(c, valsMap.keySet());
    
    Object[] valArr = null;
    
    if (co != null) {
      // found a constructor, use it 
      try {
        // create valArr according to the constructor's parameter ordering
        valArr = new Object[valsMap.size()];
        Parameter[] params = co.getParameters();
        Set<Entry<DAttr,Object>> entries = valsMap.entrySet();
        int idx = 0;
        for (Parameter param : params) {
          String attrRef = param.getAnnotation(AttrRef.class).value();
          Object val = null;
          for (Entry<DAttr, Object> e : entries) {  // find the value for param
            if (e.getKey().name().equals(attrRef)) {
              val = e.getValue(); break;
            }
          }
          valArr[idx++] = val;
        }
        T o = co.newInstance(valArr);
        
        return o;
      } catch (InstantiationException | IllegalAccessException
          | IllegalArgumentException | InvocationTargetException e) {
          throw new NotPossibleException(
              NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
              e, new Object[] {c, "init", Arrays.toString(valArr)}
              );
      }
    } else {
      valArr = valsMap.values().toArray(new Object[0]);
      // no such constructor exists, fall back to the primitive method of using the values to 
      // find the best constructor that matches them
      return createObject(c, valArr);
    }
  }
  
  /**
   * This is an OLD method. Uses {@link #createObject(Class, Map)} instead. 
   * 
   * @requires 
   *  attributeVals != null
   *  
   * @effects if no constructors of the domain class <code>c</code> exist for
   *          the arguments <code>attributeVals</code> throws
   *          <code>NotFoundException</code>, else if could not execute the
   *          constructor throws <code>NotPossibleException</code>, else returns
   *          a new instance of <code>c</code>, the values of whose domain
   *          attributes are initialised to the elements of
   *          <code>attributeVals</code>;
   * @version 
   * - 3.3: made static
   */
  public static <T> T createObject(Class<T> c, Object[] attributeVals)
      throws NotFoundException, NotPossibleException {

    T o = null;
    try {

      // create a new object using the default constructor method
      Constructor[] cons = c.getDeclaredConstructors();

      // find the constructor that has the same signature as the attributes
      // specified in values
      Constructor co = null;
      Class[] paramTypes;
      
      // v2.7.3: keep track of attribute types to use in exception message (if needed)
      Class[] attributeTypes = new Class[attributeVals.length];
      
      OUTER: for (int i = 0; i < cons.length; i++) {
        co = cons[i];
        paramTypes = co.getParameterTypes();
        if (paramTypes.length == attributeVals.length) {
          boolean match = true;
          CONS: for (int k = 0; k < paramTypes.length; k++) {
            Class type = paramTypes[k];
            Object obj = attributeVals[k];
            Class oc = (obj != null) ? obj.getClass() : null;
            attributeTypes[k] = oc; // v2.7.3
            
            // compare the object type with the parameter type
            if (obj == null)
              continue; // consider a match

            // v3.0: if (!type.equals(oc) && !isDecendant(oc, type)) {
            if (!isDecendant(oc, type)) {
              match = false;
              break CONS;
            }
          } // end CONS loop
          if (match) {
            // found the constructor
            break OUTER;
          }
        }
        co = null;
      } // end OUTER loop

      if (co == null) {
        throw new NotFoundException(
            NotFoundException.Code.CONSTRUCTOR_METHOD_NOT_FOUND,
            new Object[] {c,
            "\nArgs: " + Arrays.toString(attributeVals) + "\nTypes: " +  
            Arrays.toString(attributeTypes)});
      }

      // System.out.println("constructor: " + co);
      // create a new object
      o = (T) co.newInstance(attributeVals);
    } catch (InstantiationException e) {
      // e.printStackTrace();
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
          // v3.3: "Không thể thực thi phương thức: {0}({1})", c.getName(), ""
          e, new Object[] {c, "init", Arrays.toString(attributeVals)}
          );
    } catch (IllegalAccessException e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
          // v3.3: "Không thể thực thi phương thức: {0}({1})", c.getName(), ""
          e, new Object[] {c, "init", Arrays.toString(attributeVals)}
          );
    } catch (InvocationTargetException e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
          // v3.3: e.getCause(),"Không thể thực thi phương thức: {0}({1})", c.getName(), ""
          e, new Object[] {c, "init", Arrays.toString(attributeVals)}
          );
    }

    return o;
  }
  
  /**
   * @effects 
   *  if exists {@link Constructor} method of <tt>c</tt> whose parameters are annotated with 
   *  names of the attributes in <tt>attribs</tt>
   *    return it
   *  else
   *    return null
   *    
   * @version 5.0
   */
  private static <T> Constructor<T> findConstructorMethod(Class<T> c, Collection<DAttr> attribs) {
    // find in c the constructor whose parameters reference these attributes
    Constructor[] constructors = c.getConstructors(); 
    
    if (constructors.length == 0) // no constructors
      return null;
    
    int numAttribs = attribs.size();
    Constructor theCons = null;
    for (Constructor cons : constructors) {
      Parameter[] params = cons.getParameters();
      if (params.length == numAttribs) {
        // possibly this one...
        boolean match = true;
        for (Parameter param : params) {
          AttrRef attrRef = param.getAnnotation(AttrRef.class);
          if (attrRef == null) {
            // param is not annotated with AttrRef -> not this constructor
            match = false;
            break;
          } else {
            boolean paramMatchAttrib = false;
            for (DAttr attrib : attribs) {
              if (attrib.name().equals(attrRef.value())) {
                // param references an attrib -> possibly this one
                paramMatchAttrib = true;
                break;
              }
            }
            
            if (!paramMatchAttrib) {
              // param does not reference any attribs -> not this constructor
              match = false;
              break;
            }
          }
//          else if (attribs.contains(attrRef.value())) {
//            // param references a required attrib -> possibly this one
//          } else {
//            // param does not references a required attrib -> not this constructor
//            match = false;
//            break;
//          }
        } // end params
        
        if (match) {
          // cons is a match
          theCons = cons;
          break;
        }
      }
    }
    
    // return result
    return theCons;
  }
  
  /**
   * @effects if class <code>c1</code> is a descendent of class <code>c2</code>
   *          returns <code>true</code>, else returns <code>false</code>.
   * 
   * @version 
   *  - 3.0: fixed error in not recognising c2 when it is an interface type <br>
   *  - 3.3: made static
   */
  private static boolean isDecendant(Class c1, Class c2) {
    /* v3.0
    Class sup = c1.getSuperclass();

    if (sup == null)
      return false;

    if (sup == c2) {
      return true;
    } else if (sup != Object.class) {
      return isDecendant(sup, c2);
    } else {
      return false;
    }
    */
    return c2.isAssignableFrom(c1);
  }
}
