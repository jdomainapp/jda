package jda.modules.dcsl.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DOpt;

/**
 * @overview 
 *  Domain class Tool kit.
 *  
 * @author dmle
 *
 * @version 2017 
 */
public class DClassTk {
  
  public static final Class<DAttr> dcClass = DAttr.class;
  public static final Class<AttrRef> attrClass = AttrRef.class;
  private static final String thisCls = DClassTk.class.getSimpleName();

  private static final String[] JavaCollectionTypes = 
    {"Collection", "List", "Vector", "Set", "Queue", "Stack"};
  private static final String[] BasicJavaTypes = 
    {"Integer", "int", "Long", "long", "Float", "float", "Double", "double", 
        "char", "Character", "String"};
  
  private static final String[] DefaultOptNames = {
      "toString", "equals", "hashCode", "repOK"
  };
  
  private static final List<Class<? extends Annotation>> NonDomainAnos;
  static {
    NonDomainAnos = new ArrayList<>();
//    NonDomainAnos.add(ValidateExcl.class);
  }
  

  /**
   * A short-cut for {@link #getDAttrs(Class, boolean)} with args: <tt>(cls,false)</tt>.
   * 
   * @effects 
   *  if exists domain attributes in <tt>cls</tt>
   *    return their {@link DAttr}s in a {@link Map}
   *  else
   *    return null
   */
  public static Map<String,DAttr> getMyDAttrs(Class cls) {
    boolean recursive = false;
    return getDAttrs(cls, recursive);
  }
  
  /**
   * 
   * @effects
   *  return {@link DAttr} of the attribute named <tt>attributeName</tt> of 
   *  the domain class <tt>cls</tt> if present, otherwise return <tt>null</tt>
   *  
   *  <p>throws NoSuchFieldException if a field with the specified name is not found, 
   *  SecurityException if failed to access the field for some reasons
   */
  public static DAttr getDAttr(Class cls, String attributeName)
      throws NoSuchFieldException, SecurityException {
    if (attributeName == null)
      return null;

    
    // check for overriding domain constraint (declared in a method)
    Method mdc = getDomainMethodWithDAttr(cls, attributeName);
    if (mdc != null) {
      return mdc.getAnnotation(dcClass);
    } else {
      // use the field domain constraint (if any)
      return (DAttr) cls.getDeclaredField(attributeName)
          .getAnnotation(dcClass);
    }
  }
  
