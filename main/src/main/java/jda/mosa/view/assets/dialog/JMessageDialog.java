package jda.mosa.view.assets.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.ApplicationException;
import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.io.ToolkitIO;

/**
 * @overview 
 *  A sub-type of {@link #JDialog} that contains a panel for displaying message and 
 *  its details. The message is displayed at the top, the details are displayed in a text area below it. 
 *  This text area is invisible by default but can be made visible by the user clicking on the 
 *  "Details" label.
 *  
 * @author dmle
 *
 * @version 3.1
 */
public class JMessageDialog extends JDialog {
  private static final Border BORDER_LABEL_HIGHLIGHT = BorderFactory.createLineBorder(Color.ORANGE, 2);
  private static final Border BORDER_LABEL_NORMAL = BorderFactory.createCompoundBorder(
      BorderFactory.createEmptyBorder(2,2,0,0),
      BorderFactory.createMatteBorder(0,0,2,0,Color.ORANGE));
  

  //private static final int MESSAGE_MAX_SIZE = 60;

  protected static final String EXPAND_TEXT = "\u270e \u2026";  // pencil with dots
  protected static final String COLLAPSE_TEXT = "\u270e";       // pencil without dots
  private static final String NL = "\n";
  private static final String BULLET = "\u2022";

  /**
   * @overview
   *  A mouse handler for the detailed label 
   *  
   * @author dmle
   */
  private class LabelMouseHandler extends MouseAdapter {
    
    private JLabel label;
    private JComponent comp;
    
    public LabelMouseHandler(JLabel label, JComponent comp) {
      this.label = label;
      this.comp = comp;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
      // 
      if (!comp.isVisible()) {
        comp.setVisible(true);
        label.setText(COLLAPSE_TEXT);
      } else { 
        comp.setVisible(false);
        label.setText(EXPAND_TEXT);
      }
      
      JMessageDialog.this.pack();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      // highlight
      label.setBorder(BORDER_LABEL_HIGHLIGHT);
    }

    @Override
    public void mouseExited(MouseEvent e) {
      // undo highlight
      label.setBorder(BORDER_LABEL_NORMAL);
    }
  } // end LabelMouseHandler

  private static JMessageDialog defaultDialog;
  private static Map<Component,JMessageDialog> dialogCache = new HashMap();

  private JPanel detailedPanel;
  private JTextArea messageArea;
  private JTextArea detailedArea;
  private JLabel detailsLabel;
  private JScrollPane scrollableDetailsArea;
  private boolean confirmation;
  private static ImageIcon defErrorIcon;
  
