package jda.test.view.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.junit.BeforeClass;

import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.view.assets.GUIToolkit;
import jda.test.view.ViewTestCase;
import jda.util.SwTk;

/**
 * @overview
 *
 * @author dmle
 *
 * @version 
 */
public class JOptionDialogTestCase extends ViewTestCase {

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
    
    final JButton button = new JButton("test multiple-line message");
    
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String[] mObj = {"Bạn (Trần Văn Quân) không tham gia giảng dạy trong học kì: Spring", "2,016"};
        JOptionPane infoDialogPane = new JOptionPane(mObj, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = infoDialogPane.createDialog(getFrame(), "Thông tin");
        dialog.setModal(false);
        dialog.setVisible(true);
      }
    });
    
    panel.add(button);
    
    return panel;
  }
}
