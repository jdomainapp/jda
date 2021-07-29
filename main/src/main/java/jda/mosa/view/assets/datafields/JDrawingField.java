package jda.mosa.view.assets.datafields;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.view.RegionDataField;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.drawing.Drawable;

public class JDrawingField extends JBindableField {

  private static final int DEFAULT_WIDTH = 500;
  private static final int DEFAULT_HEIGHT = 300;

  public JDrawingField(DataValidator validator, Configuration config,  
      Object val,
      JDataSource dataSource, // the data source to which this field is bound 
      DAttr dconstraint,
      DAttr boundConstraint,
      Boolean editable
      ) throws ConstraintViolationException {
    // auto-validation = true
    super(validator, config, val, dataSource, dconstraint, boundConstraint, editable, true);
    
    // value must be a Drawable object
    Object value = getValueDirectly(); // v5.1c
    if (value != null && !(value instanceof Drawable)) {
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
          "Giá trị dữ liệu không đúng: {0}, cần kiểu {1}", val, Drawable.class);
    }
  }
  
  @Override
  protected void loadBoundedData() throws NotPossibleException {
    // TODO: implement this if a data source is used
  }

  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    // display component is a drawing object
    Drawing display = new Drawing();  // v5.1c:
    setGUIComponent(display);
    
    // v2.7.3: set width and height
    RegionDataField df = getDataFieldConfiguration();
    int width, height;
    if (df != null && df.isSizeConfigured()) {
      width = df.getWidth();
      height = df.getHeight();
    } else {
      width = DEFAULT_WIDTH; 
      height = DEFAULT_HEIGHT;
    }
    
    display.setPreferredSize(new Dimension(width, height));
    
    display.setBorder(BorderFactory.createEtchedBorder());
    
    // handle user's mouse events locally 
    MouseHandler mouseHandler = new MouseHandler();
    display.addMouseListener(mouseHandler);
    display.addMouseMotionListener(mouseHandler);
    
    return display;
  }

  @Override
  public Object getValue() throws ConstraintViolationException {
    Object value = getValueDirectly(); // v5.1c
    return value;
  }

  @Override
  public void setValue(Object val) {
    if ((val == null) || !(val instanceof Drawable)) {
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
          "Giá trị dữ liệu không đúng: {0}, cần kiểu {1}", val, Drawable.class);
    }
    
    super.setValue(val);
  }
  
  @Override
  protected void setDisplayValue(Object dispVal) {
    // update drawing
    JComponent display = getGUIComponent(); // v5.1c:

    display.repaint();
    
    // v2.7.4: uncomment these if this field is bounded
//    if (!validated) validated=true;
//    updateGUI(false);
  }

// v2.7.4: not implemented
//  @Override
//  public void reset() {
//    // TODO: reset the display to its initial state
//    // means to draw the initial value state
//  }
  
  @Override 
  public void deleteBoundedData() {
    JDataSource dataSource = getDataSource(); // v5.1c
    
    if (dataSource != null) {
      //TODO: implement this if data source is used
      throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED,
          this.getClass().getSimpleName()+".clearBinding()");
    }
  }

  private class Drawing extends JComponent {
    public void paintComponent(Graphics g) {
      //TODO: clear the current drawing
      Object value = getValueDirectly(); // v5.1c
      if (value != null)
        ((Drawable)value).draw(g);
    }
  }
  
  private class MouseHandler extends MouseAdapter implements MouseMotionListener {

    @Override
    public void mouseDragged(MouseEvent e) {
      // TODO: show dragging icon
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      // TODO show tool tip text
    }
    
    public void mouseReleased(MouseEvent e) {
      // TODO: to work with mouse dragging 
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
      // update drawable with the current mouse pointer position
      Object value = getValueDirectly(); // v5.1c
      ((Drawable) value).setCurrentPosition(e.getPoint());
      
      // redraw
      JComponent display = getGUIComponent(); // v5.1c:

      display.repaint();
    }
  }
}
