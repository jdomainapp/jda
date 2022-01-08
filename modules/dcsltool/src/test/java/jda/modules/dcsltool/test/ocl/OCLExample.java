package jda.modules.dcsltool.test.ocl;
import static jda.modules.dcsltool.transform.ecore.EcoreOCLToolkit.createIntegerExp;
import static jda.modules.dcsltool.transform.ecore.EcoreOCLToolkit.createOclInfixedExp;
import static jda.modules.dcsltool.transform.ecore.EcoreOCLToolkit.lookUpIntegerOperators;
import static jda.modules.dcsltool.transform.ecore.EcoreOCLToolkit.normaliseInfixExpStr;

import java.lang.reflect.Method;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.expressions.OCLExpression;

import javafx.beans.NamedArg;
import jda.modules.dcsltool.transform.ecore.EcoreModel;


/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class OCLExample {
 
  /**
   * 
   * @overview A test class used for generating ocl expression. 
   *
   * @author Duc Minh Le (ducmle)
   *
   * @version
   */
  public class Test {
    
    private String name;
    
    public void test(@NamedArg("n") String n) {
      //
    }
  }
  /** end {@link Test} */
  
  public static void main(String[] args) {

    //initBaseMetamodel();
    
    try {
      //Method testM = Test.class.getMethod("test", String.class);

      //OCLHelper helper = Ocl.createOCLHelper();
      EcoreModel ecoreModel = new EcoreModel();
      
      Class cls = Test.class;
      Method m = cls.getMethod("test", String.class);

      EClass ecls = ecoreModel.addClass(cls);
      EOperation opt = ecoreModel.addOperation(ecls, m);
      
      //System.out.println(opt);
      
      // create pre-condition expression 
      String expStr;
      /* ERROR: */
      EParameter param = opt.getEParameters().get(0);
      expStr = createPreExpTest1(param);
      
      System.out.println(expStr);

      //alternative: 
      /* 
      expStr = "n.size() <= 10";
      */ 
      ecoreModel.setOclOperationContext(ecls, opt);
      Constraint pre = (Constraint) ecoreModel.applyPreConditionOnCurrContext(expStr);
      
      System.out.println("\n" + pre.getClass() + ":\n" + pre);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
/**
   * @effects 
   * 
   * @version 
   * 
   */
  private static String createPreExpTest1(EParameter param) {
    // feature call expression
    // ERROR: size() is not defined!!!!!
    /*
    String paramName = param.getName();
    VariableExp varExp = createVariableExp(paramName);
    //System.out.println(varExp.toString());
    EClassifier EString = Ocllib.getString();
    EOperation sizeOpt = lookUpOpt(EString, "size");
    CallExp exp1 = createOperationCallExp(varExp, sizeOpt);
    System.out.println(exp1);
    */
    
    // infixed expression
    // "n.size() <= 10"
    String opName = "<="; 
    EOperation leq = lookUpIntegerOperators(opName);
    OCLExpression exp2 = createOclInfixedExp(param, "size", leq, 
        createIntegerExp(10));
    
    String expStr = exp2.toString();

    expStr = normaliseInfixExpStr(expStr, opName);
    
    return expStr;
  }

//  /**
//   * @effects 
//   * 
//   * @version 
//   * 
//   */
//  private static void initBaseMetamodel() {
//    EcoreOCLStandardLibraryImpl ocllib = OCLStandardLibraryImpl.INSTANCE;
//    EClassifier EString = (EClassifier) ocllib.getString();
//    
//    createEOperation(EString, ocllib.getInteger(), "size");
//  }

//  /**
//   * @effects 
//   * 
//   * @version 
//   * 
//   */
//  private static void createBaseModel() {
//    EcoreFactory ecoref = EcoreFactory.eINSTANCE;
//    EDataType string = ecoref.createEDataType();
//    string.setName("String");
//
//    OCLStandardLibraryImpl.INSTANCE
//    .getBoolean();
//  }


}