  /**
   * @requires 
   * frame != null /\ 
   * title != null /\
   * message != null 
   */
  private JMessageDialog(Frame frame, Component locationComp, String title,
      ImageIcon errorIcon, 
      String message, Throwable throwable
      , boolean confirm) {    
    super(frame, title,
        // modal
        // v3.2: make modaless: true
        false
        );

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        // reset 
        confirmation = false;
        updateOnHidding();
      }
    });
    
    // Error icon
    if (errorIcon == null) {
      // use default
      errorIcon = getDefaultErrorIcon();
    }
    
    JLabel errorLabel = new JLabel(errorIcon);
    errorLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
    
    // create the message component: either a label or a text area, depending on the message size
    // use text area
    messageArea = new JTextArea(message, 4, 50);
    messageArea.setLineWrap(true);
    messageArea.setWrapStyleWord(true);
    messageArea.setEditable(false);
    //vertCentraliseAreaText(messageArea);

    // make text area same bg as the frame (needed for Nimbus LAF)
    Color bg = frame.getBackground();
    Color bg1 = null;
    if (bg != null)
      bg1 = new Color(bg.getRed(), bg.getGreen(), bg.getBlue());
    messageArea.setBackground(bg1);
    
    JScrollPane scrollMessage = new JScrollPane(messageArea);
    scrollMessage.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
    Dimension prefSize = scrollMessage.getPreferredSize();
    scrollMessage.setMinimumSize(new Dimension(prefSize));
    
    // create button panel: 
    // if confirm = true create Yes/No buttons; otherwise create an OK button which hides dialog on clicked
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 5));
    
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
    if (!confirm) {
      JButton ok = new JButton("OK");
      ok.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          JMessageDialog.this.setVisible(false);
          updateOnHidding();
        }
      });
      buttonPanel.add(ok);
    } else {
      JButton yes = new JButton("Yes");
      yes.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          confirmation = true;
          JMessageDialog.this.setVisible(false);
          updateOnHidding();
        }
      });
      
      JButton no = new JButton("No");
      no.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          confirmation = false;          
          JMessageDialog.this.setVisible(false);
          updateOnHidding();
        }
      });
      buttonPanel.add(yes);
      buttonPanel.add(no);
    }
    
    // create message content panel use grid layout
    detailedPanel = new JPanel(new GridBagLayout());
    detailedPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    
    GridBagConstraints c = new GridBagConstraints();

    // error icon
    c.weightx = 0; // all extra horiz.space for display component 
    c.weighty = 0; // no extra vertical space 
    c.fill = GridBagConstraints.NONE; 
    c.gridx = 0; c.gridy = 0;
    //c.gridheight = GridBagConstraints.REMAINDER;
    c.anchor = GridBagConstraints.WEST;
    detailedPanel.add(errorLabel, c);
    
    // message label: (0,0) & only resizable horizontally to fill all extra space
    c.fill = GridBagConstraints.HORIZONTAL; // only fills horz. 
    c.anchor = GridBagConstraints.WEST;
    c.weightx = 1; // all extra horiz. space for label 
    c.weighty = 0; // no extra vertical space 
    c.gridx = 1; c.gridy = 0; 
    c.gridwidth = GridBagConstraints.REMAINDER; // i.e. spans entire row
    c.gridheight = 1; // default
    detailedPanel.add(scrollMessage, c);
    
    if (throwable != null) {
      String stackTrace = ToolkitIO.getStackTrace(throwable, "utf-8");
      String[] appSpecificCauses = Toolkit.getCauses(throwable, 
          ApplicationRuntimeException.class, ApplicationException.class);
      
      // append application-specific causes to message area
      if (appSpecificCauses != null) {
        addCauseMessages(appSpecificCauses);
      }
      
      initDetailedArea(stackTrace);
    }
    
    // button panel
    c.weightx=0; c.weighty=0; c.fill = GridBagConstraints.NONE; c.anchor = GridBagConstraints.CENTER; 
    c.gridx=0; c.gridy=3; c.gridheight = 1; c.gridwidth=GridBagConstraints.REMAINDER;
    detailedPanel.add(buttonPanel, c);
    
    // add to content pane
    add(detailedPanel, BorderLayout.CENTER);

    pack();
    setLocationRelativeTo(locationComp);
  }

  /**
   * @requires 
   *  appSpecificCauses != null
   * @modifies
   *  {@link #messageArea}
   *  
   * @effects 
   *  add each String in <tt>appSpecificCauses</tt> to {@link #messageArea}
   */
  private void addCauseMessages(String[] appSpecificCauses) {
    //  int numRows = messageArea.getRows();
    //  int newRows = appSpecificCauses.length - (numRows - messageArea.getLineCount()); 
    //  if (newRows > 0) {
    //    // add new rows to accommodate
    //    messageArea.setRows(numRows+newRows);
    //  }
    int index = 0;
    String currText = messageArea.getText();
    for (String cause : appSpecificCauses){
      if (index > 0 || 
        ((currText != null && !currText.equals("")))) {
          // append NL 
        messageArea.append(NL);
      }
      messageArea.append(BULLET +  " " + cause);
      index++;
    }  
  }

  private static ImageIcon getDefaultErrorIcon() {
    if (defErrorIcon == null) {
      String fileName = "resources/error.png";
      URL imageURL = JMessageDialog.class.getResource(fileName);
      if (imageURL != null) {
        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
        Image img = tk.getImage(imageURL);
        defErrorIcon = new ImageIcon(img, null);
      }
    }
    
    return defErrorIcon;
  }

