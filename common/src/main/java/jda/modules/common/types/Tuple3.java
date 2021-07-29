package jda.modules.common.types;

/**
 * @overview 
 *  Represents a triple.
 *  
 * @author ducmle
 *
 * @version 3.4 
 */
public class Tuple3<U, V, T> {
  private Tuple2<U, Tuple2<V,T>> tuple;
  
  public Tuple3(U u, V v, T t) {
    tuple = new Tuple2(u, new Tuple2(v,t));
  }

  public U getFirst() {
    return tuple.getFirst();
  }
  
  public V getSecond() {
    return tuple.getSecond().getFirst();
  }

  public T getThird() {
    return tuple.getSecond().getSecond();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((tuple == null) ? 0 : tuple.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Tuple3 other = (Tuple3) obj;
    if (tuple == null) {
      if (other.tuple != null)
        return false;
    } else if (!tuple.equals(other.tuple))
      return false;
    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return "Tuple3 <" + getFirst() + "," + getSecond() + "," + getThird() + ">";
  }
}
