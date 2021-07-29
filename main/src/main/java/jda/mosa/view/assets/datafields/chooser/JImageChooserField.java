package jda.mosa.view.assets.datafields.chooser;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;

/**
 * @overview A sub-type of {@link JChooserDataField} that opens a file dialog to
 *           enables a user to choose an <b>image</b> file. The selected
 *           {@link File} object is then read as an {@link Image} object and
 *           then used as the value for this data field.
 * 
 * @author dmle
 */
public class JImageChooserField<C> extends JFileChooserField {
  private JLabel pic;

  /** constants */
  /** the height of the picture box */
  private static final int PICTURE_HEIGHT = 200;
  
  public JImageChooserField(DataValidator validator, Configuration config, Object val, 
      DAttr dc, Boolean editable, Boolean autoValidation) throws ConstraintViolationException {
    super(validator, config, val, dc, editable, autoValidation);

    if (val != null && !(val instanceof ImageIcon)) {
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_DATA_TYPE, 
          "Kiểu giá trị không đúng {0} (cần kiểu {1})", val, "ImageIcon");
    }
    
    /*v2.7: moved to createDisplayComponent
    // the current display component of this field 
    
    Dimension dispSize = display.getPreferredSize();

    // the picture box 
    pictureBox = new JScrollPane();
    pic = new JLabel();
    pic.setHorizontalAlignment(JLabel.CENTER);
    //pic.setBackground(Color.WHITE);
    if (val != null)
      pic.setIcon((ImageIcon)val);
    
    // same width as display component
//    int width, height;
//    if (val != null) {
//      width = val.getIconWidth();
//      height = val.getIconHeight();
//    } else {
//      width = dispSize.width;
//      height = PICTURE_HEIGHT;
//    }
    // fix width,height
    Dimension d = new Dimension(dispSize.width, PICTURE_HEIGHT);
    pictureBox.setPreferredSize(d);
    pictureBox.setViewportView(pic);

    
     // the picture frame contains two components: (1) a picture box to display
     // the image and (2) the original display component of this field
     
    GridBagLayout layout = new GridBagLayout();
    JPanel pictureFrame = new JPanel(layout);

    GridBagConstraints c = new GridBagConstraints();

    // the picture box
    c.weightx = 1; // no extra horiz.space 
    c.weighty = 0; // no extra vertical space
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 0;
    pictureFrame.add(pictureBox, c);

    // the remaining components are added to the row below the picture box
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0; // no extra horiz. space 
    c.weighty = 0; // no extra vert space
    c.gridy = 1;
    Component[] comps = getComponents();
    for (int i = 0; i < comps.length; i++) {
      c.gridx = i;      
      pictureFrame.add(comps[i], c);
    }

    // insert the picture frame into the position of the display component 
    add(pictureFrame, 0);

    // validate
    validate();
    */
  }

  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper dfh) {
    // initialise a panel containing a text field and a chooser button
    /**
     * the picture frame contains two components: (1) a picture box to display
     * the image and (2) the original display component of this field
     **/
    GridBagLayout layout = new GridBagLayout();

    //JPanel panel = new JPanel();
    JPanel chooserPanel = new JPanel(layout);

    GridBagConstraints c = new GridBagConstraints();

    // the display component  
    JTextField display = createTextField();
    
    // v5.1c: 
    setGUIComponent(display);
    
    // v2.7.2: add listener etc.
    setUpTextField();
    setUpListener(dfh);
    
    // the display component
    /* v2.7.3: moved to method
    // v2.7.2: get display size from the field config
      RegionDataField df = getDataFieldConfiguration();
      int width, height;
      if (df != null && df.isSizeConfigured()) {
        width = df.getWidth();
        height = df.getHeight();
        
        // make the text field the same width 
        //display.setPreferredSize(new Dimension(width, display.getPreferredSize().height));
      } else {
        width = display.getPreferredSize().width; 
        height = PICTURE_HEIGHT;
      }  
     */
    int width, height;
    Dimension configDim = getConfiguredDimension();
    if (configDim == null) {
      width = display.getPreferredSize().width; 
      height = PICTURE_HEIGHT;
    } else {
      width = (int) configDim.getWidth(); height = (int) configDim.getHeight();
    }
    
    JComponent picBox = createPictureBox(width, height);

    // the picture box
    c.weightx = 1; // no extra horiz.space 
    c.weighty = 0; // no extra vertical space
    c.fill = GridBagConstraints.NONE;
    c.ipadx = 5;
    c.gridx = 0;
    c.gridy = 0;
    chooserPanel.add(picBox, c);

    // the remaining components are added to the row below the picture box
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0; // no extra horiz. space 
    c.weighty = 0; // no extra vert space
    c.gridy = 1;
    chooserPanel.add(display, c);
    
    /*
     * the chooser button
     */
    /**v2.7.3: support 2 buttons
    JButton choose = createChooseButton();
    
    c.gridx += 1;
    chooserPanel.add(choose, c);
     */
    c.gridx += 1;
    createChooserButton(ChooserAction.Choose, chooserPanel, c);
    c.gridx += 1;
    createChooserButton(ChooserAction.Delete, chooserPanel, c);
    
    return chooserPanel;
  }
  
  protected JComponent createPictureBox(int width, int height) {
    JScrollPane pictureBox;
    /** the current display component of this field */
    
    /** the picture box */
    pictureBox = new JScrollPane();
    pic = new JLabel();
    pic.setHorizontalAlignment(JLabel.CENTER);
    //pic.setBackground(Color.WHITE);
    Object value = getValueDirectly(); // v5.1c
    if (value != null)
      pic.setIcon((ImageIcon)value);
    
    // same width as display component
//    int width, height;
//    if (val != null) {
//      width = val.getIconWidth();
//      height = val.getIconHeight();
//    } else {
//      width = dispSize.width;
//      height = PICTURE_HEIGHT;
//    }
    // fix width,height
    //int width = getFieldWidth();
    Dimension d = new Dimension(width, height);
    //pic.setSize(d);
    pictureBox.setPreferredSize(d);
    pictureBox.setViewportView(pic);
    
    return pictureBox;
  }
  
  @Override
  protected ImageIcon getChooserIcon() throws NotFoundException {
    return GUIToolkit.getImageIcon("picturechooser.gif", "picture chooser");
  }

  @Override
  protected void processFile(File f) {
    if (f != null) {
      // read the file and display on the picture box
      ImageIcon img = GUIToolkit.getImageIcon(f,null);
      
      // v2.7.2: save file path to description
      img.setDescription(f.getPath());
      
      setValidatedValue(img);
    }    
  }

  @Override
  public void setValue(Object val) throws ConstraintViolationException {
    if (val instanceof ImageIcon) {
      super.setValue(val);
      /* fixed to display image file on the txt field
      // display the image
      ImageIcon icon = (ImageIcon) val;
      pic.setIcon(icon);
      */
      ImageIcon icon = (ImageIcon) val;

      // update image file (contained in description) in parent
      String filePath = icon.getDescription();
      if (filePath != null) {
        updateFile(filePath);
      } 
      
      // display the image
      pic.setIcon(icon);

    } else if (val == null) { 
      //v2.7.3: reset();  // set icon to null
      super.setValue(null);
      pic.setIcon(null);
    } else  
      {
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
          "Dữ liệu không đúng {0}", val);
    }
  }
    
  public Object getValue() throws ConstraintViolationException {
    Object value = getValueDirectly(); // v5.1c
    if (value != null && value.equals(Nil)) {
      setValueDirectly(null); // v5.1c: value = null;
      value = null;
    }
    
    return value;
  }
  
  @Override // v3.2
  protected String getValueLabel(Object val) {
    ImageIcon img = (ImageIcon) val;
    String label = img.getDescription();
    if (label == null || label.equals(Nil)) {
      label = img.toString();
    }
    
    return label;
  }
  
  @Override
  public void reset() {
    super.reset();
    //pic.setIcon(null);
    //v3.0: nullify();
    
    ImageIcon icon = (ImageIcon) getValue();
    pic.setIcon(icon);
  }
  
  @Override
  protected void nullify() {
    super.nullify();
    pic.setIcon(null);
  }
}
