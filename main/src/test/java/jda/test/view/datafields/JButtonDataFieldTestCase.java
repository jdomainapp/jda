package jda.test.view.datafields;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.DODMBasic;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.view.Style;
import jda.modules.setup.init.StyleConstants;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.JBindableField;
import jda.mosa.view.assets.datafields.JButtonGroupField;
import jda.test.model.commerce.Product;
import jda.test.view.ViewTestCase;
import jda.test.view.datafields.JAbstractListFieldTestCase.TestDataSource;
import jda.util.SwTk;

public class JButtonDataFieldTestCase extends JDataFieldTestCase {

  @DAttr(name="productImage",type=Type.Image,optional=false)
  private ImageIcon img;

  @DAttr(name="product",type=Type.Domain,optional=false)
  private ImageIcon product;

  private static DODMBasic schema;
  private static Configuration config;
  private static DAttr dcImage;
  private static DAttr dcProduct;

  @BeforeClass
  public static void init() throws Exception {
    // register domain classes
    //config = new Configuration();
    config = SwTk.createMemoryBasedConfiguration("");
    GUIToolkit.initInstance(config);
    schema = DODMBasic.getInstance(config);
    //schema = schema.getInstance(null, false);
    schema.addClass(Product.class);
    schema.addClass(JButtonDataFieldTestCase.class);
    
    dcImage = schema.getDsm().getDomainConstraint(JButtonDataFieldTestCase.class, "img");
    dcProduct = schema.getDsm().getDomainConstraint(JButtonDataFieldTestCase.class, "product");
    
  }

  @Test
  public void createAndShowGUI () { super.createAndShowGUI(); }
  
  public static void main(String[] args) throws Exception {
    String thisClass = JButtonDataFieldTestCase.class.getName();
    ViewTestCase.main(new String[] { thisClass });
  }

  protected JComponent getContent() {
    JPanel panel = new JPanel(new SpringLayout());
    final String SEP = "------------------------";
    String[] labels = { // 
        "product icons", //
        "get value: ", //
        SEP, //
        "products", //
        "get value: ", //
        // load values
        "connect data source: ",
        "clear: "
    };
    
    numComponents = labels.length;

    final JBindableField df, df1;

    // product images
    int i = 0;
    
    Style style = StyleConstants.Heading1;
    
    // unbounded products
    df = createUnboundedProductGroupField();
    
    df.setStyle(style);
    
    createLabelledComponent(panel, labels[i++], df);
    JButton b = createValueButton(df);
    
    createLabelledComponent(panel, labels[i++], b);
    
    //configStyle(df.getGUIComponent());
    
    // sep
    JLabel sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);

    // bounded products
    df1 = createBoundedProductGroupField();
    createLabelledComponent(panel, labels[i++], df1);
    b = createValueButton(df1);
    createLabelledComponent(panel, labels[i++], b);

    // create a button to load values
    b = new JButton("...");
    b.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        df.connectDataSource();
        df1.connectDataSource();

        // pack the GUI to show the icons
        JButtonDataFieldTestCase.this.pack();
      }
    });
    createLabelledComponent(panel, labels[i++], b);

    // create a button to remove values
    b = new JButton("clr");
    b.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        df.clearBinding();
        df1.clearBinding();
      }
    });
    createLabelledComponent(panel, labels[i++], b);

    return panel;
  } 
  
  public JBindableField createUnboundedProductGroupField() {
    final List<ImageIcon> images = getImages();
    
    // a data source for serving raw values (unbounded)
    /*v2.7.3:
    JDataSource dataSource = new JDataSource() {
      @Override
      public boolean isEmpty() {
        return images.isEmpty();
      }

      @Override 
      public Iterator iterator() {
        return images.iterator();
      }
    }; // end JDataSource
    */
    JDataSource dataSource = JAbstractListFieldTestCase.createUnboundedDataSource(images);
    
    ImageIcon initVal = images.get(0);

    JBindableField df = (JBindableField) DataFieldFactory
        .createMultiValuedDataField(getDataValidator(schema,null), config, dcImage, 
            null, // boundConstraint
            JButtonGroupField.class,
            dataSource,
            initVal);  
    
    return df;
  }
  
  public List<ImageIcon> getImages() {
    List<ImageIcon> images = new ArrayList();
    String[] names = {"coke", "sprite", "oj"};
    
    URL imgURL;
    ImageIcon img;
    for (String name: names) {
      imgURL = this.getClass().getResource("/images/test/"+name+".jpeg");
      if (imgURL != null) {
        img = GUIToolkit.getImageIcon(imgURL.getPath(), name);
        images.add(img);
      }
    }
    
    if (images.isEmpty()) 
      return null;
    else
      return images;
  }
  
  public JBindableField createBoundedProductGroupField() {
    final List<Product> products = new ArrayList<Product>();
    List<ImageIcon> images = getImages();
    Product p = new Product("Coke", 0.75, images.get(0));    
    products.add(p);
    p = new Product("Sprite", 0.75, images.get(1));    
    products.add(p);
    p = new Product("Oj", 0.6, images.get(2));    
    products.add(p);

    // a data source for serving bounded values
    /*v2.7.3: 
    JDataSource dataSource = new JDataSource(schema, Product.class) {
      @Override
      public boolean isEmpty() {
        return products.isEmpty();
      }

      @Override 
      public Iterator iterator() {
        return products.iterator();
      }
    }; // end JDataSource
    */
    TestDataSource dataSource = new TestDataSource(schema, Product.class);
    dataSource.setValues(products);
    
    DAttr boundConstraint = schema.getDsm().getDomainConstraint(
        Product.class, "productImage");
    Product initVal = products.get(1);
    JBindableField df = 
        (JBindableField) DataFieldFactory.createMultiValuedDataField(getDataValidator(schema,null), config, 
            dcProduct, boundConstraint,
            JButtonGroupField.class,
            dataSource,
            initVal);

    return df;
  }  
}
