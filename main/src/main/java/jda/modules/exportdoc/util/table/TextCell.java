package jda.modules.exportdoc.util.table;

import static jda.modules.exportdoc.util.table.Table.Prop.*;

public class TextCell extends Cell<String> {
  
  public static final String EmptyString = "";
  
  public static final TextCell EmptyCell = new TextCell(EmptyString);

  public TextCell(String val) {
    super(val);
  }
  
  @Override
  public void finalise() {
    // use the pre-defined width, height + cell spacing
    int cellSpacing = getProperty().getIntegerValue(MarginCell, 0);

    int prefW = getProperty().getIntegerValue(PreferredWidth, 0);
    int prefH = getProperty().getIntegerValue(PreferredHeight, 0);
    
    prefW += 2*cellSpacing;
    prefH += 2*cellSpacing;

    setProperty(
        PreferredWidth, prefW, 
        PreferredHeight, prefH
        );
  }

  @Override
  public TextCell clone() {
    return new TextCell(getVal());
  } 
}

