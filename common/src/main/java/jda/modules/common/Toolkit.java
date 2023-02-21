package jda.modules.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import jda.modules.common.collection.CollectionToolkit;
import jda.modules.common.datetime.ShortDayLabel;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;


public class Toolkit {
  /**
   * 
   * @overview
   *  Represents seasons in a year.
   *  
   * @author dmle
   */
  public static enum Season {
    Spring,
    Summer,
    Fall, 
    Winter;
    
    /**
     * @requires 
     *  month in [1,12]
     */
    public static Season lookUp(int month) {
      if (month >= 1 && month <= 3)
        return Spring;
      else if (month >= 4 && month <= 6)
        return Summer;
      else if (month >= 7 && month <= 9)
        return Fall;
      else
        return Winter;
    }
  } // end Season

  private static final long MILLIS_IN_A_DAY = 24 * 60 * 60 * 1000;
  
  /**
   * {@link #getSharedCalendarInstance()}
   */
  private static Calendar sharedCalendarInstance;

//  /**
//   * @effects 
//   *  set debug to <tt>debugClassesAsString</tt>
//   * @version 3.3
//   */
//  public static void setSystemDebug(String debugClassesAsString) {
//    if (debugClassesAsString == null) {
//      System.clearProperty("debug");
//    } else {
//      System.setProperty("debug", debugClassesAsString);
//    }
//  }
//
//  /**
//   * @effects 
//   *  add <tt>classes</tt> to system debug and return the former debug as <tt>String</tt>
//   *   
//   * @version 3.3
//   */
//  public static String addSystemDebug(Class...classes) {
//    String debugStr = System.getProperty("debug"); 
//    
//    if (debugStr.equals("true") || debugStr.equals("false")) {
//      // cannot change debug
//      return debugStr;
//    }
//    
//    StringBuffer classAsString = new StringBuffer();
//    int numCls = classes.length;
//    int index = 0;
//    for (Class c : classes) {
//      classAsString.append(c.getSimpleName());
//      if (index < numCls - 1)
//        classAsString.append(",");
//      index++;
//    }
//    
//    String oldDebug = debugStr;
//    
//    if (debugStr == null) {
//      debugStr = classAsString.toString();
//    } else {
//      debugStr += "," + classAsString.toString();
//    }
//    
//    System.setProperty("debug", debugStr);
//    
//    return oldDebug;
//  }
  
  /**
   * Determine whether or not a debug value is set for a given class. It is set if the 
   * Java option <tt>-Ddebug</tt> is specified when the application is run. The value 
   * of this option can either be <tt>true/false</tt> or the simple name of a class. 
   * The former case specifies the system-wide debug, while the later case specifies
   * that only the class(es) whose simple name(s) match the specified name have 
   * debug turned on.  
   * 
   * @effects
   *  if <tt>debug</tt> is a system property 
   *    if it is a boolean value
   *      return it
   *    else if <tt>forClass != null</tt> and <tt>debug</tt> is the simple name of <tt>forClass</tt>
   *      return true 
   *  return false<tt>
   */
  public static boolean getDebug(final Class forClass) {
    return getProgramPropertyBoolean(forClass, "debug");
  }


  /**
   * @modifies {@link System}
   * @effects 
   *  register  to the debug list all classes whose simple names are <tt>debugClasses</tt>
   */
  public static void addDebug(String...debugClasses) {
    String key = "debug";
    String debugStr = System.getProperty(key);
    StringBuilder newDebug;
    if (debugStr != null) {
      newDebug = new StringBuilder(debugStr);
      newDebug.append(",");
    } else {
      newDebug = new StringBuilder();
    }
    
    int idx = 0;
    for (String c : debugClasses) {
      if (idx > 0) newDebug.append(",");
      newDebug.append(c);
      idx++;
    }
    
    System.setProperty(key, newDebug.toString());
  }
  
  /**
   * Determine whether or not a logging value is set for a given class. It is set if the 
   * Java option <tt>-Dlogging</tt> is specified when the application is run. The value 
   * of this option can either be <tt>true/false</tt> or the simple name of a class. 
   * The former case specifies the system-wide debug, while the later case specifies
   * that only the class(es) whose simple name(s) match the specified name have 
   * logging turned on.  
   * 
   * @effects
   *  if <tt>logging</tt> is a system property 
   *    if it is a boolean value
   *      return it
   *    else if <tt>forClass != null</tt> and <tt>logging</tt> is the simple name of <tt>forClass</tt>
   *      return true 
   *  return false<tt>
   */
  public static boolean getLoggingOn(final Class forClass) {
    return getProgramPropertyBoolean(forClass, "logging");
  }
  