  /**
   * This method uses {@link #getDomainAttributes(Class, boolean)}. 
   * A special feature of this method is that it supports domain constraint overriding!
   * 
   * @effects 
   *  if exists domain attributes in <tt>cls</tt> and, if <tt>recursive=true</tt>, also in 
   *  ancestor classes of <tt>cls</tt> (if any)
   *    return their {@link DAttr}s in a {@link Map}
   *  else
   *    return null
   */
  public static Map<String,DAttr> getDAttrs(
      Class cls, boolean recursive) {
    Collection<Field> attribs = getDomainAttributes(cls, recursive);
    if (attribs != null) {
      Map<String,DAttr> dcs = new LinkedHashMap<>();
      
      // any methods that are attached with @DAttr 
      Collection<Method> methods = getDomainMethodsWithDAttrs(cls);

      String fname;
      DAttr dc;
      for (Field f : attribs) {
        fname = f.getName();
        dc = f.getAnnotation(dcClass);
        if (methods != null) {
          // determine if f's domain constraint is overwritten by a method in methods
          // if so use that method's domain constraint instead of f's
          for (Method m : methods) {
            AttrRef mref = m.getAnnotation(attrClass);
            if (mref.value().equals(fname)) {
              // m references f
              dc = m.getAnnotation(dcClass);
              break;
            }
          }
        }
        
        dcs.put(fname, dc);
      }
      
      return dcs;
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if exists in <tt>cls</tt> a declared method that is annotated with {@link #dcClass} and {@link #attrClass}
   *  and that {@link #attrClass} annotation references <tt>attribName</tt>
   *    return the method
   *  else
   *    return null
   */
  private static Method getDomainMethodWithDAttr(
      Class cls, String attribName) {
    
    Method[] allMethods = cls.getDeclaredMethods();
    
    if (allMethods.length > 0) {
      AttrRef ac;
      for (Method m : allMethods) {
        ac = m.getAnnotation(attrClass);
        if (m.isAnnotationPresent(dcClass) && ac != null) {
          // found a method
          return m;
        }
      }
      
      // not found
      return null;
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if exists in <tt>cls</tt> declared methods that are annotated with {@link #dcClass} and {@link #attrClass}
   *    return them as {@link Collection}
   *  else
   *    return null
   */
  private static Collection<Method> getDomainMethodsWithDAttrs(
      Class cls) {
    
    Method[] allMethods = cls.getDeclaredMethods();
    
    if (allMethods.length > 0) {
      Collection<Method> methods = new ArrayList<>();
      
      for (Method m : allMethods) {
        if (m.isAnnotationPresent(dcClass) && m.isAnnotationPresent(attrClass)) {
          // found a method
          methods.add(m);
        }
      }
      
      if (methods.isEmpty())
        return null;
      else
        return methods;
    } else {
      return null;
    }
  }

  /**
   * A short-cut for {@link #getDomainAttributes(Class, boolean)} with args: <tt>(cls,false)</tt>.
   * 
   * @effects 
   *  if exists domain attributes in <tt>cls</tt>
   *    return them in a {@link Collection}
   *  else
   *    return null
   */
  public static Collection<Field> getMyDomainAttributes(Class cls) {
    boolean recursive = false;
    return getDomainAttributes(cls, recursive);
  }
  
  /**
   * @effects 
   *  if exists domain attributes in <tt>cls</tt> and, if <tt>recursive=true</tt>, also in 
   *  ancestor classes of <tt>cls</tt> (if any)
   *    return them in a {@link Collection}
   *  else
   *    return null
   */
  private static Collection<Field> getDomainAttributesWithDuplicates(final Class c, boolean recursive) {

    // add fields that are inherited from the parent first
    // this order must be the same as the order of the constructor arguments
    // used to create objects
    // of the class

    Vector<Field> supFields = new Vector();
    if (recursive) {
      Class superClass = c.getSuperclass();
      if (superClass != null) {
        Collection<Field> parentFields = getDomainAttributesWithDuplicates(superClass, true);
        if (parentFields != null) {
          supFields.addAll(parentFields);
        }
      }
    }

    Field[] myFields = c.getDeclaredFields();
    // Collections.addAll(_fields, myFields);

    Annotation an;

    Vector<Field> fields = new Vector();
    if (!supFields.isEmpty()) {
      fields.addAll(supFields);
    }

    if (myFields.length > 0) {
      // Class type = null;
      String n;
      Field f = null;
      // for (Iterator it = _fields.iterator(); it.hasNext();) {
      // f = (Field) it.next();
      for (int i = 0; i < myFields.length; i++) {
        f = myFields[i];
        n = f.getName();
        if (!n.startsWith("this$")) {
          // type = f.getType();

          // if annotation is specified then only return fields with
          // the given annotation
          if (dcClass != null) {
            an = f.getAnnotation(dcClass);
            if (an == null) {
              continue;
            }
          }

          fields.add(f);
        }
      } // end 2nd for loop
    } // end if

    return (!fields.isEmpty()) ? fields : null;
  }

  /**
   * This method differs from {@link #getAttributes(Class, Class, boolean, boolean, boolean)} in 
   * that it filters the result such that no two {@link Field}s have the same name (due to inheritance). 
   * 
   * @effects returns a <code>List</code> of <code>Field</code>s of the class
   *          <code>c</code> and if <code>recursive=true</code> then recursively
   *          those of the non-Object parent and ancestor classes of
   *          <code>c</code> (if any), that have the annotation
   *          {@link #dcClass}, or <code>null</code> if no such
   *          fields exist.
   *          
   *          <p>Preference is given to <tt>Field</tt>s of <tt>c</tt> if they have the same 
   *          name as those found in an ancestor class; thus effectively resulting in no two fields having
   *          the same name in <tt>result</tt>
   *          
   */
  public static Collection<Field> getDomainAttributes(final Class c, boolean recursive) {
    // add fields that are inherited from the parent first
    // this order must be the same as the order of the constructor arguments
    // used to create objects
    // of the class

    Collection<Field> supFields = new ArrayList();
    if (recursive) {
      Class superClass = c.getSuperclass();
      if (superClass != null) {
        Collection<Field> parentFields = getDomainAttributes(superClass, true);
        if (parentFields != null) {
          supFields.addAll(parentFields);
        }
      }
    }

    Field[] myFields = c.getDeclaredFields();

    Annotation an;
    List fields = new ArrayList();
    String sn, n;
    
    // first, process inherited fields (if any)
    Stack matchingFields = new Stack(); 
    if (!supFields.isEmpty()) {
      /*v3.2: additional filtering for duplicate
       * if there is another inherited field that has the same name then replace that field
          by my field
      */
      if (myFields.length > 0) {
        // to filter
        SUP: for (Field supField : supFields) {
          sn = supField.getName();
          Field match = null;
          for (Field f : myFields) {
            n = f.getName();
            if (sn.equals(n)) {
              // same name: replace sn by n
              //continue SUP;
              match = f;
              break;
            }
          }
          
          if (match != null) {
            fields.add(match);
            matchingFields.push(match);
          } else {
            // no duplicate: add
            fields.add(supField);
          }
        } // end SUP
      } else {
        // add all inherited fields
        fields.addAll(supFields);
      }
    }

    // now process c's fields (if any)
    if (myFields.length > 0) {
      //v3.1: boolean selected = false;
      Field f = null;
      for (int i = 0; i < myFields.length; i++) {
        f = myFields[i];
        if (!matchingFields.contains(f)) {
          n = f.getName();
          if (!n.startsWith("this$")) {
            // if annotation is specified then only return fields with
            // the given annotation
            an = f.getAnnotation(dcClass);
            if (an == null) {
              continue;
            }

            fields.add(f);
          }          
        }        
      } // end 2nd for loop
    } // end if

    return (!fields.isEmpty()) ? fields : null;
  }
  
  /**
   * This method differs from {@link #getDomainAttributes(Class, boolean)} in 
   * that it works on {@link Field}s in general (not just domain attributes). 
   * 
   * @effects returns a <code>List</code> of <code>Field</code>s of the class
   *          <code>c</code> and if <code>recursive=true</code> then recursively
   *          those of the non-Object parent and ancestor classes of
   *          <code>c</code> (if any), or <code>null</code> if no such
   *          fields exist.
   *          
   *          <p>Preference is given to <tt>Field</tt>s of <tt>c</tt> if they have the same 
   *          name as those found in an ancestor class; thus effectively resulting in no two fields having
   *          the same name in <tt>result</tt>
   *          
   */
  public static Collection<Field> getFields(final Class c, boolean recursive) {
    // add fields that are inherited from the parent first
    // this order must be the same as the order of the constructor arguments
    // used to create objects
    // of the class

    Collection<Field> supFields = new ArrayList();
    if (recursive) {
      Class superClass = c.getSuperclass();
      if (superClass != null) {
        Collection<Field> parentFields = getFields(superClass, true);
        if (parentFields != null) {
          supFields.addAll(parentFields);
        }
      }
    }

    Field[] myFields = c.getDeclaredFields();

    Annotation an;
    List fields = new ArrayList();
    String sn, n;
    
    // first, process inherited fields (if any)
    Stack matchingFields = new Stack(); 
    if (!supFields.isEmpty()) {
      /*v3.2: additional filtering for duplicate
       * if there is another inherited field that has the same name then replace that field
          by my field
      */
      if (myFields.length > 0) {
        // to filter
        SUP: for (Field supField : supFields) {
          sn = supField.getName();
          Field match = null;
          for (Field f : myFields) {
            n = f.getName();
            if (sn.equals(n)) {
              // same name: replace sn by n
              //continue SUP;
              match = f;
              break;
            }
          }
          
          if (match != null) {
            fields.add(match);
            matchingFields.push(match);
          } else {
            // no duplicate: add
            fields.add(supField);
          }
        } // end SUP
      } else {
        // add all inherited fields
        fields.addAll(supFields);
      }
    }

    // now process c's fields (if any)
    if (myFields.length > 0) {
      Field f = null;
      for (int i = 0; i < myFields.length; i++) {
        f = myFields[i];
        if (!matchingFields.contains(f)) {
          n = f.getName();
          if (!n.startsWith("this$")) {
            fields.add(f);
          }          
        }        
      } // end 2nd for loop
    } // end if

    return (!fields.isEmpty()) ? fields : null;
  }

  /**
   * @effects 
   *   if exists <b>declared</b> domain operations of <tt>cls</tt> 
   *   that have the modifier {@link Modifier#PUBLIC}
   *    return them as {@link Collection}
   *   else
   *    return null
   *    
   *  <p><b>Domain operations</b> are those operations that are annotated with {@link DOpt})
   *  plus constructors and default operations.
   */
  public static Collection<Executable> getDeclaredPublicDomainOperations(Class cls) {
    Collection<Executable> result = new ArrayList<>();

    Class<DOpt> anoType = DOpt.class;
    
    // find constructors
    Constructor[] constructors = cls.getDeclaredConstructors();
    for (Constructor cons : constructors) {
      if (Modifier.isPublic(cons.getModifiers()) 
          // (optional) && cons.isAnnotationPresent(anoType)
          ) {
        result.add(cons);
      }
    }
    
    // find methods
    Method[] methods = cls.getDeclaredMethods();
    for (Method m : methods) {
      if (Modifier.isPublic(m.getModifiers())) {
        if (m.isAnnotationPresent(anoType)) { // m is annotated with DOpt
          result.add(m);
        } else if (isDefaultOpt(m)) { // m is a default opt
          result.add(m);
        }
      }
    }

    if (result.isEmpty())
      return null;
    else
      return result;
  }
  
  /**
   * @effects 
   *  if exists <b>declared</b> operations of <tt>cls</tt> (excluding inheritance) that are annotated with <tt>anoType</tt>
   *    return these operations
   *  else
   *    return null    
   */
  public static <T extends Annotation> Collection<Executable> getAnnotatedOperations(Class cls,
      Class<T> anoType) {
    Collection<Executable> result = new ArrayList<>();

    // find constructors
    Constructor[] constructors = cls.getDeclaredConstructors();
    for (Constructor cons : constructors) {
      if (cons.isAnnotationPresent(anoType)) {
        result.add(cons);
      }
    }
    
    // find methods
    Method[] methods = cls.getDeclaredMethods();
    for (Method m : methods) {
      if (m.isAnnotationPresent(anoType)) {
        result.add(m);
      }
    }

    if (result.isEmpty())
      return null;
    else
      return result;    
  }
  

  /**
   * @effects 
   *  if <tt>cls</tt> has at least one <b>declared</b> operation annotated with <tt>anoType</tt>
   *    return true
   *  else
   *    return false 
   */
  public static <T extends Annotation> boolean hasAnnotatedOpts(Class cls, Class<T> anoType) {
    // find constructors
    Constructor[] constructors = cls.getDeclaredConstructors();
    for (Constructor cons : constructors) {
      if (cons.isAnnotationPresent(anoType)) {
        return true;
      }
    }
    
    // find methods
    Method[] methods = cls.getDeclaredMethods();
    for (Method m : methods) {
      if (m.isAnnotationPresent(anoType)) {
        return true;
      }
    }
    
    return false;  
  }
  
  /**
   * @effects 
   *  if <tt>cls</tt> (or an ancestor class of <tt>cls</tt> via inheritance) has at least one field annotated with <tt>anoType</tt>
   *    return true
   *  else
   *    return false 
   */
  public static <T extends Annotation> boolean hasAnnotatedFields(Class cls, Class<T> anoType) {
    
    Field[] fields = cls.getDeclaredFields();
    for (Field f : fields) {
      if (f.isAnnotationPresent(anoType)) {
        return true;
      }
    }
    
    Class superType = getDomainSuperType(cls);
    if (superType != null) {
      return hasAnnotatedFields(superType, anoType);
    } else {
      return false;
    }
  }
  
  /**
   * This is a convenient short-cut for {@link #getAnnotatedMethodFor(Class, DOpt.Type, String)}
   * with <tt>(cls, optType, null)</tt>
   */
  public static Method getAnnotatedMethodFor(Class cls,
      DOpt.Type optType) {
    return getAnnotatedMethodFor(cls, optType, null);
  }
  
  /**
   * This is a more general method than {@link #getMutatorFor(Class, String)} and {@link #getObserverFor(Class, String)} 
   * in that it only considers the combination of {@link DOpt} and {@link AttrRef} and does not 
   * concern with the method name. 
   * 
   * @effects 
   *  if exist {@link Method}(s) of <tt>cls</tt> that is annotated with <tt>{@link DOpt#type()}=optType</tt>
   *  and, if <tt>attribName != null</tt>, then with <tt>{@link AttrRef#value()}=attribName</tt>
   *    return the first of such method method
   *  else
   *    return null
   */
  public static Method getAnnotatedMethodFor(Class cls,
      DOpt.Type optType, String attribName) {
    
    Method[] methods = cls.getMethods();
    
    Method fm = null;
    boolean checkAttrRef = attribName != null;
    
    for (Method m : methods) {
      // check combination of DOpt and AttrRef
      DOpt opt = m.getAnnotation(DOpt.class);
      AttrRef aref = m.getAnnotation(AttrRef.class);
      /*
      if (opt != null && aref != null && 
          opt.type().equals(optType) && aref.name().equals(attribName)) {*/
      if (opt != null && opt.type().equals(optType)) {
        if (!checkAttrRef || (aref != null && aref.value().equals(attribName))) {
          // found matching method by the combination
          fm = m; break;
        }
      }
    }

    return fm;    
  }
  
  /**
   * @effects 
   *  return the camel case getter method name for <code>fieldName</code>
   * @version 5.4 
   */
  public static String getGetterNameFor(String fieldName) {
    fieldName = (fieldName.charAt(0)+"").toUpperCase() + fieldName.substring(1);
    return "get" + fieldName;
  }
  
  
  /**
   * @effects 
   *  return the camel case setter method name for <code>fieldName</code>
   * @version 5.4 
   */
  public static String getSetterNameFor(String fieldName) {
    fieldName = (fieldName.charAt(0)+"").toUpperCase() + fieldName.substring(1);
    return "set" + fieldName;
  }
  
  /**
   * @effects 
   *  Find and return <b>declared</b> {@link Method} of <tt>cls</tt> whose name is <tt>name</tt> or 
   *  return null if no such method exists.
   */
  public static Method getMethod(Class cls, String name) {
    Method[] methods = cls.getDeclaredMethods();

    if (methods.length == 0) 
      return null;
    
    for (Method m : methods) {
      if (m.getName().equals(name))
        return m;
    }

    return null;
  }
  
  /**
   * @effects 
   *  if cls contains mutable attributes 
   *    return all {@link Method}s of cls that are mutators for these attributes
   *  else
   *    return null
   */
  public static Collection<Executable> getMutators(Class cls) {
    Collection<Field> fields = getMyDomainAttributes(cls);
    
    Collection<Executable> methods = new ArrayList<>();
    if (fields != null) {
      // domainCls has domain attributes
      for (Field field : fields) {
        DAttr dc = field.getAnnotation(dcClass);
        if (dc.mutable()) {
          // dc is a mutable attribute
          Executable m = null;
          m = DClassTk.getMutatorFor(cls, field.getName());
          
          if (m != null) {
            methods.add(m);
          }
        }
      }
    }
    
    if (methods.isEmpty())
      return null;
    else
      return methods;
  }
  
  /**
   * @effects 
   *  return the mutator method in <tt>cls</tt> 
   *  for the attribute whose name is <tt>attribName</tt>,
   *  or return null if no such method is found
   *  
   *  <p>The matching method either has the setter-name for the attribute or
   *  has <tt>{@link DOpt}.type = {@link DOpt.Type#Mutator} /\ {@link AttrRef}.name = attribName</tt> 
   */
  public static Method getMutatorFor(Class cls, String attribName) {
    //return findSetterMethod(cls, attribName);
    
    String mname = (attribName.charAt(0) + "").toUpperCase()
        + attribName.substring(1);
    mname = "set" + mname;
    
    Method[] methods = cls.getMethods();
    
    Method fm = null;
    for (Method m : methods) {
      if (m.getName().equals(mname)) {
        // found matching method by name
        fm = m; break;
      } else {
        // check combination of DOpt and AttrRef
        DOpt opt = m.getAnnotation(DOpt.class);
        AttrRef aref = m.getAnnotation(AttrRef.class);
        if (opt != null && aref != null && 
            opt.type().equals(DOpt.Type.Setter) && aref.value().equals(attribName)) {
          // found matching method by the combination
          fm = m; break;
        }
      }
    }

    return fm;
  }
  
  /**
   * @effects returns <code>Method</code> object of the class <code>cls</code>,
   *          whose name is <code>"set" + attribName</code> (with first letter
   *          capitalised), 
   *          or return null if no such method is found
   */
  public static Method findSetterMethod(Class cls, String fieldName) {
    
    fieldName = (fieldName.charAt(0) + "").toUpperCase()
        + fieldName.substring(1);
    String mname = "set" + fieldName;

    return findMethodByName(cls, mname);
  }
  
  /**
   * @effects 
   *  return {@link Method} of <tt>cls</tt> whose name is <tt>mname</tt>,
   *  or return null if no such method is found
   */
  private static Method findMethodByName(Class cls, String mname) {
    Method[] methods = cls.getMethods();
    
    for (Method m : methods) {
      if (m.getName().equals(mname)) {
        // found
        return m;
      }
    }
    
    // not found
    return null;
  }

  /**
   * @effects returns <code>Method</code> object of the class <code>c</code>,
   *          whose name is <code>"get" + fieldName</code> (with first letter
   *          capitalised), or return null if no such method is found
   * 
   */
  public static Method findGetterMethod(Class c, String fieldName) {
    
    fieldName = (fieldName.charAt(0) + "").toUpperCase()
        + fieldName.substring(1);
    String mname = "get" + fieldName;
    return findMethodByName(c, mname);
  }

  /**
   * @effects 
   *  if exists {@link Method} <tt>cls.m</tt> such that either 
   *  <tt>m.name.equals("get"+fieldName)</tt> or 
   *  <pre>m@DOpt.Type = {@link DOpt.Type#Observer} /\ 
   *    m@AttrRef.value.equals(fieldName)</tt>
   *    return m
   *  else
   *    return null
   */
  public static Method findGetterMethodFor(Class cls,
      String fieldName) {
    fieldName = (fieldName.charAt(0) + "").toUpperCase()
        + fieldName.substring(1);
    final String mname = "get" + fieldName;
    
    Method[] methods = cls.getMethods();
    final DOpt.Type optType = DOpt.Type.Getter;
    
    for (Method m : methods) {
      if (m.getName().equals(mname)) {  // match by name
        // found
        return m;
      } else {  // try matching DOpt and AttrRef if both are specified
        DOpt opt = m.getAnnotation(DOpt.class);
        AttrRef aref = m.getAnnotation(AttrRef.class);
        if (opt != null && opt.type().equals(optType)) {
          if (aref != null && aref.value().equals(fieldName)) {
            // found matching method by the combination
            return m;
          }
        }
      }
    }
    
    // not found
    return null;
  }
  
//  /**
//   * @effects return <tt>Method</tt> object of the class <tt>c</tt>,
//   *          whose name is <tt>methodName</tt>; 
//   *          
//   *          <p>Throws <tt>NotFoundException</tt> if no
//   *          such method exists; NotPossibleException if could not access the method
//   * 
//   */
//  public static Method findMethod(String methodName, Class c, Class returnType) 
//      throws NotPossibleException, NotFoundException {
//    Method getter = c.getMethod(methodName, null);
//    
//    if (returnType != null) {
//      Class rt = getter.getReturnType();
//      if (rt == null || 
//          !returnType.isAssignableFrom(rt)) {
//        throw new NotFoundException(NotFoundException.Code.METHOD_WITH_RETURN_TYPE_NOT_FOUND, 
//            "Không tìm thấy phương thức {0}.{1}(): {2}", c, methodName, returnType);
//      }
//    }
//    return getter;
//  }
  
  /**
   * @effects returns <code>Method</code> object of the class <code>c</code>,
   *          whose name is <code>prefix + f.getName(f.getType)</code> (with the first letter
   *          capitalised).
   *          
   *          <p>Throws SecurityException if could not access the method, 
   *          NoSuchMethodException if the method is not found.
   * 
   */
  private static Method findMethod(Class cls, Field f, String prefix) throws SecurityException,
      NoSuchMethodException {
    String fname = f.getName();
    Class type = f.getType();
    fname = (fname.charAt(0) + "").toUpperCase() + fname.substring(1);
    String mname = prefix + fname;

    // first try the method with the same type
//    try {
    Method m = cls.getMethod(mname, type);

    if (m == null) { // otherwise, find methods having the same name
      Method[] methods = cls.getMethods();

      for (int i = 0; i < methods.length; i++) {
        m = methods[i];
        if (m.getName().equals(mname)) {
          break;
        }
      }
    }
    return m;
//    } catch (NoSuchMethodException e ) {
//      throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND, 
//          e, new Object[] {cls, mname});
//    }
  }


  /**
   * @effects 
   *  if exists in <tt>cls</tt> {@link Constructor}s whose {@link DOpt.Type} is one of the specified types
   *    return them
   *  else
   *    return null
   */
  public static Collection<Constructor> getAnnotatedConstructorsFor(
      Class cls, DOpt.Type...types) {
    Constructor[] constructors = cls.getDeclaredConstructors();
    
    if (constructors.length > 0) {
      Collection<Constructor> consCol = new ArrayList<>(constructors.length);
      for (Constructor cons : constructors) {
        DOpt[] optAnos = cons.getAnnotationsByType(DOpt.class);
        if (optAnos != null && optAnos.length > 0) {
          // cons is DOpt-annotated
          OUTER: for (DOpt oa : optAnos) {
            for (DOpt.Type otype : types) {
              if (oa.type().equals(otype)) {
                // match
                consCol.add(cons);
                break OUTER;
              }
            }
          }
        }
      }
      
      if (consCol.isEmpty())
        return null;
      else
        return consCol;
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  return all the declared {@link Constructor} operations of cls or 
   *  throws NoSuchMethodException no such constructors are found. 
   */
  public static Collection<Executable> getConstructors(Class cls) throws NoSuchMethodException {
    Constructor[] constructors = cls.getDeclaredConstructors();
    
    if (constructors.length > 0) {
      return Arrays.asList(constructors);
    } else {
      throw new NoSuchMethodException(DClassTk.class.getSimpleName()+".getConstructors: no declared constructors in " + cls.getSimpleName());
    }
  }

  /**
   * @effects 
   *  return all the declared {@link Constructor} operations of <tt>cls</tt> whose modifier contains <tt>modifier</tt>
   *  or return null if no such constructors are found. 
   */
  public static Collection<Executable> getConstructors(Class cls, int modifier) {
    Constructor[] constructors = cls.getDeclaredConstructors();
    
    if (constructors.length == 0) {
      // no constructors
      return null;
    } else {
      Collection<Executable> result = new ArrayList<>();
      for (Executable m : constructors) {
        int mod = m.getModifiers();
        if ((Modifier.isPublic(modifier) && Modifier.isPublic(mod)) ||
            (Modifier.isPrivate(modifier) && Modifier.isPrivate(mod)) || 
            (Modifier.isProtected(modifier) && Modifier.isProtected(mod))
            ){
          // match
          result.add(m);
        }
      }
      
      if (result.isEmpty())
        return null;
      else
        return result;
    }
  }
  
  /**
   * @effects returns the <tt>Constructor</tt> that is used to create objects
   *          for testing.
   */
  public static Constructor getConstructor(Class cls, Class[] paramTypes) {
    // the constructor used to create objects (already validated
    // to be defined)
    Constructor cons = null;

    try {
      cons = cls.getDeclaredConstructor(paramTypes);
      return cons;
    } catch (Exception e) {
      // should not happen
      assert false : "Test program internal error: could not find constructor: "
          + e;
    }

    return null;
  }

  /**
   * This is a special version of {@link #getRequiredConstructor(Class, Map)} where 
   * the second argument is all the domain attributes in <tt>domainCls</tt> (including 
   * the inherited attributes).
   */
  public static Constructor getRequiredConstructor(Class domainCls) {
    Map<String,DAttr> allSDcMap = getDAttrs(domainCls, true);

    return getRequiredConstructor(domainCls, allSDcMap);
  }
  
  /**
   * @requires 
   *  <tt>dcMap</tt> contains the domain constraints of the 
   *  domain attributes of <tt>cls</tt>
   *  (i.e. <tt>dcMap = {@link #getDAttrs(Class, boolean)})</tt> /\
   *  at least one of these attributes is a required attribute (i.e. its {@link DAttr}<tt>.optional = false</tt>)
   *  
   * @effects 
   *  Find in <tt>cls</tt> the {@link Constructor} whose parameters are defined based on 
   *  the required domain attributes in <tt>dcMap</tt>.
   *  If found then return the {@link Constructor} else return <tt>null</tt>.
   *  
   *  <p>The required constructor will have enough parameters to  
   *  reference the corresponding <i>noncollection-based</i> required attributes 
   */
  public static Constructor getRequiredConstructor(Class cls,
      Map<String, DAttr> dcMap) {
    // extract from dcMap the required attributes
    Set<String> requiredAttribs = new HashSet<>();
    
    boolean hasRequiredColTypeAttrib = false;
    for(Entry<String,DAttr> e : dcMap.entrySet()) {
      String attribName = e.getKey();
      DAttr dc = e.getValue();
      
      if (!dc.optional()) {
        if (//isCollectionType(cls, attribName)
            isArrayOrCollectionType(cls, attribName)
            ) {
          hasRequiredColTypeAttrib = true;
        } else {          
          // a required attribute that is not collection-typed
          requiredAttribs.add(attribName);
        }
      }
    }
    
    if (requiredAttribs.isEmpty() && !hasRequiredColTypeAttrib) {
      // no required attributes -> no required constructor
      return null;
    }
        
    // find in cls the constructor whose parameters reference these attributes
    Constructor[] constructors = cls.getConstructors(); 
    
    if (constructors.length == 0) // no constructors
      return null;
    
    int numAttribs = requiredAttribs.size();
    Constructor requiredCons = null;
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
          } else if (requiredAttribs.contains(attrRef.value())) {
            // param references a required attrib -> possibly this one
          } else {
            // param does not references a required attrib -> not this constructor
            match = false;
            break;
          }
        } // end params
        
        if (match) {
          // cons is a match
          requiredCons = cons;
          break;
        }
      }
    }
    
    // return result
    return requiredCons;
  }

  /**
   * @effects 
   *  if exists a {@link Constructor} of <tt>cls</tt> whose parameters exactly refer 
   *  to <tt>refAttribs</tt> (not necessarily in the same order).
   *    return it
   *  else
   *    return null 
   */
  public static <T> Constructor<T> getConstructor(Class<T> cls,
      //String[] refAttribs
      Collection<String> refAttribs
      ) {
    // find in cls the constructor whose parameters reference these attributes
    Constructor[] constructors = cls.getConstructors(); 
    
    if (constructors.length == 0) // no constructors
      return null;
    
    int numAttribs = refAttribs.size(); //.length;
    Constructor<T> requiredCons = null;
    for (Constructor<T> cons : constructors) {
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
            boolean refAnAttrib = false;
            for (String refAttrib : refAttribs) {
              if (refAttrib.equals(attrRef.value())) {
                // param references a required attrib -> possibly this one
                refAnAttrib = true; break;
              }                
            }
            
            if (refAnAttrib) {
              // param references a required attrib -> possibly this one
            } else {
              // param does not references a required attrib -> not this constructor
              match = false;
              break;
            }
          }
        } // end params
        
        if (match) {
          // cons is a match
          requiredCons = cons;
          break;
        }
      }
    }
    
    // return result
    return requiredCons;
  }
  
  /**
   * @effects 
   *  if the default constructor is defined in <tt>cls</tt>
   *    return it
   *  else
   *    return null
   */
  public static <T> Constructor<T> getDefaultConstructor(Class<T> cls) {
    try {
      Constructor<T> cons = cls.getDeclaredConstructor();
      return cons;
    } catch (NoSuchMethodException | SecurityException e) {
      return null;
    }
  }

  /**
   * This method's check is stronger than the check performed by {@link #isCollectionType(String)}. 
   * 
   * @effects 
   *  if the declared type of the attribute of <tt>cls</tt> whose name is <tt>attribName</tt> is a  
   *  Java's built-in {@link Collection}-typed
   *    return true
   *  else
   *    return false
   */
  public static boolean isCollectionType(Class cls, String attribName) {
    Collection<Field> fields = getFields(cls, true);
    
    if (fields != null) {
      for (Field attrib : fields) {
        if (attrib.getName().equals(attribName) && 
            Collection.class.isAssignableFrom(attrib.getType())) {
          return true;
        }
      }
      
      return false;
    } else {
      return false;
    }
  }

  /**
   * More flexible than {@link #isCollectionType(Class, String)} to support array-typed.
   * 
   * @effects 
   *  if the declared type of the attribute of <tt>cls</tt> whose name is <tt>attribName</tt> is 
   *  an array-typed or a Java's built-in {@link Collection}-typed
   *    return true
   *  else
   *    return false
   */
  public static boolean isArrayOrCollectionType(Class cls, String attribName) {
    Collection<Field> fields = getFields(cls, true);
    
    if (fields != null) {
      for (Field attrib : fields) {
        if (attrib.getName().equals(attribName)) {
          Class atype = attrib.getType();
          if (atype.isArray() ||  
              Collection.class.isAssignableFrom(atype)) {
            return true;
          }
        }
      }
      
      return false;
    } else {
      return false;
    }
  }
  
  /**
   * @effects 
   *  return the number of declared {@link Field}s of <tt>c</tt> or 
   *  return 0 if <tt>c</tt> has not such fields.
   * 
   */
  public static int countDeclaredFields(Class c) {
    return c.getDeclaredFields().length;
  }

  /**
   * @effects 
   *  return the number of declared {@link Method}s of <tt>c</tt> or 
   *  return 0 if <tt>c</tt> has not such methods.
   */
  public static int countDeclaredMethods(Class c) {
    return c.getDeclaredMethods().length;
  }
  
  /**
   * @requires 
   *  modifier is one of the followings: {@link Modifier#PUBLIC}, {@link Modifier#PRIVATE}, {@link Modifier#PROTECTED}.
   * @effects 
   *  return the number of all kinds of declared {@link Method}s of ({@link Method}s declared within)
   *  class <tt>c</tt> whose modifiers contain <tt>modifier</tt>, or return 0 if no such methods are found. 
   *  
   *  <p>This method excludes constructors.
   */
  public static int countMethods(Class c, int modifier) {
    Method[] methods = c.getDeclaredMethods();
    
    int count = 0;
    
    if (methods.length > 0) {
      for (Method m : methods) {
        int mod = m.getModifiers();
        if ((Modifier.isPublic(modifier) && Modifier.isPublic(mod)) ||
            (Modifier.isPrivate(modifier) && Modifier.isPrivate(mod)) || 
            (Modifier.isProtected(modifier) && Modifier.isProtected(mod))
            ){
          // match
          count++;
        }
      }
    }      
    
    return count;
  }
  
  /**
   * @requires 
   *  modifier is one of the followings: {@link Modifier#PUBLIC}, {@link Modifier#PRIVATE}, {@link Modifier#PROTECTED}.
   *   
   * @effects 
   *  return all <b>declared</b> {@link Method}s of <tt>cls</tt> with modifier matches <tt>modifier</tt> 
   *  or <tt>null</tt> if no such methods are defined.
   */
  public static Collection<Method> getDeclaredMethods(Class cls, int modifier) {
    Method[] methods = cls.getDeclaredMethods();
    
    if (methods.length == 0) {
      // no methods
      return null;
    } else {
      Collection<Method> result = new ArrayList<>();
      for (Method m : methods) {
        int mod = m.getModifiers();
        if ((Modifier.isPublic(modifier) && Modifier.isPublic(mod)) ||
            (Modifier.isPrivate(modifier) && Modifier.isPrivate(mod)) || 
            (Modifier.isProtected(modifier) && Modifier.isProtected(mod))
            ){
          // match
          result.add(m);
        }
      }
      
      if (result.isEmpty())
        return null;
      else
        return result;
    }
  }
  
  /**
   * This method is more generic than {@link #getDeclaredMethods(Class, int)} in that it also 
   * includes constructors in the result.
   * 
   * @requires 
   *  modifier is one of the followings: {@link Modifier#PUBLIC}, {@link Modifier#PRIVATE}, {@link Modifier#PROTECTED}.
   *   
   * @effects 
   *  return all declared {@link Method}s and {@link Constructor}s of <tt>cls</tt> 
   *  with modifier matches <tt>modifier</tt> 
   *  or <tt>null</tt> if no such operations are defined.
   *  
   *  <p>Result excludes operations that are annotated with <tt>DOpt#optional=true</tt>.
   */
  public static Collection<Executable> getDeclaredOperations(Class cls, int modifier) {
    Collection<Executable> result = new ArrayList<>();

    // find constructors
    Collection<Executable> constructors = getConstructors(cls, modifier);
    if (constructors != null) result.addAll(constructors);
    
    // find methods
    Collection<Method> methods = getDeclaredMethods(cls, modifier);
    if (methods != null) result.addAll(methods);

    if (result.isEmpty())
      return null;
    else
      return result;
  }

  /**
   * This method is more generic than {@link #getDeclaredMethods(Class, int)} in that it also 
   * includes constructors in the result.
   * 
   * @requires 
   *  modifier is one of the followings: {@link Modifier#PUBLIC}, {@link Modifier#PRIVATE}, {@link Modifier#PROTECTED}.
   *   
   * @effects 
   *  return all declared {@link Method}s and {@link Constructor}s of <tt>cls</tt> 
   *  with modifier matches <tt>modifier</tt> 
   *  or <tt>null</tt> if no such operations are defined.
   *  
   *  <p>Result excludes operations that are annotated with <tt>DOpt#optional=true</tt>.
   */
  public static Collection<Executable> getDeclaredOperationsWithFilter(Class cls, int modifier) {
    Collection<Executable> result = new ArrayList<>();

    // find constructors
    Collection<Executable> constructors = getConstructors(cls, modifier);
    if (constructors != null) result.addAll(constructors);
    
    // find methods
    Collection<Method> methods = getDeclaredMethods(cls, modifier);
    if (methods != null) {
      for (Method m : methods) {
        DOpt dopt = m.getAnnotation(DOpt.class);
        if (dopt != null //&& dopt.optional() == true
            ) {
          // ignore
          continue;
        }
        
        result.add(m);
      }
    }

    if (result.isEmpty())
      return null;
    else
      return result;
  }
  
  /**
   * This method is another version of {@link #getDeclaredOperations(Class, String[])} which return 
   * all operations.
   * 
   * @effects 
   *  return all declared {@link Method}s or {@link Constructor}s of <tt>cls</tt>
   *  or <tt>null</tt> if no operations are found (i.e. <tt>cls</tt> is empty).
   */
  public static Collection<Executable> getDeclaredOperations(Class cls) {
    Collection<Executable> result = new ArrayList<>();

    // find constructors
    Constructor[] constructors = cls.getDeclaredConstructors();

    Collections.addAll(result, constructors);
    
    // find methods
    Method[] methods = cls.getDeclaredMethods();

    Collections.addAll(result, methods);

    if (result.isEmpty())
      return null;
    else
      return result;
  }
  
  /**
   * This method is another version of {@link #getDeclaredOperations(Class, int)} which use 
   * operation names instead of their modifiers.
   * 
   * @requires 
   *  <tt>optNames</tt> neq null
   *   
   * @effects 
   *  return all declared {@link Method}s or {@link Constructor}s of <tt>cls</tt> 
   *  whose names are in <tt>optNames</tt> 
   *  or <tt>null</tt> if no such operations are found.
   */
  public static Collection<Executable> getDeclaredOperations(Class cls,
      String[] optNames) {
    if (optNames == null || optNames.length == 0) return null;
    
    Collection<Executable> result = new ArrayList<>();

    // find constructors
    Constructor[] constructors = cls.getDeclaredConstructors();

    for (Constructor cons : constructors) {
      for (String optName : optNames) {
        if (optName.equals(getOperationSimpleName(cons))) {
          result.add(cons);
          break;
        }
      }
    }
    
    // find methods
    Method[] methods = cls.getDeclaredMethods();

    for (Method m : methods) {
      for (String optName : optNames) {
        if (optName.equals(getOperationSimpleName(m))) {
          result.add(m);
          break;
        }
      }
    }

    if (result.isEmpty())
      return null;
    else
      return result;
  }
  
  /**
   * This method is another version of {@link #getDeclaredOperations(Class, String[])} which use 
   * a pre-defined set of operations to match (instead of names).
   * 
   * @requires 
   *  <tt>matchingOpts</tt> neq null
   *   
   * @effects 
   *  return declared {@link Method}s or {@link Constructor}s of <tt>cls</tt>
   *  whose names match those in <tt>matchingOpts</tt> 
   *  or <tt>null</tt> if no such operations are found.
   */
  public static Collection<Executable> getDeclaredOperations(Class cls,
      Collection<Executable> matchingOpts) {
    if (matchingOpts == null || matchingOpts.isEmpty()) return null;
    
    //TODO (?) match also the parameter list of the operations
    Collection<Executable> result = new ArrayList<>();

    // find constructors
    Constructor[] constructors = cls.getDeclaredConstructors();

    for (Constructor cons : constructors) {
      for (Executable opt : matchingOpts) {
        if (getOperationSimpleName(opt).equals(getOperationSimpleName(cons))) {
          result.add(cons);
          break;
        }
      }
    }
    
    // find methods
    Method[] methods = cls.getDeclaredMethods();
  
    for (Method m : methods) {
      for (Executable opt : matchingOpts) {
        if (getOperationSimpleName(opt).equals(getOperationSimpleName(m))) {
          result.add(m);
          break;
        }
      }
    }

    if (result.isEmpty())
      return null;
    else
      return result;
  }
  
  /**
   * This method is another version of {@link #getDeclaredOperations(Class, String[])} which use 
   * a pre-defined set of operations to match (instead of names).
   * 
   * @requires 
   *  <tt>matchingOpts</tt> neq null
   *   
   * @effects 
   *  return all {@link Method}s or {@link Constructor}s of <tt>cls</tt> (or of an ancestor of <tt>cls</tt>) 
   *  whose names match those in <tt>matchingOpts</tt> 
   *  or <tt>null</tt> if no such operations are found.
   */
  public static Collection<Executable> getOperations(Class cls,
      Collection<Executable> matchingOpts) {
    if (matchingOpts == null || matchingOpts.isEmpty()) return null;
    
    //TODO (?) match also the parameter list of the operations
    Collection<Executable> result = new ArrayList<>();

    // find constructors
    Constructor[] constructors = cls.getDeclaredConstructors();

    for (Constructor cons : constructors) {
      for (Executable opt : matchingOpts) {
        if (getOperationSimpleName(opt).equals(getOperationSimpleName(cons))) {
          result.add(cons);
          break;
        }
      }
    }
    
    // find methods
    for (Executable mopt : matchingOpts) {
      Executable opt = getMethodRecursively(cls, mopt);
      if (opt != null) {
        result.add(opt);
      }
    }

    if (result.isEmpty())
      return null;
    else
      return result;
  }
  
  /**
   * @effects 
   *  if exists in <tt>cls</tt> (or an ancestor class of <tt>cls</tt>) 
   *  an {@link Executable} whose name matches <tt>matchingOpt.name</tt>
   *    return it
   *  else
   *    return null 
   */
  public static Executable getOperation(Class cls, Executable matchingOpt) {
    if (matchingOpt == null) return null;
    
    //TODO (?) match also the parameter list of the operations
    // find constructors
    Constructor[] constructors = cls.getDeclaredConstructors();

    for (Constructor cons : constructors) {
      if (getOperationSimpleName(matchingOpt).equals(getOperationSimpleName(cons))) {
//        if (matchParamTypes(matchingOpt, cons)) {
//          return cons;
//        }
        return cons;
      }
    }
    
    // find methods
    return getMethodRecursively(cls, matchingOpt);
  }
  
  /**
   * @effects 
   *  if <tt>cls</tt> or one of its ancestor classes (in order) contains an {@link Executable} 
   *  whose name matches <tT>matchingOpt.name</tt>
   *    return that method
   *  else
   *    return null  
   */
  private static Method getMethodRecursively(Class cls, Executable matchingOpt) {
    Method[] methods = cls.getDeclaredMethods();

    for (Method m : methods) {
      if (getOperationSimpleName(matchingOpt).equals(getOperationSimpleName(m))) {
        return m;
      }
    }
    
    // try super-type
    Class superType = getDomainSuperType(cls);
    if (superType != null) {
      return getMethodRecursively(superType, matchingOpt);
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if opt1.paramTypes and opt2.paramTypes are equal
   *    return true
   *  else
   *    return false
   */
  private static boolean matchParamTypes(Executable opt1,
      Executable opt2) {
    Class[] paramT1s = opt1.getParameterTypes(), paramT2s = opt2.getParameterTypes();
    
    if (paramT1s.length == paramT2s.length) {
      for (int i = 0; i < paramT1s.length; i++) {
        if (paramT1s[i] != paramT2s[i])
          return false;
      }
      
      return true;
    } else {
      return true;
    }
  }

  /**
   * @effects 
   *  if cls contains observer operations for its attributes 
   *    return all such operations
   *  else
   *    return null
   */
  public static Collection<Executable> getObservers(Class cls) {
    Collection<Field> fields = getMyDomainAttributes(cls);
    
    Collection<Executable> methods = new ArrayList<>();
    if (fields != null) {
      // domainCls has domain attributes
      for (Field field : fields) {
        Executable m = null;
        m = getObserverFor(cls, field.getName());
        
        if (m != null) {
          methods.add(m);
        }
      }
    }
    
    if (methods.isEmpty())
      return null;
    else
      return methods;
  }

  /**
   * @effects 
   *  return the observer method in <tt>cls</tt> for the attribute 
   *  whose name is <tt>attribName</tt>,
   *  or return null if no such method is found
   *  
   *  <p>The matching method either has the getter-name for the attribute or
   *  has <tt>{@link DOpt}.type = {@link DOpt.Type#Observer} /\ {@link AttrRef}.name = attribName</tt> 
   *   
   */
  public static Method getObserverFor(Class cls, String attribName) {
    
    String mname = (attribName.charAt(0) + "").toUpperCase()
        + attribName.substring(1);
    mname = "get" + mname;
    
    Method[] methods = cls.getMethods();
    
    Method fm = null;
    for (Method m : methods) {
      if (m.getName().equals(mname)) {
        // found matching method by name
        fm = m; break;
      } else {
        // check combination of DOpt and AttrRef
        DOpt opt = m.getAnnotation(DOpt.class);
        AttrRef aref = m.getAnnotation(AttrRef.class);
        if (opt != null && aref != null && 
            opt.type().equals(DOpt.Type.Getter) && aref.value().equals(attribName)) {
          // found matching method by the combination
          fm = m; break;
        }
      }
    }

    return fm;
  }

  /**
   * @effects 
   * if cls contains default operations 
   *    return all such operations
   *  else
   *    return null
   */
  public static Collection<Executable> getDefaults(Class cls) {
    // TODO: (?) support more types of default operations 
    // for now support toString
    Collection<Executable> methods = new ArrayList<>();
    try {
      Method toString = cls.getMethod("toString");
      
      methods.add(toString);
    } catch (NoSuchMethodException | SecurityException e) {
      // no such method
    }
    
    if (!methods.isEmpty()) {
      return methods;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   * if cls contains helper operations 
   *    return all such operations
   *  else
   *    return null
   */
  public static Collection<Executable> getHelpers(Class cls) {
    // TODO: (?) support more types of default operations 
    // for now support repOK
    Collection<Executable> methods = new ArrayList<>();
    try {
      Method repOk = cls.getMethod("repOK");
      
      methods.add(repOk);
    } catch (NoSuchMethodException | SecurityException e) {
      // no such method
    }
    
    if (!methods.isEmpty()) {
      return methods;
    } else {
      return null;
    }
  }

  /**
   * This finds a specific repOk method that is included in {@link #getHelpers(Class)}. 
   * 
   * @effects 
   *  if <tt>cls</tt> contains repOk method
   *    return it
   *  else
   *    return <tt>null</tt>
   */
  public static Method getRepOkMethod(Class cls) {
    Method repOk = getMethod(cls, "repOk", "repOK"); // cls.getMethod("repOK");
    
    return repOk;
  }
  
  /**
   * @effects 
   *  if exists a {@link Method} of <tt>cls</tt> whose name is one of the specified <tt>names</tt>
   *    return the first of such method
   *  else
   *    return <tt>null</tt>
   */
  public static Method getMethod(Class cls, String...names) {
    if (names == null) return null;
    
    for (String name : names) {
      try {
        Method m = cls.getMethod(name);
        // found one
        return m;
      } catch (NoSuchMethodException | SecurityException e) {
        // no method with that name: ignore
      }
    }
    
    // not found
    return null;
  }

  /**
   * @effects 
   *  return the number of properties of {@link DAttr}.
   */
  public static int getDAttrSize() {
    return dcClass.getDeclaredMethods().length;
  }

  /**
   * @effects <pre> 
   *  if m is Method
   *    return m.getName()
   *  else  // m is a constructor
   *    return the simple name of m
   *  </pre>
   */
  public static String getOperationSimpleName(Executable m) {
    String name = m.getName();
    if (m instanceof Method)
      return ((Method)m).getName();
    else if (m instanceof Constructor) {
      int dotidx = name.lastIndexOf(".");
      if (dotidx > -1) {  // has dots
        return name.substring(dotidx+1);
      } else {  // no dots
        return name;
      }
    } else {
      return name;
    }
  }

  /**
   * @effects 
   *  if m is a {@link Constructor}
   *    return null
   *  else
   *    if m has a non-void return type 
   *      return it
   *    else
   *      return null
   */
  public static Class getOperationReturnType(Executable m) {
    if (m == null) {
      return null;
    } else {
      if (m instanceof Constructor) {
        return null;
      } else {
        Class rtype = ((Method)m).getReturnType();
        return rtype;
      }
    }
  }

  /**
   * @effects 
   *  if <tt>cls</tt> is explicitly designed to be a collection-typed class, 
   *  i.e. it implements interface {@link utils.Collection}
   *    return true
   *  else
   *    return false
   */
  public static boolean isCollectionDomainClass(Class cls) {
    //return cls.isAnnotationPresent(CollectionType.class);
    return Collection.class.isAssignableFrom(cls);
  }
  
  /**
   * @effects 
   *  return the generic type of the specified type, 
   *  e.g. if <tt>type = Collection&lt;Customer&gt;</tt> then 
   *  result = <tt>Customer</tt>; 
   *  or return <tt>null</tt> if the collection type 
   *  uses a type variable (e.g. Collection&lt;T&gt;>)
   */
  private static Class getGenericCollectionType(java.lang.reflect.Type type) {
    if (type instanceof ParameterizedType) {
      ParameterizedType colType = (ParameterizedType) type;
        
      java.lang.reflect.Type[] typeVars = colType.getActualTypeArguments();
      
      java.lang.reflect.Type t = typeVars[0];
      
      if (t instanceof Class)
        return (Class) t;
      else
        // t is not a Class but a type variable (e.g. as in Collection<T>)
        return null;
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  return the generic type of the specified attribute, 
   *  e.g. if <tt>attribute = Collection&lt;Customer&gt;</tt> then 
   *  result = <tt>Customer</tt>; 
   *  or return <tt>null</tt> if the collection type 
   *  uses a type variable (e.g. Collection&lt;T&gt;>)
   */
  public static Class getGenericCollectionType(Field attribute) {
    /* v3.0: support the case where no parameterised type is specified
    ParameterizedType colType = (ParameterizedType) attribute.getGenericType();
    */
    java.lang.reflect.Type type = attribute.getGenericType();
    if (type instanceof ParameterizedType) {
      ParameterizedType colType = (ParameterizedType) type;
        
      java.lang.reflect.Type[] typeVars = colType.getActualTypeArguments();
      
      java.lang.reflect.Type t = typeVars[0];
      
      if (t instanceof Class)
        return (Class) t;
      else
        // t is not a Class but a type variable (e.g. as in Collection<T>)
        return null;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if attrib.type is a generic type
   *    return type
   *  else 
   *    return false
   */
  public static boolean isGenericType(Field attrib) {
    java.lang.reflect.Type type = attrib.getGenericType();
    if (type instanceof ParameterizedType) {
      return true;
    } else {
      return false;
    }
  }
  
//  /**
//   * @effects 
//   *  if exists a domain attribute of <tt>cls</tt> that is annotated with {@link Sorted}
//   *    returns the value of its {@link SortOrder} property
//   *  else
//   *    return <tt>null</tt> 
//   */
//  public static SortOrder getIfCollectionSortOrder(Class cls) {
//    
//    Collection<Field> fields = getMyDomainAttributes(cls);
//    
//    if (fields != null) {
//      for (Field attrib : fields) {
//        if (attrib.isAnnotationPresent(Sorted.class)) {
//          // sorted
//          return attrib.getAnnotation(Sorted.class).order();
//        }
//      }
//    }
//    
//    return null;
//  }

  /**
   * @effects 
   *  if <tt>cls</tt> declares an inner class that is a sub-type of <tt>supType</tt>
   *    return true
   *  else
   *    return false
   */
  public static boolean hasInnerClassTyped(Class cls, Class supType) {
    Class[] inners = cls.getDeclaredClasses();
    if (inners.length > 0) {
      for (Class inner : inners) {
        if (supType.isAssignableFrom(inner)) {
          // found one
          return true;
        }
      }
    }
    
    // not found
    return false;
  }

  /**
   * This method is similar to {@link #hasInnerClassTyped(Class, Class)}, except that it
   * returns the inner class as supposed to return a {@link Boolean}. 
   * 
   * @effects 
   *  if <tt>cls</tt> has an inner class that is a sub-type of <tt>supType</tt>
   *    return it
   *  else
   *    return null
   */
  public static Class getInnerClassTyped(Class cls, Class supType) {
    Class[] inners = cls.getDeclaredClasses();
    if (inners.length > 0) {
      for (Class inner : inners) {
        if (supType.isAssignableFrom(inner)) {
          // found one
          return inner;
        }
      }
    }
    
    // not found
    return null;
  }

  /**
   * @effects 
   *  Use the <b>current class loader</b> to load the class whose FQN is fqn
   *  and return it.
   *  If failed return null.
   */
  public static Class findClass(String fqn) {
    return findClass(fqn, true);
  }

  /**
   * @effects 
   *  Use the <b>current class loader</b> to load the class whose FQN is fqn
   *  and return it.
   *  
   *  If class is not found then (
   *    if <code>throwsIfNotFound = true</code> then   
   *      throws NotFoundException
   *    else 
   *      return null.
   *  )
   *      
   */
  public static Class findClass(String fqn, boolean throwsIfNotFound) throws NotFoundException {
    try {
      return Class.forName(fqn);
    } catch (ClassNotFoundException e) {
      if (throwsIfNotFound) {
        throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e, new Object[] {fqn});
      } else {
        return null;
      }
    }
  }
  
  /**
   * @requires 
   *  if <tt>clsName</tt> is not a built-in Java type (i.e. in java.lang package) then 
   *    <tt>clsName</tt> must be a FQN 
   *    
   * @effects 
   *  Use the <b>specified class loader</b> to load the class whose name is clsName
   *  and return it.
   *  If failed return null.
   */
  public static Class loadClass(ClassLoader classLoader, String clsName) {
    try {
      if (isBasicJavaType(clsName)) {
        return Class.forName("java.lang."+clsName);
      } else {
        Class c = classLoader.loadClass(clsName);
        return c;
      }
    } catch (ClassNotFoundException e) {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if <tt>cls</tt> has no members (excluding the inherited members, if any)
   *    return true
   *  else
   *    return false 
   */
  public static boolean isEmpty(Class cls) {
    boolean hm = hasDeclaredField(cls);
    
    if (hm) return false;
    
    return !hasDeclaredOpt(cls);
  }

  /**
   * @effects 
   *  if <tt>cls</tt> has declared fields (excluding inheritance)
   *    return true
   *  else 
   *    return false
   */
  public static boolean hasDeclaredField(Class cls) {
    return cls.getDeclaredFields().length > 0;
  }

  /**
   * @effects 
   *  if <tt>cls</tt> has declared operations (excluding inheritance)
   *    return true
   *  else 
   *    return false
   */
  public static boolean hasDeclaredOpt(Class cls) {
    if (cls.getDeclaredConstructors().length > 0)
      return true;
    
    return cls.getDeclaredMethods().length > 0;
  }

  /**
   * @requires
   * <tt>cls</tt> has a default constructor.
   * 
   * @effects 
   *  create and return an empty object of <tt>cls</tt>.
   *  
   *  <p>Throws NotPossibleException if fails.
   */
  public static Object createObject(Class cls) throws NotPossibleException {
    try {
      return cls.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new String[] {cls.getName(), ""});
    }
  }
  
  /**
   * @requires 
   *  <tt>attribValMap != null => 
   *    attribValMap.keys contains names of attributes of cls /\ 
   *    attribValMap.values contains either suitable values for the attributes 
   *       OR {@link Map} of attribute-values of the linked objects for the attributes 
   *  </tt> 
   * @effects 
   *  
   *  create an object of <tt>cls</tt> by invoking a suitable constructor with input values
   *  specified in <tt>attribValMap</tt>. 
   *  
   *  <p>If <tt>attribValMap</tt> contains entries whose values are {@link Map} then 
   *  replace them first by the (linked) objects that are created using those {@link Map} as input values.
   *  
   *  <p>If <tt>attribValMap = null</tt> then use the default constructor to create object. 
   *  
   *  <p>Throws NotPossibleException if fails.
   */
  public static <T> T createObject(Class<T> cls, Map<String, Object> attribValMap) {
    
    Object[] args;
    Constructor<T> cons = null;
    if (attribValMap == null || attribValMap.isEmpty()) {
      // empty string: find default constructor
      cons = getDefaultConstructor(cls);
      args = null;
    } else {
      cons = getConstructor(cls, attribValMap.keySet());
      
      int numPairs = attribValMap.size();
      args = new Object[numPairs];
      int i = 0;
      for (Entry<String,Object> pair : attribValMap.entrySet()) {
        String attrib = pair.getKey();
        Object val = pair.getValue();
        
        if (val instanceof Map) {
          // val contains Map of a linked object: create this object and use it for val
          Class attribType = getField(cls, attrib, true).getType();
          val = createObject(attribType, (Map<String,Object>) val);
        }
        args[i++] = val;
      }
    }
    
    if (cons == null) { // constructor not found
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, 
          new Object[] {cls, "required-constructor"} );
    }
    
    T instance;
    try {
      instance = cons.newInstance(args);
      return instance;
    } catch (InstantiationException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e,
          new Object[] {cls.getName(), Arrays.toString(args)});  
    }
  }
  
  /**
   * @effects 
   *  if exists the declared field <tt>cls.fieldName</tt>
   *    return it
   *  else if recursive = true and cls has a domain super-class
   *    return {@link #getField(Class, String, boolean)} on the domain super-class
   *  else
   *    return <tt>null</tt> 
   */
  public static Field getField(Class cls, String fieldName, boolean recursive) {
    Field f = null;
    try {
      try {
        f = cls.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        // may be in the super-type (below)
      }

      if (f == null) {
        Class superType = getDomainSuperType(cls);
        if (superType != null) {
          f = getField(superType, fieldName, recursive);
        } 
      }
    } catch (SecurityException e) {
      e.printStackTrace();
      // should not happen: ignore
    }
    
    
    return f;
  }

  /**
   * @effects 
   *  if cls has a non-Object super-type
   *    return that supertype
   *  else
   *    return null
   */
  private static Class getDomainSuperType(Class cls) {
    Class stype = cls.getSuperclass();
    
    if (stype != Object.class) {
      return stype;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  convert <tt>data</tt> to a value suitable for <tt>dataType</tt>.
   *  <p>Throws NotPossibleException if fails. 
   */
  public static Object convertToTypeValue(String data, Class dataType) throws NotPossibleException {
    if (data.equalsIgnoreCase("null"))
      return null;

    String typeName = dataType.getSimpleName();
    
    Object v;
    if (isBasicJavaTypeOrArrayThereOf(dataType)) {
      v = convertToBasicTypeValue(data, typeName);
    } else if (isCollectionType(typeName)) {
      v = convertToColTypeValue(data, dataType);
    } // end collection-type
    // add other cases here
    else {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONVERT_VALUE, 
          new Object[] {data, typeName});
    }
    
    return v;
  }

  /**
   * Another version of {@link #convertToTypeValue(String, Class)} that accepts {@link DAttr} as an input 
   * and uses its <tt>type()</tt> value to process. 
   * 
   * @requires 
   *  <tt>testAttrib</tt> has the same declaration as the domain attribute whose domain constraint is <tt>dc</tt>.
   *   
   * @effects 
   *   convert data to the type specified in <tt>dc.type</tt> and return the result.
   *   Throws NotPossibleException if failed to convert.
   */
  public static Object convertToAttribValue(String data, Field testAttrib, DAttr dc) throws NotPossibleException {
    
    if (data.equalsIgnoreCase("null"))
      return null;
    
    String typeName = dc.type().name();
    String fieldName = testAttrib.getName();
    
    if (typeName.equals("null")) {  // not specified in domain constraint specification
      // try to get type from testAttribute
      typeName = testAttrib.getType().getSimpleName();
    }
    
    Object v;
    if (isBasicJavaTypeOrArrayThereOf(typeName)) {
      v = convertToBasicTypeValue(data, typeName);
    } else if (isCollectionType(typeName)) {
      v = convertToColTypeValue(data, testAttrib.getGenericType());
    } // end collection-type
    // add other cases here
    else {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_CONVERT_VALUE, 
          new Object[] {data, typeName});
    }
    
    return v;
    
    /*
    Object v;
    if (isBasicJavaType(typeName)) {
      v = convertToBasicTypeValue(data, typeName);
    } else if (isCollectionType(typeName)) {
      // get the type parameter (if available) and convert data to it
      Class typeParam = DClassUtils.getGenericCollectionType(testAttrib.getGenericType()); //DClassUtils.getGenericCollectionType(testAttrib);
      if (typeParam != null) {
        if (isBasicJavaType(typeParam.getSimpleName())) {
          v = convertToBasicTypeValue(data, typeParam.getSimpleName());
        } else {
          // not basic (e.g. Comparable)
          if (typeParam.equals(Comparable.class)) {
            // Comparable
            // keep same data (no convert) - all basic data values (e.g. int, String, etc.) are comparable
            v = data;
          } 
          // add other supported special type params here
          else {
            throw new NotPossibleException(String.format("%s.convert: failed to convert \"%s\" to type: %s (type is wrong or not supported)", thisCls, data, typeParam));
          }
        }
      } else {
        throw new NotPossibleException(String.format("%s.convert: failed to convert \"%s\" to type: %s (collection-type has no type parameter)", thisCls, data, typeName));
      }
    } // end collection-type
    // add other cases here
    else {
      throw new NotPossibleException(String.format("%s.convert: failed to convert \"%s\" to type: %s (type is wrong or not supported)", thisCls, data, typeName));
    }
    
    return v;
    */
  }

  /**
   * @requires 
   *  <tt>dataType</tt> is a generic collection type.
   *  
   * @effects 
   *  convert <tt>data</tt> to a value suitable for <tt>dataType</tt>.
   *  <p>Throws NotPossibleException if fails. 
   */
  public static Object convertToColTypeValue(String data, Type dataType) throws NotPossibleException {
    // get the type parameter (if available) and convert data to it
    Object v;
    Class typeParam = DClassTk.getGenericCollectionType(dataType);
    if (typeParam != null) {
      if (isBasicJavaType(typeParam.getSimpleName())) {
        v = convertToBasicTypeValue(data, typeParam.getSimpleName());
      } else {
        // not basic (e.g. Comparable)
        if (typeParam.equals(Comparable.class)) {
          // Comparable
          // keep same data (no convert) - all basic data values (e.g. int, String, etc.) are comparable
          v = data;
        } 
        // add other supported special type params here
        else {
          throw new NotPossibleException(
              NotPossibleException.Code.FAIL_TO_CONVERT_VALUE, 
              new Object[] {data, typeParam});
        }
      }
    } else {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_CONVERT_VALUE, 
          new Object[] {data, dataType.getTypeName()});
    }
    
    return v;
  }
  
  /**
   * @requires 
   *  <tt>type is in {@link BasicJavaTypes}</tt>
   *  
   * @effects 
   *  convert <tt>data</tt> to a value suitable for <tt>type</tt>.
   *  <p>Throws NotPossibleException if fails.
   */
  public static Object convertToBasicTypeValue(String data, String type) throws NotPossibleException {
    
    if (data.equalsIgnoreCase("null"))
      return null;
    
    Object v;
    if (type.equals("String")) { // no conversion needed
      v = data;
    } else if (type.equals("Integer") || type.equals("int")) {
      try {
        v = Integer.parseInt(data);
      } catch (NumberFormatException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONVERT_VALUE, 
            new Object[] {data, type});
      }
    } else if (type.equalsIgnoreCase("Long") || type.equals("long")) {
      try {
        v = Long.parseLong(data);
      } catch (NumberFormatException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONVERT_VALUE, 
            new Object[] {data, type});
      }
    } else if (type.equalsIgnoreCase("Float") || type.equals("float")) {
      try {
        v = Float.parseFloat(data);
      } catch (NumberFormatException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONVERT_VALUE, 
            new Object[] {data, type});
      }
    } else if (type.equalsIgnoreCase("Double") || type.equals("double")) {
      try {
        v = Double.parseDouble(data);
      } catch (NumberFormatException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONVERT_VALUE, 
            new Object[] {data, type});
      }
    } else if (type.equals("Character") || type.equals("char")) {
      v = data.charAt(0);
    } else if (type.equals("String[]")) { // String array
      if (data.indexOf(",") > -1) { // multiple elements
        // a negative number (e.g. -1) to include trailing empty element
        String[] items = data.split(",", -1);   
        // some item may be the string "null"
        for (int i = 0; i < items.length; i++) {
          if (items[i].equalsIgnoreCase("null")) items[i] = null;
        }
        
        v = items;
      } else {  // single element
        v = new String[] {data};
      }
    } else if (type.equals("char[]")) { // char array
      if (data.indexOf(",") > -1) { // multiple elements
        // a negative number (e.g. -1) to include trailing empty element
        String[] items = data.split(",", -1);
        char[] chars = new char[items.length];
        for (int i = 0; i < items.length; i++) {
          chars[i] = items[i].charAt(0);
        }
        
        v = chars;
      } else {  // single element
        v = new char[] {data.charAt(0)};
      }
    } else if (type.equals("int[]")) { // int array
      if (data.indexOf(",") > -1) { // multiple elements
        // a negative number (e.g. -1) to include trailing empty element
        String[] items = data.split(",", -1);
        int[] nums = new int[items.length];
        for (int i = 0; i < items.length; i++) {
          try {
            nums[i] = Integer.parseInt(items[i]);
          } catch (NumberFormatException e) {
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONVERT_VALUE, 
                new Object[] {items[i], "int"});
          }
        }
        
        v = nums;
      } else {  // single element
        try {
          v = new int[] {Integer.parseInt(data)};
        } catch (NumberFormatException e) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONVERT_VALUE, 
                  new Object[] {data, "int"});
        }
      }
    }
    else {
      // should not happen
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONVERT_VALUE, 
              new Object[] {"", type});
    }
    
