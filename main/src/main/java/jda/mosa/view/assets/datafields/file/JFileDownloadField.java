package jda.mosa.view.assets.datafields.file;

import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.StandardCopyOption;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.JObjectLabelField;

/**
 * @overview
 *  A {@link JObjectLabelField} that has a label for displaying the path of a {@link File} and enables the user
 *  to click on the label to save the file on the local hard drive.  
 *  
 *  <p>This 'mimicks' the downloading action because the {@link File} is not actually loaded remotely. Instead, 
 *  the {@link File} is already loaded before hand and is recorded as the value of this field.
 *  
 *  <p>The {@link File} can be the value of a bounded attribute.
 *  
 * @author dmle
 */
public class JFileDownloadField<C> extends JObjectLabelField {

  private JFileChooser fileChooser;
  private static ImageIcon imageIcon;
  
  /**
   * Use this for non-bounded image field
   */
  public JFileDownloadField(DataValidator validator, Configuration config,  
      C val,
      DAttr dconstraint, Boolean editable, Boolean autoValidation) throws ConstraintViolationException {
    this(validator, config, val, 
        null,   // no data source 
        dconstraint, 
        null,   // no data source
        editable, autoValidation);
  }
  
  /**
   * Use this for bounded image field.
   */
  public JFileDownloadField(DataValidator validator, Configuration config, 
      C val,
      JDataSource dataSource, // the data source to which this field is bound  
      DAttr dconstraint, 
      DAttr boundConstraint, Boolean editable, Boolean autoValidation) throws ConstraintViolationException {
    // always non-editable
    super(validator, config, val, dataSource, dconstraint, boundConstraint, false, autoValidation);
  }

  @Override
  protected ImageIcon getImageIcon() {
    if (imageIcon == null) {
      imageIcon = GUIToolkit.getImageIcon("download.gif", "download");
    }
    
    return imageIcon;
  }
  
  /**
   * @effects
   *  return {@link File}.name
   */
  /* (non-Javadoc)
   * @see domainapp.basics.view.datafields.JObjectLabelField#getObjectLabel()
   */
  @Override
  protected String getObjectLabel() {
    C value = (C) getValueDirectly();
    return ((File) value).getPath(); //Name();
  }

  /**
   * @effects
   *  ensure that <tt>this.value</tt> is a {@link File}
   */
  /* (non-Javadoc)
   * @see domainapp.basics.view.datafields.JObjectLabelField#validateObjectType()
   */
  @Override
  protected void validateObjectType() throws ConstraintViolationException {
    C value = (C) getValueDirectly();
    
    if (!(value instanceof File))
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, new Object[] {value});
  }

  /**
   * @effects
   *  Displays a "Save as" dialog to allow user to save {@link File} to local hard drive
   */
  /* (non-Javadoc)
   * @see domainapp.basics.view.datafields.JObjectLabelField#handleMouseClick()
   */
  @Override
  protected void handleMouseClick(MouseEvent e) {
    
    if (getValueDirectly() == null) {
      // only handle click if value is not null
      return;
    }
    
    if (fileChooser == null) {
      fileChooser = new JFileChooser();
      fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }
    
    int opt = fileChooser.showSaveDialog(this);
    
    File dir = null;
    if (opt == JFileChooser.APPROVE_OPTION) {
      dir = fileChooser.getSelectedFile();
      // save File to dir
      File fileObj = (File) getValueDirectly();
      try {
        ToolkitIO.copyFile(fileObj.toPath(), dir.toPath(), fileObj.getName(), StandardCopyOption.REPLACE_EXISTING);
        
        updateGUI(false);
      } catch (NotPossibleException ex) {
        displayError(ex.getCode(), ex// "Failed to save file: " + fileObj
            , true, true, false);
      }
    }
  }

  /**
   * @effects
   *  if <tt>this.value != null</tt> 
   *    change this.container.icon to hand-icon
   *  else
   *    do nothing
   */
  /* (non-Javadoc)
   * @see domainapp.basics.view.datafields.JObjectLabelField#handleMouseMoved()
   */
  @Override
  protected void handleMouseEntered(MouseEvent e) {
    if (getValueDirectly() != null) {
      setAncestorWindowCursor(HAND_CURSOR);
    }
  }

  /**
   * @effects
   *  reset the icon of <tt>this.container</tt> to default (if not already)
   */
  /* (non-Javadoc)
   * @see domainapp.basics.view.datafields.JObjectLabelField#handleMouseExited(java.awt.event.MouseEvent)
   */
  @Override
  protected void handleMouseExited(MouseEvent e) {
    setAncestorWindowCursor(DEFAULT_CURSOR);    
  }
}
