package jda.mosa.view.assets;

import static jda.mosa.view.assets.GUIToolkit.LookAndFeel.Metal;
import static jda.mosa.view.assets.GUIToolkit.LookAndFeel.Nimbus;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.StyleConstants;

import jda.modules.common.exceptions.InfoCode;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentY;
import jda.mosa.view.assets.panels.card.CardPanel;
import jda.mosa.view.assets.tables.TableInputEventsHelper;

/**
 * @overview
 *  A utility class that contains shared methods for GUI-related operations. 
 *  
 * @author dmle
 */
public class GUIToolkit {
  
  private static String imageLocation;
  
  // v2.7.2
  private static Dimension screenSize;

  /**v2.7: a cache of the images used in the application */
  private static Map<String,ImageIcon> imageMap = new HashMap<String,ImageIcon>();

  private static boolean initLookAndFeel = false;

  private static boolean debug = jda.modules.common.Toolkit.getDebug(GUIToolkit.class);

  // v2.7.4
  public static enum LookAndFeel {
    Metal,
    System,
    Motif, 
    Nimbus,
    GTK, 
    Default
  };
  
  private static LookAndFeel selectedLaf;
  
  /**
   * This is the initial static initialiser, which is run once for all
   * <code>AppGUI</code> objects.
   * 
   * @effects 
   *  if look-and-feel has not been initialised
   *    sets default look-and-feel and performs other initialisation tasks
   *  else
   *    do nothing
   * @version 2.7.4
   */
  public static void initLookAndFeel() {
    if (!initLookAndFeel) {
      // L&F names: "Metal", "System", "Motif", "Nimbus", and "GTK"
      LookAndFeel[] preferLAFs = { Nimbus, LookAndFeel.System, Metal };
  
      OUTER: for (LookAndFeel laf : preferLAFs) {
        if (initLookAndFeel(laf)) {
          // supported -> done 
          break;
        }
      }
    }
  }

