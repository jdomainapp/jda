package jda.modules.dcsltool.behaviourspace.validator;

import static jda.modules.dcsl.parser.ParserConstants.TypeInt;
import static jda.modules.dcsl.util.SpaceConsts.ASSOC;
import static jda.modules.dcsl.util.SpaceConsts.idCounterVar;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.Type;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.parser.ClassAST;
import jda.modules.dcsl.parser.ParserConstants;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.SourceModel;
import jda.modules.dcsl.parser.behaviourspace.MethodSig;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.util.BSpaceToolkit;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2 
 */
public class BSpaceValidator {

  private static BSpaceValidator instance;

  private ValidationReport report;

  private SourceModel sourceModel;
  
  /** 
   * the {@link EcoreModel} that collects all the elements of the model (for references)
   * @version 5.2
   * */
//  private EcoreModel ecoreModel;
  
//  private BSpaceValidator() {
//    this(null);
//  }
  
  /**
   * @effects 
   *
   * @version 
   */
  private BSpaceValidator(SourceModel sourceModel) {
    report = new ValidationReport();
    
//    if (sourceModel != null)
    this.sourceModel = sourceModel;
//    else
//      this.sourceModel = new SourceModel();
  }

//  /**
//   * @effects 
//   * 
//   * @version 
//   * 
//   */
//  public static BSpaceValidator getInstance() {
//    return getInstance(null);
//  }
  

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public static BSpaceValidator getInstance(SourceModel sourceModel) {
    if (instance==null) {
      instance = new BSpaceValidator(sourceModel);
    } else {
//      if (sourceModel != null) {
      instance.sourceModel = sourceModel;
//      }
      instance.reset();
    }
    
    return instance;
  }
  
  /**
   * @effects return report
   */
  public ValidationReport getReport() {
    return report;
  }

  /**
   * @effects 
   *  reset this for reuse. 
   * @version 5.2
   */
  private void reset() {
    report = new ValidationReport();
//    ecoreModel.clear();
  }
  
  /**
   * @param clsPkgName: name of the package of the domain class
   * @param clsName: simple name of the domain class
   * @param srcFile: full path to the source code file of the domain class
   * 
   * @effects 
   *  perform the validation on the domain class named <tt>clsPkgName.clsName</tt>, whose source code is 
   *  stored in <tt>srcFile</tt>, using pre-defined packages <tt>domainPkgs</tt>.
   *  
   *  <p>Validation result is recoreded in a {@link ValidationReport} object and is returned.
   *  
   *  <p>Throws NotPossibleException if failed for some reasons
   */
  public ValidationReport exec(String clsPkgName, String clsName, String srcFile) throws NotPossibleException {
    
    ClassAST dcls = sourceModel.loadClass(clsPkgName, clsName, srcFile); // new ClassAST(clsName, srcFile);
    
    // import necessary domain-specific packages
    // dcls.addImports(domainPkgs);
    
    // initialise state space map 
    dcls.getStateSpace();
    
    // v5.2: support Ecore/OCL
//    EClass ecls = ecoreModel.addClass(dcls);
//    ecoreModel.addFieldsOf(dcls, ecls);
    
    // validate behaviour spec
    validateBehaviourSpec(dcls);
    
//    // TODO: remove orphaned comments
//    String updatedClsSrc = dcls.getSourceCode(); //cu.toString();
    
//    // write class to file
//    String fqn;
//    String pkg = dcls.getPackageDeclaration();
//    
//    if (pkg != null) {
//      fqn = pkg + "." + clsName;
//    } else {
//      // no package
//      fqn = clsName;
//    }
    
    return report;
  }