  /**
   * @param forClass  the class for which the property is applied (null means application-wide property)
   * @param propertyName  the environment property name (that which is set by the JVM's -D option)
   */
  private static boolean getProgramPropertyBoolean(final Class forClass, final String propertyName) {
    java.util.Properties env = System.getProperties();
    boolean bolVal = false;
    if (env.containsKey(propertyName)) {
      String val = env.getProperty(propertyName);

      if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false")) {
        bolVal = Boolean.parseBoolean(val);
        // system-wide property
        return bolVal;
      } else if (forClass != null) {
        // class-specific property
        StringTokenizer st = new StringTokenizer(val, ",");
        String clsName = forClass.getSimpleName();
        // class-specific debug
        while (st.hasMoreTokens()) {
          if (st.nextToken().equalsIgnoreCase(clsName)) {
            bolVal=true;
            break;
          }
        }
      }
    }

    return bolVal;
  }
  
  /**
   * @effects if there are <code>public static final</code> <code>Field</code>s
   *          of <code>c</code> that are declared with the type <code>c</code>
   *          then return them as <code>List</code>, else return
   *          <code>null</code>
   */
  public static <T> List<T> getConstantObjects(Class enclosingClass, Class<T> constType) {
    /*v3.0: support collection-typed fields
    java.util.Map<String,T> objects = getConstantObjectsAsMap(enclosingClass, constType);
    List<T> list = new ArrayList<>();
    if (objects != null) {
      list.addAll(objects.values());
      return list;
    } else {
      return null;
    }
    */
    Field[] fields = enclosingClass.getFields();
    if (fields.length == 0)
      return null;

    int mod;
    Class type;
    List<T> objects = new ArrayList();
    T o;
    final Class<Collection> COL = Collection.class;
    Collection col;
    
    for (Field f : fields) {
      mod = f.getModifiers();
      type = f.getType();
      if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
          && Modifier.isPublic(mod)
          ) {
        // constant object
        if (constType.isAssignableFrom(type)) { // can be the super-class)
          // non-collection-typed constant
          try {
            o = (T) f.get(null);
            objects.add(o);
          } catch (IllegalAccessException e) {
            // should not happen
          }
        } else if (COL.isAssignableFrom(type)) {
          // collection-typed constant
          // must be a generic specific type
          ParameterizedType colType = (ParameterizedType) f.getGenericType();
          
          java.lang.reflect.Type[] typeVars = colType.getActualTypeArguments();
          
          java.lang.reflect.Type genType = typeVars[0];

          if (genType instanceof Class) {
            // a specific actual type (e.g. Customer) rather than a type variable (e.g. T)
            if (constType.isAssignableFrom((Class)genType)) {
              // ok
              try {
                col = (Collection) f.get(null);
                for (Object oi : col) {
                  objects.add((T) oi);  // casting is safe because of the assignable check above
                }
              } catch (IllegalArgumentException | IllegalAccessException e) {
                // should not happen
              }
            } 
          }
        }
      }
    }

    if (objects.isEmpty())
      return null;
    else
      return objects;
  }
  
  /**
   * @effects 
   *  read all the <tt>public static final</tt> constant fields of type <tt>T</tt> in <tt>enclosingClass</tt>
   *  and return them as <tt>Map</tt> whose key is the field name and whose object is the constant object assigned to the field.
   *  <br>Return <tt>null</tt> if no objects are found
   */
  public static <T> LinkedHashMap<String,T> getConstantObjectsAsMap(Class enclosingClass, Class<T> constType) {
    Field[] fields = enclosingClass.getFields();
    if (fields.length == 0)
      return null;

    int mod;
    Class type;
    LinkedHashMap<String,T> objects = new LinkedHashMap();
    //List objects = new ArrayList();
    T o;
    
    for (Field f : fields) {
      mod = f.getModifiers();
      type = f.getType();
      if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
          && Modifier.isPublic(mod)
          ) {
        // constant object
        if (constType.isAssignableFrom(type)) { // can be the super-class)
          // non-collection-typed constant
          try {
            o = (T) f.get(null);
            //objects.add(o);
            objects.put(f.getName(), o);
          } catch (IllegalAccessException e) {
            // should not happen
          }
        }
      }
    }

    if (objects.isEmpty())
      return null;
    else
      return objects;
  }
  
  /**
   * @effects 
   *  return the <tt>public</tt> constant object in <tt>enclosingClass</tt> whose 
   *  name is <tt>name</tt> and whose declared type is <tt>type</tt> and return it.
   *  
   *  <p>If <tt>inherited = true</tt> then process also fields inherited from ancestor classes; 
   *  else process only fields declared in <tt>enclosingClass</tt> 
   *  
   *  <p>Throws NotFoundException if the object cannot be found
   * @version 
   * - 3.2: added support for inherited option
   */
  public static <T> T getConstantObject(Class enclosingClass, String name, Class<T> type, 
      boolean inherited) throws NotFoundException {
    /* v3.2: support inherited option
    Field[] fields = enclosingClass.getFields();
    */
    Field[] fields;
    if (inherited) {
      fields = enclosingClass.getFields();
    } else {
      fields = enclosingClass.getDeclaredFields();
    }
    
    if (fields.length == 0)
      throw new NotFoundException(NotFoundException.Code.CONSTANT_NOT_FOUND, 
          new Object[]{ name, enclosingClass});

    int mod;
    String fieldName;
    T o;
    Class declaredType;
    for (Field f : fields) {
      mod = f.getModifiers();
      fieldName = f.getName();
      declaredType = f.getType();
      if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
          && Modifier.isPublic(mod) && 
          fieldName.equals(name) &&
          type.isAssignableFrom(declaredType)
          ) {
        // found constant object
        try {
          o = (T) f.get(null);
          return o;
        } catch (IllegalAccessException e) {
          // should not happen
        }
        break;
      }
    }
    
    throw new NotFoundException(NotFoundException.Code.CONSTANT_NOT_FOUND, 
        "Không tìm thấy hằng số {0} ở lớp {1}", name, enclosingClass);
  }
  
  /**
   * @effects 
   *  return the method of <tt>c</tt> whose name is <tt>name</tt> and whose input parameter types
   *  are <tt>paramTypes</tt>, throws NotFoundException if no such method can be found.
   */
  public static Method getMethod(Class c, String name, Class[] paramTypes)
      throws NotFoundException {
    Method m = null;

    try {
      if (paramTypes != null) {
        m = c.getMethod(name, paramTypes);
      } else {
        // find method with same name and parameter types array's length = 0
        Method[] methods = c.getMethods();
        for (Method method : methods) {
          if (method.getName().equals(name) && 
              method.getParameterTypes().length == 0) {
            m = method;
            break; // stop at first found
          }
        }
      }
    } catch (Exception e) {
      throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND, e,
          new Object[] {c, name, Arrays.toString(paramTypes)});
    }

    if (m == null) {
      throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND,
          new Object[] {c, name, Arrays.toString(paramTypes)});
    } else {
      return m;
    }
  }
  
  /**
   * @effects 
   *  return the method of <tt>c</tt> whose name is <tt>name</tt> and whose input parameter types
   *  are <tt>paramTypes</tt> (if specified), throws NotFoundException if no such method can be found.
   */
  public static Method getMethodWithOptionalParams(Class c, String name, Class[] paramTypes)
      throws NotFoundException {
    Method m = null;

    try {
      if (paramTypes != null) {
        m = c.getMethod(name, paramTypes);
      } else {
        // find method with same name and parameter types array's length = 0
        Method[] methods = c.getMethods();
        for (Method method : methods) {
          if (method.getName().equals(name) && 
              method.getParameterTypes().length == 0) {
            m = method;
            break; // stop at first found
          }
        }
      }
    } catch (Exception e) {
      throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND, e,
          new Object[] {c, name, Arrays.toString(paramTypes)});
    }

    if (m == null) {
      // last resort: get method by name
      return getMethod(c, name);
    } else {
      return m;
    }
  }
  
  
  /**
   * @effects 
   *  return the method of <tt>c</tt> whose name is <tt>name</tt>, throws NotFoundException if no such method can be found.
   *  
   * @version 4.0
   */
  public static Method getMethod(Class c, String name) throws NotFoundException, NotPossibleException {
    try {
      Method[] methods = c.getMethods();
      for (Method method : methods) {
        if (method.getName().equals(name)) {
          return method;
        }
      }

      throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND,
          new Object[] {c, name, ""});
    } catch (SecurityException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, e,
          new Object[] {"Method not found", c.getSimpleName()+"."+name, e.getMessage()});
    }
  }
  
  /**
   * @requires 
   *  vals != null /\ length(vals) > 0
   * @effects 
   *  return the aggregated hash code of the values in <tt>vals</tt>, using the formula:
   *  <pre>
   *    int hashCode = 1;
   *    for (Object e : vals)
   *      hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
   *  </pre>
   */
  public static int hashOfValues(Object[] vals) {
    int hashCode = 1;
    for (Object e : vals)
        hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
    
    return hashCode;
  }

  /**
   * @requires
   *  cls != null
   * @effects
   *  if <tt>cls</tt> is defined in a package
   *    return the last name of this package (e.g. <tt>model</tt> in the package named <tt>domainapp.model</tt>)
   *  else
   *    return <tt>null</tt>
   */
  public static String getPackageLastName(Class cls) {
    if (cls == null)
      return null;
    
    Package pkg = cls.getPackage();
    
    if (pkg == null)
      return null;
    
    String pkgName = pkg.getName(); // fully qualified, e.g. domainapp, domainapp.model, etc.
    int index = pkgName.lastIndexOf(".");
    String lastName;
    if (index > -1) {
      // multi-level package
      lastName = pkgName.substring(index+1);
    } else {
      // 1-level package
      lastName = pkgName;
    }
    
    return lastName;
  }


  /**
   * Use this method to avoid creating many instances of the {@link Calendar} when 
   * the current state of the calendar is not of concern (i.e. can be changed arbitrarily) 
   * 
   * @effects 
   *  return the shared {@link Calendar} instance 
   */
  public static Calendar getSharedCalendarInstance() {
    if (sharedCalendarInstance == null) {
      sharedCalendarInstance = Calendar.getInstance();
    }
    
    return sharedCalendarInstance;
  }
  
  /**
   * @requires date != null
   * @effects 
   *  return year(currentDate) - year(date) 
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified
   *   
   * @version 3.1
   */
  public static int getAge(Date date) {
    if (date == null)
      return -1;
    
    int currentYear = getCurrentYear();
    
    Calendar cal = getSharedCalendarInstance();
    cal.setTime(date);
    int year = cal.get(Calendar.YEAR);
    
    return (currentYear - year);
    
  }
  
  /**
   * @requires
   *  day and year are valid /\ 
   *  month in range [1,12] (1 = Jan, 12 = Dec)
   *   
   * @effects 
   *  return a {@link Date} with <tt>day/month/year</tt>
   *  
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified
   *  
   */
  public static Date getDateZeroTime(int day, int month, int year) {
    Calendar cal = getSharedCalendarInstance();
    
    cal.set(Calendar.DAY_OF_MONTH, day); 
    cal.set(Calendar.MONTH, month-1);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    
    return cal.getTime();
  }
  
  /**
   * @effects 
   *  return the current date/time
   */
  public static Date getCurrentDateTime() {
    return Calendar.getInstance().getTime();
  }

  /**
   * @effects 
   *  return the current year
   */
  public static int getCurrentYear() {
    return Calendar.getInstance().get(Calendar.YEAR);
  }
  
  /**
   * @effects 
   *  return the current date/time offset by (i.e. added with) <tt>num</tt> in the calendar field <tt>calendarField</tt>
   */
  public static Date getCurrentDateTimeWithOffSet(int calendarField, int num) {
    Calendar cal = Calendar.getInstance();
    
    cal.add(calendarField, num);
    
    return cal.getTime();
  }
  
  /**
   * @effects 
   *  return the current date whose time is set to '0:0:0:0'
   */
  public static Date getCurrentDateZeroTime() {
    // use the current calendar year
    Calendar cal = Calendar.getInstance();
    
    // set time to 0:0:0:0
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    
    Date date  = cal.getTime();
    return date;
  }

  /**
   * @effects 
   *  return {@link Season} suitable for current time of the year
   */
  public static Season getCurrentSeason() {
    int month = getCurrentMonth(getSharedCalendarInstance());
    
    return Season.lookUp(month);
  }

  /**
   * @effects 
   *  the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this date.
   */
  public static long dateToMilliseconds(Date atDate) {
    return atDate.getTime();
  }

  /**
   * @modifies {@link #sharedCalendarInstance}
   * 
   * @effects 
   *  return the total number of days in the month represented by <tt>monthOfYear</tt>
   * @example 
   *  <pre>monthOfYear = Date("06/2015") -> dateCountInMonth(monthOfYear) = 30</pre>
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified
   *   
   */
  public static int dateCountInMonth(Date monthOfYear) {
    Calendar cal = getSharedCalendarInstance(); //Calendar.getInstance();

    cal.setTime(monthOfYear);
    
    /*
    int minDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
    int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    
    return maxDay-minDay+1;
    */
    return dateCountInMonth(cal);
  }
  
  /**
   * This is a safer version of {@link #dateCountInMonth(Date)} in that it does not affect 
   * {@link #sharedCalendarInstance}
   *  
   * @requires 
   *  <tt>cal</tt> has been set to the correct month
   *  
   * @modifies <tt>cal</tt>
   * 
   * @effects 
   *  return the total number of days in the month represented by <tt>monthOfYear</tt>
   * @example 
   *  <pre>monthOfYear = Date("06/2015") -> dateCountInMonth(monthOfYear) = 30</pre>
   */
  public static int dateCountInMonth(Calendar cal) {
    int minDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
    int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    
    return maxDay-minDay+1;
  }
  
  /**
   * @requires 
   *  <tt>dayOfWeekLabel</tt> is one of {{@link Calendar#MONDAY},...,{@link Calendar#SUNDAY}}
   * 
   * @modifies {@link #sharedCalendarInstance}
   * 
   * @effects 
   *  return the total number of days whose label equal to <tt>dayOfWeekLabel</tt> 
   *    in the month represented by <tt>monthOfYear</tt>.
   *  
   * @example 
   *  <pre>monthOfYear = Date("06/2015") /\ dayOfWeekLabel = {@link Calendar#SATURDAY} 
   *      -> dateCountInMonth(monthOfYear, dayOfWeekLabel) = 4</pre>
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified
   *   
   */
  public static int dateCountInMonth(Date monthOfYear, int dayOfWeekLabel) {
    Calendar cal = getSharedCalendarInstance(); //Calendar.getInstance();
    
    cal.setTime(monthOfYear);

    // reset day to first day
    cal.set(Calendar.DAY_OF_MONTH, 1);
    
    // get the first day of the month that has the specified label
    int firstDay = 1;
    int dayLabel;
    do {
      dayLabel = cal.get(Calendar.DAY_OF_WEEK);
      if (dayLabel == dayOfWeekLabel) {
        firstDay = cal.get(Calendar.DAY_OF_MONTH);
      } else {
        // increment 
        cal.add(Calendar.DAY_OF_MONTH, 1);
      }
    } while (dayLabel != dayOfWeekLabel);
    
    // get the number of days of the month
    int numDays = dateCountInMonth(cal);
    
    // count the number of days that have the specified label
    int count = 1;
    do {
      firstDay += 7;
      if (firstDay <= numDays) {
        count++;
      }
    } while (firstDay < numDays);
    
    return count;
  }

  /**
   * @requires
   *  <tt>[startDate,endDate]</tt> is a valid date range /\  
   *  <tt>dayOfWeekLabel</tt> is one of {{@link Calendar#MONDAY},...,{@link Calendar#SUNDAY}}
   * 
   * @modifies {@link #sharedCalendarInstance}
   * 
   * @effects 
   *  return the total number of days whose label equal to <tt>dayOfWeekLabel</tt> 
   *    in the date range <tt>[startDate,endDate]</tt>; 
   *  or return -1 if date range is invalid 
   *  
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified
   *   
   */
  public static int dateCountInPeriod(Date startDate, Date endDate, int dayOfWeekLabel) {
    
    if (startDate == null || endDate == null)
      return -1;
    
    // count the number of days that have the specified label
    int count;
    int dayLabel;
    if (dateLtEq(startDate, endDate)){
      // startDate <= endDate: valid date range
      // cannot use shared calendar instance here b/c it is changed by dateLtEq
      Calendar cal = Calendar.getInstance();   
      cal.setTime(startDate);
      
      Date nextDay;
      count = 0;
      do {
        dayLabel = cal.get(Calendar.DAY_OF_WEEK);
        
        if (dayLabel == dayOfWeekLabel) {
          // found one
          count++;
        }
        
        // scroll next 
        cal.add(Calendar.DAY_OF_MONTH, 1);
        nextDay = cal.getTime();
      } while (dateLtEq(nextDay, endDate));      
    } else {
      // invalid date range
      count = -1;
    }

    return count;
  }

  /**
   * @requires
   *  <tt>[startDate,endDate]</tt> is a valid date range /\  
   *  <tt>dayOfWeekToExclude</tt> is one of {{@link Calendar#MONDAY},...,{@link Calendar#SUNDAY}}
   * 
   * @modifies {@link #sharedCalendarInstance}
   * 
   * @effects 
   *  return the total number of days in the date range <tt>[startDate,endDate]</tt> 
   *  whose labels are BUT <tt>dayOfWeekToExclude</tt>; 
   *  or return 0 if no dates found to satisfy, -1 if date range is invalid  
   *  
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified
   *   
   */
  public static int dateCountInPeriodExcept(Date startDate, Date endDate, int dayOfWeekToExclude) {
    
    if (startDate == null || endDate == null)
      return -1;
    
    // count the number of days that have the specified label
    int count;
    int dayLabel;
    if (dateLtEq(startDate, endDate)){
      // startDate <= endDate: valid date range
      // cannot use shared calendar instance here b/c it is changed by dateLtEq
      Calendar cal = Calendar.getInstance();   
      cal.setTime(startDate);
      
      Date nextDay;
      count = 0;
      do {
        dayLabel = cal.get(Calendar.DAY_OF_WEEK);
        
        if (dayLabel != dayOfWeekToExclude) {
          // found one
          count++;
        }
        
        // scroll next 
        cal.add(Calendar.DAY_OF_MONTH, 1);
        nextDay = cal.getTime();
      } while (dateLtEq(nextDay, endDate));      
    } else {
      // invalid date range
      count = -1;
    }

    return count;
  }
  
  /**
   * Use {@link #dateAfter(Date, Date)}
   * 
   * @requires 
   *  d1 != null /\ d2 != null
   * @effects 
   *  if <tt>d1 <= d2</tt> (<b>including ONLY</b> 
   *  the values of day, month, and year fields, i.e. excluding time of day),
   *    return <tt>true</tt>
   *  else 
   *    return <tt>false</tt>
   *    
   *  i.e.<br> 
   *  let <tt>result</tt> = {@link #dateAfter(Date, Date)}
   *  return <tt>!result</tt>
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified
   *    
   */
  public static boolean dateLtEq(Date d1, Date d2) {
    return !dateAfter(d1, d2);
  }
  
  /**
   * Use {@link #dateBefore(Date, Date)}
   * 
   * @requires 
   *  d1 != null /\ d2 != null
   * @effects 
   *  if <tt>d1 >= d2</tt> (<b>including ONLY</b> 
   *  the values of day, month, and year fields, i.e. excluding time of day),
   *    return <tt>true</tt>
   *  else 
   *    return <tt>false</tt>
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified
   */  
  public static boolean dateGtEq(Date d1, Date d2) {
    return !dateBefore(d1, d2);
  }
  
  /**
   * This method has the same behaviour as {@link Date#after(Date)} but excludes the time of day in 
   * comparison. 
   * 
   * @requires 
   *  d1 != null /\ d2 != null
   * @effects 
   *  if <tt>d1 > d2</tt> (i.e. is strictly after <b>including ONLY</b> 
   *  the values of day, month, and year fields, i.e. excluding time of day)
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>  
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified
   *   
   */
  public static boolean dateAfter(Date d1, Date d2) {
    Calendar cal = getSharedCalendarInstance();
    
    cal.setTime(d1);
    // exclude time of day
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    
    Date d1ZeroTime = cal.getTime();
    
    cal.setTime(d2);
    // exclude time of day
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    
    Date d2ZeroTime = cal.getTime();
    
    return d1ZeroTime.after(d2ZeroTime);
  }
  
  /**
   * This method has the same behaviour as {@link Date#before(Date)} but excludes the time of day in 
   * comparison. 
   * 
   * @requires 
   *  d1 != null /\ d2 != null
   * @effects 
   *  if <tt>d1 < d2</tt> (i.e. is strictly before <b>including ONLY</b> 
   *  the values of day, month, and year fields, i.e. excluding time of day)
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>  
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified
   *   
   */
  public static boolean dateBefore(Date d1, Date d2) {
    Calendar cal = getSharedCalendarInstance();
    
    cal.setTime(d1);
    // exclude time of day
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    
    Date d1ZeroTime = cal.getTime();
    
    cal.setTime(d2);
    // exclude time of day
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    
    Date d2ZeroTime = cal.getTime();
    
    return d1ZeroTime.before(d2ZeroTime);
  }
  
  /**
   * @effects 
   *   return the number days gap between <tt>d1, d2</tt>,
   *   i.e. <br>
   *   if <tt>d1 >= d2</tt> 
   *      <tt>result = d1 - d2</tt>
   *   else 
   *      <tt>result = d2 - d1</tt>
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified      
   */
  public static int dateDiff(Date d1, Date d2) {
    Calendar cal = getSharedCalendarInstance();
    // normalise the dates to zerotime (so that day calculation results in whole days)
    
    cal.setTime(d1);
    // zero time of day
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    
    double t1 = cal.getTimeInMillis();
    
    cal.setTime(d2);
    // zero time of day
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    
    double t2 = cal.getTimeInMillis();
    
    int daysGap;
    if (t1 >= t2) {
      daysGap = (int) ((t1-t2) / MILLIS_IN_A_DAY);
    } else {
      daysGap = (int) ((t2-t1) / MILLIS_IN_A_DAY);
    }
    
    return daysGap + 1;
  }

  /**
   * @requires cal != null
   * @effects 
   *  return the current month of {@link Calendar} <tt>cal</tt> (the first month 
   *  starts from 1.)
   *  
   * @version 3.0
   */
  public static int getCurrentMonth(Calendar cal) {
    return cal.get(Calendar.MONTH) + 1;
  }

  /**
   * @effects 
   *  return the year of {@link Calendar} cal
   * @version 3.0
   */
  public static int getCurrentYear(Calendar cal) {
    return cal.get(Calendar.YEAR);
  }

  /**
   * @requires 
   *  date != null
   * @modifies 
   *  {@link #sharedCalendarInstance}
   * @effects
   *  return the value of the field {@link field} of <tt>date</tt> 
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified
   *   
   * @version 3.1
   */
  public static int getDateField(Date date, int field) {
    if (date == null)
      return -1;
    
    Calendar cal = getSharedCalendarInstance();
    cal.setTime(date);
    return cal.get(field);
  }
  
  /**
   * @requires 
   *  date != null
   * 
   * @modifies {@link #sharedCalendarInstance}
   * 
   * @effects 
   *  add to <tt>date</tt> <tt>numDays</tt> and number of seconds <tt>secs</tt> and 
   *  return the result
   * @version 3.1
   *  
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified
   *   
   */
  public static Date addDate(Date date, int numDays, int secs) {
    Calendar cal = getSharedCalendarInstance();

    // convert date to millisecs and perform operation on it
    cal.setTime(date);
    long millis = cal.getTimeInMillis();
    
    long addMillis = numDays * MILLIS_IN_A_DAY + secs * 1000;
    
    millis += addMillis;
    
    Date newDate = new Date(millis);
    return newDate;
  }
  
  /**
   * @requires cal != null /\ days != null
   * @modifies cal, days
   * @effects 
   *  get the current day numbers (e.g. 1,2,...30) of the month <tt>month</tt> 
   *  of {@link Calendar} <tt>cal</tt> and add them to <tt>days</tt>
   *  
   *  <p>Note: month starts from 0.
   *  
   * @version 3.0
   * 
   */
  public static void getDaysOfMonth(final Calendar cal, final int month, final List days) {
    cal.set(Calendar.MONTH, month);
    
    int minDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
    int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

    for (int i = minDay; i <= maxDay; i++) {
      days.add(i);
    }
  }

  /**
   * @effects
   *  return {@link Date} object representing the first day of <tt>month</tt>
   *  
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified  
   */
  public static Date getFirstDayOfMonth(Date month) {
    Calendar cal = getSharedCalendarInstance(); 
    cal.setTime(month);
    
    int minDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
    cal.set(Calendar.DAY_OF_MONTH, minDay);
    Date first = cal.getTime();
    
    return first;
  }

  /**
   * @effects
   *  return {@link Date} object representing the last day of <tt>month</tt> 
   *  
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified 
   */
  public static Date getLastDayOfMonth(Date month) {
    Calendar cal = getSharedCalendarInstance(); 
    cal.setTime(month);
    
    int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    cal.set(Calendar.DAY_OF_MONTH, maxDay);
    Date last = cal.getTime();
    
    return last;
  }
  
  /**
   * @effects 
   *  return the short label for <tt>dayOfWeek</tt>
   */
  public static String getShortDayLabelFor(Class<? extends ShortDayLabel> dayLabelCls, int dayOfWeek) throws IllegalArgumentException {
    ShortDayLabel[] dayLabels = dayLabelCls.getEnumConstants();
    for (ShortDayLabel dl : dayLabels) {
      if (dl.getDayOfWeek() == dayOfWeek)
        return dl.getLabel();
    }
    
    throw new IllegalArgumentException(Toolkit.class.getSimpleName()+".getLabelFor: invalid day of week: " + dayOfWeek);
  }
  

  /**
   * @requires 
   *  <tt>dayOfWeek</tt> is one of {{@link Calendar#MONDAY},...,{@link Calendar#SUNDAY}}
   *  
   * @modifies cal
   * 
   * @effects 
   *  <tt>cal.day = dayNum</tt>; 
   *  if day <tt>dayNum</tt> corresponds to <tt>dayOfWeek</tt> in the calendar <tt>cal</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public static boolean isDayOfWeek(Calendar cal, int dayNum, int dayOfWeek) {
    cal.set(Calendar.DAY_OF_MONTH, dayNum);
    return cal.get(Calendar.DAY_OF_WEEK) == dayOfWeek;
  }

  /**
   * @requires 
   * 1 <= weekNo <= 4
   * 
   * @modifies cal
   * @effects
   *  <tt>cal.time = date</tt>;
   *  if <tt>date</tt> falls into the week <tt>weekNo</tt> of the specified {@link Calendar} cal
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public static boolean isWeekNo(Calendar cal, Date date, int weekNo) {
    cal.setTime(date);
    
    int calWeek = cal.get(Calendar.WEEK_OF_MONTH);//cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    return calWeek == weekNo;
  }
  
  /**
   * @modifies cal
   * @effects 
   *  <tt>cal.day = dayNum</tt>;
   *  if day <tt>dayNum</tt> falls into the week <tt>weekNo</tt> of the month specified by {@link Calendar} <tt>cal</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public static boolean isWeekNo(Calendar cal, int dayNum, int weekNo) {
    cal.set(Calendar.DAY_OF_MONTH, dayNum);
    
    int calWeek = cal.get(Calendar.WEEK_OF_MONTH);
    return calWeek == weekNo;
  }
  
  /**
   * @requires 
   *  it != null
   * @effects 
   *  return a new {@link Collection} whose content contains the objects in <tt>it</tt>
   *   
   * @version 3.0
   * @deprecated v3.3: use {@link CollectionToolkit} instead
   */
  public static <T> Collection<T> createCollection(Iterator<T> it) {
    Collection<T> col = null;
    if (it != null) {
      col = new ArrayList();
      while (it.hasNext()) {
        col.add(it.next());
      }
    }
    
    return col;
  }

  /**
   * @effects 
   *  create and return a new {@link Collection} that has the same actual type as <tt>col1</tt> and that  
   *  contains exactly the elements in <tt>col1</tt> but not in <tt>col2</tt>. 
   *  
   *  <p>if the intersection is empty or col2 is null then return <tt>col1</tt>; 
   *  if col1 is <tt>null</tt> or empty then return <tt>null</tt>; 
   *  
   *  <p> if could not create a new collection for some reasons return <tt>null</tt>
   *  
   * @version 3.2
   * @deprecated v3.3: use {@link CollectionToolkit} instead
   */
  public static <T> Collection<T> createCollectionFromDisjoint(
      Collection<T> col1, Collection<T> col2) {
    if (col1 == null || col1.isEmpty())
      return null;
    
    if (col2 == null)
      return col1;
    
    Collection<T> result = null;
    for (T o : col1) {
      if (!col2.contains(o)) {
        if (result == null) {
          try {
            result = col1.getClass().newInstance();
          } catch (InstantiationException | IllegalAccessException e) {
            // failed
            break;
          }
        }
        result.add(o);
      }
    }
    
    if (result != null && result.size() == col1.size())
      return col1;
    else
      return result;
  }
  
  /**
   * @requires map != null
   * 
   * @effects
   * <pre> 
   *  if not exists entry <tt>e</tt> in <tt>map</tt>, s.t <tt>equals(e.key,key)</tt>
   *    init <tt>e = Entry(key, new Collection())</tt>
   *    add <tt>e</tt> to <tt>map</tt>
   *    
   *  add <tt>value</tt> to <tt>e.value</tt>
   * </pre>
   * @version 3.1
   * @deprecated v3.3: use {@link CollectionToolkit} instead
   */
  public static <K,V> void updateCollectionBasedMap(Map<K, Collection<V>> map, K key, V value) {
    if (map == null)
      return; // do nothing
    
    Collection<V> values = map.get(key);
    if (values == null) {
      values = new ArrayList();
      map.put(key, values);
    }
    
    values.add(value);
  }

  /**
   * @requires 
   *  causeClasses.length > 0
   * @effects 
   *  if exists in stack trace of <tt>throwable</tt> objects of type specified in 
   *  <tt>causeClasses</tt>
   *    return an array of the non-null messages of those objects
   *  else
   *    return <tt>null</tt>  
   * @version 3.1  
   */
  public static String[] getCauses(Throwable throwable,
      Class...causeClasses) {
    if (causeClasses == null || causeClasses.length == 0)
      return null;
    
    Throwable cause = throwable;
    
    List<String> causeMessages = new ArrayList();
    
    while (cause != null) {
      for (Class causeCls : causeClasses) {
        if (causeCls.isInstance(cause)) {
          // a cause of interest
          if (cause.getMessage() != null) {
            causeMessages.add(cause.getMessage());
            break;
          }
        }
      }
      
      cause = cause.getCause();
    }

    return causeMessages.isEmpty() ? null : causeMessages.toArray(new String[causeMessages.size()]); 
  }

  /**
   * @requires 
   *  map != null /\ i >= 0
   * @effects 
   *  return the <tt>i</tt>th entry of <tt>map</tt>; 
   *    or return <tt>null</tt> if <tt>map</tt> is empty or <tt>i</tt> is not a valid entry
   *    index
   * @deprecated v3.3: use {@link CollectionToolkit} instead
   */
  public static <K,V> Entry<K,V> getMapEntryAt(Map<K, V> map, int i) {
    if (map == null || i < 0 || i >= map.size())
      return null;
    
    if (map.isEmpty())
      return null;
    
    int index = 0;
    for (Entry e : map.entrySet()) {
      if (index == i) {
        return e;
      }
      index++;
    }
    
    // should not happen
    return null;
  }

  /**
   * @effects 
   *  if className is a valid FQN of a class
   *    load and return it
   *  else
   *    throw NotFoundException
   * @version 3.3
   */
  public static Class loadClass(String className) throws NotFoundException {
    Class c;
    try {
      c = Class.forName(className);
      return c;
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e, 
          new Object[] {className}); 
    }
  }

  /**
   * @effects 
   *  Return a <tt>String[]</tt> sub-array of <tt>args<tt> containing elements from position <tt>startIndex</tt> to end (inclusively)
   *  
   *  <p>throws IllegalArgumentException if args = null, IndexOutOfBoundsException if <tt>startIndex</tt> is invalid
   * @version 3.3
   */
  public static String[] subArray(final String[] args, final int startIndex) throws IllegalArgumentException, IndexOutOfBoundsException{
    if (args == null)
      throw new IllegalArgumentException("Input array is null");
    
    int len = args.length;
    if (startIndex < 0 || startIndex >= len) {
      throw new IndexOutOfBoundsException("Index "+startIndex+" is not in the expected range ["+0+","+(len-1)+"]");
    }
    
    String[] sub = new String[len-startIndex];
    System.arraycopy(args, startIndex, sub, 0, sub.length);
    
    return sub;
  }

  /**
   * @effects 
   *  sleep on the current thread for <tt>millis</tt> milli-seconds.
   * @version 4.0
   */
  public static void sleep(int millis) {
    sleep(millis, null);
  }
  
  /**
   * @effects 
   *  sleep on the current thread for <tt>millis</tt> milli-seconds, 
   *  and if <code>mesg</code> is specified then print it before going to sleep.
   * @version 5.4
   */
  public static void sleep(int millis, String mesg) {
    try {
      if (mesg != null) {
        System.out.println(mesg);
      }
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      // wakes up...
    }
  }
}
