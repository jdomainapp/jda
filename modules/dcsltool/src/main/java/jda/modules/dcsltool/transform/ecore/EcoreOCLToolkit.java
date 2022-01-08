package jda.modules.dcsltool.transform.ecore;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.internal.OCLFactoryImpl;
import org.eclipse.ocl.ecore.internal.OCLStandardLibraryImpl;
import org.eclipse.ocl.expressions.CallExp;
import org.eclipse.ocl.expressions.IntegerLiteralExp;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.expressions.VariableExp;
import org.eclipse.ocl.utilities.OCLFactory;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class EcoreOCLToolkit {
  
  public static final OCLFactory Oclf = OCLFactoryImpl.INSTANCE;
  public static final OCL Ocl = OCL.newInstance(EcoreEnvironmentFactory.INSTANCE);
  private static final Environment Env = Ocl.getEnvironment();
  private static final EcoreFactory Ecoref = EcoreFactory.eINSTANCE;

  public static final OCLStandardLibraryImpl Ocllib = OCLStandardLibraryImpl.INSTANCE;
  
  private EcoreOCLToolkit() {}
  


  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public static EOperation lookUpIntegerOperators(String name) throws IllegalArgumentException {
    EList<EOperation> opts = Ocllib.getExistingOperations(Ocllib.getInteger());
    
    for (EOperation opt : opts) {
      if (opt.getName().equals(name))
        return opt;
    }
    
    throw new IllegalArgumentException("Invalid Integer's operation name: " + name);
  }


  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public static EOperation lookUpOpt(EClassifier ecls,
      String optName) throws IllegalArgumentException {
    
    for (EObject c : ecls.eContents()) {
      if (c.eClass().equals(EOperation.class)) {
        EOperation opt = (EOperation) c;
        if (opt.getName().equals(optName)) {
          return opt;
        }
      }
    }

    throw new IllegalArgumentException("EClassifier "+ecls+": \n invalid operation name: " + optName);
  }
  
  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public static OperationCallExp createEmptyOperationCallExp() {
    OperationCallExp exp = Oclf.createOperationCallExp();
    /* REQUIRED:
    for other types: look for usage of OCLExpression.setType() in 
    org.eclipse.ocl.parser.AbstractOCLAnalyzer
    and find the method that produces the desired expression type.
    For example: if the expression type is OperationCallExp then find the method genOperationCallExp
    */
    exp.setType(Env.getOCLStandardLibrary().getOclVoid());
    
    return exp;
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public static OCLExpression createIntegerExp(int i) {
    IntegerLiteralExp exp = Oclf.createIntegerLiteralExp();
    exp.setIntegerSymbol(i);
    
    return exp;
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public static OCLExpression createOclInfixedExp(EParameter param,
      String builtInParamTypeOpt,
      EOperation opt,
      OCLExpression rightExp) {
    // create a pseudo EOperation for builtInParamTypeOpt
//    EOperation bopt = EcoreModel.Ecoref.createEOperation();
    EOperation bopt = Ecoref.createEOperation();
    bopt.setName(builtInParamTypeOpt);
    
    // create leftExp
    OperationCallExp leftExp = createEmptyOperationCallExp();
    VariableExp varExp = createVariableExp(param.getName());
    leftExp.setSource(varExp);
    leftExp.setReferredOperation(bopt);

    //debug
    //System.out.println(leftExp);
    
    // create infix operation
    OperationCallExp exp = createEmptyOperationCallExp();
    exp.setSource(leftExp);
    exp.setReferredOperation(opt);
    exp.getArgument().add(rightExp);
    
    return exp;
  }
  
  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public static OCLExpression createOclInfixedExp(OCLExpression leftExp,
      EOperation opt,
      OCLExpression rightExp) {
    OperationCallExp exp = createEmptyOperationCallExp();
    exp.setSource(leftExp);
    exp.setReferredOperation(opt);
    exp.getArgument().add(rightExp);
    
    return exp;
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public static VariableExp createVariableExp(String varName) {
    VariableExp vexp = Oclf.createVariableExp();
    Variable var = createVariable(varName);
    vexp.setReferredVariable(var);
    
    vexp.setType(var.getType());

    return vexp;
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public static CallExp createOperationCallExp(VariableExp source, EOperation opt) {
    OperationCallExp call = createEmptyOperationCallExp();
    
    call.setSource(source);
    
    call.setReferredOperation(opt);
    
    return call;
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public static Variable createVariable(String varName) {
    Variable var = Oclf.createVariable();
    var.setName(varName);
    
    return var;
  }
  
  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public static String normaliseInfixExpStr(String expStr, String opName) {
    // for some reasons OclExpression.toString produces an additional
    // '.' before the operator (perhaps b/c it treats this as an operation call and so 
    // the '.' refers to the operation).
    expStr = expStr.replaceAll("."+opName, " "+ opName+ " ");
    
    return expStr;
  }
}
