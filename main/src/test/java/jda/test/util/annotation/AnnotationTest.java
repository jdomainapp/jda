package jda.test.util.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jda.modules.dcsl.syntax.DAttr;
import jda.test.util.annotation.Attrib.AttribRef;


public class AnnotationTest {
  @Attrib(ref=AttribRef.product)
  private String testAttrib;
}

@Retention(RetentionPolicy.RUNTIME)
@interface Attrib {
  AttribRef ref();

  enum AttribRef {
    Product_id(Product.Attribute.productId),
    product(Product.Attribute.product),
    customer(Product.Attribute.customer);
    
    private AttributeName name;
    private AttribRef(AttributeName name) {
      this.name=name;
    }
  }

}

interface AttributeName {}

class Product {
  enum Attribute implements AttributeName {
    productId("productId"),
    product("product"),
    customer("customer");
    
    private String name;
    private Attribute(String name) {
      this.name = name;
    }
    
    public String getName() {return name;}
  }
  
  @DAttr(name="productId")
  private int productId;
}