  /**
   * @effects 
   *   validate the behaviour specification of the domain class represented by <tt>dcls</tt> 
   *   based on its state space specification and the mapping rules between the two spaces.
   *   
   *   <p>Throws NotPossibleException if failed to perform validation for some reason(s).
   * 
   * @pseudocode (adapted from pseudocode of BSpaceGen) <pre>
   *   (c: domain class)
   *   let FS = {f | f in c.fields,f.serialisable=true,isCollection(f.type)=false}
   *   let FU = {f | f in c.fields,f.auto=false}
   *   let FR = {f | f in c.fields,f.auto=false,f.optional=false,isCollection(f.type)=false}
   *   (check that) c has data-source-constructor c(s1,...,sm) (si in FS)
   *   c has object-form-constructor c(u1,...,um) (uj in FU)
   *   c has required-constructor c(r1,...,rp) (rk in FR)
   *      
   *   for each f in c.fields
   *     c has getter for f
   *     
   *     if isMutable(f)
   *       c has setter for f
   *     else
   *       c has NO setter for f
   *     
   *     if def(DAssoc(f)) 
   *       if isOneManyAsc(DAssoc(f)) /\ isOneEnd(DAssoc(f))
   *         c has link-adder, link-adder-new, link-updater, and link-remover for f
   *         c has link-count-getter, link-count-setter for f
   *       else if isOneOneAsc(DAssoc(f))
   *         c has link-adder-new for f
   *         
   *     if isAuto(DAttr(f)) 
   *       if undef(DAssoc(f))
   *         c has value-generator for f
   *       
   *       if isSerialisable(f)
   *         c has auto-attribute-value-synchroniser for f
   *   end for   
   *   </pre>
   */
  private void validateBehaviourSpec(ClassAST dcls) 
      throws NotPossibleException, NotFoundException {
    // a map to record auto-attribute-value-generator operations
    Map<LinkedHashMap<FieldDef, Type>, MethodDeclaration> autoAttribGenOptMap = new HashMap<>();
//    LinkedHashMap<FieldDef, Type> paramTypeMap;
    
    Map<DAttrDef, FieldDef> stateSpaceMap = dcls.getStateSpace();
    
    // v5.2c:
    ClassAST superCls = dcls.getSuperClass(sourceModel);
    boolean isSubCls = (superCls != null);
    
    if (stateSpaceMap == null && isSubCls) {
      // empty state space & a sub-class: validate the constructors
      // TODO: validate sub-class constructors
      return;
    } 
    
    /* id-counter field (static) */
    checkIdCounterVarField(dcls);
      
//    DAttrDef dc = null;
//    DAssoc assoc = null;
//    String fieldName;
//    FieldDef f;
    
    // four sets of attributes needed to create the three types of constructors and the value-sync opt
    LinkedHashMap<DAttrDef, FieldDef> FS = new LinkedHashMap<>();
    LinkedHashMap<DAttrDef, FieldDef> FU = new LinkedHashMap<>();
    LinkedHashMap<DAttrDef, FieldDef> FR = new LinkedHashMap<>();
    LinkedHashMap<DAttrDef, FieldDef> FSA = new LinkedHashMap<>();

    for (Entry<DAttrDef, FieldDef> e : stateSpaceMap.entrySet()) {
      DAttrDef dc = e.getKey();
      FieldDef f = e.getValue();
      
      String fieldName = dc.name(); // same as f.getName()
      
      /* getter */
      checkGetter(dcls, dc, f); 
      
      /* setter */
      checkSetter(dcls, dc, f);
      
      /*association link operations*/
      DAssoc assoc = (DAssoc) f.getAnnotation(ASSOC);
      
      if (assoc != null) {
        // association is defined
        checkAssocLinkOpts(dcls, f.getType(), fieldName, dc, assoc);
      } // end (assoc != null)
      
      /** other operations
       *     if f.auto=true
       *       create in c value-generator for f
       *       if f.serialisable=true
       *         create/update in c auto-attribute-value-synchroniser for f
       */
      if (dc.auto() && assoc == null) {
        /*auto attribute value generator*/
        //genAutoAttribValueGen(clazz, dc, f, autoAttribGenOptMap, stateSpaceMap);
        checkAutoAttribValueGen(dcls, dc, f, autoAttribGenOptMap);
        
        if (dc.serialisable()) {
          /*auto-attribute-value-synchroniser*/
          // NOTE: one method for all attributes
          // record attribute here to generate last
          FSA.put(dc,f);
        }
      }
      
      // if f is a member of any of FS, FU, FR then add f to them 
      if (dc.serialisable() && !dc.type().isCollection()) {
        // FS: serialisable and not a collection type
        FS.put(dc,f);
      } 
      
      if (!dc.auto() && !dc.type().isCollection()) {
        // FU: non-auto, non collection type attribute
        FU.put(dc,f);
        if (!dc.optional()) {
          // FR: + non optional (i.e. required)
          FR.put(dc,f);
        }
      }
    } // end for
    
    /*** create three constructors and value sync opt */
    /* three constructors  */
    checkConstructors(dcls, autoAttribGenOptMap, FS, FU, FR);
    
    /* value sync opt */
    if (!FSA.isEmpty()) {
      //genAutoAttribValueSynch(cu, clazz, FSA);
      checkAutoAttribValueSynch(dcls, FSA);
    }
  }

