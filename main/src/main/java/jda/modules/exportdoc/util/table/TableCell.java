package jda.modules.exportdoc.util.table;

import static jda.modules.exportdoc.util.table.Table.Prop.*;

public class TableCell extends Cell<Table> {
  public TableCell(Table val) {
    super(val);
  }
  
  @Override
  public void finalise() {
    int w, h;
    
    Table v = getVal();
    // finalise this table
    v.finalise();
    
    int cellSpacing = getProperty().getIntegerValue(MarginCell, 0);

    w = v.getProperty().getIntegerValue(PreferredWidth, 0) + 2*cellSpacing;
    h = v.getProperty().getIntegerValue(PreferredHeight, 0) + 2*cellSpacing;
    
    setProperty(
        PreferredWidth, w,
        PreferredHeight, h
        );
  }

  @Override
  public TableCell clone() {
    // this is an expensive operation, so just return itself!!
    return this;
  } 
}


