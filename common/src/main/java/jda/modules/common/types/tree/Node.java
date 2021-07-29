package jda.modules.common.types.tree;

/**
 * @overview Represent tree nodes
 * 
 * @author dmle
 */
public class Node<V> {
  private V val;
  private Object tag;
  
  public Node(V val) {
    //this.val = val;
    this(val, null);
  }
  
  public Node(V val, Object tag) {
    this.val = val;
    this.tag = tag;
  }

  public V getValue() {
    return val;
  }

  public void setValue(V val) {
    this.val = (V) val;
  }

  public String getValueAsString() {
    return val+"";
  }
  
  public Object getTag() {
    return tag;
  }

  public void setTag(Object tag) {
    this.tag = tag;
  }

  public boolean hasTag() {
    return tag != null;
  }

  public String getTagAsString() {
    return tag != null ? tag.toString() : null;
  }
  
  @Override
  public String toString() {
    return "Node("+getValueAsString()+","+getTagAsString()+")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((val == null) ? 0 : val.hashCode());
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
    Node other = (Node) obj;
    if (val == null) {
      if (other.val != null)
        return false;
    } else if (!val.equals(other.val))
      return false;
    return true;
  }
  
  
}