  /**
   * @modifies {@link #report}
   * @effects 
   *  if dcls has suitable constructor operations for the domain fields specified in <tt>FS, FU, FR</tt>
   *    return true
   *  else
   *    add an error to {@link #report} and return false
   */
  private void checkConstructors(ClassAST dcls,
      Map<LinkedHashMap<FieldDef, Type>, MethodDeclaration> autoAttribGenOptMap,
      LinkedHashMap<DAttrDef, FieldDef> FS,
      LinkedHashMap<DAttrDef, FieldDef> FU,
      LinkedHashMap<DAttrDef, FieldDef> FR) {
    boolean check = false; 
    
    ConstructorDeclaration consS = null, consU = null, consR = null;
    
    if (!FS.isEmpty()) {
      /* check data source constructor */
      consS = checkConstructor(dcls, FS, autoAttribGenOptMap, DOpt.Type.DataSourceConstructor);
      if (consS == null) {
        report.addError("DATA-SOURCE-CONSTRUCTOR is incorrectly defined or not found");
      }
    }
    
    if (!FU.isEmpty()) {
      /* check object form constructor */
      if (FS.equals(FU)) {
        consU = consS;
        if (consU == null) {
          report.addError("OBJECT-FORM-CONSTRUCTOR is incorrectly defined or not found");          
        } else {
          // same constructor (same set of fields) -> add annotation
          check = ParserToolkit.hasMethodAnnotation(consU, DOpt.Type.ObjectFormConstructor);
          if (!check) {
            report.addError("OBJECT-FORM-CONSTRUCTOR is incorrectly defined or not found");          
          }
        }
      } else {
        // different constructor
        consU = checkConstructor(dcls, FU, autoAttribGenOptMap, DOpt.Type.ObjectFormConstructor);
        if (consU == null) {
          report.addError("OBJECT-FORM-CONSTRUCTOR is incorrectly defined or not found");
        }
      }
    }

    if (!FR.isEmpty()) {
      /* check required constructor */
      if (FR.equals(FU)) {
        // same constructor: update with new annotation
        consR = consU;
        if (consR == null) {
          report.addError("REQUIRED-CONSTRUCTOR is incorrectly defined or not found");
        } else {
          check = ParserToolkit.hasMethodAnnotation(consR, DOpt.Type.RequiredConstructor);
          if (!check) {
            report.addError("REQUIRED-CONSTRUCTOR is incorrectly defined or not found");
          }
        }
      } else {
        // different constructor
        consR = checkConstructor(dcls, FR, autoAttribGenOptMap, DOpt.Type.RequiredConstructor);
        if (consR == null) {
          report.addError("REQUIRED-CONSTRUCTOR is incorrectly defined or not found"); 
        }
      }
    }
  }
  
  /**
   * @effects 
   *  if <tt>dcls</tt> has a constructor annotated with <tt>consType</tt> and parameters 
   *  matching those specified in <tt>consFieldMap</tt>
   *    return the {@link ConstructorDeclaration}
   *  else
   *    return null
   */
  private ConstructorDeclaration checkConstructor(ClassAST dcls,
      LinkedHashMap<DAttrDef, FieldDef> consFieldsMap,
      Map<LinkedHashMap<FieldDef, Type>, MethodDeclaration> autoAttribGenOptMap,
      DOpt.Type consType) {
    
    // look up constructor by consType
    ConstructorDeclaration cons = ParserToolkit.getDomainConstructorByOptType(dcls.getCls(), consType);

    if (cons == null) {
      return null;
    }
    
    // check parameters
    int idx = 0;
    Type[] paramTypes = new Type[consFieldsMap.size()];
    
    for (Entry<DAttrDef, FieldDef> entry : consFieldsMap.entrySet()) {
      DAttrDef dc = entry.getKey();
      FieldDef field = entry.getValue();
      
      Type fieldType = field.getType();
      Type paramType = ParserToolkit.getObjectType(fieldType); // cast to object type
      paramTypes[idx] = paramType;
      idx++;
    }
    
    //TODO (?) check pre-/post-conditions
    boolean check = ParserToolkit.hasParameterTypes(cons, paramTypes);

    if (check)
      return cons;
    else
      return null;
  }

