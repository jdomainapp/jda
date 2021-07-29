package jda.modules.exportdoc.util.table;

import static jda.modules.exportdoc.util.table.Table.Prop.AlignX;
import static jda.modules.exportdoc.util.table.Table.Prop.Border;
import static jda.modules.exportdoc.util.table.Table.Prop.ConstrainedWidth;
import static jda.modules.exportdoc.util.table.Table.Prop.HeaderRowsSpan;
import static jda.modules.exportdoc.util.table.Table.Prop.Height;
import static jda.modules.exportdoc.util.table.Table.Prop.MarginCell;
import static jda.modules.exportdoc.util.table.Table.Prop.MarginPage;
import static jda.modules.exportdoc.util.table.Table.Prop.PageWidth;
import static jda.modules.exportdoc.util.table.Table.Prop.PreferredHeight;
import static jda.modules.exportdoc.util.table.Table.Prop.PreferredWidth;
import static jda.modules.exportdoc.util.table.Table.Prop.RowSpan;
import static jda.modules.exportdoc.util.table.Table.Prop.TextFont;
import static jda.modules.exportdoc.util.table.Table.Prop.TopX;
import static jda.modules.exportdoc.util.table.Table.Prop.TopY;
import static jda.modules.exportdoc.util.table.Table.Prop.Width;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jda.modules.common.collection.Map;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.util.properties.PropertySet;

public class Table<T extends Row> extends ArrayList<Row> {

  public static enum TableType {
    SideTable,
    NormalTable
  };

  public static enum Prop {
    Border,
    MarginTop, MarginBottom, MarginLeft, MarginRight, MarginCell, MarginPage, 
    AlignX, AlignY,
    TopX, TopY, 
    ColSpan, RowSpan, HeaderRowsSpan, 
    MinWidth, PreferredWidth, MinHeight, PreferredHeight,
    ConstrainedWidth, ConstrainedHeight, Width, Height,
    PageWidth, 
    WrapText, 
    TextFont, TextColor,
    Visible;
  }

  public static final int DEF_CELL_MARGIN = 5;
  public static final int DEF_SPACE_BELOW = 20;
  private static final AlignmentX DEFAULT_ALIGNX = AlignmentX.Center;
  
  private int id;
  private String refId;
  
  private static int idCounter;
  
  private TableType tableType;
  
  private PropertySet printCfg; 
  
  private Row<HeaderCell> header;
  
  private int numCols;
  
  // the column-span of each column of this
  private int[] colSpans;
  
  private Map<Prop,Object> props;
  
  private Row<Cell> currRow;
  
  // keep the extension rows of this (if any) separate from other rows
  private List<Table<Row<Cell>>> extensions;
  //private boolean isNestedWithMultipleRowsSpan;
  private Boolean hasMultipleNestedRows;
  
  public Table(int numCols, TableType tableType, PropertySet printCfg) {
    idCounter++; id = idCounter;
    this.tableType = tableType;
    
    props = new Map<>();
    this.numCols = numCols;
    this.printCfg = printCfg;
    
    colSpans = new int[numCols];
  }

  public int getId() {
    return id;
  }
  

  public void setRefId(String refId) {
    this.refId = refId;
  }
  
  public String getRefId() {
    return refId;
  }

  public TableType getType() {
    return tableType;
  }

  public int getNumberOfColumns() {
    return numCols;
  }

  /**
   * @requires 
   *  header != null /\ c.colSpan is not null (for all c in header)
   * @return
   *   if header is null
   *      return 0
   *   else
   *      return the sum of the column spans of the header cells
   */
  public int getColumnsSpan() {
//    if (header != null) {
      int colSpan = 0;
//      for (HeaderCell c : header) {
//        colSpan += c.getProperty().getIntegerValue(ColSpan, 0);
//      }
      for (int i = 0; i < colSpans.length; i++) {
        colSpan += colSpans[i];
      }
      return colSpan;
//    } else {
//      // use property if specified
//      return getProperty().getIntegerValue(ColSpan, 0);
//    }
  }

