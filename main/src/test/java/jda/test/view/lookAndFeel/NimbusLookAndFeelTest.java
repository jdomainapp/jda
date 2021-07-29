package jda.test.view.lookAndFeel;

import java.awt.Font;
import java.io.ByteArrayInputStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.GUIToolkit.LookAndFeel;

public class NimbusLookAndFeelTest {
  public static void main(String[] args) {
    GUIToolkit.initLookAndFeel(LookAndFeel.Nimbus);

    JFrame f = new JFrame("Nimbus...");
    
    JTextArea props = new JTextArea();
    f.add(new JScrollPane(props));
    
    StringBuffer guiDefaults = new StringBuffer();
    
    GUIToolkit.printUIDefaults(guiDefaults);
    
    Font font  = new Font("courier new", Font.PLAIN, 12);
    
    props.setFont(font);
    props.setText(guiDefaults.toString());
    
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    f.setSize(1000, 600);
    f.setVisible(true);
    //f.pack();
  }
}
