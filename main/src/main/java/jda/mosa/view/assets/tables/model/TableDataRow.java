package jda.mosa.view.assets.tables.model;

import java.awt.Color;

/**
 * @overview
 *  A special interface that is used to obtain domain-specific row settings for domain objects that user 
 *  application wish to use. These settings will be used as part of table-rendering to display suitable 
 *  view for the table rows that display these objects. 
 *  
 * @author dmle
 *
 * @version 3.3 
 */
public interface TableDataRow {
  /**
   * whether or not this data row displays an aggregated object
   */
  public boolean isAggregated();
  
  /**
   * the background colour that is used for this row. Return <tt>null</tt> to use the default colour
   */
  public Color getBgColor();
  
  /**
   * the foreground colour that is used for this row. Return <tt>null</tt> to use the default colour
   */
  public Color getFgColor();
}