  /**
   * @effects 
   *  return the value of the column-span property of the specified column or 0 if the property is not configured
   */
  public int getColumnSpan(int colIndx) {
    int sz = colSpans.length;
    if (colIndx > -1 && colIndx < sz) {
      return colSpans[colIndx];
    } else
      return 0;
  }

  /**
   * @effects 
   *  return the value of the column-span property of the specified column or 0 if the property is not configured
   */
  public void setColumnSpan(int colIndx, int span) {
    int sz = colSpans.length;
    if (colIndx > -1 && colIndx < sz) {
      colSpans[colIndx] = span;
    }
  }
  
  /**
   * @effects 
   *  if userDefinedW is not null
   *    set pre-defined width of this to userDefinedW
   *  else
   *    do nothing 
   */
  public void setUserDefinedWidth(String userDefinedW) {
    if (userDefinedW != null) {
      setProperty(Width, userDefinedW);
    } 
  }
  
  public String getWidth() {
    return getProperty().get(Width, -1)+"";
  }

  public int getAbsoluteWidth(double pageWidth) {
    String w = getProperty().get(Width, -1)+"";
    
    if (w.endsWith("%")) {
      // relative width
      int relW = (int) (Integer.parseInt(w.substring(0, w.length()-1)) * pageWidth);
      return relW;
    } else {
      // absolute width 
      return Integer.parseInt(w);
    }
  }
  
  public int getHeight() {
    return getProperty().getIntegerValue(Height, -1);      
  }

  /**
   * @requires 
   *  this is not empty
   * @return
   *   if this is empty
   *      return 0
   *   else
   *      return sum(r.property(RowSpan).value) for all r in this
   */
  public int getRowsSpan() {
    int sum = 0;
    for (Row r : this) {
      sum += r.getProperty().getIntegerValue(RowSpan, 0);
    }
    
    return sum;
  }
  
  /**
   * Header rows span is 1 if this does not have any sub-tables, or the max of all the header rows span
   * of the sub-tables of this. 
   * 
   * @effects 
   *  set header rows span of this to <tt>headerRowsSpan</tt>
   */
  public void setHeaderRowsSpan(int headerRowsSpan) {
    setProperty(HeaderRowsSpan, headerRowsSpan);
  }

  public Integer getHeaderRowsSpan() {
    return getProperty().getIntegerValue(HeaderRowsSpan, 0);
  }
  
  public float getRowHeight(int rowInd) {
    return get(rowInd).getProperty().getIntegerValue(Height, -1);
  }

  public float getHeaderFontSize() {
    if (header != null)
      return getHeaderTextFont().getSize();
    else
      return -1;
  }

  public Font getHeaderTextFont() {
    if (header != null)
      return header.get(0).getProperty().getObjectValue(TextFont, Font.class);
    else
      return null;
  }

  public int getHeaderRowHeight() {
    if (header != null)
      return header.getProperty().getIntegerValue(Height, -1);
    else
      return 0;
  }

  public boolean hasHeader() {
    return header != null;
  }
  
  public Row<HeaderCell> getHeaderRow() {
    return header;
  }

  public int getCellMargin() {
    return getProperty().getIntegerValue(MarginCell, DEF_CELL_MARGIN);
  }

  public PropertySet getPrintCfg() {
    return printCfg;
  }

  public int getSpaceBelow() {
    return DEF_SPACE_BELOW;
  }

  public boolean isLandscape() {
    //TODO
    return 
    false
    //((RegionGui)printCfg).isPrintLayoutLandscape()
    ;
  }

  public Integer getNumberOfRows() {
    return size();
  }

  public void setTopX(float f) {
    setProperty(TopX, f);
  }

  public void setTopY(float f) {
    setProperty(TopY, f);
  }

  public float getTopY() {
    return getProperty().getFloatValue(TopY, 0f);
  }
  
  public float getTopX() {
    return getProperty().getFloatValue(TopX, 0f);
  }