//  /**
//   * @effects 
//   *  current text of <tt>textArea</tt> does not take up all rows, adjust it so that it appears vertically aligned in the 
//   *  area
//   */
//  private void vertCentraliseAreaText(JTextArea textArea) {
//    // if current text does not take up all rows, adjust it so that it appears vertically aligned in the 
//    // area
//    //TODO: fix this
//    int rows = textArea.getRows();
//    int lines = textArea.getLineCount();
//    if (lines < rows) {
//      // text does not take up all rows
//      int diff = rows - lines;
//      int linesBefore = (int) (diff / 2);
//      if (linesBefore > 0) {
//        // insert extra lines
//        for (int i = 0; i < linesBefore; i++) {
//          textArea.insert("\n", 0);
//        }
//      }
//    }
//  }

  /**
   * @effects 
   *  update this before being made invisible
   */
  private void updateOnHidding() {
    // hide detailed area (if any)
    if (scrollableDetailsArea != null && scrollableDetailsArea.isVisible()) {
      scrollableDetailsArea.setVisible(false);
      detailsLabel.setText(EXPAND_TEXT);
    }
  }

  /**
   * @effects 
   *  initialise {@link #detailsLabel}, {@link #detailedArea}, {@link #scrollableDetailsArea}
   *   and add them to {@link #detailedPanel}
   */
  private void initDetailedArea(String txt) {
    // create the details panel: containing a "Details" label and a text area
    
    // details label
    detailsLabel = new JLabel(EXPAND_TEXT);
    Font f = detailsLabel.getFont();
    f = f.deriveFont(18.0f);
    detailsLabel.setFont(f);
    detailsLabel.setForeground(Color.RED);
    detailsLabel.setOpaque(true);
    //detailsLabel.setHorizontalAlignment(JLabel.CENTER);
    detailsLabel.setBorder(BORDER_LABEL_NORMAL);
    
    // details area
    detailedArea = new JTextArea();
    scrollableDetailsArea = new JScrollPane(detailedArea);
    // wrapping: wrap at character boundary 
    detailedArea.setLineWrap(true);
    detailedArea.setWrapStyleWord(false);
    detailedArea.setText(txt);
    detailedArea.setBorder(null);
    scrollableDetailsArea.setBorder(BorderFactory.createMatteBorder(0,0,2,0,Color.ORANGE));
    //scrollableArea.setPreferredSize(new Dimension(300,200));
    scrollableDetailsArea.setVisible(false);  // initially hidden
    
    // enable user to click on label to show/hide details
    // requires: detailsLabel.setOpaque(true);
    detailsLabel.setLabelFor(scrollableDetailsArea);
    detailsLabel.addMouseListener(new LabelMouseHandler(detailsLabel, scrollableDetailsArea));
    
    GridBagConstraints c = new GridBagConstraints();
    // details label:  (0,1), resizable horz. to fill all extra spaces
    c.weightx = 1; // all extra horiz.space for display component 
    c.weighty = 0; // no extra vertical space 
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0; c.gridy = 1;
    c.gridwidth = GridBagConstraints.REMAINDER; c.gridheight = 1; // reset to default
    c.anchor = GridBagConstraints.WEST;
    detailedPanel.add(detailsLabel, c);
    
    // detailed area:  (1,1), resizable both horizontally and vertically to fill all extra spaces
    c.weightx = 1; // all extra horiz.space for display component 
    c.weighty = 1; // all extra vertical space 
    c.fill = GridBagConstraints.BOTH; 
    c.gridx = 0; c.gridy = 2; 
    c.gridheight = 1; c.gridwidth = GridBagConstraints.REMAINDER;
    c.anchor = GridBagConstraints.CENTER; // default
    detailedPanel.add(scrollableDetailsArea, c);
  }
  
  /**
   * This method is adopted from the Java tutorial on Custom Dialog
   *  
   * @requires 
   * frame != null /\ 
   * title != null /\ 
   * message != null 
   * e != null  
   * @effects 
   *  set up and show the dialog that depends on <tt>frameComp</tt> and is 
   *  displayed relative to <tt>locationComp</tt> if it is specified or 
   *  is displayed with its top-left corner in the center of the screen if 
   *  <tt>locationComp</tt> is not specified.
   *  
   *  <p>if <tt>confirm=true</tt> and if user clicks 'Ok' return <tt>true</tt> else return <tt>false</tt>
   */
  public static boolean showDialog(Component frameComp,
                                  Component locationComp,
                                  String title,
                                  ImageIcon errorIcon, 
                                  String message,
                                  Throwable throwable, 
                                  boolean confirm) {
    ModalityType modality = ModalityType.MODELESS;
    return showDialog(frameComp, locationComp, title, errorIcon, message, throwable, confirm, modality);
  }
  
  /**
   * This method is a short-cut for {@link #showDialog(Component, Component, String, ImageIcon, String, Throwable, boolean, ModalityType)}
   * where <tt>modal</tt> is translated to two default {@link ModalityType}s. 
   *  
   * @requires 
   * frame != null /\ 
   * title != null /\ 
   * message != null 
   * e != null  
   * @effects 
   *  set up and show the dialog that depends on <tt>frameComp</tt> and is 
   *  displayed relative to <tt>locationComp</tt> if it is specified or 
   *  is displayed with its top-left corner in the center of the screen if 
   *  <tt>locationComp</tt> is not specified.
   *  
   *  <p>if <tt>confirm=true</tt> and if user clicks 'Ok' return <tt>true</tt> else return <tt>false</tt>
   *  
   *  <p>if <tt>modal = true</tt> then create a modal dialog, else create a <tt>modaless</tt> dialog
   * @version 3.2c
   */
  public static boolean showDialog(Component frameComp,
                                  Component locationComp,
                                  String title,
                                  ImageIcon errorIcon, 
                                  String message,
                                  Throwable throwable, 
                                  boolean confirm, boolean modal) {
    ModalityType modality;
    if (modal) {
      modality =  ModalityType.APPLICATION_MODAL;
    } else {
      modality = ModalityType.MODELESS;
    }
    
    return showDialog(frameComp, locationComp, title, errorIcon, message, throwable, confirm, modality);
  }
  
  /**
   * This method is adopted from the Java tutorial on Custom Dialog
   *  
   * @requires 
   * frame != null /\ 
   * title != null /\ 
   * message != null 
   * e != null  
   * @effects 
   *  set up and show the dialog that depends on <tt>frameComp</tt> and is 
   *  displayed relative to <tt>locationComp</tt> if it is specified or 
   *  is displayed with its top-left corner in the center of the screen if 
   *  <tt>locationComp</tt> is not specified.
   *  
   *  <p>if <tt>confirm=true</tt> and if user clicks 'Ok' return <tt>true</tt> else return <tt>false</tt>.
   *  
   *  <p>if <tt>modality != null</tt> displays a dialog with the specified modality, otherwise display a modaless dialog
   * @version 
   * - 3.2c: added modality
   */
  public static boolean showDialog(Component frameComp,
                                  Component locationComp,
                                  String title,
                                  ImageIcon errorIcon, 
                                  String message,
                                  Throwable throwable, 
                                  boolean confirm,
                                  ModalityType modalityType  // v3.2c
                                  ) {
    Frame frame = JOptionPane.getFrameForComponent(frameComp);
    
    JMessageDialog dialog; 
    
    if (frame == null) {
      // no owner, use default instance
      if (defaultDialog == null) {
        defaultDialog = new JMessageDialog(frame,
                              locationComp,
                              title,
                              errorIcon, 
                              message,
                              throwable, confirm);
      } else {
        // update dialog
        defaultDialog.setTitle(title);
        defaultDialog.setMessage(message);
        defaultDialog.setThrowable(throwable);
        defaultDialog.setLocationRelativeTo(locationComp);
        defaultDialog.pack();
      }
      
      dialog = defaultDialog;
    } else {
      // retrieve dialog for owner from cache
      dialog = dialogCache.get(frameComp);
      if (dialog == null) {
        dialog = new JMessageDialog(frame,
            locationComp,
            title, errorIcon, 
            message,
            throwable, confirm);
        dialogCache.put(frameComp, dialog);
      } else {
        // same owner: update dialog
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setThrowable(throwable);
        dialog.setLocationRelativeTo(locationComp);
        dialog.pack();
      }
    }
    
    // v3.2c: modality
    if (!modalityType.equals(dialog.getModalityType())) {
      // different modality
      dialog.setModalityType(modalityType);
    }
    
    // show dialog
    dialog.setVisible(true);
    
    return dialog.getConfirmation();
  }

  private boolean getConfirmation() {
    return confirmation;
  }
  
  /**
   * @effects 
   *  update this to show stack trace of <tt>e</tt>
   */
  private void setThrowable(Throwable throwable) {
    if (throwable == null && 
        scrollableDetailsArea != null) {
      
      // nullify detailed text
      detailedArea.setText(null);
      
      if (scrollableDetailsArea.isVisible()) {
        // hide detailed area
        scrollableDetailsArea.setVisible(false);
      }
      
      detailsLabel.setText(EXPAND_TEXT);
      if (detailsLabel.isVisible()) 
        detailsLabel.setVisible(false);
    } else if (throwable != null) {
      // update detailed area to show content
      String stackTrace = ToolkitIO.getStackTrace(throwable, "utf-8");
      String[] appSpecificCauses = Toolkit.getCauses(throwable, 
          ApplicationRuntimeException.class, ApplicationException.class);
      
      if (scrollableDetailsArea == null) {
        initDetailedArea(stackTrace);
      } else if (!scrollableDetailsArea.isVisible() &&  !detailsLabel.isVisible()) {
        // not visible: make it visible
        detailsLabel.setVisible(true);
      }
      
      // append application-specific causes to message area
      if (appSpecificCauses != null) {
        addCauseMessages(appSpecificCauses);
      }
      
      detailedArea.setText(stackTrace);      
    }
  }

  /**
   * @effects 
   *  update this to show <tt>message</tt>
   */
  private void setMessage(String message) {
    messageArea.setText(message);
  }
}