    return v;
  }
  
  /**
   * @effects 
   *  if <tt>type</tt> is the name of one of Java's built-in {@link Collection} type
   *    return true
   *  else
   *    return false
   */
  public static boolean isCollectionType(String type) {
    if (type != null) {
      for (String colType : JavaCollectionTypes) {
        if (type.equals(colType)) {
          return true;
        }
      }
    }
    
    return false;
  }

  /**
   * @effects 
   *  if type represents either a primitive Java type or the String type
   *    return true
   *  else
   *    return false
   */
  public static boolean isBasicJavaType(String type) {
    if (type != null) {
      for (String basicType : BasicJavaTypes) {
        if (type.equals(basicType)) {
          return true;
        }
      }
    }
    
    return false;
  }

  /**
   * @effects 
   *  if <tt>dataType</tt> represents a basic Java type or an array thereof
   *    return true
   *  else
   *    return false
   */
  public static boolean isBasicJavaTypeOrArrayThereOf(Class dataType) {
    /*
    boolean isArrayType = isArrayType(dataType);
    
    if (isArrayType) {
      Class componentType = dataType.getComponentType();
      return isBasicJavaType(componentType.getSimpleName());
    } else {
      return isBasicJavaType(dataType.getSimpleName());
    }
    */
    //TODO (?) to have a better logic here (if needed)
    return isBasicJavaTypeOrArrayThereOf(dataType.getSimpleName());
  }

  /**
   * A version of {@link #isBasicJavaTypeOrArrayThereOf(Class)} that supports string-value type name. 
   * This is used to process {@link DAttr#type()}. 
   * 
   * @effects 
   *  if <tt>type</tt> represents a basic Java type or an array thereof
   *    return true
   *  else
   *    return false
   */
  public static boolean isBasicJavaTypeOrArrayThereOf(String typeName) {
    boolean isArrayType = isArrayType(typeName);
    
    if (isArrayType) {
      String componentTypeName = typeName.split("\\[")[0];
      return isBasicJavaType(componentTypeName);
    } else {
      return isBasicJavaType(typeName);
    }
  }
  
  /**
   * @effects 
   *  if <tt>typeName</tt> represents an array type
   *    return true
   *  else
   *    return false 
   */
  public static boolean isArrayType(String typeName) {
    return typeName.endsWith("[]");
  }

  /**
   * @effects 
   *  if <tt>dataType</tt> represents an array type
   *    return true
   *  else
   *    return false
   */
  public static boolean isArrayType(Class dataType) {
    //TODO (?) improve this check
    return isArrayType(dataType.getSimpleName());
  }
  
  /**
   * @effects 
   *  if <tt>m</tt> is a default Java operation
   *    return true
   *  else
   *    return false 
   */
  public static boolean isDefaultOpt(Method m) {
    String mName = m.getName();
    for (String defaultOptName : DefaultOptNames) {
      if (mName.equals(defaultOptName)) {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * @effects 
   *  invoke <tt>testObj.method(args)</tt>. 
   *  If terminates successfully 
   *    return the result
   *  else
   *    return the exception's cause
   *  
   */
  public static Object invokeMethod(Object testObj, Method method, Object...args) {
    Object actual;
    try {
      actual = method.invoke(testObj, args);
      
      return actual;
    } catch (IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      return (e instanceof InvocationTargetException) ? e.getCause() : e;
    }
  }

  /**
   * @effects 
   *  invoke <tt>testObj.method(args)</tt>. 
   *  If terminates successfully 
   *    return output result or value of the argument in <tt>args</tt> at the index <tt>modifiedParam</tt>
   *  else
   *    return the exception's cause
   *  
   */
  public static Object invokeMethodWithSideEffects(Object testObj, Method method, int modifiedParam, 
      Object...args) {
    Object actual;
    try {
      actual = method.invoke(testObj, args);
      
      if (modifiedParam > -1 && modifiedParam < args.length) {
        // method has side-effect that we are interested in using as the actual output
        actual = args[modifiedParam];
      }
      
      return actual;
    } catch (IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      return (e instanceof InvocationTargetException) ? e.getCause() : e;
    }
  }
  
  /**
   * @requires rootPkg != null /\ pc is a class in the root package or in one of the descendant packages
   * @effects 
   *  given the root package name <tt>rootPkg</tt>, 
   *    return the relative package path name of a class <tt>c</tt> (from the root package).
   *    
   *  <p>If the root package has no sub-packages then return <tt>c.simpleName</tt>
   */
  public static String getRelativeClassName(String rootPkg, Class c) {
    if (rootPkg == null || c == null) return null;
    
    String fqn = c.getName();
    String sname = c.getSimpleName();
    String rootPrefix = rootPkg+".";
    int prefixIdx = fqn.indexOf(rootPrefix);
    String rest = fqn.substring(prefixIdx+rootPrefix.length());
    
    if (rest.equals(sname)) {
      // no sub-package
      return sname;
    } else { // has sub-packages
      return rest;
    }
  }

  /**
   * @effects 
   *  if <code>fqnClsName</code> contains "." 
   *    return the last name token (separated by "."
   *  else
   *    return <code>fqnClsName</code>
   * @version 5.4
   */
  public static String getClassNameFromFqn(String fqnClsName) {
    if (fqnClsName == null) return null;
    String[] tokens = fqnClsName.split("\\.");
    return tokens[tokens.length-1];
  }
  
//  /**
//   * @effects 
//   *  if <tt>sm</tt> is defined with {@link ValidateExcl#elements()} that includes 
//   *  {@link MethodDecl#ReturnType} 
//   *    return <tt>true</tt>
//   *  else
//   *    return <tt>false</tt>
//   */
//  public static boolean isRetTypeCheckExcl(Executable sm) {
//    ValidateExcl valExcl = sm.getAnnotation(ValidateExcl.class);
//    
//    if (valExcl != null && Toolkit.isInArray(MethodDecl.ReturnType, valExcl.elements())) {
//      return true;
//    } else {
//      return false;
//    }
//  }
  
  /**
   * @effects 
   *  if exists domain-specific annotations for <tt>m</tt>
   *    return them as an array
   *  else
   *    return null
   */
  public static List<Annotation> getDomainMethodAnnotations(Executable m) {
    Annotation[] allAnos = m.getAnnotations();
    
    if (allAnos.length > 0) {
      List<Annotation> anos = new ArrayList<>();
      for (Annotation a : allAnos) {
        if (!NonDomainAnos.contains(a.getClass())) {
          anos.add(a);
        }
      }
      
      if (anos.isEmpty()) {
        return null;
      } else {
        return anos;
      }
    } else {
      return null;
    }
    
    
  }

  /**
   * @effects 
   *  converts and returns the camel-case version of <code>name</code>
   * @version 5.4<br>
   * - 5.4.1: supports the reverse conversion (e.g. hello -> Hello)
   */
  public static String toCamelCase(String name) {
    /* v5.4.1: 
     if (name == null || name.length() < 2) return name;
    
     return (name.charAt(0)+"").toLowerCase() +  name.substring(1);
     */
    if (name == null || name.isEmpty()) return name;
    
    StringBuilder sb = new StringBuilder(name);
    String firstChar = sb.charAt(0)+"";
    
    String newName;
    String lowerFirst = firstChar.toLowerCase();
    if (lowerFirst.equals(firstChar)) {
      newName = firstChar.toUpperCase() + sb.delete(0, 1).toString();
    } else {
      if (name.length() < 2) return name;
      
      newName = lowerFirst +  sb.substring(1);  
    }
    
    return newName;
  }

  /**
   * @effects 
   *  converts class name to a camel-case name suitable for use as attribute 
   *  name in a referencing class.
   *  
   * @version 5.4
   */
  public static String getAttribNameFromType(String clsName) {
    return toCamelCase(clsName);
  }

  /**
   * @effects 
   *  return the package name of the specified class
   * @version 5.4 
   * 
   */
  public static String getPackageName(Class c) {
    if (c == null) return null;
    
    return c.getPackage().getName();
  }


  /**
   * @effects 
   *  return the package name of the specified class or null if 
   *  it has no package name
   *  
   * @version 5.4<br> 
   * - 5.4.1: fixed: return null if no package is specified
   */
  public static String getPackageName(String fqn) {
    if (fqn == null) return null;
    
    int lastDot = fqn.lastIndexOf(".");
    if (lastDot > -1)
      return fqn.substring(0, lastDot);
    else { // no package
      // v5.4.1: return fqn;
      return null;
    }
  }
  
  /**
   * @effects 
   *  create and return a dot-representation of an enum value.
   *  e.g. MyEnum.Value1 =&gt; "MyEnum.Value1"
   * @version 5.4
   */
  public static String getFieldAccessString(Enum val) {
    return val.getClass().getSimpleName() + "." + val;
  }

  /**
   * @effects 
   *  sort names in <code>clsNames</code> and join them with the separator "-"
   *  to form an association name.
   *  Return this name
   *  
   * @version 5.4
   * 
   */
  public static String getAutoAssociationName(String...clsNames) {
    if (clsNames == null)
      return null;
    
    Arrays.sort(clsNames);
    String assocName = String.join("-", clsNames);
    return assocName;
  }

  /**
   * @effects 
   *  return the FQN of the class given its package and simple name
   * @version 5.4
   */
  public static String getFQNClassName(String pkg, String simpleName) {
    if (pkg == null || simpleName == null)
      return null;
    
    return pkg + "." + simpleName;
  }

  /**
   * @effects 
   *  if exists domain attributes of <code>dcls</code> whose data types are Enum
   *    return Optional containing them
   *  else
   *    return empty Optional 
   *    
   * @version 5.4.1
   */
  public static Optional<Collection<Field>> getDomainEnumTypedAttribs(Class dcls) {
    Collection<Field> dattrs = getDomainAttributes(dcls, true);
    
    if (dattrs == null) {
      return Optional.ofNullable(null);
    }
    
    List<Field> enumFields = new ArrayList<>();
    dattrs.forEach(f -> {
      if (f.getType().isEnum()) {
        enumFields.add(f);
      }
    });
    
    return !enumFields.isEmpty() ? Optional.of(enumFields) : Optional.ofNullable(null);
  }

  /**
   * @effects 
   *  if exists a constructor <code>c = cls(paramType)</code> 
   *    creates and return an object using this constructor and <code>param</code>, i.e. <code>c(param)</code>
   *  
   *  else throws NotFoundException
   * @version 5.4.1
   * 
   */
  public static <T> T createObject(Class<T> cls,
      Class paramType,
      Object param) throws NotPossibleException, NotFoundException {
    
    Constructor<T> c;
    try {
      c = cls.getConstructor(paramType);
      
      return c.newInstance(param);
    } catch (NoSuchMethodException e) {
      throw new NotFoundException(NotFoundException.Code.CONSTRUCTOR_METHOD_NOT_FOUND, 
          e,
          new Object[] {cls, paramType});
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, 
          e,
          new Object[] {cls, param});
    }
    
  }
  
  /**
   * @effects 
   *  if exists a constructor <code>c = cls(paramTypes)</code> 
   *    creates and return an object using this constructor and <code>params</code>, i.e. <code>c(params)</code>
   *  
   *  else throws NotFoundException
   * @version 5.4.1
   * 
   */
  public static <T> T createObject(Class<T> cls,
      Class[] paramTypes,
      Object[] params) throws NotPossibleException, NotFoundException {
    
    Constructor<T> c;
    try {
      c = cls.getConstructor(paramTypes);
      
      return c.newInstance(params);
    } catch (NoSuchMethodException e) {
      throw new NotFoundException(NotFoundException.Code.CONSTRUCTOR_METHOD_NOT_FOUND, 
          e,
          new Object[] {cls, Arrays.toString(paramTypes)});
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, 
          e,
          new Object[] {cls, Arrays.toString(params)});
    }
    
  }

  /**
   * @effects 
   *  if c is an enum type
   *    return true
   *  else
   *    return false
   * @version 5.4.1
   */
  public static boolean isEnum(Class c) {
    return (c != null && c.isEnum());
  }

  /**
   * @effects 
   *  if c is a sub-type of a non-object type
   *    return true
   *  else
   *    return false
   * @version 5.4.1
   * 
   */
  public static boolean isProperSubType(Class c) {
    if (c == null) return false;
    
    Class superType = c.getSuperclass();
    
    return (superType != null && superType != Object.class);
  }
  
//  /**
//   * @effects 
//   *  create and return a dot-representation of an enum value.
//   *  e.g. MyEnum.Value1 =&gt; "MyEnum.Value1"
//   * @version 5.4
//   */
//  public static String getFieldAccessString(Class ownerCls, Object val) {
//    if (val instanceof Enum)
//      return getFieldAccessString((Enum) val);
//    else {
//      return ownerCls.getSimpleName() + "." + val;
//    }
//  }
}