  /**
   * @effects
   *  if horizontal alignment of this table is specified
   *    return it as String
   *  else
   *    return {@link #DEFAULT_ALIGNX} (as String)
   */
  public String getAlignXAsString() {
    String alignX = getProperty().getStringValue(AlignX, DEFAULT_ALIGNX.getHtmlName());
    return alignX;
  }
  
  /**
   * @effects
   *  if alignX is not null
   *    set horizontal alignment of this to <tt>alignX</tt>
   *  else
   *    do nothing
   */
  public void setAlignX(AlignmentX alignX) {
    if (alignX != null)
      setProperty(AlignX, alignX.getHtmlName());
  }

  public Row<Cell> beginRow() {
    currRow = new Row<>(numCols);
    return currRow;
  }
  
  public void endRow() {
    addRow(currRow);
    currRow = null;
  }

  private void addRow(Row r) {
    super.add(r);
    
    // make this a nested table if the current row is nested
//    if (!isNestedWithMultipleRowsSpan && currRow.isNestedWithMultipleRowsSpan())
//      isNestedWithMultipleRowsSpan = true;

    if (currRow.isNested()) {
      if (hasMultipleNestedRows == null)
        hasMultipleNestedRows = Boolean.FALSE;  // one nested row
      else if (hasMultipleNestedRows == Boolean.FALSE)
        hasMultipleNestedRows = Boolean.TRUE; // multiple nested rows
    }

  }
  
  /**
   * @effects 
   *  adds n empty rows to this, taking into account nested columns
   *  (i.e. columns whose column-span is > 1)
   */
  public void addEmptyRows(int n) {
    TextCell emptyCell = TextCell.EmptyCell;
    int colIndx, colSpan;
    for (int rowIndx = 0; rowIndx < n; rowIndx++) {
      beginRow();
      //currRow.fill(emptyCell, false);
      for (colIndx = 0; colIndx < numCols; colIndx++) {
        //emptyCell.setProperty(ColSpan, getColumnSpan(colIndx));
        //addTextCell(emptyCell);
        colSpan = getColumnSpan(colIndx);
        for (int s = 0; s < colSpan; s++) addTextCell(emptyCell);
      }
      endRow();
    }
  }

  public void addHeaderCell(HeaderCell c) {
    if (header == null)
      header = new Row<>(numCols);
    
    c.setProperty(MarginCell, 
        getCellMargin()
        );
    header.add(c);
  }

  public void addTextCell(TextCell c) {
    if (currRow != null) {
      c.setProperty(MarginCell, 
          getCellMargin()
          );
      currRow.add(c);
    }
  }

  public void addImageCell(ImageCell c) {
    if (currRow != null) {
      c.setProperty(MarginCell, 
          getCellMargin()
          );
      currRow.add(c);
    }      
  }

  public void addTableCell(TableCell c) {
    if (currRow != null) {
      c.setProperty(MarginCell, 
          getCellMargin()
          );
      currRow.add(c);
    }      
  }

  public void addExtension(Table<Row<Cell>> ext) {
    if (extensions == null) extensions = new ArrayList<>();
    
    extensions.add(ext);
  }
  
  public Iterator<Table<Row<Cell>>> getExtensions() {
    if (extensions != null)
      return extensions.iterator();
    else
      return null;
  }
  
  public void setBorder(boolean b) {
    setProperty(Border, b);
  }
  
  public boolean getBorder() {
    return getProperty().getBooleanValue(Border, false);
  }
  
  public void setProperty(Object...propValPairs) {
    props.put(propValPairs);
  }
  
  public Map<Prop,Object> getProperty() {
    return props;
  }
  
