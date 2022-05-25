package jda.modules.common.collection;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.NotPossibleException;

/**
 * A sub-class of <code>LinkedHashMap</code> specifically designed to handle
 * special (key,value) entries.
 * 
 * @author dmle
 * 
 */
public class Map<K,V> extends java.util.LinkedHashMap<K,V> {
  public static final String ENTRY_DELIM = "=>"; 
  
  public Map() {
    super();
  }

  public Map(String...entries) throws IllegalArgumentException {
    super();
    if (entries.length > 0) {
      for (String s : entries) {
        if (!s.contains(ENTRY_DELIM) || (s.lastIndexOf(ENTRY_DELIM) != s.indexOf(ENTRY_DELIM))) {
          throw new IllegalArgumentException("entries must contain exactly one delimiter " + ENTRY_DELIM + ": " + s);
        }
        String[] items = s.split(ENTRY_DELIM);
        K key = (K) items[0].trim();
        V value = (V) items[1].trim();
        put(key,value);
      }
    }
  }
  
  /**
   * @effects if <code>this.get(key)</code> is a color string of the form <code>"r,g,b"</code>
   *          then returns a <code>Color</code> object, else throws <code>NotPossibleException</code>
   */
  public Color getColorValue(K key) throws NotPossibleException {
    V v = super.get(key);
    if (v == null) {
      return null;
    } else {
      String colorStr = (String) v;
      if (colorStr.indexOf(",") > -1) {
        String[] colorComps = colorStr.split(",");
        if (colorComps.length < 3) {
          throw new NotPossibleException(NotPossibleException.Code.CONFIGURATION_VALUE_ERROR, 
              "Lỗi giá trị cấu hình: {0}", v);
        } else {
          try {
            int r = Integer.parseInt(colorComps[0]);
            int g = Integer.parseInt(colorComps[1]);
            int b = Integer.parseInt(colorComps[2]);
            return new Color(r,g,b);
          } catch (NumberFormatException e) {
            throw new NotPossibleException(NotPossibleException.Code.CONFIGURATION_VALUE_ERROR,
                "Lỗi giá trị cấu hình: {0}", v);
          }
        }
      } else {
        throw new NotPossibleException(NotPossibleException.Code.CONFIGURATION_VALUE_ERROR,
            "Lỗi giá trị cấu hình: {0}", v);
      }
    }
    
  }
  
  /**
   * @effects <pre>
   *  if the value v of key in this is a standard <tt>String</tt> representation 
   *  of {@link Font}, i.e. of the form <tt>"family,size,style"</tt>
   *    parse this string as Font object and return it
   *  else 
   *    throws NotPossibleException
   *  </pre>   
   */
  public Font getFontValue(K key) throws NotPossibleException {
    V v = super.get(key);
    if (v == null) {
      return null;
    } else {
      String fontStr = (String) v;
      if (fontStr.indexOf(",") > -1) {
        String[] fontComps = fontStr.split(",");
        if (fontComps.length < 3) {
          throw new NotPossibleException(NotPossibleException.Code.CONFIGURATION_VALUE_ERROR,
              "Lỗi giá trị cấu hình: {0}", v);
        } else {
          try {
            String fname = fontComps[0];
            int fsize = Integer.parseInt(fontComps[1]);
            int fstyle = Integer.parseInt(fontComps[2]);
            return new Font(fname, fstyle, fsize);
          } catch (NumberFormatException e) {
            throw new NotPossibleException(NotPossibleException.Code.CONFIGURATION_VALUE_ERROR,
                "Lỗi giá trị cấu hình: {0}", v);
          }
        }
      } else {
        throw new NotPossibleException(NotPossibleException.Code.CONFIGURATION_VALUE_ERROR,
            "Lỗi giá trị cấu hình: {0}", v);
      }
    }
  }
  
  /**
   * @effects if an integer value exists for <code>key</code> returns its value, 
   *          else returns <code>defaultVal</code>.
   *          <br>The value of <code>defaultVal</code> is typically set to -1 for 
   *          positive numbers.
   */
  public int getIntegerValue(K key, final int defaultVal) {
    int v;
    Object vo = super.get(key);
    if (vo != null && vo instanceof Integer)
      v = ((Integer) vo).intValue();
    else
      v = defaultVal;
    
    return v;
  }
  
