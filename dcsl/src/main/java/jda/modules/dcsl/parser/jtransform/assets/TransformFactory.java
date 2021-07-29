package jda.modules.dcsl.parser.jtransform.assets;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.parser.Dom;

/**
 * @overview 
 *  Transformation factory that creates specific {@link Transform} objects from {@link TransfAction}.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4
 * @deprecated (not yet operational)
 */
public class TransformFactory {
  private static Map<Object,Transform> objCache;
  
  private TransformFactory() {}
  
  /**
   * @effects 
   *  create and return a {@link Transform} object from the specified <code>transfType</code>
   *  and using argument <code>dom</code>.
   *  
   *  <p>Throws NotPossibleException if failed.
   */
//  public static <T extends Transform> T createTransfObject(Dom dom, TransfAction transfType) {
//    Class<T> cls = transfType.lookUpTransfClass();
//    try {
//      return cls.getConstructor(Dom.class).newInstance(dom);
//    } catch (InstantiationException | IllegalAccessException
//        | IllegalArgumentException | InvocationTargetException
//        | NoSuchMethodException | SecurityException e) {
//      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, 
//         new Object[] {cls.getName(), dom});
//    }
//  }
  
  /**
   * @effects 
   *  create and return a {@link Transform} object whose class is <code>transfCls</code>
   *  and using argument <code>dom</code>.
   *  
   *  <p>Throws NotPossibleException if failed.
   */
  public static <T extends Transform> T createTransfObject(Dom dom, 
      Class<T> transfCls, 
      boolean cache) {
    T obj = null;
    
    if (cache) {
      obj = (T) lookUpObject(transfCls);
    }
    
    if (obj == null) {
      try {
        obj = transfCls.getConstructor(Dom.class).newInstance(dom);
        cacheObj(transfCls, obj);
      } catch (InstantiationException | IllegalAccessException
          | IllegalArgumentException | InvocationTargetException
          | NoSuchMethodException | SecurityException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, 
           new Object[] {transfCls.getName(), dom});
      }
    }
    
    return obj;
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  private static void cacheObj(Object key,
      Transform obj) {
    if (objCache == null) {
      objCache = new HashMap<>();
    }
    objCache.put(key, obj);
  }

  /**
   * @effects 
   * 
   */
  private static Transform lookUpObject(Object key) {
    if (objCache == null) {
      objCache = new HashMap<>();
    }
    
    return objCache.get(key);
  }
}
