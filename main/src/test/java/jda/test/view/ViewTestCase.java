package jda.test.view;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.junit.Test;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dodm.DODMBasic;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.controller.assets.helper.DefaultDataValidator;
import jda.mosa.view.assets.datafields.JDataField;

public abstract class ViewTestCase {
  protected JFrame frame;

  protected int numComponents;
  
  private boolean stopThread = false;
  private boolean threadStopped = false;
  
  /**
   * Prepares a JFrame to display the test component <br>
   * Not to be overridden by the sub-classes
   */
  @Test
  public void createAndShowGUI() {
    // Create and set up the window.
    frame = new JFrame(this.getClass().getSimpleName());
    //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        stopThread = true;
        while (!threadStopped) {
          System.out.print(".");
          try {
            Thread.sleep(500);
          } catch (InterruptedException e1) {
          }
        }
        System.exit(0);
      }
    });

    // Create and set up the content pane.
    // JComponent newContentPane = getContent();
    // newContentPane.setOpaque(true); // content panes must be opaque
    // frame.setContentPane(newContentPane);

    JComponent comp = getContent();
    
    prepareContent(comp, numComponents);
    
    JScrollPane newContentPane = new JScrollPane(comp);
    newContentPane.setOpaque(true);

    frame.setContentPane(newContentPane);

    // Display the window.
    // frame.setSize(300, 200);
    frame.pack();
    Dimension fs = frame.getSize();
    
    Dimension deskSize = Toolkit.getDefaultToolkit().getScreenSize();
    if (fs.height > deskSize.height) {
      frame.setSize(fs.width, deskSize.height-50);
    } else if (fs.width > deskSize.width) {
      frame.setSize(deskSize.width-50, fs.height);      
    }
    
    frame.setVisible(true);
    
    // keeps it running
    while (!stopThread) {
      try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }
    threadStopped = true;
    System.out.println("END");
  }

  protected void pack() {
    Dimension dim = frame.getPreferredSize();
    frame.setSize(dim);
  }
  
  // to be overriden by the sub-classes
  protected abstract JComponent getContent();

  protected void prepareContent(JComponent comp, int numComponents) {
    if (comp instanceof JPanel) {
      JPanel panel = (JPanel) comp;
      
      ////////////////////////// layout
      // Lay out the panel.
      SpringUtilities.makeCompactGrid(panel, numComponents, 2, // rows, cols
          10, 10, // initX, initY
          6, 10); // xPad, yPad

      //panel.setSize(new Dimension(500,400));
      
    }
  }
  
  // v3.1
  protected JFrame getFrame() {
    return frame;
  }

  public static void main(String[] args) throws Exception {
    // execute the createAndShowGUI method of the specified class
    // which eventually invokes the getContent() method
    // to get the component to be displayed on a JFrame
    Class cls = ViewTestCase.class;
    if (args.length > 0) {
      String cn = args[0];
      cls = Class.forName(cn);
    }

    final Class theClass = cls;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          Object o = theClass.newInstance();
          theClass.getMethod("createAndShowGUI").invoke(o, null);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  
  protected void createLabelledComponent(JPanel panel, String label,
      JComponent comp) {
    JLabel l = new JLabel(label);
    l.setLabelFor(comp);
    panel.add(l);
    panel.add(comp);
  }
  

  protected JButton createValueButton(final JDataField f) {
    JButton b = new JButton("value...");

    b.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          Object v = f.getValue();
          if (v != null)            
            System.out.println("value: " + v + " (Class: " + v.getClass() + ")");
          else
            System.out.println("value: " + null);
        } catch (ConstraintViolationException ex) {
          System.err.println(ex.getMessage());
        }
      }
    });
    
    return b;
  }

  protected JButton createResetButton(final JDataField f) {
    JButton b = new JButton("reset value");

    b.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        f.reset();
      }
    });
    
    return b;
  }
  
  public static DataValidator getDataValidator(DODMBasic schema, Class domainClass) {
    return new DefaultDataValidator(schema, domainClass);
  }

  protected void displayMessage(String s) {
    JOptionPane.showMessageDialog(frame, s, this.getClass().getSimpleName(),  
        JOptionPane.INFORMATION_MESSAGE);
  }

  protected void displayWarning(String s) {
    JOptionPane.showMessageDialog(frame, s, this.getClass().getSimpleName(),  
        JOptionPane.WARNING_MESSAGE);
  }

  protected void displayError(Throwable t) {
    JOptionPane.showMessageDialog(frame, t.getMessage(), this.getClass().getSimpleName(),  
        JOptionPane.ERROR_MESSAGE);
    t.printStackTrace();
  }

}