  /**
   * @modifies {@link #report}
   * @effects 
   *  if dcls has a suitable id-counter-var field 
   *    return true
   *  else
   *    add an error to {@link #report} and return false 
   */
  private boolean checkIdCounterVarField(ClassAST dcls) {
    boolean check = dcls.hasField(idCounterVar, TypeInt, ParserConstants.modPrivateStatic);
    if (!check) {
      report.addError(String.format("No '%s' field found", idCounterVar));
    }    
    
    return check;
  }

  /**
   * @modifies {@link #report}
   * @effects 
   *  if dcls has an auto-attribute-value-synchroniser for the domain fields specified in <tt>FSA</tt>
   *    return true
   *  else
   *    add an error to {@link #report} and return false 
   */
  private boolean checkAutoAttribValueSynch(ClassAST dcls,
      LinkedHashMap<DAttrDef, FieldDef> FSA) {
    // check that dcls has a method with the correct DOpt.type and header
    DOpt.Type optType = DOpt.Type.AutoAttributeValueSynchroniser;
    
    MethodSig sig = MethodSig.getInstance(optType);
    if (sig == null) { // for performance, create method signature once!
      String[] paramNames = {
          "attrib",
          "derivingValue",
          "minVal",
          "maxVal"
      };
      
      Type[] paramTypes = {
          ParserToolkit.createClassOrInterfaceType(DAttr.class.getSimpleName()),    // 1: attrib 
          ParserToolkit.createClassOrInterfaceType(Tuple.class.getSimpleName()),    // 2: derivingValue
          ParserToolkit.createClassOrInterfaceType(Object.class.getSimpleName()),   // 3: minVal
          ParserToolkit.createClassOrInterfaceType(Object.class.getSimpleName())   // 4: maxVal
        };
      
      Class[] exceptions = { ConstraintViolationException.class }; 
      
      sig = MethodSig.createInstance(optType, ParserConstants.TypeVoid, null, 
          paramTypes, paramNames, exceptions, ParserConstants.modPublicStatic);
    }
    
    boolean check = ParserToolkit.hasMethod(dcls.getCls(), optType, sig);
    
    if (!check) {
      StringBuilder fieldNames = new StringBuilder();
      for (DAttrDef dc : FSA.keySet()) { fieldNames.append(dc.name()).append(","); }
      fieldNames.deleteCharAt(fieldNames.length()-1);
      report.addError(String.format("AUTO-ATTRIBUTE-VALUE-SYNCHRONISER method incorrectly defined or not found for domain fields: %s", 
          fieldNames.toString()));
    }

    return check;

  }

  /**
   * @modifies {@link #report}
   * @effects 
   *  if dcls has a suitable auto-attribute-value-generator for the domain field specified by <tt>(dc, f)</tt>
   *    return true
   *  else
   *    add an error to {@link #report} and return false
   */
  private boolean checkAutoAttribValueGen(ClassAST dcls, DAttrDef dc,
      FieldDef f,
      Map<LinkedHashMap<FieldDef, Type>, MethodDeclaration> autoAttribGenOptMap) {
    // check that dcls has a method with the correct DOpt.type and header
    DOpt.Type optType = DOpt.Type.AutoAttributeValueGen;

    String fieldName = dc.name();
    Type fieldType = f.getType();
    
    List<Type> paramTypeLst = 
        BSpaceToolkit.getAutoAttributeValueGenParamTypesArray(dcls, f, fieldType, dc);
    
    Type returnType = fieldType;
    String name = null;
    String[] paramNames = null;
    Type[] paramTypes = paramTypeLst.toArray(new Type[paramTypeLst.size()]);
    Class[] exceptions = null;

    boolean check = ParserToolkit.hasMethod(dcls.getCls(), optType, fieldName, 
        returnType, 
        name, paramNames, paramTypes, 
        exceptions, ParserConstants.modPrivateStatic);
    
    if (!check) {
      report.addError(String.format("AUTO-ATTRIBUTE-VALUE-GENERATOR method incorrectly defined or not found for domain field: %s", fieldName));
    }

    return check;
  }

