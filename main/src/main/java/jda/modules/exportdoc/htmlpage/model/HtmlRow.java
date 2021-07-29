package jda.modules.exportdoc.htmlpage.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @overview 
 *  Represents a data row in a data document, whose content is generated from a row in the table model of the document. 
 *   
 * @author dmle
 */
public class HtmlRow {
  private int tableId;
  private StringBuffer buffer;
  private List<HtmlCell> cells;
  private int height;
  
  public HtmlRow(int tableId) {
    this.tableId = tableId;
  }

  public void addCell(HtmlCell htmlCell) {
    if (cells == null) cells = new ArrayList<>();
    cells.add(htmlCell);
  }

  public int getTableId() {
    return tableId;
  }

  public int getHeight() {
    return height;
  }
  
  public void setHeight(int height) {
    this.height = height;
  }

  public String toString() {
    if (buffer == null) {
      // generate buffer
      buffer = new StringBuffer();
      if (cells != null) {
        for (HtmlCell cell : cells) {
          buffer.append(cell.toString());
        }
      }
    }
    
    return buffer.toString();
  }
}