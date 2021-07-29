package jda.modules.common.filter;

/**
 * @overview
 *  A generic interface used for the purpose of filtering output. 
 *  
 * @author dmle
 *
 * @param <T> the type of objects being filtered
 */
public interface Filter<T> {
  public boolean check(T o, Object...args);
}