  /**
   * @effects <pre>
   *  if the value v of key in this is a {@link Float}
   *    cast and return it
   *  else 
   *    return defaultVal
   *  </pre>   
   */
  public float getFloatValue(K key, final float defaultVal) {
    float v;
    V vo = super.get(key);
    if (vo != null && vo instanceof Float) {
      v = ((Float) vo).floatValue();
    } else {
      v = defaultVal;
    }
    
    return v;
  }

  /**
   * @effects 
   *  if value of key is of type <tt>type</tt>
   *    cast and return it 
   *  else
   *    return null
   * @return
   */
  public <T> T getObjectValue(K key, Class<T> type) {
    V v = get(key);
    
    if (type.isInstance(v)) {
      return (T) v;
    } else {
      return null;
    }
  }
  
  /**
   * @effects if a <code>String</code> value exists for <code>key</code> return it, 
   *          else returns <code>defVal</code>.
   */
  public String getStringValue(K key, String defVal) {
    Object v = super.get(key);
    
    if (v != null && v instanceof String) {
      return (String)v;
    } else {
      return defVal;
    }
  }

  /**
   * @effects if a <code>Boolean</code> value exists for <code>key</code> then returns its boolean value, 
   *          else returns <code>defaultVal</code>.
   */
  public boolean getBooleanValue(K key, boolean defaultVal) {
    Object v = super.get(key);
    
    if (v != null) {
      try {
        return Boolean.parseBoolean(v.toString());
      } catch (Exception e) {
        // not a boolean
        return defaultVal;
      }
    } else {
      return defaultVal;
    }
  }

  /**
   * @effects returns the value mapped to the key whose index is at the <code>keyIndex</code>
   */
  public V get(int keyIndex) {
    int i = 0;
    for (K key : keySet()) {
      if (i == keyIndex) {
        return super.get(key);
      }
      i++;
    }
    
    return null;
  }
  
  /**
   * @effects 
   *  if exists entry <tt>(k,v)</tt> in this s.t. <tt>v != null</tt>
   *    return <tt>v</tt>
   *  else
   *    return <tt>defVal</tt>
   */
  public V get(K k, V defVal) {
    V v = super.get(k);
    
    if (v == null)
      return defVal;
    else
      return v;
  }

  /**
   * @effects puts the value <code>val</code> mapped to the key whose index is at the <code>keyIndex</code>
   *          to this
   */
  public V putAt(int keyIndex, V val) {
    int i = 0;
    for (K key : keySet()) {
      if (i == keyIndex) {
        return super.put(key,val);
      }
      i++;
    }
    
    return null;    
  }


  public void put(Object...propValPairs) throws ApplicationRuntimeException {
    int len = propValPairs.length;
    
    if (len > 0 && len % 2 == 0) {
      K p = null;
      V v = null;
      for (int i = 0; i < len; i++) {
        p = (K) propValPairs[i];
        i++;
        v = (V) propValPairs[i];

//        if (i % 2 == 0)
//          p = (K) propValPairs[i];
//        else
//          v = (V) propValPairs[i];
        
        super.put(p, v);
      }
    } else {
      throw new ApplicationRuntimeException(null, "Invalid properties {0}", Arrays.toString(propValPairs));
    }
  }
  
//  /**
//   * @effects returns <code>entrySet()</code> of <code>this</code> as <code>List</code>
//   */
//  public List<java.util.Map.Entry<K,V>> entryList() {
//    Set entries = entrySet();
//    
//    if (entries.size() > 0) {
//      List l = new ArrayList();
//      l.addAll(entries);
//      return l;
//    } else {
//      return null;
//    }
//  } 

  /**
   * @effects if <code>this.size > 0</code> returns the last element, else returns <code>null</code>
   */
  public java.util.Map.Entry<K, V> getFirstEntry() {
    Set<java.util.Map.Entry<K, V>> entries = entrySet();
    
    if (entries.size() > 0) {
      java.util.Map.Entry<K, V> entry = entries.iterator().next();
      return entry;
    } else {
      return null;
    }
  }
  
  /**
   * @effects if <code>this.size > 0</code> returns the last element, else returns <code>null</code>
   */
  public java.util.Map.Entry<K, V> getLastEntry() {
    Set<java.util.Map.Entry<K, V>> entries = entrySet();
    
    if (entries.size() > 0) {
      java.util.Map.Entry<K, V> entry = null;
      for (Iterator<java.util.Map.Entry<K, V>> it = entries.iterator(); it.hasNext();) {
        entry = it.next();
      }
      return entry;
    } else {
      return null;
    }
  }
}
