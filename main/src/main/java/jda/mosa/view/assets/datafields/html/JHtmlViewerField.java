package jda.mosa.view.assets.datafields.html;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.datafields.JBindableField;

/**
 * 
 * @overview
 *  A {@link JBindableField} that has a {@link JEditorPane} component for displaying the content of the Html file 
 *  that is encapsulated by the value of this field. 
 *  
 * @author dmle
 */
public class JHtmlViewerField<C> extends JBindableField<C> {
  
  // the display component
  private JEditorPane textPane;

  // bounded
  public JHtmlViewerField(DataValidator validator, Configuration config, C val,
      JDataSource dataSource, DAttr domainConstraint,
      DAttr boundConstraint, Boolean editable, Boolean autoValidation)
      throws ConstraintViolationException {
    super(validator, config, val, dataSource, domainConstraint, boundConstraint,
        editable, autoValidation);
  }

  // unbounded
  public JHtmlViewerField(DataValidator validator, Configuration config, C val, 
      DAttr domainConstraint, Boolean editable, Boolean autoValidation) {
    super(validator, config, val, null, domainConstraint, null, editable, autoValidation);
  }

  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    textPane = new JEditorPane();
    //textPane.setEditable(false);
    textPane.setContentType("text/html;charset=utf-8");

    //textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

    // v3.2c: set editability and support hyperlink handling
    boolean editable = isEditable();
    if (!editable) {
      textPane.setEditable(editable);
      
      textPane.addHyperlinkListener(new HyperlinkListener() {
        @Override
        public void hyperlinkUpdate(HyperlinkEvent e) {
          if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            handleHyperLinkActivated(e);
          }
        }
      });
    }
    
    // v5.1c: display = textPane;
    setGUIComponent(textPane);

    JScrollPane scrollable = new JScrollPane(textPane);
    
    // v2.7.4: set width, height
    Dimension dim = getConfiguredDimension();
    
    if (dim != null) {
      scrollable.setPreferredSize(dim);
    }
    
    return scrollable;
  }
  
  @Override
  protected void initLayout(JComponent displayComp) {
    /* use GridBagLayout (displayComp, labelIndi): 
     *  displayComp: can be resized vertically and horizontally to fill all the extra spaces 
     *    when the object form containing this field is resized.
     *  labelIndi: only resizable vertically to fill all extra space
     */
    
    // use grid layout
    GridBagLayout layout = new GridBagLayout(); setLayout(layout);
    GridBagConstraints c = new GridBagConstraints();
    
    // display component: (0,0), resizable both horizontally and vertically to fill all extra spaces
    c.weightx = 1; // all extra horiz.space for display component 
    c.weighty = 1; // all extra vertical space 
    c.fill = GridBagConstraints.BOTH; 
    c.gridx = 0; c.gridy = 0; 
    add(displayComp, c);
    
    // label indicator: (1,0) & only resizable vertically to fill all extra space
    c.fill = GridBagConstraints.VERTICAL; // only fills vertically 
    c.weightx = 0; // no extra horiz. space for label 
    c.weighty = 1; // all extra vertical space 
    c.gridx = 1; c.gridy = 0; 
    
    JComponent labelIndi = getLabelIndicator();
    add(labelIndi, c);
  }

  @Override
  public void deleteBoundedData() {
    JDataSource dataSource = getDataSource(); // v5.1c
    
    if (dataSource != null) {
      /* 
       * clear the data objects that have been loaded via the binding (without removing the binding)
       */
      setValueDirectly(null); // v5.1c: value = null;
      //v5.1c: validated = false;
      setIsValidated(false);
    }    
  }

  @Override
  protected void loadBoundedData() throws NotPossibleException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void setDisplayValue(final Object dispVal) {
    //TODO: support dispVal as File or String
    // if dispVal is File then load file, else set text content from dispVal
    
    if (dispVal instanceof File) {
      setContentFile((File) dispVal);
    } else {
      String content = (String) dispVal;
      setNormalContent(content);
      
      // use this for large (complex) Html content
      // setLargeContent(content);      
    }
    
    //  v2.7.4: uncomment this if this field is bounded and editable
//    if (!validated) validated = true;
//    updateGUI(false);

  }
  
  /**
   * @requires 
   *  if e.getURL() points to a local anchor then that anchor definition must be written 
   *  using HTML tag <tt>a</tt> (e.g. &lt;a name="..."&gt;...&lt;/a&gt;)
   * 
   * @effects 
   *  Handle event <tt>e</tt> which was fired when user clicks a hyperlink on {@link #textPane} 
   * @version 3.2c
   */
  private void handleHyperLinkActivated(HyperlinkEvent e) {
    URL targetURL = e.getURL();
    if (targetURL == null)
      return; // no target
    
    String targetPath = targetURL.getPath();
    String currentPath = textPane.getPage().getPath();
    //String desc = e.getURL().getRef();
    // debug
    //System.out.println("JHtmlViewerField: hyperlink desc: " + desc);
    
    // if target is to a local anchor then move to it, else load the target in to textPane
    if (targetPath.equals(currentPath)) {
      // same path, could be a local anchor
      String ref = targetURL.getRef();
      if (ref != null) {
        // local anchor
        textPane.scrollToReference(ref);
      }          
    } else {
      // a separate URL: load it
      //TODO: does this work if this URL also has an anchor?
      setContentAsURL(e.getURL());
    }
  }
  
  /**
   * 
   * @effects 
   *  sets content of {@link #textPane} from <tt>url</tt>
   *  
   * @version 3.2c
   */
  private void setContentAsURL(URL url) throws NotPossibleException {
    try {
      textPane.setPage(url);
    } catch (Throwable t) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_DISPLAY_URL, 
          new Object[] {url.toString()});
    }          
  }
  
  private void setContentFile(File file) throws NotPossibleException {
    try {
      // use this to force reload document if file.url is same as the one being displayed on this
      Document doc = textPane.getDocument();
      doc.putProperty(Document.StreamDescriptionProperty, null);
      
      textPane.setPage(file.toURI().toURL());
      
//      // v3.2c: move to begining
//      if (textPane.getCaretPosition() > 0) {
//        textPane.setCaretPosition(0);
//      }
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_DISPLAY_URL, 
          new Object[] {file.getPath()});
    }
  }

  private void setNormalContent(String content) {
    textPane.setText(content);
  }
  
  private void setLargeContent(String content) {
    StringReader reader = new StringReader(content);
    //textPane.setText(content);
    Object desc = null;
    try {
      textPane.read(reader, desc);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