  public int getColumnWidth(int col) {
    if (!isEmpty()) {
      return ((Row<Cell>) get(0)).get(col).getProperty().getIntegerValue(PreferredWidth, 0);
    } else {
      return -1;
    }
  }

//  /**
//   * @requires 
//   *  {@link #isEmpty}() = false
//   * @effects 
//   *  if this is a nested table with multiple rows span (i.e. at least one of its rows is nested and the rowspan of this row is > 1)
//   * @see {@link Row#isNestedWithMultipleRowsSpan()}
//   */
//  public boolean isNestedWithMultipleRowsSpan() {
//    return isNestedWithMultipleRowsSpan;
//  }

  /**
   * @requires 
   *  {@link #isEmpty}() = false
   * @effects 
   *  if this is a nested table with multiple nested rows (i.e. at least two of its rows are nested)
   * @see {@link Row#isNested()}
   */
  public boolean hasMultipleNestedRows() {
    return (hasMultipleNestedRows != null) ? hasMultipleNestedRows : false;
  }
  
  public void finalise() {
    if (isEmpty())
      return; // skip if empty
    
    // compute table properties: column widths, row heights

    /* compute the columns widths
     * 1/ finalise the rows
     * 
     * 2/ determine the column's preferred widths
     * 3/ determine the actual widths taking into account the page width and the ratio of the preferred widths, as follows:
     *    totalPrefW = sum(col.prefW)
     *    if (totalPrefW > pageW)
     *      reducedW = totalPrefW - pageW
     *      col.constrainedW = col.prefW - reducedW * col.prefW/totalPrefW
     *      col.width = col.constrainedW
     *    else
     *      col.width = col.prefW
     * 
     * 4/ set the table's preferred width and height 
     */
    // finalise each row
    int tablePrefH = 0;
    Row row;
    for (int i = 0; i < size(); i++) {
      row = get(i);
      if (row.isEmpty()) {
        // should not happen, but could happen due to programming mistake
        // remove this row
        remove(i);
        i--;
      } else {
        // finalise the row
        row.finalise();
        // compute table's height
        tablePrefH += row.getProperty().getIntegerValue(PreferredHeight, 0);
      }
    }
    
    // then finalise the cell widths in each column to the max of the preferred widths of each cell
    Cell c;
    int cellPrefW;
    int colPrefW = 0;
    int tablePrefW = 0, tableConsW = 0;
    for (int colInd = 0; colInd < numCols; colInd++) {
      // compute col[colInd].prefW
      for (Row<Cell> r : this) {
        if (colInd >= r.size()) continue;
        
        c = r.get(colInd);
        cellPrefW = c.getProperty().getIntegerValue(PreferredWidth,0);
        if (cellPrefW > colPrefW) 
          colPrefW = cellPrefW;
      }

      tablePrefW += colPrefW;
      
      // update the cell's preferred width to column width
      for (Row<Cell> r : this) {
        if (colInd >= r.size()) continue;

        c = r.get(colInd);
        c.setProperty(PreferredWidth, colPrefW);
      } 
    }

    // compute the columns constrained width (if needed)
    
    float pageW = getProperty().getIntegerValue(PageWidth, 0);
    
    // table's pre-defined width (if any)
    String tablePreWStr = getProperty().getStringValue(Width, "100%");
    
    // debug
    //System.out.printf("Table(%s).finalise: user-defined width = %s%n", getRefId(), tablePreWStr);
    
    if (tablePreWStr.endsWith("%")) { // relative width
      finaliseRelativeWidth(tablePreWStr, tablePrefW, tablePrefH, pageW);
    } else {  // absolute width
      finaliseAbsoluteWidth(tablePreWStr, tablePrefW, tablePrefH, pageW);
    }
    
    // debug
    //System.out.printf("Table(%s).finalise: width = %s%n", getRefId(), getProperty().getObjectValue(Width,Object.class));
    
    // finally, finalise extensions (if any)
    if (extensions != null)
      for (Table t: extensions) t.finalise();
  }
  
