package jda.test.model.commerce;

import javax.swing.ImageIcon;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview Represents drink mechandise.
 * 
 * @author dmle
 */
@DClass(schema="test_commerce")
public class Product {
  @DAttr(name="id",auto=true,id=true,type=Type.Integer,mutable=false,optional=false)
  private int id;
  @DAttr(name="name",type=Type.String,length=15,mutable=true,optional=false)
  private String name;
  @DAttr(name="price",type=Type.Double,mutable=true,optional=false)
  private double price;
  @DAttr(name="productImage",type=Type.Image,
      length=5000000, // 5MB
      mutable=true,optional=false)
  private ImageIcon productImage;
  
  private static int idCounter = 0;

  public Product(String name, Double price, ImageIcon prodImage) throws ConstraintViolationException {
    this(null,name,price,prodImage);
  }

  public Product(Integer id, String name, Double price, ImageIcon prodImage) throws ConstraintViolationException {
    this.id=nextId(id);
    this.name=name;
    this.price=price.doubleValue();
    this.productImage=prodImage;
  }
  
  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name=name;
  }
  
  public void setPrice(double price) {
    this.price = price;
  }
  
  public double getPrice() {
    return price;
  }
  
  public void setProductImage(ImageIcon img) {
    this.productImage = img;
  }
  
  public ImageIcon getProductImage() {
    return productImage;
  }
  
  private Integer nextId(Integer currID) throws ConstraintViolationException {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();
      
      if (num > idCounter) 
        idCounter=num;
      
      return currID;
    }
  }
  
  @Override
  public String toString() {
    return "Product<"+name+">";
  }
}