  /**
   * @effects 
   *  sets current look and feel to laf and update UIManager accordingly
   */
  public static boolean initLookAndFeel(LookAndFeel laf) {
    if (laf == LookAndFeel.Default) {
      //
      javax.swing.LookAndFeel currLaf = UIManager.getLookAndFeel();
      
      selectedLaf = laf;
      
      initUIManager(selectedLaf);
      
      if (debug)
        java.lang.System.out.printf("Initialised L&F: %s%n", currLaf.getName());
      
      initLookAndFeel = true;
      
      return true;
    } else {
      // find look and feel
      String lAndF;
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if (laf.name().equals(info.getName())) {
          try {
            lAndF = info.getClassName();
            UIManager.setLookAndFeel(lAndF);
            
            selectedLaf = laf;

            // v2.7.4: initialise UIManager for components
            initUIManager(selectedLaf);
            
            if (debug)
              java.lang.System.out.printf("Initialised L&F: %s%n", lAndF);
            
            initLookAndFeel = true;
            
            return true;
          } catch (Exception e) {
            // ignore if not supported
          }
        }
      }
    }

    
    return false;
  }

  /**
   * @requires
   *  selected look-and-feel is not null
   */
  public static void printUIDefaults() {
    if (selectedLaf != null) {
      UIDefaults defs = UIManager.getDefaults();
      System.out.printf("%s: UI defaults%n", selectedLaf);
      
      Object k, v;
      for (Entry<Object,Object> e : defs.entrySet()) {
        k  = e.getKey();
        v = e.getValue();
        System.out.printf("  %s\t %s%n", k, v);
      }
    }
  }
  
  public static void printUIDefaults(StringBuffer sb) {
    if (selectedLaf != null) {
      UIDefaults defs = UIManager.getDefaults();
      //System.out.printf("%s: UI defaults%n", selectedLaf);
      
      Object k, v;
      String line;
      for (Entry<Object,Object> e : defs.entrySet()) {
        k  = e.getKey();
        v = e.getValue();
        //System.out.printf("  %s\t %s%n", k, v);
        line = String.format("%100s: %s%n", k, v);
        sb.append(line);
      }
    }
  }
  
  /**
   * @effecs
   *  return the currently selected look-and-feel
   */
  public static LookAndFeel getCurrentLookAndFeel() {
    return selectedLaf;
  }
  
  /**
   * This method is used for 'special' settings for Swing components that cannot be customised through 
   * the standard setter/getter methods.
   *  
   * @effects 
   *  set customised values for <b>special</b> UI settings of Swing components 
   *  
   * @version 2.7.4
   */
  private static void initUIManager(LookAndFeel laf) {
    //TODO: improve this to generically support UI property table of the selected look-and-feel
    Object[][] propVals;
    
    if (debug)
      java.lang.System.out.println("Initialising UI properties...");
    
    if (laf == Nimbus) {
      propVals = new Object[][] { 
          /** COMBO BOX **/
//          { 
//            "nimbusBase", COLOUR_LAF_BASE//new ColorUIResource(UIManager.getColor("TextField.foreground")) 
//          },
          { 
            "nimbusDisabledText", new ColorUIResource(UIManager.getColor("TextField.foreground")) 
          },
      };
      
      String prop; Object oldVal, val;
      for (Object[] propVal : propVals) {
        prop = (String) propVal[0];
        val = propVal[1];
        oldVal = UIManager.put(prop, val);
        
        if (debug) {
          java.lang.System.out.printf("  %s = %s (old: %s)%n", prop, val, oldVal);
        }
      }
    } else {
      // assume default
      /** COMBO BOX **/
      // make default bg, fg same as text field
      UIManager.put("ComboBox.background", UIManager.getColor("TextField.background"));
      UIManager.put("ComboBox.foreground", UIManager.getColor("TextField.foreground"));
      // customise disabled bg, fg
      UIManager.put("ComboBox.disabledBackground", UIManager.getColor("TextField.background"));
      UIManager.put("ComboBox.disabledForeground", UIManager.getColor("TextField.foreground"));      
    }
  }

  /**
   * @effects initialise the GUI context with settings from <tt>appConfig</tt>
   */
  public static void initInstance(Configuration appConfig) {
    imageLocation = appConfig.getImageLocation();
  }
  
  /**
   * To be used by host components to create anonymous inner class for handling
   * mouse events to provide content for display
   * 
   * @author Duc Le
   * 
   */
  public static interface MouseContentHandler {
    /**
     * Implements this method to allow pop-up retrieve content to display
     */
    public String getContent(final TableInputEventsHelper pup, MouseEvent me);

    /**
     * Implements this method to process pop-up content
     * 
     * @param s
     * @return
     */
    public void setContent(final String s);
  }

  /**
   * To be used by host components to create anonymous inner class for handling
   * mouse events to provide content for display
   */
  public static class TableMouseHandler implements MouseContentHandler {
    private JTable tbl;
    private int[] prevPositions = { -1, -1 };

    public TableMouseHandler(JTable tbl) {
      this.tbl = tbl;
    }

    public String getContent(final TableInputEventsHelper pup, MouseEvent me) {
      return getTableContent(prevPositions, pup, tbl, me);
    }

    public void setContent(final String s) {
      // change the value of the active cell
      int row = prevPositions[0];
      int col = prevPositions[1];
      if (row > -1 && col > -1) {
        tbl.setValueAt(s, row, col);
      }
    }
  }

  /**
   * This method is used to get the string value of a selected cell of a table.
   * It then returns this value if it is longer than the selected column's
   * width.
   * 
   * @param tbl
   * @param me
   * @return
   */
  public static String getTableContent(int[] prevPositions,
      final TableInputEventsHelper pup, final JTable tbl, MouseEvent me) {
    // int row = tbl.getSelectedRow();
    // int col = tbl.getSelectedColumn();
    Point p = me.getPoint();
    int col = tbl.columnAtPoint(p);
    int row = tbl.rowAtPoint(p);

    String cellValStr = null;
    AbstractTableModel tmodel = (AbstractTableModel) tbl.getModel();

    if (row > -1 && col > -1 && col < tbl.getColumnCount()
        && row < tbl.getRowCount()) {
      // only display content if mouse position has been changed to a new cell
      int prevRow = prevPositions[0];
      int prevCol = prevPositions[1];

      try {
        Object cell = tmodel.getValueAt(row, col);
        String cellValue = cell + "";
        // measure text length
        // FontMetrics metrics =
        // host.getGraphics().getFontMetrics(host.getFont());
        // int textLength = metrics.stringWidth(text);
        int textLength = SwingUtilities.computeStringWidth(tbl.getGraphics()
            .getFontMetrics(tbl.getFont()), cellValue);
        TableColumn column = tbl.getColumn(tbl.getColumnName(col));
        int colWidth = column.getWidth();
        final int colInsets = 2 * 5; // the space between column and text

        // System.out.println("txt: " + cellValue);
        // System.out.println("length: " + textLength + "; col width:"
        // + colWidth);

        // disable pop-up if cell is not editable
        boolean editable = tbl.isCellEditable(row, col);
        pup.setEditable(editable);

        if ((prevRow == -1 && prevCol == -1)
            || (prevRow != row || prevCol != col)) {
          // System.out.println("moved from "+prevRow+","+prevCol+" to Cell " +
          // row + "," + col);
          if (cell != null) {
            if (textLength + colInsets > colWidth) {
              cellValStr = cellValue;
            }
          }

          prevPositions[0] = row;
          prevPositions[1] = col;
        } else {
          if (cell != null && textLength + colInsets > colWidth) {
            cellValStr = cell + "";
            // System.out.println(cellValStr);
          }
        }
      } catch (Exception ex) {
        cellValStr = null;
      }
    }

    return cellValStr;
  }

  /**
   * @effects if <code>colorStr</code> is a color string of the form
   *          <code>r,g,b</code> then returns a <code>Color</code> object, else
   *          if <code>colorStr = null</code> returns <code>null</code> else
   *          throws <code>NotPossibleException</code>
   */
  public static Color getColorValue(String colorStr)
      throws NotPossibleException {
    if (colorStr == null) {
      return null;
    } else {
      if (colorStr.indexOf(",") > -1) {
        String[] colorComps = colorStr.split(",");
        if (colorComps.length < 3) {
          throw new NotPossibleException(
              NotPossibleException.Code.CONFIGURATION_VALUE_ERROR,
              "Lỗi giá trị cấu hình: {0}", colorStr);
        } else {
          try {
            int r = Integer.parseInt(colorComps[0]);
            int g = Integer.parseInt(colorComps[1]);
            int b = Integer.parseInt(colorComps[2]);
            return new Color(r, g, b);
          } catch (NumberFormatException e) {
            throw new NotPossibleException(
                NotPossibleException.Code.CONFIGURATION_VALUE_ERROR,
                "Lỗi giá trị cấu hình: {0}", colorStr);
          }
        }
      } else {
        throw new NotPossibleException(
            NotPossibleException.Code.CONFIGURATION_VALUE_ERROR,
            "Lỗi giá trị cấu hình: {0}", colorStr);
      }
    }
  }


  /**
   * @effects 
   *  if val != null
   *    create a return a string of the form "r,g,b" of the three colour elements of <tt>val</tt>
   *  else
   *    return null
   */
  public static String toColorString(Color val) {
    if (val != null) {
      return val.getRed()+","+val.getGreen()+","+val.getBlue();
    } else 
      return null;
  }
  
  /**
   * @effects if <code>fontStr</code> is a font string of the form
   *          <code>name,size,style</code> then returns a <code>Font</code>
   *          object, else if <code>fontStr = null</code> returns
   *          <code>null</code> else throws <code>NotPossibleException</code>
   */
  public static Font getFontValue(String fontStr) throws NotPossibleException {
    if (fontStr == null) {
      return null;
    } else {
      if (fontStr.indexOf(",") > -1) {
        String[] fontComps = fontStr.split(",");
        if (fontComps.length < 3) {
          throw new NotPossibleException(
              NotPossibleException.Code.CONFIGURATION_VALUE_ERROR,
              "Lỗi giá trị cấu hình: {0}", fontStr);
        } else {
          try {
            String fname = fontComps[0];
            int fsize = Integer.parseInt(fontComps[1]);
            int fstyle = Integer.parseInt(fontComps[2]);
            return new Font(fname, fstyle, fsize);
          } catch (NumberFormatException e) {
            throw new NotPossibleException(
                NotPossibleException.Code.CONFIGURATION_VALUE_ERROR,
                "Lỗi giá trị cấu hình: {0}", fontStr);
          }
        }
      } else {
        throw new NotPossibleException(
            NotPossibleException.Code.CONFIGURATION_VALUE_ERROR,
            "Lỗi giá trị cấu hình: {0}", fontStr);
      }
    }
  }
  
  /**
   * @effects 
   *  if val != null
   *    create a return a string of the form "f,z,s" of the three font elements of <tt>val</tt>: 
   *    f = font-name, z = font-size, s = font-style
   *  else
   *    return null
   */
  public static String toFontString(Font val) {
    if (val != null) {
      return val.getFontName() + "," + val.getSize() + "," + val.getStyle();
    } else 
      return null;
  }

  /**
   * @effects shows a confirmation dialog on <code>parentComponent</code> with
   *          <code>title</code>, message <code>mesg</code>. If
   *          <code>args!=null</code> then parse <code>mesg</code> using
   *          <code>args</code>.
   * 
   *          If <code>OK</code> is pressed then returns <code>true</code>,
   *          otherwise returns <code>false</code>.
   * @version 
   * - 3.2: use InfoCode parameter
   */
  public static boolean confirm(
      //v3.2: LAName act,
      InfoCode mesgCode, 
      Component parentComponent,
      String title, 
      //v3.2: String mesg, 
      Object... args) {
    String mesg = "";
    if (args != null) {
      // MessageFormat f = new MessageFormat(mesg);
      //mesg = f.format(args);
      mesg = mesgCode.getMessageFormat().format(args);
    }

    int resp = JOptionPane.showConfirmDialog(parentComponent, mesg, title,
        JOptionPane.YES_NO_OPTION);

    return (resp == 0);
  }

