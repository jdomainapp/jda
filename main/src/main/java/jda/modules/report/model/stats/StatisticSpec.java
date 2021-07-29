package jda.modules.report.model.stats;

/**
 * @overview 
 *  An abstract class that represent all statistical specification domain classes (used by statistical reports).
 *  
 * @author dmle
 */
public interface StatisticSpec {
  
  /**
   * @effects 
   *  return the domain class whose objects are queried to generate the statistics that are specified by this class. 
   */
  public Class getDomainClass();
}