  private void finaliseRelativeWidth(
      String tablePreWStr,    // user-defined width 
      int tablePrefW,   // preferred width 
      int tablePrefH,   // preferred height
      float pageW       // page width
      ) {
    String tablePreWS = tablePreWStr.substring(0, tablePreWStr.length()-1);
    final float tablePreW = Float.parseFloat(tablePreWS); //(Float.parseFloat(tablePreWS)/100) * pageW;

    
    // set the columns widths using relative widths
    int constrainedW;
    // use the first row to compute
    // get the first row that has all the columns
    Row<Cell> r0 = getFirstFullRow();//get(0);
    
    Cell c;
    float colPrefW = 0;
    for (int colInd = 0; colInd < numCols; colInd++) {
      // compute col[colInd].constrainedWidth
      c = r0.get(colInd);
      colPrefW = c.getProperty().getIntegerValue(PreferredWidth,0);
      constrainedW = (int) ((colPrefW/tablePrefW)*tablePreW);
      c.setProperty(
          ConstrainedWidth, constrainedW+"%",
          Width, constrainedW +"%");
    }
    
    // update rest of the rows
    int numRows = size();
    Row<Cell> r;
    String constrainedWStr;
    for (int ri = 0; ri < numRows; ri++) {
      r = get(ri);
      if (r == r0) continue;
      
      for (int colInd = 0; colInd < numCols; colInd++) {
        // compute col[colInd].constrainedWidth
        if (colInd >= r.size()) break;

        c = r.get(colInd);
        constrainedWStr = r0.get(colInd).getProperty().getStringValue(ConstrainedWidth, null);
        c.setProperty(ConstrainedWidth, constrainedWStr,
            Width, constrainedWStr
            );
      }
    }
    
    // update table's width, height
    setProperty(
        PreferredWidth, tablePrefW,
        PreferredHeight, tablePrefH,
        ConstrainedWidth, tablePreWStr, 
        Width, tablePreWStr,
        Height, tablePrefH
    );
  }
  
