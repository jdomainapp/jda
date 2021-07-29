package jda.mosa.view.assets.tables;

import javax.swing.table.TableCellEditor;

import jda.modules.dcsl.syntax.DAttr;

/**
 * An extended <code>TableCellEditor</code> that enables the target table to 
 * set the current value and and reset the values of the editor. 
 * 
 *  <p>This interface is used to create a <code>DataCellEditor</code> of a <code>JDataField</code>, 
 *  which is used in <code>JDataTable</code>.
 *  
 * @author dmle
 *
 */
public interface UpdatableCellEditor extends TableCellEditor {
  public void setCellEditorValue(Object val);
  
  public void reset();
  
  public DAttr[] getDomainConstraints();
}