// v3.2  
//  public static void displayMessage(Component parentComponent, String title,
//      String mesg, Object... args) {
//    if (args != null) {
//      MessageFormat f = new MessageFormat(mesg);
//      mesg = f.format(args);
//    }
//
//    JOptionPane.showMessageDialog(parentComponent, mesg, title,
//        JOptionPane.INFORMATION_MESSAGE);
//  }

  private static final int LABEL_BORDER_THICKNESS = 2;  
  
  public static final Border LABEL_BORDER = BorderFactory.createLineBorder(
      Color.GRAY, LABEL_BORDER_THICKNESS, 
      true  // rounded
      );
  
  public static final Border LABEL_EMPTY_BORDER = BorderFactory
      .createEmptyBorder(LABEL_BORDER_THICKNESS, 
          LABEL_BORDER_THICKNESS, 
          LABEL_BORDER_THICKNESS, 
          LABEL_BORDER_THICKNESS);

  public static void highlightComponentOnFocus(JComponent comp, boolean tf) {
    if (tf) {
      comp.setBorder(LABEL_BORDER);
    } else {
      comp.setBorder(LABEL_EMPTY_BORDER);
    }
  }

  public static final int PANEL_BORDER_GAP = 3;
  /** border of panel on focus */
  public static final Border PANEL_BORDER = BorderFactory.createCompoundBorder(//
      BorderFactory.createLineBorder(Color.GRAY, PANEL_BORDER_GAP), //
      BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.WHITE,
          Color.GRAY));
  /** border of panel NOT on focus */
  public static final Border PANEL_EMPTY_BORDER = BorderFactory.createEmptyBorder(
      PANEL_BORDER_GAP + 2, PANEL_BORDER_GAP + 2, PANEL_BORDER_GAP + 2, PANEL_BORDER_GAP + 2);

  /**
   * @effects draws an empty border around a panel.
   * 
   *          <p>
   *          Note: the border's width needs to be the same as the width of the
   *          coloured border that is drawn around the same panel when it is
   *          being highlighted (see method {@see MouseAdapter.highlightOnFocus}
   *          ).
   */
  public static void highlightContainerInit(JComponent comp) {
    // System.out.println("init panel " + p.getName());
    comp.setBorder(PANEL_EMPTY_BORDER);
  }

  public static void highlightContainerOnFocus(JComponent comp) {
    comp.setBorder(PANEL_BORDER);
  }
  
  /**
   * @effects 
   *  Read the content of the file specified by <tt>fileName</tt> and return it as <tt>InputStream</tt>; 
   *  or return <tt>null</tt> if file does not exist or fail to read.
   *  
   *  <br><tt>fileName</tt> can be an absolute path or a relative path to the default <tt>images</tt> directory.
   */
  public static InputStream getImageFileAsStream(String fileName) {
    String path = null;
    InputStream fins = null;
    if (fileName.indexOf('/') > -1) {
      // absolute path, use it
      path=fileName;
      
      try {
        fins = new FileInputStream(path);
      } catch (FileNotFoundException e) {
        // not found
      }
    } else {
      // assume that file is in the default images folder
      path = imageLocation + fileName;
      fins = GUIToolkit.class.getResourceAsStream(path);
    }
    
    return fins;
  }

  /**
   * @effects returns <tt>ImageIcon</tt> from file <tt>fileName</tt> stored
   *          relative to {@link #IMG_LOCATION}; throws
   *          <tt>NotFoundException</tt> if the image file could not be found.
   */
  public static ImageIcon getImageIcon(String fileName, String label)
      throws NotFoundException {
    /*v2.7: support caching 
    ImageIcon icon = null;
    if (fileName != null) {
      Image img = getImage(fileName);
      icon = new ImageIcon(img, label);
    }
    return icon;
    */
    ImageIcon icon = imageMap.get(fileName);
    
    if (icon == null) {
      // try loading it
      if (fileName != null) {
        Image img = getImage(fileName);
        icon = new ImageIcon(img, label);
        
        imageMap.put(fileName, icon);
      }
    }
    
    return icon;
  }

  /**
   * @effects returns <tt>ImageIcon</tt> from file <tt>fileName</tt> stored
   *          relative to {@link #IMG_LOCATION}; return null
   *          if the image file could not be found.
   *  @version 5.4.1         
   */
  public static ImageIcon getImageIconOptional(String fileName, String label) {
    ImageIcon icon = null;
    
    try {
      icon = GUIToolkit.getImageIcon(fileName, label);
    } catch (NotFoundException ex) {
      // ignore
    }
    
    return icon;
  }
  
  public static ImageIcon getImageIcon(File file, String label) {
    Image img = getImage(file);
    ImageIcon icon = new ImageIcon(img, label);
    return icon;
  }

  /**
   * @requires 
   * @effects returns <tt>Image</tt> from file name <tt>fileName</tt> which is either 
   * the name of the path to the image file 
   *  throws <tt>NotFoundException</tt> if the image file could not be found.
   */
  private static Image getImage(String fileName) throws NotFoundException {
    String path = null;
    Image img = null;
    if (fileName.indexOf('/') > -1) {
      // a path, use it
      path=fileName;
      Toolkit tk = Toolkit.getDefaultToolkit();
      img = tk.getImage(path);
    } else {
      // assume that file is in the default images folder
      path = imageLocation + fileName;
      URL imageURL = GUIToolkit.class.getResource(path);
      if (imageURL != null) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        img = tk.getImage(imageURL);
      } else {
        throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND,
            new Object[] {path}
            //"GUIToolkit.getImage: could not find image file " + path
        );
      }
    }
    return img;
  }

  private static Image getImage(File file) throws NotFoundException {
    Toolkit tk = Toolkit.getDefaultToolkit();
    try {
      Image img = tk.getImage(file.toURI().toURL());
      return img;
    } catch (MalformedURLException e) {
      throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND,
          "GUIToolkit.getImage: could not find image file " + file, e);
    }
  }

  /**
   * @requires 
   *  img != null /\ imgFolder != null
   * @effects 
   *  write image <tt>img</tt>, whose type is <tt>imgType</tt>, to an image file whose absolute path is <tt>filePath</tt>
   *  (if the specified file already exists then it is deleted and a new file is created).
   *    
   *  <br>throws NotPossibleException if failed to create the file.
   */
  public static void writeImageFile(ImageIcon img, ImageType imgType, String filePath) throws NotPossibleException {
    File file = new File(filePath);
    
    if (file.exists()) {
      file.delete();
    }

    jda.modules.common.io.ToolkitIO.createFileIfNotExists(file);
    
    FileOutputStream fout = null; 
    try {
      fout = new FileOutputStream(file);

      writeImage(img, imgType, fout);
    } catch (IOException e) {
      // should not happen
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, 
          e, new Object[] {"writeImageFile", filePath, (e !=null) ? e.getMessage() : ""});
    } finally {
      if (fout != null) 
        try {fout.close(); } catch (Exception e) {}
    }
  }
  
  /**
   * @overview
   *  Defines well-known image types and their extenssions
   *  
   * @author dmle
   *
   */
  public static enum ImageType {
    JPEG("jpg","jpeg"),
    GIF("gif"),
    PNG("png");
    
    private String[] knownExts;
    
    private ImageType(String...knownExtensions) {
      knownExts = knownExtensions;
    }
    
    public boolean isType(String ext) {
      if (ext == null)
        return false;
      
      for (String ex : knownExts) {
        if (ex.equalsIgnoreCase(ext)) {
          return true;
        }
      }
      
      return false;
    }
    
    public String getCommonExtension() {
      // return the first extension
      return knownExts[0];
    }
    
    public static ImageType lookUp(String ext) {
      for (ImageType type : values()) {
        if (type.isType(ext)) return type;
      }
      
      return null;
    }
  } // end ImageType
  
  /**
   * @param imgType the image extension definition (e.g. jpg, gif)
   *  
   * @effects return array <tt>byte[]</tt> of the content of the <tt>Image img</tt>, 
   *  whose type is <tt>imageType</tt>
   *  
   *  <br>Throws NotPossibleException if failed to convert.
   */
  public static byte[] imageToBytes(ImageIcon img, ImageType imgType) 
      throws NotPossibleException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    /*v2.7.3: use method
    try {
      int width = img.getIconWidth();
      int height = img.getIconHeight();
      BufferedImage bimg = new BufferedImage(
          width,
          height, 
          BufferedImage.TYPE_INT_RGB);
      Graphics2D g = bimg.createGraphics();
      g.drawImage(img.getImage(), 0, 0, null);
      ImageIO.write(bimg, imgType.getCommonExtension(), bout);

      return bout.toByteArray();
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM,
          e, new Object[]{"imageToBytes", img, e.getMessage()});
    }
    */
    writeImage(img, imgType, bout);
    return bout.toByteArray();
  }
  
  /**
   * @param imgType the image extension definition (e.g. jpg, gif)
   *  
   * @effects 
   *  write the content of the <tt>Image img</tt>, 
   *  whose type is <tt>imageType</tt>, to output stream <tt>out</tt>
   *  
   *  <br>Throws NotPossibleException if failed to do so.
   */
  public static void writeImage(ImageIcon img, ImageType imgType, OutputStream out) 
      throws NotPossibleException {
    try {
      int width = img.getIconWidth();
      int height = img.getIconHeight();
      BufferedImage bimg = new BufferedImage(
          width,
          height, 
          BufferedImage.TYPE_INT_RGB);
      Graphics2D g = bimg.createGraphics();
      g.drawImage(img.getImage(), 0, 0, null);
      ImageIO.write(bimg, imgType.getCommonExtension(), out);
      
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM,
          e, new Object[]{"writeImage", img, e.getMessage()});
    }
  }
  
