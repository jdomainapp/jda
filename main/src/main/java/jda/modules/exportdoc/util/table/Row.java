package jda.modules.exportdoc.util.table;

import static jda.modules.exportdoc.util.table.Table.Prop.*;

import java.util.ArrayList;

import jda.modules.common.collection.Map;
import jda.modules.exportdoc.util.table.Table.Prop;

public class Row<T extends Cell> extends ArrayList<T> {
  private Map<Prop,Object> props;

  private int numCols;
  
  public Row(int numCols) {
    super(numCols);
    this.numCols = numCols;
    this.props = new Map<>();
  }
  
  /**
   * @effects 
   *  if copies = false
   *    fill each cell of this with the Cell object <tt>c</tt>
   *  else
   *    fill each cell of this with a copy of the Cell object <tt>c</tt>
   */
  public <U extends T> void fill(U c, boolean copies) {
    for (int i = 0; i < numCols; i++) {
      if (copies == false) {
        add(c);
      } else {
        U cloned = (U) c.clone();
        add(cloned);
      }
    }
  }
  
  public void setProperty(Object...propValPairs) {
    props.put(propValPairs);
  }
  
  public Map<Prop,Object> getProperty() {
    return props;
  }

  /**
   * @requires 
   *  {@link #finalise()} has been invoked on this
   */
  public int getHeight() {
    return getProperty().getIntegerValue(Height, 0);
  }
  

  /**
   * @effects 
   *  if this is not nested 
   *    return {@link #getHeight}
   *  else 
   *    return <tt>nr.{@link #getNormalisedHeight()}</tt>, where <tt>nr</tt> is the first row of the first nested table of this
   */
  public int getNormalisedHeight() {
    TableCell tc;
    Table<Row> t;
    for (Cell c : this) {
      if (c instanceof TableCell) {
        // nested
        tc = (TableCell) c;
        t = tc.getVal();
        if (!t.isEmpty()) {
          // not empty
          return t.getFirstRow().getNormalisedHeight(); 
        }
      }
    }
    
    // not nested or all nested tables are empty
    return getHeight();
  }

  /**
   * @effects 
   *  if this is a nested row (i.e. at least one of its cells is a TableCell)
   *    return true
   *  else
   *    return false  
   */
  public boolean isNested() {
    for (Cell c : this) {
      if (c instanceof TableCell) {
        return true;
      }
    }
    
    return false;
  }

  /**
   * @effects 
   *  if this is a nested row (i.e. at least one of its cells is a TableCell) AND this.rowspan > 1
   *    return true
   *  else
   *    return false  
   */
  public boolean isNestedWithMultipleRowsSpan() {
    return (isNested() && getProperty().getIntegerValue(RowSpan, 0) > 1);
  }
  
  void finalise() {
    // finalise each cell
    int h, rowH = 0;
    for (Cell c : this) {
      c.finalise();
      
      //w = c.getProperty().getIntegerValue(MinWidth, 0);
      h = c.getProperty().getIntegerValue(PreferredHeight, 0);
      if (h > rowH) rowH = h;
    }
    
    // compute row's height and preferred height to the max of all cell's min heights
    // row's width is not used (same as table's width)
    setProperty(PreferredHeight, rowH,
        Height, rowH
        );
  }
}