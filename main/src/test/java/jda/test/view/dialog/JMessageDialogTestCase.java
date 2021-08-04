package jda.test.view.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.junit.BeforeClass;

import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.dialog.JMessageDialog;
import jda.test.view.ViewTestCase;
import jda.util.SwTk;

public class JMessageDialogTestCase extends ViewTestCase {

  @BeforeClass
  public static void init() throws Exception {
    //config = new Configuration();
    Configuration config = SwTk.createMemoryBasedConfiguration("");
    
//    GUIToolkit.initLookAndFeel(
//        //LookAndFeel.Default
//        LookAndFeel.Nimbus
//        );
    
    GUIToolkit.initInstance(config);
  }
  
  @Override
  protected JComponent getContent() {
    JPanel panel = new JPanel();
    
    final JButton button = new JButton("test message");
    
    String ecFile = "exclamation.png";
    final ImageIcon errorIcon =  GUIToolkit.getImageIcon(ecFile, "error");
        
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // create an exception and use it to display the dialog
        long rand = 1;
        try {
          rand = System.nanoTime();
          if (rand % 2 == 1) {
            double result = 1/0;
          } else {
            Double.parseDouble("not a double");
          }
        } catch (Exception ex) {
          boolean confirmed = JMessageDialog.showDialog(getFrame(), button, 
              "Program error", errorIcon, 
              ex.getMessage(),
//              "Program error: "+ex.getMessage()+" "
//                  + "extended with multiple lines to show wrapping, and the text goes on and on for very long, long enough to make the display wrapped to multiple lines...", 
              //throwable: alternate between showing and not showing
              (rand % 2 == 1) ? ex : null,
              // confirm
              true
              );
          
          System.out.printf("Confirmed: %b%n", confirmed);
        }
      }
    });
    
    panel.add(button);
    
    return panel;
  }

}
