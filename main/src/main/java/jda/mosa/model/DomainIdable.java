package jda.mosa.model;

/**
 * @overview
 *  A generic interface for a domain class to implement, which provides access to 
 *  the <tt>Oid</tt> for each object of the class and to a number of other common 
 *  features. 
 * @author dmle 
 */
public interface DomainIdable {
  
  /**
   * @effects 
   *  initialises <tt>this.oid</tt> to <tt>id</tt> 
   */
  public void setOid(Oid id);
  
  /**
   * @effects 
   *  return <tt>this.oid</tt>
   */
  public Oid getOid();
}
