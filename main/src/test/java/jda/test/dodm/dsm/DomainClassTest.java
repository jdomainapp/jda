package jda.test.dodm.dsm;

import org.junit.Ignore;
import org.junit.Test;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.util.SwTk;

@DClass(serialisable=false)
public class DomainClassTest {
  
  
  String test;
  
  @Ignore
  @Test
  public void isAbstract() {
    Configuration config = SwTk.createMemoryBasedConfiguration("unamed");
    DODMBasic schema = DODMBasic.getInstance(config);//
    
    System.out.println("isAbstract()");
    Class ac = A.class;
    System.out.println("Class " + ac);
    boolean abst = schema.getDsm().isAbstract(ac);
    System.out.println("abstract: " + abst);
  }

  @Ignore
  @Test
  public void isTransient() {
    Configuration config = SwTk.createMemoryBasedConfiguration("unamed");
    DODMBasic schema = DODMBasic.getInstance(config);//
    
    System.out.println("isTransient()");
    Class ac = DomainClassTest.class;
    System.out.println("Class " + ac);
    boolean tf = schema.getDsm().isTransient(ac);
    System.out.println("transient: " + tf);
  }

  @Test
  public void getAnnotationDeclaringClass() throws Exception {
    System.out.println("getAnnotationDeclaringClass()");

    Configuration config = SwTk.createMemoryBasedConfiguration("unamed");
    DODMBasic schema = DODMBasic.getInstance(config);//DODM schema = DODM.getInstance("unamed", false);
    
    Class c = B.class;

    schema.registerClass(A.class);
    schema.registerClass(c);
    
    String attribName = "domainAttrib";
    DAttr dc = schema.getDsm().getDomainConstraint(c, attribName);
    Class an = dc.annotationType();
    Class domainClass = schema.getDsm().getDeclaringClass(c, dc);
    
    System.out.printf("domain attribute: %s.%s %n annotation type %s%n declaring class %s%n", c.getName(), dc.name(), an, domainClass.getName());
  }
  
  @Ignore
  @Test
  public void className() throws Exception {
    System.out.println("className()");

    Class[] classes = { A.class, jda.test.model.basic.Student.class,
        jda.modules.security.def.Resource.class };

    Configuration config = SwTk.createMemoryBasedConfiguration("unamed");
    DODMBasic schema = DODMBasic.getInstance(config);//"unamed", false);
    
    for (Class ac : classes) {
      System.out.println("Class: " + ac);
      System.out.println("getName(): " + ac.getName());
      System.out.println("getSimpleName(): " + ac.getSimpleName());
      System.out.println("getCanonicalName(): " + ac.getCanonicalName());
      System.out.println("getPackage(): " + ac.getPackage());
      System.out.println("domain name: " + schema.getDsm().getDomainClassName(ac));
    }
  }

  abstract class A {
    @DAttr(name="domainAttrib")
    private String domainAttrib;
  }
  
  class B extends A {
    //
  }
}
