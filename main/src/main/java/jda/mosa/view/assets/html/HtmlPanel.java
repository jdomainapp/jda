package jda.mosa.view.assets.html;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import jda.modules.exportdoc.controller.html.BasicHtmlDocumentBuilder;
import jda.modules.exportdoc.page.model.Page;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.panels.DefaultPanel;


/**
 * @overview
 *  Represents a panel for displaying the content of {@link BasicHtmlDocumentBuilder}
 *  
 * @author dmle
 *
 */
public class HtmlPanel extends DefaultPanel {
  
  private JEditorPane textPane;
  
  public HtmlPanel(Region cfg, ControllerBasic.DataController controller, String name, 
      JDataContainer parent) {
    super(cfg, controller, name, parent);
    
  }
  
  @Override
  public void createLayout() {
    // ignore the virtual attributes of the domain object
    // initialise this to be used to display the content
    
    setLayout(new BorderLayout());
    
    // TODO: create a JTextPane to display the content
    textPane = new JEditorPane();
    textPane.setEditable(false);
    textPane.setContentType("text/html;charset=utf-8");

    //textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

    JScrollPane scrollable = new JScrollPane(textPane);

    add(scrollable);
  }
  
  @Override
  public JComponent[] getComponents(Collection attributes) {
    return new JComponent[] {textPane};
  }
  
  @Override
  public void update(Object obj) {
    // display the document content 
    
    final Page doc = (Page) obj;
    
    // display content on the text pane
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        String content = doc.getContentString();
        textPane.setText(content);
      }
     });

    /*for testing with a new template file 
    final String docFile = 
        HtmlPanel.class.getResource("DefaultHtml.html").getPath();
    File fileObj = new File(docFile);
    
    try {
      final URL fileURL = fileObj.toURI().toURL();

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            textPane.setPage(fileURL);

            // force reload
            Document document = textPane.getDocument();
            document.putProperty(Document.StreamDescriptionProperty, null);
            
          } catch (IOException e) {
            System.err.println(HtmlPanel.class.getSimpleName()+".update(): failed to read Html file: " + docFile);
            e.printStackTrace();
          }
        }
      });

    } catch (MalformedURLException e) {
      System.err.println(HtmlPanel.class.getSimpleName()+".update(): failed to read Html file: " + docFile);
      e.printStackTrace();
    }
    */
    
  }
  
}
