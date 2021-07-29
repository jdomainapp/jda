package jda.modules.common.types.tree;

/**
 * @overview Represent a binary edge.
 * 
 * @author dmle
 */
public class Edge {
  private Node n1;
  private Node n2;
  
  // tag used for adding edge's metadata (e.g. weight, etc.)
  private Object tag;
  
  /**
   * @requires <tt>n1 !=null /\ n2 != null</tt>
   */
  public Edge(Node n1, Node n2) {
    this(n1,n2,null);
  }
  
  /**
   * @requires <tt>n1 !=null /\ n2 != null</tt>
   */
  public Edge(Node n1, Node n2, Object tag) {
    this.n1=n1;
    this.n2=n2;
    this.tag=tag;
  }

  public Node getNode1() {
    return n1;
  }
  
  public Node getNode2() {
    return n2;
  }
  
  /**
   * @effects
   *  if tag is not null
   *    this.tag = tag
   *  else
   *    do nothing
   */
  public void setTag(Object tag) {
    if (tag != null) {
      this.tag = tag;
    }
  }

  public Object getTag() {
    return tag;
  }
  
  public boolean hasTag() {
    return tag != null;
  }

  /**
   * @effects 
   *  if this.tag is defined
   *    return this.tag.toString()
   *  else
   *    return <tt>null</tt>
   */
  public String getTagAsString() {
    if (tag != null)
      return tag.toString();
    else
      return null;
  }
  
  @Override
  public String toString() {
    return "Edge<"+n1+","+n2+">";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((n1 == null) ? 0 : n1.hashCode());
    result = prime * result + ((n2 == null) ? 0 : n2.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Edge other = (Edge) obj;
    if (n1 == null) {
      if (other.n1 != null)
        return false;
    } else if (!n1.equals(other.n1))
      return false;
    if (n2 == null) {
      if (other.n2 != null)
        return false;
    } else if (!n2.equals(other.n2))
      return false;
    return true;
  }
  
}



