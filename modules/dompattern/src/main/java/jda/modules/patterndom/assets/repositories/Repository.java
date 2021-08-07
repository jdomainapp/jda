package jda.modules.patterndom.assets.repositories;

import java.io.Serializable;
import java.util.Collection;

import jda.modules.common.exceptions.NotPossibleException;
import jda.mosa.model.Oid;

/**
 * @overview 
 *  Represents the Repository in the DDD pattern REPOSITORIES.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public interface Repository {

  void registerClass(Class<?> cls) throws NotPossibleException ;
  
  /**
   * @effects 
   * 
   */
  <T> T getObjectById(Class<T> cls, Serializable id);

  /**
   * @effects 
   */
  <T> Collection getObjectsByCluster(T root, Class<?>[] boundary);

  /**
   * @effects 
   * 
   */
  <T> Collection<T> getObjects(Class<T> cls);
  
  /**
   * @effects 
   */
  <T> Oid add(T obj) throws NotPossibleException;
  
  /**
   * @effects 
   */
  <T> T update(Serializable id, T obj) throws NotPossibleException;
  
  /**
   * @effects 
   */
  void remove(Serializable id, Class<?> cls) throws NotPossibleException;
}
