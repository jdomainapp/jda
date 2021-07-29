package jda.modules.exportdoc.htmlpage.model;

import jda.modules.exportdoc.util.table.Cell;

public class HtmlCell {
  
  private Cell dataCell;
  private String columnNo;
  private boolean withCellStyles;
  private StringBuffer buffer;

  public HtmlCell(Cell dataCell, String columnNo, boolean withCellStyles,
      StringBuffer buffer) {
    this.dataCell = dataCell;
    this.columnNo = columnNo;
    this.withCellStyles = withCellStyles;
    this.buffer=buffer;
  }
  
  
  public Cell getDataCell() {
    return dataCell;
  }


  public String getColumnNo() {
    return columnNo;
  }


  public boolean isWithCellStyles() {
    return withCellStyles;
  }


  public StringBuffer getBuffer() {
    return buffer;
  }


  public String toString() {
    return buffer.toString();
  }
}