//  /**
//   * @requires 
//   *  imgIcon != null  /\ imageFile != null
//   * @effects 
//   *  convert and return <tt>imgIcon</tt> whose file name is <tt>imageFile</tt> 
//   *  to a Pdf-typed {@link com.itextpdf.text.Image}.
//   *  
//   *  <p>Throws NotPossibleException if <tt>fileName</tt> is not well-formed (e.g. no extension)
//   *  or failed to convert the image.
//   */
//  public static com.itextpdf.text.Image toPdfimage(PdfWriter pdfWriter, ImageIcon imgIcon) 
//  throws NotPossibleException {
//    try {
//      com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(
//          pdfWriter, 
//          imgIcon.getImage(), 
//          1);
//      
//      return image;
//    } catch (Exception e) {
//      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONVERT_IMAGE, e,  
//          "Không thể tạo ảnh từ: {0}", imgIcon);
//    }
//  }

  
  /**
   * @effects returns <tt>Cursor</tt> from file <tt>fileName</tt> stored
   *          relative to {@link #IMG_LOCATION}; throws
   *          <tt>NotFoundException</tt> if the image file could not be found.
   */
  public static Cursor getCursor(String fileName, Point p, String label)
      throws NotFoundException {
    Toolkit tk = Toolkit.getDefaultToolkit();
    /* v2.7: support caching 
     Cursor cur = tk.createCustomCursor(getImage(fileName), p, label);
     * */
    ImageIcon icon = getImageIcon(fileName, null);
    Cursor cur = tk.createCustomCursor(icon.getImage(), p, label);

    return cur;
  }
  
  /**
   * @effects 
   *  return an array of standard <tt>Color</tt> objects in the <tt>Color</tt> class.
   */
  public static Color[] getStandardColors() {
    Color[] colors = {
      Color.RED, 
      Color.ORANGE,
      Color.BLUE,
      Color.GREEN, 
      Color.CYAN, 
      //Color.DARK_GRAY, 
      //Color.GRAY, 
      Color.LIGHT_GRAY, 
      Color.MAGENTA, 
      Color.PINK, 
      //Color.WHITE,
      Color.YELLOW,
      Color.BLACK,
    };
    
    return colors;
  }
  
  /**
   * GLOBAL CONSTANTS used by view components
   */
  /** Colours */
  public static final Color COLOUR_LIGHT_YELLOW = new Color(255, 255, 150);
  public static final Color COLOUR_LIGHT_YELLOW_2 = new Color(134,131,0);
  public static final Color COLOUR_YELLOW_3 = new Color(220,180,0);
  
  private static final Object COLOUR_LAF_BASE = new Color(51,98,140); // Nimbus default
  
  public static final Color COLOUR_FOREGROUND_DISABLED = Color.DARK_GRAY;
  public static final Color COLOUR_FOREGROUND = Color.BLACK;

  private static final char[] ASCII_LETTERS = {
    'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
    'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'
  };

  /**
   * @effects 
   *  return the computer screen size
   * @version 2.7.2
   */
  public static Dimension getScreenSize() {
    if (screenSize == null) {
      Toolkit tk = Toolkit.getDefaultToolkit();
      screenSize = tk.getScreenSize();
    }
    
    return screenSize;
  }

  /**
   * @effects 
   *  convert <tt>alignX</tt> to the equivalent <tt>SwingContants</tt> and return this.
   *  
   *  Throws IllegalArgumentException if cannot convert <tt>alignX</tt>. 
   */
  public static int toSwingAlignmentX(AlignmentX alignX) throws IllegalArgumentException {
    if (alignX == AlignmentX.Left) {
      return SwingConstants.LEFT;
    } else if (alignX == AlignmentX.Center) {
      return SwingConstants.CENTER;
    } else if (alignX == AlignmentX.Right) {
      return SwingConstants.RIGHT;
    } else {
      throw new IllegalArgumentException(GUIToolkit.class.getSimpleName()+".toSwingAlignmentX: invalid alignment X: " + alignX);
    }
  }
  
  /**
   * @effects 
   *  convert <tt>SwingContants</tt> <tt>alignX</tt> to the equivalent <tt>AlignmentX</tt> and return this.
   *  
   *  Throws IllegalArgumentException if cannot convert <tt>alignX</tt>. 
   */
  public static AlignmentX fromSwingAlignmentX(int alignX) throws IllegalArgumentException {
    switch (alignX) {
      case SwingConstants.LEFT:
      case SwingConstants.LEADING:
        return AlignmentX.Left;
      case SwingConstants.RIGHT:
      case SwingConstants.TRAILING:
        return AlignmentX.Right;
      case SwingConstants.CENTER:
        return AlignmentX.Center;
    }
    
    throw new IllegalArgumentException(GUIToolkit.class.getSimpleName()+".fromSwingAlignmentX: invalid alignment X: " + alignX);
  }
  
  /**
   * @effects 
   *  convert <tt>alignX</tt> to the equivalent <tt>SwingContants</tt> and return this.
   *  
   *  Throws IllegalArgumentException if cannot convert <tt>alignX</tt>. 
   */
  public static int toEditorPaneAlignmentX(AlignmentX alignX) throws IllegalArgumentException {
    if (alignX == AlignmentX.Left) {
      return StyleConstants.ALIGN_LEFT;
    } else if (alignX == AlignmentX.Center) {
      return StyleConstants.ALIGN_CENTER;
    } else if (alignX == AlignmentX.Right) {
      return StyleConstants.ALIGN_RIGHT;
    } else {
      throw new IllegalArgumentException(GUIToolkit.class.getSimpleName()+".toEditorPaneAlignmentX: invalid alignment X: " + alignX);
    }
  }
  
  /**
   * @effects 
   *  convert <tt>alignY</tt> to the equivalent <tt>SwingContants</tt> and return this.
   *  
   *  Throws IllegalArgumentException if cannot convert <tt>alignY</tt>. 
   */
  public static int toSwingAlignmentY(AlignmentY alignY) throws IllegalArgumentException {
    if (alignY == AlignmentY.Top) {
      return SwingConstants.TOP;
    } else if (alignY == AlignmentY.Middle) {
      return SwingConstants.CENTER;
    } else if (alignY == AlignmentY.Bottom) {
      return SwingConstants.BOTTOM;
    } else {
      throw new IllegalArgumentException(GUIToolkit.class.getSimpleName()+".toSwingAlignmentY: invalid alignment Y: " + alignY);
    }
  }
  
  /**
   * @effects 
   *  return the value of the key code constant <tt>VK_index</tt> in 
   *  the class <tt>KeyEvent</tt>, or throws NotFoundException if 
   *  no such contant exists.
   */
  public static int getNumericKeyCode(int index) throws NotFoundException {
    String constantName = "VK_"+index;
    int kc = (Integer) 
        jda.modules.common.Toolkit.getConstantObject(KeyEvent.class, constantName, int.class, true);
  
    return kc;
  }
  
  /**
   * @effects 
   *  return the value of the key code constant <tt>VK_c</tt> in 
   *  the class <tt>KeyEvent</tt>, or throws NotFoundException if 
   *  no such contant exists.
   */
  public static int getAlphabeticKeyCode(char c) throws NotFoundException {
    String constantName = "VK_"+c;
    int kc = (Integer) 
        jda.modules.common.Toolkit.getConstantObject(KeyEvent.class, constantName, int.class, true);
  
    return kc;
  }
  
  /**
   * This method reserves mnemonics in case-sensitive manner; that is for every new mnemonic character
   * that is used, the upper or lower case version of that character is also excluded for future use. 
   * 
   * @requires
   *  name != null /\ existingMnemonics != null /\ 
   *  name contains some standard ASCII characters 
   *  
   * @modifies existingMnemonics
   * 
   * @effects 
   *  find (in order from left to right) the first character <tt>c</tt> of <tt>name</tt> that is not
   *  already in <tt>existingMnemonics</tt> and return it as a mnemonic 
   *  for <tt>name</tt>. Also add <tt>c</tt> and the upper or lower case version of <tt>c</tt> 
   *  to <tt>existingMnemonics</tt>.
   *  
   *  <p>Throws NotFoundException if all characters in <tt>name</tt> are 
   *  already in <tt>existingMnemonics</tt>
   */
  public static char getMnemonicFromASCIIName(String name, Stack<Character> existingMnemonics) 
      throws NotFoundException {

    if (name == null || existingMnemonics == null) // invalid name
      return '\u0000';
    
    char mne = '\u0000';
    
    boolean found = false;
    
    /* v3.1: support ASCII char only
    if (existingMnemonics.isEmpty()) {
      mne = name.charAt(0);
      existingMnemonics.add(mne);
      found = true;
    } else {
      char[] chars = name.toCharArray();
      for (char c : chars) {
        if (!existingMnemonics.contains(c)) {
          mne = c;
          existingMnemonics.add(c);
          found = true;
          break;
        }
      }
    }
    */
    
    char[] chars = name.toCharArray();
    for (char c : chars) {
      if (isASCIILetter(c) && !existingMnemonics.contains(c)) {
        mne = c;
        existingMnemonics.add(c);
        // also add the upper | lower case version of c to the list
        if (Character.isUpperCase(c)) {
          existingMnemonics.add(Character.toLowerCase(c));
        } else {
          existingMnemonics.add(Character.toUpperCase(c));
        }
        
        found = true;
        break;
      }
    }

    if (!found) {
      throw new NotFoundException(NotFoundException.Code.MNEMONIC_NOT_FOUND,
          "Không thể tìm được ký tự phím tắt của: {0}", name);
    } else { 
      return mne;
    }
  }

  /**
   * @effects 
   *  if <tt>c</tt> is a standard ASCII letter 
   *    return <tt>true</tt>
   *  else 
   *    return <tt>false</tt>
   */
  public static boolean isASCIILetter(char c) {
    for (char a : ASCII_LETTERS) {
      if (a == c) {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * @effects <pre>
   *  If exists an {@link JInternalFrame} that contains <tt>comp</tt> in its containment hierarchy
   *    return it
   *  else if exists an {@link JFrame} that contains <tt>comp</tt> in its containment hierarchy
   *    return it
   *  else
   *    return <tt>null</tt> (i.e. <tt>comp</tt> is not contained in a Window)     
   *</pre>
   * @version 3.2
   */
  public static Container getWindowAncestor(Component comp) {
    Container iframe = SwingUtilities.getAncestorOfClass(JInternalFrame.class, comp);
    
    if (iframe != null) {
      return iframe;
    } else {
      return SwingUtilities.getWindowAncestor(comp);
    }
  }

  /**
   * @effects 
   *    if exists a {@link Container} that is either a parent or an ancestor container of <tt>comp</tt>
   *    in the containment hierarchy
   *      return it
   *    else
   *      return null
   * @version 5.2 
   */
  public static <T extends Container> T getContainerAncestor(Component comp,
      Class<T> containerType) {
    return (T) SwingUtilities.getAncestorOfClass(containerType, comp);
  }
  
  /**
   * @requires 
   *  colClass has a default constructor 
   *  
   * @effects 
   *  create and return an empty {@link Collection} whose actual type is the same as 
   *  <tt>colClass</tt>
   *  
   * @version 3.2
   */
  public static Collection newEmptyCollection(Class<? extends Collection> colClass) throws NotPossibleException {
    try {
      Collection col = colClass.newInstance();
      
      return col;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, new Object[] {colClass, ""});
    }
  }
  
  /**  
   * Convert a Java Color to equivalent HTML Color.
   *
   * @param color The Java Color
   * @return The String containing HTML Color.
   * @version 
   * 3.3: adopted from http://stackoverflow.com/questions/13285526/jtextpane-text-background-color-does-not-work
   */
  public static String getHTMLColor(Color color) {
    if (color == null) {
      return "#000000";
    }
    return "#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
  }

  /**
   * @effects 
   *  make the tab of <tt>tabGroup</tt> containing <tt>comp</tt> selected and 
   *  update tab label (etc.) accordingly  
   * @version 4.0
   */
  public static void updateTabOnComponentVisible(final JTabbedPane tabGroup,
      final Component comp) {
    int tabCount = tabGroup.getTabCount();
    
    Component tabComp;  
    // look for the tab containing comp
    for (int i = 0; i < tabCount; i++) {
      tabComp = tabGroup.getComponentAt(i);
      // need to compare using == (not equals())
      if (tabComp == comp) {
        // found the selected tab
        tabGroup.setSelectedIndex(i);
        
        // update tab title & icon etc
        updateTabTitleOnVisibilityUpdate(tabGroup, i, true);
      } else {
        // not a selected tab
        // change icon to 'closed' if not already
        updateTabTitleOnVisibilityUpdate(tabGroup, i, false);
      }
    }
  }

  /**
   * @effects 
   *   make the card containing <tt>cardComp</tt> selected and 
   *    update the corresponding control button of that card accordingly
   * @version 5.2
   */
  public static void updateCardOnComponentVisible(CardPanel cardPanel,
      Component cardComp) {
    // show the card component
    cardPanel.showCard(cardComp);
  }
  
  /**
   * @effects 
   *  update visibility status of the title component for the tab of <tt>tabGroup</tt> at <tt>index</tt>
   *  according to <tt>visible/tt>  
   * @version 4.0 
   */
  public static void updateTabTitleOnVisibilityUpdate(final JTabbedPane tabGroup, int index, boolean visibile) {
    Component tabTitleComp = tabGroup.getTabComponentAt(index);
    
    if (tabTitleComp instanceof JLabel) {
      GUIToolkit.updateContainerLabelOnVisibilityUpdate((JLabel)tabTitleComp, visibile);
    }    
  }

  /**
   * @requires 
   *  <tt>cont != null</tt>
   *  
   * @effects 
   *  Find in <tt>cont</tt> all components typed <tt>compType</tt> and return them (in the 
   *  order that they are found).
   *  
   *  If none are found then return <tt>null</tt>.
   *  
   * @version 5.2
   */
  public static <T extends Component> List<T> getComponents(Container cont,
      Class<T> compType) {
    if (cont == null) return null;
    
    Component[] comps = cont.getComponents();
    List<T> result = new ArrayList<>();
    if (comps == null || comps.length == 0) return null;
    for (Component c : comps) {
      if (compType.isInstance(c)) {
        result.add((T) c);
      }
    }
    
    if (result.isEmpty())
      return null;
    else
      return result;
  }

  /**
   * A variant of {@link #updateContainerLabelOnVisibilityUpdate(JLabel, boolean)} but works for {@link JButton}.
   * 
   * @effects 
   *  if <tt>button</tt>'s style does not match <tt>compVisible</tt>
   *  (the visibility status of the component to which <tt>button</tt> is associated)
   *    update <tt>button</tt>'s style to match
   *  else
   *    do nothing
   * @version 5.2
   *  - changed to static
   */
  public static void updateContainerLabelOnVisibilityUpdate(JButton button, boolean compVisible) {
    Font f = button.getFont();
  
    // check label's style
    boolean isItalic = f.isItalic();
  
    // if label's style does not match the visibility of component, update its font
    // otherwise do nothing
    if (!compVisible && !isItalic) {
      // container is closed
      f = f.deriveFont(f.getStyle() + Font.ITALIC);
      button.setFont(f);
      
      // v2.7: update icon
      button.setIcon(getImageIcon("containerclose.gif", null));
    } 
    /*v3.0: improved to check italic and icon separately 
    else if (compVisible && isItalic){
      // container is opened
      f = f.deriveFont(f.getStyle() - Font.ITALIC);
      label.setFont(f);
      
      // v2.7: update icon
      label.setIcon(GUIToolkit.getImageIcon("containeropen.gif", null));
    }
    */
    else if (compVisible){
      // container is opened
      if (isItalic) {
        f = f.deriveFont(f.getStyle() - Font.ITALIC);
        button.setFont(f);
      }
  
      ImageIcon openIcon = getImageIcon("containeropen.gif", null);
      
      if (button.getIcon() != openIcon) {
        // v2.7: update icon
        button.setIcon(openIcon);
      }
    }
  }

  /**
   * @effects 
   *  if <tt>label</tt>'s style does not match <tt>compVisible</tt>
   *  (the visibility status of the component to which <tt>label</tt> is associated)
   *    update <tt>label</tt>'s style to match
   *  else
   *    do nothing
   * @requires 
   *  this is the <b>main</b> gui
   * @version 5.2
   *  - changed to static
   */
  public static void updateContainerLabelOnVisibilityUpdate(JLabel label, boolean compVisible) {
    Font f = label.getFont();
  
    // check label's style
    boolean isItalic = f.isItalic();
  
    // if label's style does not match the visibility of component, update its font
    // otherwise do nothing
    if (!compVisible && !isItalic) {
      // container is closed
      f = f.deriveFont(f.getStyle() + Font.ITALIC);
      label.setFont(f);
      
      // v2.7: update icon
      label.setIcon(getImageIcon("containerclose.gif", null));
    } 
    /*v3.0: improved to check italic and icon separately 
    else if (compVisible && isItalic){
      // container is opened
      f = f.deriveFont(f.getStyle() - Font.ITALIC);
      label.setFont(f);
      
      // v2.7: update icon
      label.setIcon(GUIToolkit.getImageIcon("containeropen.gif", null));
    }
    */
    else if (compVisible){
      // container is opened
      if (isItalic) {
        f = f.deriveFont(f.getStyle() - Font.ITALIC);
        label.setFont(f);
      }
  
      ImageIcon openIcon = getImageIcon("containeropen.gif", null);
      
      if (label.getIcon() != openIcon) {
        // v2.7: update icon
        label.setIcon(openIcon);
      }
    }
  }

}