  private void finaliseAbsoluteWidth(
      final String tablePreWStr,    // user-defined width 
      final int tablePreferredW,   // preferred width 
      final int tablePreferredH,   // preferred height
      final float pageW       // page width
      ) {
    final int tablePreW = (int) Float.parseFloat(tablePreWStr);

    /**
     * a number of cases:
     * (1) tablePreW = 0: table.width & cell.width are set to best fit value of 0
     * (2) tablePreW in (0,pageW]:
     *      table.width=tablePreW 
     *      if tablePreW > tablePreferredW
     *         cell.width are scalled up accordingly
     *      else 
     *         cell.width are scalled down accordingly
     * (3) tablePreW > pageW:
     *      if tablePreferredW in (0,pageW]
     *        table.width=tablePreferredW and cell.width = cell.preferredWidth (no adjustement needed)
     *      else
     *        table.width=pageW and cell.width are scalled down accordingly
     */
    int actualW, adjustedW; boolean scaleUpOrDown = true; // true = scale-up; false = scale-down
    if (tablePreW == 0) {
      // table.width & cell.width are set to best fit value of 0
      actualW = 0;
      adjustedW = 0;
    } else if (tablePreW > 0 && tablePreW <= pageW) {
      actualW = tablePreW;
      if (tablePreW > tablePreferredW) {
        scaleUpOrDown = true;  // scalled up
        adjustedW = actualW-tablePreferredW;  
      } else { // tablePreW <= tablePreferredW
        scaleUpOrDown = false; // scale down
        adjustedW = tablePreferredW - actualW;
      }
    } else {  // tablePreW > pageW
      if (tablePreferredW > 0 && tablePreferredW <= pageW) {
        // use preferred width, no adjustements
        actualW = tablePreferredW;
        adjustedW = 0;
      } else { // tablePreW, tablePreferredW > pageW
        actualW = (int)pageW;
        scaleUpOrDown=false; // scale down
        adjustedW = tablePreferredW-actualW;
      }
    }

    // now set the table and cell's widths
    Cell c;
    int cellPrefW;
    int colPrefW = 0;
    float constrainedW;
    
    if (actualW == 0) { //  best fit: set table and cell widths to 0     
      int bestFitW = 0;
      for (Row<Cell> r: this) {
        for (int colInd = 0; colInd < numCols; colInd++) {
          if (colInd >= r.size()) break;

          c = r.get(colInd);
          c.setProperty(Width, bestFitW);
        }
      }
      
      // update table's width, height
      setProperty(
          PreferredWidth, tablePreferredW,
          PreferredHeight, tablePreferredH,
          Width, bestFitW,
          Height, tablePreferredH
      );      
    } else if (adjustedW == 0) {  // use preferred width 
      // sets cells and table widths to their preferred widths
      for (Row<Cell> r: this) {
        for (int colInd = 0; colInd < numCols; colInd++) {
          if (colInd >= r.size()) break;

          c = r.get(colInd);
          cellPrefW = c.getProperty().getIntegerValue(PreferredWidth, 0);
          c.setProperty(Width, cellPrefW);
        }
      }
      
      // update table's width, height
      setProperty(
          PreferredWidth, tablePreferredW,
          PreferredHeight, tablePreferredH,
          Width, tablePreferredW,
          Height, tablePreferredH
      );      
    } else {  // scale up or down
      // use the first row to compute
      // get the first row that has all the columns
      Row<Cell> r0 = getFirstFullRow();//get(0);
      for (int colInd = 0; colInd < numCols; colInd++) {
        // compute col[colInd].constrainedWidth
        c = r0.get(colInd);
        colPrefW = c.getProperty().getIntegerValue(PreferredWidth,0);
        constrainedW = (int) (scaleUpOrDown ? 
              (colPrefW + (adjustedW * colPrefW)/tablePreferredW) : // scale up     
              (colPrefW - (adjustedW * colPrefW)/tablePreferredW))  // scale down
              ;
        c.setProperty(
            ConstrainedWidth, constrainedW,
            Width, constrainedW);
      }
      
      // update rest of the rows
      int numRows = size();
      Row<Cell> r;
      for (int ri = 0; ri < numRows; ri++) {
        r = get(ri);
        if (r == r0) continue;
        
        for (int colInd = 0; colInd < numCols; colInd++) {
          // compute col[colInd].constrainedWidth
          if (colInd >= r.size()) break;

          c = r.get(colInd);
          constrainedW = r0.get(colInd).getProperty().getIntegerValue(ConstrainedWidth, 0);
          c.setProperty(ConstrainedWidth, constrainedW,
              Width, constrainedW
              );
        }
      }
      
      // update table's width, height
      setProperty(
          PreferredWidth, tablePreferredW,
          PreferredHeight, tablePreferredH,
          ConstrainedWidth, actualW, 
          Width, actualW,
          Height, tablePreferredH
      );
    }
    
    /* OLD code
    Cell c;
    int cellPrefW;
    int colPrefW = 0;
    int tableConsW = 0;
    if (tablePreferredW > pageW) {
      // scale the columns down to fit page width
      float constrainedW;
      float reducedW = tablePreferredW - pageW;
      // use the first row to compute
      // get the first row that has all the columns
      Row<Cell> r0 = getFirstFullRow();//get(0);
      for (int colInd = 0; colInd < numCols; colInd++) {
        // compute col[colInd].constrainedWidth
        c = r0.get(colInd);
        colPrefW = c.getProperty().getIntegerValue(PreferredWidth,0);
        constrainedW = (int) (colPrefW - (reducedW * colPrefW)/tablePreferredW);
        c.setProperty(
            ConstrainedWidth, constrainedW,
            Width, constrainedW);
        
        tableConsW += constrainedW;
      }
      
      // update rest of the rows
      int numRows = size();
      Row<Cell> r;
      for (int ri = 0; ri < numRows; ri++) {
        r = get(ri);
        if (r == r0) continue;
        
        for (int colInd = 0; colInd < numCols; colInd++) {
          // compute col[colInd].constrainedWidth
          if (colInd >= r.size()) break;

          c = r.get(colInd);
          constrainedW = r0.get(colInd).getProperty().getIntegerValue(ConstrainedWidth, 0);
          c.setProperty(ConstrainedWidth, constrainedW,
              Width, constrainedW
              );
        }
      }
      
      // update table's width, height
      setProperty(
          PreferredWidth, tablePreferredW,
          PreferredHeight, tablePreferredH,
          ConstrainedWidth, tableConsW, 
          Width, tableConsW,
          Height, tablePreferredH
      );
    } else  // tablePrefW <= pageW 
      if (tablePreW > -1 && tablePreW > tablePreferredW) {
      // table has pre-defined width and this width is bigger than the actual width
      // scale the columns up (reverse of the previous case)
      float constrainedW;
      tableConsW = (int) tablePreW;
      float upW = tablePreW - tablePreferredW;
      // use the first row to compute
      // get the first row that has all the columns
      Row<Cell> r0 = getFirstFullRow();//get(0);
      for (int colInd = 0; colInd < numCols; colInd++) {
        // compute col[colInd].constrainedWidth
        c = r0.get(colInd);
        colPrefW = c.getProperty().getIntegerValue(PreferredWidth,0);
        constrainedW = (int) (colPrefW + (upW * colPrefW)/tablePreferredW);
        c.setProperty(
            ConstrainedWidth, constrainedW,
            Width, constrainedW);
      }
      
      // update rest of the rows
      int numRows = size();
      Row<Cell> r;
      for (int ri = 0; ri < numRows; ri++) {
        r = get(ri);
        if (r == r0) continue;
        
        for (int colInd = 0; colInd < numCols; colInd++) {
          // compute col[colInd].constrainedWidth
          if (colInd >= r.size()) break;

          c = r.get(colInd);
          constrainedW = r0.get(colInd).getProperty().getIntegerValue(ConstrainedWidth, 0);
          c.setProperty(ConstrainedWidth, constrainedW,
              Width, constrainedW
              );
        }
      }
      
      // update table's width, height
      setProperty(
          PreferredWidth, tablePreferredW,
          PreferredHeight, tablePreferredH,
          ConstrainedWidth, tableConsW, 
          Width, tableConsW,
          Height, tablePreferredH
      );
    } else { 
      // sets cells and table widths to their preferred widths
      for (Row<Cell> r: this) {
        for (int colInd = 0; colInd < numCols; colInd++) {
          if (colInd >= r.size()) break;

          c = r.get(colInd);
          cellPrefW = c.getProperty().getIntegerValue(PreferredWidth, 0);
          c.setProperty(Width, cellPrefW);
        }
      }
      
      // update table's width, height
      setProperty(
          PreferredWidth, tablePreferredW,
          PreferredHeight, tablePreferredH,
          Width, tablePreferredW,
          Height, tablePreferredH
      );
    }
    */
  }
  
  /**
   * @requires 
   *  {@link #isEmpty() = false}
   */
  public Row getFirstRow() {
    return get(0);
  }

  /**
   * @effects 
   *  if this is not empty
   *    return the first row of this that has the same number of columns as this.numCols
   *  
   *  <br>throws NotFoundException if could not find such a row
   */
  private Row<Cell> getFirstFullRow() throws NotFoundException {
    if (!isEmpty()) {
      for (Row<Cell> r : this) {
        if (r.size() == numCols) {
          return r;
        }
      }
    }
    
    // should not happen 
    throw new NotFoundException(NotFoundException.Code.FIRST_FULL_ROW_NOT_FOUND, "Không tìm thấy dòng có đầy đủ cột");
  }

  @Override
  public String toString() {
    if (this.isEmpty()) {
      return "[]";
    } else {
      final char NL = '\n'; final String tab = "  ";
      StringBuffer sb = new StringBuffer("Table#");
      sb.append(getId()).append(NL);
      for (Row r : this) {
        sb.append(tab).append(r).append(NL);
      }
      
      return sb.toString();
    }
  }
} // END Table
