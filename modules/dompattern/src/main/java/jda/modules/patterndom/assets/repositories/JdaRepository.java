package jda.modules.patterndom.assets.repositories;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jda.modules.common.collection.CollectionToolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dodm.DODM;
import jda.modules.dodm.dom.DOM;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public abstract class JdaRepository implements Repository {
  private DODM dodm;
  
  private static JdaRepository instance;
  
  protected JdaRepository(Configuration config) {
    dodm = DODM.getInstance(DODM.class, config);
  }
  
  /**
   * @effects 
   *  return the single instance of this
   */
  public static <T extends JdaRepository> T getInstance(Class<T> type, Configuration config) {
    if (instance == null) {
      try {
        // invoke the constructor to create object 
        instance = type.getConstructor(Configuration.class).newInstance(config);
        
      } catch (Exception e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
            new Object[] {type.getSimpleName()});
      }
    }
    
    return (T) instance;
  }
  
  /**
   * @effects 
   *  return the single instance of this
   */
  public static <T extends JdaRepository> T getInstance(Class<T> type) {
    if (instance == null) {
      try {
        // invoke the constructor to create object 
        instance = type.getConstructor().newInstance();
        
      } catch (Exception e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
            new Object[] {type.getSimpleName()});
      }
    }
    
    return (T) instance;
  }
  
  @Override
  public void registerClass(Class<?> cls) throws NotPossibleException {
    dodm.registerClass(cls);
  }
  
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public <T> T getObjectById(Class<T> cls, Serializable id) throws NotFoundException, NotPossibleException {
    DOM dom = dodm.getDom();
    try {
      return dom.retrieveObjectByDefaultId(cls, id);
    } catch (NotFoundException e) {
      throw e;
    } catch (DataSourceException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB,
          new Object[] {e.getMessage()}, e);
    }
  }

  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public <T> Collection getObjectsByCluster(T root, Class<?>[] boundary) {
    Set cluster = new HashSet<>();
    getObjectsByCluster(root, boundary, cluster);
    
    return cluster;
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  private <T> void getObjectsByCluster(T currObj, Class<?>[] boundary,
      Collection cluster) {
    DOM dom = dodm.getDom();
    Collection<?> assocs = dom.getLinkedAssociates(currObj, currObj.getClass());
    if (assocs != null) {
      assocs.forEach(o -> {
        boolean inClusterB4Added = cluster.contains(o);
        if (!inClusterB4Added) {
          if (CollectionToolkit.isInArray(o.getClass(), boundary)) {
            cluster.add(o);
          }
          // recursive call
          getObjectsByCluster(o, boundary, cluster);
        }
      });
    }
  }


  @Override
  public <T> Collection<T> getObjects(Class<T> cls) {
    try {
      Map<Oid, T> objects = dodm.getDom().retrieveObjects(cls);
      if (objects != null) {
        return objects.values();
      } else {
        return null;
      }
    } catch (DataSourceException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB,
          new Object[] {e.getMessage()}, e);
    } catch (NotPossibleException e) {
      throw e;
    }
  }
  
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public <T> Oid add(T obj) throws NotPossibleException {
    try {
      return dodm.getDom().addObject(obj);
    } catch (DataSourceException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, 
          new Object[] {e.getMessage()}, e);
    }
  }

  /**
   * @effects 
   *  updates the data source record of <code>obj</code>.
   *  Throws NotFoundException if fails to retrieve domain attributes, 
   *  NotPossibleException if fails to change attribute value or to update the object 
   *  in the data source 
   */
  @Override
  public <T> T update(Serializable id, T obj) 
      throws NotFoundException, NotPossibleException {
    Map<DAttr, Object> attribVals = 
        dodm.getDsm().getAttributeValuesAsMap(obj);
    
    try {
      dodm.getDom().updateObject(obj, attribVals);
      
      return obj;
    } catch (NotPossibleException | NotFoundException e) {
      throw e;
    } catch (DataSourceException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, 
          new Object[] {e.getMessage()}, e);
    }
  }

  /**
   * @effects 
   *  
   */
  @Override
  public void remove(Serializable id, Class<?> cls) throws NotPossibleException {
    Query query = QueryToolKit.createSearchQuery(dodm.getDsm(), cls, 
        DCSLConstants.ATTRIB_ID_DEFAULT_NAME, 
        Op.EQ, 
        id);
    try {
      dodm.getDom().deleteObjects(cls, query);
    } catch (DataSourceException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, 
          new Object[] {e.getMessage()}, e);
    }
  }
  
  
}