  /**
   * @modifies {@link #report}
   * @effects 
   *  if dcls has a suitable association-link methods for the associative field specified by <tt>(fieldName, fieldType, assoc)</tt>
   *    return true
   *  else
   *    add an error to {@link #report} and return false
   */
  private void checkAssocLinkOpts(ClassAST dcls, Type fieldType, String fieldName,
      DAttrDef dc, DAssoc assoc) {

    String mname = null;
    String[] paramNames;
    Type returnType;
    Type[] paramTypes; 
    Class[] exceptions;
    
    Class assocCls = assoc.associate().type();
    final Type cAssocCls = ParserToolkit.createClassOrInterfaceType(assocCls.getSimpleName());

    boolean check;
    if (assoc.ascType().equals(AssocType.One2Many) && assoc.endType().equals(AssocEndType.One)) {
      /* link count field */
      String linkCountFieldName = fieldName+"Count";
      check = ParserToolkit.hasField(dcls.getCls(), linkCountFieldName, ParserConstants.TypeInt , Modifier.PRIVATE);

      if (!check) {
        report.addError(String.format("Link count field not found: %s", linkCountFieldName));
      }
      
      /* link-adder (1) */
      mname = null; returnType = ParserConstants.TypeBool;
      paramNames = null; paramTypes = new Type[] {cAssocCls}; exceptions = null;
      
      check = ParserToolkit.hasMethod(
          dcls.getCls(), DOpt.Type.LinkAdder, fieldName, 
          returnType, mname, paramNames, paramTypes, exceptions, Modifier.PUBLIC);
      
      if (!check) {
        report.addError(String.format("LINK-ADDER method incorrectly defined or not found for associative field: %s", fieldName));
      }
      
      /* link-adder (2): overloads link-adder (1) for collection-typed param */
      mname = null; returnType = ParserConstants.TypeBool;
      paramNames = null; paramTypes = new Type[] {fieldType}; exceptions = null;
      
      check = ParserToolkit.hasMethod(
          dcls.getCls(), DOpt.Type.LinkAdder, fieldName, 
          returnType, mname, paramNames, paramTypes, exceptions, Modifier.PUBLIC);
      
      if (!check) {
        report.addError(String.format("LINK-ADDER (Collection-typed) method incorrectly defined or not found for associative field: %s", fieldName));
      }
      
      /* link-adder-new */
      mname = null; returnType = ParserConstants.TypeBool;
      paramNames = null; paramTypes = new Type[] {cAssocCls}; exceptions = null;
      
      check = ParserToolkit.hasMethod(
          dcls.getCls(), DOpt.Type.LinkAdderNew, fieldName, 
          returnType, mname, paramNames, paramTypes, exceptions, Modifier.PUBLIC);
      
      if (!check) {
        report.addError(String.format("LINK-ADDER-NEW method incorrectly defined or not found for associative field: %s", fieldName));
      }

      /* link-updater */
      mname = null; returnType = ParserConstants.TypeBool;
      paramNames = null; paramTypes = new Type[] {cAssocCls}; exceptions = null;
      
      check = ParserToolkit.hasMethod(
          dcls.getCls(), DOpt.Type.LinkUpdater, fieldName, 
          returnType, mname, paramNames, paramTypes, exceptions, Modifier.PUBLIC);
      
      if (!check) {
        report.addError(String.format("LINK-UPDATER method incorrectly defined or not found for associative field: %s", fieldName));
      }
      
      /* link-remover */
      mname = null; returnType = ParserConstants.TypeBool;
      paramNames = null; paramTypes = new Type[] {cAssocCls}; exceptions = null;
      
      check = ParserToolkit.hasMethod(
          dcls.getCls(), DOpt.Type.LinkRemover, fieldName, 
          returnType, mname, paramNames, paramTypes, exceptions, Modifier.PUBLIC);
      
      if (!check) {
        report.addError(String.format("LINK-REMOVER method incorrectly defined or not found for associative field: %s", fieldName));
      }
      
      /* link-remover: overloaded for collection-typed parameter */
      mname = null; returnType = ParserConstants.TypeBool;
      paramNames = null; paramTypes = new Type[] {fieldType}; exceptions = null;
      
      check = ParserToolkit.hasMethod(
          dcls.getCls(), DOpt.Type.LinkRemover, fieldName, 
          returnType, mname, paramNames, paramTypes, exceptions, Modifier.PUBLIC);
      
      if (!check) {
        report.addError(String.format("LINK-REMOVER (Collection-typed) method incorrectly defined or not found for associative field: %s", fieldName));
      }

      /* link-count-getter */
      mname = null; returnType = ParserConstants.TypeInteger;
      paramNames = null; paramTypes = null; exceptions = null;
      
      check = ParserToolkit.hasMethod(
          dcls.getCls(), DOpt.Type.LinkCountGetter, linkCountFieldName, 
          returnType, mname, paramNames, paramTypes, exceptions, Modifier.PUBLIC);
      
      if (!check) {
        report.addError(String.format("LINK-COUNT-GETTER method incorrectly defined or not found for link count field: %s", linkCountFieldName));
      }
      
      /* link-count-setter */
      mname = null; returnType = ParserConstants.TypeVoid;
      paramNames = null; paramTypes = new Type[] {ParserConstants.TypeInt}; exceptions = null;
      
      check = ParserToolkit.hasMethod(
          dcls.getCls(), DOpt.Type.LinkCountSetter, linkCountFieldName, 
          returnType, mname, paramNames, paramTypes, exceptions, Modifier.PUBLIC);
      
      if (!check) {
        report.addError(String.format("LINK-COUNT-SETTER method incorrectly defined or not found for link count field: %s", linkCountFieldName));
      }
    } else if (assoc.ascType().equals(AssocType.One2One)) {
      /*link-adder-new: delegate to setter method (above) */
      mname = null; returnType = ParserConstants.TypeBool;
      paramNames = null; paramTypes = new Type[] {cAssocCls}; exceptions = null;
      
      check = ParserToolkit.hasMethod(
          dcls.getCls(), DOpt.Type.LinkAdderNew, fieldName, 
          returnType, mname, paramNames, paramTypes, exceptions, Modifier.PUBLIC);
      
      if (!check) {
        report.addError(String.format("LINK-ADDER-NEW method incorrectly defined or not found for link count field: %s", fieldName));
      }
    } // end association link operations   
  }

