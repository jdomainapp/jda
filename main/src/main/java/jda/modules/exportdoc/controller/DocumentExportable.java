package jda.modules.exportdoc.controller;

import java.awt.Dimension;
import java.util.Iterator;

import jda.modules.dodm.DODMBasic;
import jda.modules.exportdoc.util.table.Cell;
import jda.modules.exportdoc.util.table.Row;
import jda.modules.exportdoc.util.table.Table;
import jda.util.properties.PropertySet;

/**
 * @overview
 *  Represents a view that has <b>special</b> export requirements.
 *  
 * <p>Note: for views that do not require special export requirements, the generic export function of the 
 * export module is applied. 
 *  
 * @author dmle
 */
public interface DocumentExportable {

  /**
   * @effects 
   *  create and return a <tt>Table</tt> whose view styles are defined based on 
   *  <tt>this, pringcfg, pgSize</tt> and whose data rows are defined from <tt>buffer</tt>.
   *  if <tt>withHeaders = true</tt> then also create the header row for the table.
   *  
   *  <p>If <tt>buffer = null</tt> the table has one empty row.
   *  
   *  <p>Note: This method must never return <tt>null</tt>.
   * @param docBuilder TODO
   */
  Table<Row<Cell>> export(DocumentBuilder docBuilder, DODMBasic schema, Iterator buffer, 
      PropertySet printCfg, Dimension pgSize, boolean withHeaders);

}
