package jda.mosa.view.assets.datafields.chooser;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.file.StandardCopyOption;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeListener;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;
import jda.util.events.InputHandler;

/**
 * @overview  A sub-type of {@link JChooserDataField} that opens a file open dialog to enables a user
 *            to choose a file. The selected {@link File} object is then used as the value for 
 *            this data field.
 *             
 * @author dmle
 * @version 
 * - 3.2: improved to work correctly with JObjectTable AND to support 'save-as' action when user clicks on the text field 
 */
public class JFileChooserField<C> extends JChooserDataField {
  // the chosen file
  /**v3.2: changed to private */
  private File file;
  
  // v3.2
  private JFileChooser fileChooserDialog;
  
  /** to handle mouse events (typically mouse-click) on the label. 
   * This attribute is set for an object only when change listeners are registered to 
   * the object via the {@link #addChangeListener(ChangeListener) method.
   */
  private MouseListener mouseHandler;
  
  public JFileChooserField(DataValidator validator, Configuration config, Object val, 
      DAttr dc, Boolean editable, Boolean autoValidation) {
    super(validator, config, val, dc, editable, autoValidation);
  }

  /**
   * @version 3.2
   */
  /* (non-Javadoc)
   * @see domainapp.view.datafields.chooser.JChooserDataField#setUpListener(domainapp.basics.view.datafields.JDataField.DataFieldInputHelper)
   */
  @Override
  protected void setUpListener(DataFieldInputHelper tfh) {
    super.setUpListener(tfh);
    
    JComponent display = getGUIComponent();
    // create mouse handler for handling interaction on label
    if (mouseHandler == null) {
      mouseHandler = new InputHandler() {//new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          handleMouseClick(null);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
          handleMouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
          handleMouseExited(e);
        }
        
        // TODO: add other event handling methods here if needed
      };
      display.addMouseListener(mouseHandler);
    }
  }

  @Override
  protected ImageIcon getChooserIcon() throws NotFoundException {
    return GUIToolkit.getImageIcon("open.gif", "color chooser");
  }
  
  @Override
  //v2.7.3: public void actionPerformed(ActionEvent e) {
  protected void chooseActionPerformed() {
    if (fileChooserDialog == null) {
      fileChooserDialog = new JFileChooser();
    }
    fileChooserDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
    
    int opt = fileChooserDialog.showOpenDialog(this);
    
    File f = null;
    if (opt == JFileChooser.APPROVE_OPTION) {
      f = fileChooserDialog.getSelectedFile();
      processFile(f);
      
      //v3.2: redundant (already set by processFile above)
      // file = f;
      
      /* v3.2: use new event firing
      // v2.7.2: fire state change event 
      fireStateChanged();
      */
      fireValueChanged();
    }
  }
  
  @Override
  public void setValue(Object val) {
    super.setValue(val);
    
    //if (val != null)
    updateFile(val);
  }

  @Override
  protected String getValueLabel(Object val) {
    return ((File)val).getName();
  }

  /**
   * @effects 
   *  if value is not null
   *    sets <tt>this.file = File(value.toString)</tt>
   *  else
   *    set <tt>this.file = null</tt> 
   */
  protected void updateFile(Object value) {
    // update file
    if (value != null) {
    /* v3.2: support the case value is File 
    new File(value.toString());
     */
      if (value instanceof File)
        this.file = (File) value;
      else
        this.file = new File(value.toString());
    } else
      this.file = null;
  }
  
  /**
   * @effects 
   *  process the <tt>File f</tt> that the user selected from the chooser dialog 
   */
  protected void processFile(File f) {
    if (f != null) {
      setValidatedValue(f);      
    }    
  }
  
  /**
   * @effects 
   *  return the chosen file or <tt>null</tt> if no file chosen or no file information is available.
   */
  public File getFile() {
    return file;
  }
  
  /**
   * @effects
   *  Displays a "Save as" dialog to allow user to save {@link File} to local hard drive
   */
  /* (non-Javadoc)
   * @see domainapp.basics.view.datafields.JObjectLabelField#handleMouseClick()
   */
  protected void handleMouseClick(MouseEvent e) {
    
    if (getValueDirectly() == null) {
      // only handle click if value is not null
      return;
    }
    
    if (fileChooserDialog == null) {
      fileChooserDialog = new JFileChooser();
    }
    
    fileChooserDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    
    int opt = fileChooserDialog.showSaveDialog(this);
    
    File dir = null;
    if (opt == JFileChooser.APPROVE_OPTION) {
      dir = fileChooserDialog.getSelectedFile();
      // save File to dir
      File fileObj = (File) getValueDirectly();
      try {
        ToolkitIO.copyFile(fileObj.toPath(), dir.toPath(), fileObj.getName(), StandardCopyOption.REPLACE_EXISTING);
        
        updateGUI(false);
      } catch (NotPossibleException ex) {
        displayError(ex.getCode(), ex// "Failed to save file: " + fileObj
            , true, true, false);
        ex.printStackTrace();
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
  protected void handleMouseEntered(MouseEvent e) {
    if (getValueDirectly() != null) {
      setAncestorWindowCursor(HAND_CURSOR);
    }
  }

  /**
   * @effects
   *  reset the icon of <tt>this.container</tt> to default (if not already)
   */
  protected void handleMouseExited(MouseEvent e) {
    setAncestorWindowCursor(DEFAULT_CURSOR);    
  }
  
}