  /**
   * @modifies {@link #report}
   * @effects 
   *  if dcls has a suitable setter method for the domain field specified by <tt>(dc, f)</tt>
   *    return true
   *  else
   *    add an error to {@link #report} and return false
   */
  private boolean checkSetter(ClassAST dcls, DAttrDef dc, FieldDef f) {
    
    DOpt.Type optType = DOpt.Type.Setter;

    Type returnType = ParserConstants.TypeVoid;
    String fieldName = dc.name();
    String setterName = BSpaceToolkit.genMethodNameForField("set", fieldName);
    String[] paramNames = null;
    Type[] paramTypes = {f.getType()};
    Class[] exceptions = null;
    
    boolean check = ParserToolkit.hasMethod(dcls.getCls(), optType, fieldName, 
        returnType, setterName, paramNames, paramTypes, exceptions, Modifier.PUBLIC);
    
    boolean mutable = dc.mutable();
    if ((mutable ^ check) == true) {
      report.addError(String.format(
          mutable ? 
              "MUTATOR (SETTER) method incorrectly defined or not found for domain field: %s" :
              "MUTATOR (SETTER) method found for immutable domain field: %s" , fieldName));
    }

    return check;
  }

  /**
   * @modifies {@link #report}
   * @effects 
   *  if dcls has a suitable getter method for the domain field specified by <tt>f</tt>
   *    return true
   *  else
   *    add an error to {@link #report} and return false
   */
  private boolean checkGetter(ClassAST dcls, DAttrDef dc, FieldDef f) {
    DOpt.Type optType = DOpt.Type.Getter;

    Type returnType = f.getType();
    String fieldName = dc.name();
    String mname = BSpaceToolkit.genMethodNameForField("get", fieldName);
    String[] paramNames = null;
    Type[] paramTypes = null;
    Class[] exceptions = null;
    
    boolean check = ParserToolkit.hasMethod(dcls.getCls(), optType, fieldName, 
        returnType, mname, paramNames, paramTypes, exceptions, Modifier.PUBLIC);
    
    if (!check) {
      report.addError(String.format("OBSERVER (GETTER) method incorrectly defined or not found for domain field: %s", fieldName));
    }
    
    return check;
  }
}
