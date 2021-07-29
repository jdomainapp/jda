package jda.modules.exportdoc.util.table;

import jda.modules.common.collection.Map;
import jda.modules.exportdoc.util.table.Table.Prop;

public abstract class Cell<T> {
  private Map<Prop,Object> props;
  
  private T val;

  public Cell(T val) {
    this.props = new Map<>();
    this.val = val;
  }

  public T getVal() {
    return val;
  }

  public void setVal(T val) {
    this.val = val;
  }

  public Map<Prop,Object> getProperty() {
    return props;
  }

  public void setProperty(Object...propValPairs) {
    props.put(propValPairs);
  } 

  public abstract Object clone();
  
  @Override
  public String toString() {
    return "Cell (" + val + ")";
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
    Cell other = (Cell) obj;
    if (val == null) {
      if (other.val != null)
        return false;
    } else if (!val.equals(other.val))
      return false;
    return true;
  }

  public abstract void finalise();
  
} // end Cell
