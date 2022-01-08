/**
 * 
 */
package jda.modules.dcsltool.behaviourspace.generator;

import static jda.modules.dcsl.parser.ParserConstants.TypeInt;
import static jda.modules.dcsl.util.BSpaceToolkit.genAutoAttributeValueGenMethodName;
import static jda.modules.dcsl.util.BSpaceToolkit.genMethodNameForField;
import static jda.modules.dcsl.util.BSpaceToolkit.getAutoAttributeValueGenParamTypes;
import static jda.modules.dcsl.util.SpaceConsts.ASSOC;
import static jda.modules.dcsl.util.SpaceConsts.idCounterVar;

import java.lang.reflect.Constructor;
//import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
//import domainapp.basics.model.meta.DOpt.Type;
import com.github.javaparser.ast.type.Type;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.common.types.Tuple;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.parser.ClassAST;
import jda.modules.dcsl.parser.ParserConstants;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.SourceModel;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.util.DClassTk;
import jda.modules.dcsltool.transform.ecore.EcoreModel;

/**
 * @overview 
 *  A generator for the behaviour space from the state space. 
 *  
 *  <p>Implements DClassGen algorithm of the KSE 2016 paper.
 *   
 * @author dmle
 */
public class BSpaceGen {
  private static boolean debug = false;

  private static BSpaceGen instance;
  
  /** 
   * the {@link EcoreModel} that collects all the elements of the model (for references)
   * @version 5.2
   * */
  private EcoreModel ecoreModel;

  private SourceModel sourceModel;
  
//  private BSpaceGen() {
//    this(null);
//  }
  
  private BSpaceGen(SourceModel sourceModel) {
    ecoreModel = new EcoreModel();
//    if (sourceModel != null) 
    this.sourceModel = sourceModel;
//    else
//      this.sourceModel = new SourceModel();
  }

  public static BSpaceGen getInstance(SourceModel sourceModel) {
    if (instance==null) {
      instance = new BSpaceGen(sourceModel);
    } else {
      instance.reset();
//      if (sourceModel != null)
      instance.sourceModel = sourceModel;
    }
    
    return instance;
  }

  /**
   * @effects 
   *  reset this for reuse. 
   * @version 5.2
   */
  private void reset() {
    ecoreModel.clear();
  }

  /**
   * @effects 
   *  Generate the behaviour specification of the class whose source file is <tt>javaSrcFile</tt> based on its state space specification, 
   *  and write that specification directly into that class.
   *  
   *  <br>Return the updated class object.
   *  
   *  <p>Throws NotFoundException if failed to obtain a handle for <tt>domainClass</tt> or if this class has 
   *  no domain attributes;<br>
   *  NotPossibleException if failed to create a method or failed to add a method to the class 
   *   
   * @version 
   * - 3.3: created<br>
   * - 5.2: improved to use the ClassAST API in the common module & support {@link SourceModel}
   */
  public String exec(String pkgName, String className, String javaSrcFile, String[] domainPkgs, String classOutputDir) throws NotFoundException, NotPossibleException {
    // get the shared class pool instance (singleton)
    ClassAST dcls = sourceModel.loadClass(pkgName, className, javaSrcFile); // new ClassAST(className, javaSrcFile);
    
    // import necessary domain-specific packages
    dcls.addImports(domainPkgs);
    
    // initialise state space map 
    dcls.getStateSpace();
    
    // v5.2: support Ecore/OCL
    EClass ecls = ecoreModel.addClass(dcls);
    ecoreModel.addFieldsOf(dcls, ecls);
    
    // generate behaviour spec
    genBehaviourSpec(dcls);
    
    // TODO: remove orphaned comments
    String updatedClsSrc = dcls.getSourceCode(); //cu.toString();
    
    // write class to file
    String fqn;
    String pkg = dcls.getPackageDeclaration();
    
    if (pkg != null) {
      fqn = pkg + "." + className;
    } else {
      // no package
      fqn = ((pkgName != null) ? pkgName + "." : "") + className; // v5.2c: className
    }
    // write to file
    ToolkitIO.writeJavaSourceFile(classOutputDir, fqn, updatedClsSrc, true);
    
    return updatedClsSrc;
  }

  /**
   * @modifies <tt>dcls</tt>
   * 
   * @effects 
   *   Generate the behaviour specification of the domain class represented by <tt>dcls</tt> 
   *   based on its state space specification,  
   *   and write the generated specification directly into the source code of the class.
   *   
   *   <p>Throws NotPossibleException if failed to create a method or failed to add a method to the class, 
   *   NotFoundException if field type is not found
   * 
   * @version 
   * - 5.2: modified to use {@link ClassAST} and to support OCL pre-/post-conditions<br>
   * - 5.2c: improved to support sub-class: 
   *  (1) no idCounter field
   *  (2) genConstructors(): to "override" each constructor of the super-type, adding the extra fields (if any) to the parameter list
   *    and the body
   *  (3) other gen methods are as normal
   *  
   * @pseudocode <pre>
   *   (c: domain class)
   *   let FS = {f | f in c.fields,f.serialisable=true,isCollection(f.type)=false}
   *   let FU = {f | f in c.fields,f.auto=false}
   *   let FR = {f | f in c.fields,f.auto=false,f.optional=false,isCollection(f.type)=false}
   *   create in c data-source-constructor c(s1,...,sn) (si in FS)
   *   create in c object-form-constructor c(u1,...,um) (uj in FU)
   *   if FR ≠ FU create in c required-constructor c(r1,...,rp) (rk in FR)
   *      
   *   for each f in c.fields
   *     create in c getter for f
   *     
   *     if isMutable(f)
   *       create in c setter for f
   *       
   *     if def(DAssoc(f)) 
   *       if isOneManyAsc(DAssoc(f)) /\ isOneEnd(DAssoc(f))
   *         create in c link-adder, link-adder-new, link-updater, and link-remover for f
   *         create in c link-count-getter, link-count-setter for f
   *       else if isOneOneAsc(DAssoc(f))
   *         create in c link-adder-new for f
   *         
   *     if isAuto(DAttr(f)) 
   *       if undef(DAssoc(f))
   *         create in c value-generator for f
   *       if isSerialisable(f)
   *         create/update in c auto-attribute-value-synchroniser for f
   *   end for   
   *   </pre>
   */
  private void genBehaviourSpec(ClassAST dcls) 
      throws NotPossibleException, NotFoundException {
    // a map to record auto-attribute-value-generator operations
    Map<LinkedHashMap<FieldDef, Type>, MethodDeclaration> autoAttribGenOptMap = new HashMap<>();
    LinkedHashMap<FieldDef, Type> paramTypeMap;
    
    Map<DAttrDef, FieldDef> stateSpaceMap = dcls.getStateSpace();
    
    // import java.util
    //cu.addImport("java.util.*");
    dcls.addImport("java.util.*");
    
    // v5.2c:
    ClassAST superCls = dcls.getSuperClass(sourceModel);
    boolean isSubCls = (superCls != null);
    
    if (stateSpaceMap == null) {
      if (isSubCls) {
        // empty state space in a sub-class -> generate the constructors only
        genSubClassConstructors(dcls, superCls);
        return;
      } else {        
        throw new NotFoundException(NotFoundException.Code.STATESPACE_NOT_FOUND, new Object[] {dcls.getName()});
      }
    }
    
    /* id-counter field (static) */
    if (!isSubCls) { // only if this is not a sub-class
      genClassField(dcls, idCounterVar, TypeInt, ParserConstants.modPrivateStatic);
    }
    
    DAttrDef dc = null;
    DAssoc assoc = null;
    String fieldName;
    FieldDef f;
    
    // four sets of attributes needed to create the three types of constructors and the value-sync opt
    LinkedHashMap<DAttrDef, FieldDef> FS = new LinkedHashMap<>();
    LinkedHashMap<DAttrDef, FieldDef> FU = new LinkedHashMap<>();
    LinkedHashMap<DAttrDef, FieldDef> FR = new LinkedHashMap<>();
    LinkedHashMap<DAttrDef, FieldDef> FSA = new LinkedHashMap<>();
    
    for (Entry<DAttrDef, FieldDef> e : stateSpaceMap.entrySet()) {
      dc = e.getKey();
      f = e.getValue();
      
      fieldName = dc.name(); // same as f.getName()
      
      /* getter */
      genGetter(dcls, f); // genGetter(clazz, f);
      
      /* setter */
      if (dc.mutable()) {
        // mutable
        genSetter(dcls, f); // genSetter(clazz, f);
      }
      
      /*association link operations*/
      assoc = (DAssoc) f.getAnnotation(ASSOC);
      
      if (assoc != null) {
        // association is defined
        //genAssocLinkOpts(clazz, f.getType(), fieldName, dc, assoc);
        genAssocLinkOpts(dcls, f.getType(), fieldName, dc, assoc);
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
        genAutoAttribValueGen(dcls, dc, f, autoAttribGenOptMap);
        
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
      
      if (!dc.auto() && !dc.type().isCollection()
          // this is not needed
          //&& (assoc == null || !isDependentOneEnd(assoc))
          ) {
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
    if (!isSubCls) { // not a sub-class
      genConstructors(dcls, autoAttribGenOptMap, FS, FU, FR);
    } else { // a subclass
      // 5.2c 
      genSubClassConstructors(dcls, superCls, autoAttribGenOptMap, FS, FU, FR);
    }
    
    /* value sync opt */
    if (!FSA.isEmpty()) {
      //genAutoAttribValueSynch(cu, clazz, FSA);
      genAutoAttribValueSynch(dcls, FSA);
    }
  }

  /**
   * @modifies dcls, {@link #ecoreModel}
   * @effects 
   *  Create a {@link FieldDeclaration} from the arguments and add it to <tt>dcls</tt>.
   *  
   *  <br>Return the created field.
   * @version 
   *  - 5.2: improved to also add the generated field to {@link #ecoreModel}
   */
  private FieldDeclaration genClassField(ClassAST dcls, String name,
      Type declaredType, Modifier...modifier) {
    
    FieldDeclaration field = dcls.addField(declaredType, name, modifier);
    
    // v5.2
    EClass ecls = ecoreModel.getClass(dcls);
    ecoreModel.addField(ecls, dcls, field);
    
    return field;
  }

  /**
   * @modifies <tt>dcls</tt>
   * @effects 
   *  create in <tt>dcls</tt> essential constructors whose parameters are specified by <tt>FS, FU, FR</tt>
   *  
   *  <br>remove the default constructor of <tt>dcls</tt> if it is defined.
   */
  private void genConstructors(ClassAST dcls, 
      Map<LinkedHashMap<FieldDef, Type>, MethodDeclaration> autoAttribGenOptMap, 
      LinkedHashMap<DAttrDef, FieldDef> FS,
      LinkedHashMap<DAttrDef, FieldDef> FU,
      LinkedHashMap<DAttrDef, FieldDef> FR) {

    // remove the default constructor    
    //removeDefaultConstructor(dcls);
    
    // now create the constructors
    ConstructorDeclaration consS = null, consU = null;
    
    if (!FS.isEmpty()) {
      /* data source constructor */
      consS = genConstructor(dcls, FS, autoAttribGenOptMap, DOpt.Type.DataSourceConstructor);
    }
    
    if (!FU.isEmpty()) {
      /* object form constructor */
      if (FS.equals(FU)) {
        // same constructor (same set of fields) -> add annotation
        genMethodTypeAnnotation(consS, DOpt.Type.ObjectFormConstructor);
        consU = consS;
      } else {
        // different constructor
        consU = genConstructor(dcls, FU, autoAttribGenOptMap, DOpt.Type.ObjectFormConstructor);
      }
    }

    if (!FR.isEmpty()) {
      /* required constructor */
      if (FR.equals(FU)) {
        // same constructor: update with new annotation
        genMethodTypeAnnotation(consU, DOpt.Type.RequiredConstructor);
      } else {
        // different constructor
        genConstructor(dcls, FR, autoAttribGenOptMap, DOpt.Type.RequiredConstructor);
      }
    }
  }

  /**
   * @requires <tt>dcls</tt> represents the source of a sub-clas of <tt>supCls</tt> /\
   * <tt>supCls</tt>'s behaviour space contains all the necessary constructors 
   * 
   * @modifies <tt>dcls</tt>
   * @effects 
   *  create in <tt>dcls</tt> essential constructors that 'overload' the constructors of <tt>supCls</tt>
   *  with additional parameters specified in <tt>FS, FU, FR</tt>.
   *  
   *  <br>remove the default constructor of <tt>dcls</tt> if it is defined.
   *  
   *  <p>Throws NotFoundException if <tt>supCls</tt> does not have any domain-specific constructors defined.
   */
  private void genSubClassConstructors(ClassAST dcls, ClassAST supCls, 
      Map<LinkedHashMap<FieldDef, Type>, MethodDeclaration> autoAttribGenOptMap, 
      LinkedHashMap<DAttrDef, FieldDef> FS,
      LinkedHashMap<DAttrDef, FieldDef> FU,
      LinkedHashMap<DAttrDef, FieldDef> FR) throws NotFoundException {

    // get the supCls's construtors
    Collection<ConstructorDeclaration> consts = ParserToolkit.getDomainConstructorsByOptType(supCls.getCls(), 
        DOpt.Type.DataSourceConstructor, 
        DOpt.Type.RequiredConstructor, 
        DOpt.Type.ObjectFormConstructor);
    
    if (consts == null) {
      // error:
      throw new NotFoundException(NotFoundException.Code.CONSTRUCTOR_METHOD_NOT_FOUND, 
          new Object[] {supCls.getName(), ""+DOpt.Type.DataSourceConstructor + ", " + 
              DOpt.Type.RequiredConstructor + "," + 
              DOpt.Type.ObjectFormConstructor});
    }
    
    // 'overload' the constructors with additional parameters
    for (ConstructorDeclaration cons : consts) {
      genSubtypeConstructorByCopy(supCls, dcls, cons, autoAttribGenOptMap, FS, FU, FR);
    }
  }
  
  /**
   * @requires <tt>dcls</tt> represents the source of a sub-clas of <tt>supCls</tt> /\
   * <tt>supCls</tt>'s behaviour space contains all the necessary constructors 
   * 
   * @modifies <tt>dcls</tt>
   * @effects 
   *  create in <tt>dcls</tt> essential constructors that 'copy' the constructors of <tt>supCls</tt>.
   *  
   *  <br>remove the default constructor of <tt>dcls</tt> if it is defined.
   *  
   *  <p>Throws NotFoundException if <tt>supCls</tt> does not have any domain-specific constructors defined.
   */
  private void genSubClassConstructors(ClassAST dcls, ClassAST supCls) throws NotFoundException {

    // get the supCls's construtors
    Collection<ConstructorDeclaration> consts = ParserToolkit.getDomainConstructorsByOptType(supCls.getCls(), 
        DOpt.Type.DataSourceConstructor, 
        DOpt.Type.RequiredConstructor, 
        DOpt.Type.ObjectFormConstructor);
    
    if (consts == null) {
      // error:
      throw new NotFoundException(NotFoundException.Code.CONSTRUCTOR_METHOD_NOT_FOUND, 
          new Object[] {supCls.getName(), ""+DOpt.Type.DataSourceConstructor + ", " + 
              DOpt.Type.RequiredConstructor + "," + 
              DOpt.Type.ObjectFormConstructor});
    }
    
    // 'overload' the constructors with additional parameters
    for (ConstructorDeclaration cons : consts) {
      genSubtypeConstructorByCopy(supCls, dcls, cons, null, null, null, null);
    }
  }
  
//  /**
//   * @effects 
//   *  if exists a default constructor in <tt>dcls</tt>
//   *    remove it
//   *  else
//   *    do nothing
//   */
//  private void removeDefaultConstructor(CtClass dcls) {
//    CtConstructor[] consts = dcls.getConstructors();
//    
//    CtConstructor def = null;
//    for (CtConstructor cons : consts) {
//      if (cons.isEmpty()) {
//        def = cons;
//        break;
//      }
//    }
//    
//    if (def != null)
//      try {
//        dcls.removeConstructor(def);
//      } catch (javassist.NotFoundException e) {
//        // should not happen
//        e.printStackTrace();
//      }
//  }

  /**
   * @modifies dcls
   * @effects 
   *  generate in <tt>dcls</tt> a {@link ConstructorDeclaration} that 'overloads' (or copies) <tt>cons</tt> with 
   *  additional parameters recorded in either FS, FU, or FR, depending on the {@link DOpt.Type} of the constructor.
   *  
   *  <p>Throws NotFoundException if <tt>cons</tt>'s parameters are not annotated with suitable AttrRefs.
   */
  private ConstructorDeclaration genSubtypeConstructorByCopy(ClassAST supCls, ClassAST dcls, ConstructorDeclaration cons,
      Map<LinkedHashMap<FieldDef, Type>, MethodDeclaration> autoAttribGenOptMap,
      LinkedHashMap<DAttrDef, FieldDef> FS,
      LinkedHashMap<DAttrDef, FieldDef> FU,
      LinkedHashMap<DAttrDef, FieldDef> FR) throws NotFoundException, NotPossibleException {
    // get the DOpt.Type of cons to determine which FS, FU, FR to use
    // also extract the cons.preCond and cons.postCond
    Collection<DOpt.Type> optTypes = ParserToolkit.getOptTypes(cons);
    HashMap<DAttrDef, FieldDef> consFieldsMap = null;
    String preCondSup = null, postCondSup = null;
    // the first optType in this list is the one that has pre and post-conditions (if any) specified 
//    List<DOpt.Type> optTypes = new ArrayList<>(opts.length);
    for (DOpt.Type optType : optTypes) {
      if (optType.equals(DOpt.Type.RequiredConstructor)) {
        consFieldsMap = FR;
      }
      
      if (optType.equals(DOpt.Type.ObjectFormConstructor)) {
        consFieldsMap = FU;
      }
      
      if (optType.equals(DOpt.Type.DataSourceConstructor)) {
        consFieldsMap = FS;
      }
      
      // pre/post conditions
      // TODO ? genSubtypeConstructorByCopy(): how do we re-use the super's pre and post that have the "self." prefix?
      /*
      String pre = opt.requires(), post = opt.effects();
      if (!MetaToolkit.isDefaultPreCondition(pre)) {
        preCondSup = pre;
        // replace self. by super.
        preCondSup = preCondSup.replaceAll("self\\.", "super\\.");
        optTypes.add(0, opt.type());  // push this to be the first element
      } else {
        optTypes.add(opt.type());
      }
      
      if (!MetaToolkit.isDefaultPostCondition(post)) {
        postCondSup = post;
        postCondSup = postCondSup.replaceAll("self\\.", "super\\.");
      }
      */
//      optTypes.add(opt.type());
    }
    
    if (debug) System.out.printf("   genSubtypeConstructorByCopy(...):%n   class:%s%n", dcls.getName());
    
    StringBuilder body = new StringBuilder();
    Class[] exceptions = { ConstraintViolationException.class };
    dcls.addImports(exceptions);
    
    // get paramTypes and names from cons; also add to body the super(...) statement using the extracted params 
    List<Parameter> params = cons.getParameters();
    List<Type> paramTypes = new ArrayList<>();
    int paramsCount = params.size();
    int paramsSz = (consFieldsMap != null) ? consFieldsMap.size() + paramsCount : paramsCount;
    List<String> paramNames = new ArrayList<>(paramsSz);
    List<String> refFieldNames = new ArrayList<>(paramsSz);

    body.append("super(");  // start super(...)
    int idx = 0;
    for (Parameter param: params) {
      Type ptype = param.getType();
      if (!ParserToolkit.isJavaBuiltInType(ptype)) {
        // ptype is not a basic type: add import
        //dcls.addImport(ParserToolkit.getTypeName(ptype));
        ParserToolkit.transferImportsForType(supCls.getAst(), ptype, dcls.getAst());
      }
      
//      Type ptype = ParserToolkit.createClassOrInterfaceType(ptype.getSimpleName());
      paramTypes.add(ptype);
      
      String refFldName = ParserToolkit.getAttrRefValue(param);
      if (refFldName == null)
        throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[] {AttrRef.class.getName(), param});
//      else
//        refFldName = refAno.value();
      
      refFieldNames.add(refFldName);
      paramNames.add(refFldName);
      
      // update super(...)
      body.append(refFldName);
      if (idx < paramsCount-1) body.append(",");
      idx++;
    }
    
    body.append("); \n"); // end super(...)

    // initialise pre/post conditions
    StringBuilder preCond = null, postCond = null;
    if (preCondSup != null) {
      preCond = new StringBuilder(preCondSup);
    } else {
      preCond = new StringBuilder();
    }
    
    if (postCondSup != null) {
      postCond = new StringBuilder(postCondSup);
    } else {
      postCond = new StringBuilder();
    }
    
    // generate the constructor structure
    EClass ecls = null;
    if (consFieldsMap != null) {
      ecls = ecoreModel.getClass(dcls);
      genConstructorStruct(dcls, ecls, paramTypes, paramNames, refFieldNames, preCond, postCond, body, consFieldsMap, autoAttribGenOptMap);
    }
    
    // generate the constructor
    ConstructorDeclaration c = ParserToolkit.createConstructor(dcls,  
        paramTypes, paramNames, refFieldNames,  
        exceptions, body.toString(), Modifier.PUBLIC);
    
    // gen pre/post
    if (ecls != null) {
      EOperation eopt = ecoreModel.addOperation(ecls, dcls, c);
      if (preCond != null)
        ecoreModel.applyPreConditionOnOpt(ecls, eopt, preCond.toString());
      if (postCond != null)
        ecoreModel.applyPostConditionOnOpt(ecls, eopt, postCond.toString()); 
    }
    
    // generate annotations
    idx = 0;
    for (DOpt.Type consType : optTypes) {
      if (idx == 0) { // first optType: could have pre- post-conditions
        genMethodTypeAnnotation(c, consType, null, 
          (preCond != null) ? preCond.toString() : null, 
          (postCond != null) ? postCond.toString(): null);
      } else { // other optTypes: just add annotation
        genMethodTypeAnnotation(c, consType);        
      }
      idx++;
    }
    
    return c;
  }
  
  /**
   * @modifies dcls
   * @effects 
   *  generate in <tt>dcls</tt> a {@link ConstructorDeclaration} that 'overloads' (or copies) <tt>cons</tt> with 
   *  additional parameters recorded in either FS, FU, or FR, depending on the {@link DOpt.Type} of the constructor.
   *  
   *  <p>Throws NotFoundException if <tt>cons</tt>'s parameters are not annotated with suitable AttrRefs.
   */
  private ConstructorDeclaration genSubtypeConstructorByCopy(ClassAST dcls, Constructor cons,
      Map<LinkedHashMap<FieldDef, Type>, MethodDeclaration> autoAttribGenOptMap,
      LinkedHashMap<DAttrDef, FieldDef> FS,
      LinkedHashMap<DAttrDef, FieldDef> FU,
      LinkedHashMap<DAttrDef, FieldDef> FR) throws NotFoundException, NotPossibleException {
    // get the DOpt.Type of cons to determine which FS, FU, FR to use
    // also extract the cons.preCond and cons.postCond
    DOpt[] opts = cons.getAnnotationsByType(DOpt.class);
    HashMap<DAttrDef, FieldDef> consFieldsMap = null;
    String preCondSup = null, postCondSup = null;
    // the first optType in this list is the one that has pre and post-conditions (if any) specified 
    List<DOpt.Type> optTypes = new ArrayList<>(opts.length);
    for (DOpt opt : opts) {
      if (opt.type().equals(DOpt.Type.RequiredConstructor)) {
        consFieldsMap = FR;
      }
      
      if (opt.type().equals(DOpt.Type.ObjectFormConstructor)) {
        consFieldsMap = FU;
      }
      
      if (opt.type().equals(DOpt.Type.DataSourceConstructor)) {
        consFieldsMap = FS;
      }
      
      // pre/post conditions
      // TODO ? genSubtypeConstructorByCopy(): how do we re-use the super's pre and post that have the "self." prefix?
      /*
      String pre = opt.requires(), post = opt.effects();
      if (!MetaToolkit.isDefaultPreCondition(pre)) {
        preCondSup = pre;
        // replace self. by super.
        preCondSup = preCondSup.replaceAll("self\\.", "super\\.");
        optTypes.add(0, opt.type());  // push this to be the first element
      } else {
        optTypes.add(opt.type());
      }
      
      if (!MetaToolkit.isDefaultPostCondition(post)) {
        postCondSup = post;
        postCondSup = postCondSup.replaceAll("self\\.", "super\\.");
      }
      */
      optTypes.add(opt.type());
    }
    
    if (debug) System.out.printf("   genSubtypeConstructorByCopy(...):%n   class:%s%n", dcls.getName());
    
    StringBuilder body = new StringBuilder();
    Class[] exceptions = { ConstraintViolationException.class };
    dcls.addImports(exceptions);
    
    // get paramTypes and names from cons; also add to body the super(...) statement using the extracted params 
    java.lang.reflect.Parameter[] params = cons.getParameters();
    List<Type> paramTypes = new ArrayList<>();
    int paramsSz = (consFieldsMap != null) ? consFieldsMap.size() + params.length : params.length;
    List<String> paramNames = new ArrayList<>(paramsSz);
    List<String> refFieldNames = new ArrayList<>(paramsSz);

    body.append("super(");  // start super(...)
    int idx = 0;
    for (java.lang.reflect.Parameter param: params) {
      Class ptypeCls = param.getType();
      if (!DClassTk.isBasicJavaTypeOrArrayThereOf(ptypeCls)) {
        // ptype is not a basic type: add import
        dcls.addImport(ptypeCls);
      }
      
      Type ptype = ParserToolkit.createClassOrInterfaceType(ptypeCls.getSimpleName());
      paramTypes.add(ptype);
      
      String refFldName;
      AttrRef refAno = param.getAnnotation(AttrRef.class);
      if (refAno == null)
        throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[] {AttrRef.class.getName(), param});
      else
        refFldName = refAno.value();
      
      refFieldNames.add(refFldName);
      paramNames.add(refFldName);
      
      // update super(...)
      body.append(refFldName);
      if (idx < params.length-1) body.append(",");
      idx++;
    }
    
    body.append("); \n"); // end super(...)

    // initialise pre/post conditions
    StringBuilder preCond = null, postCond = null;
    if (preCondSup != null) {
      preCond = new StringBuilder(preCondSup);
    } else {
      preCond = new StringBuilder();
    }
    
    if (postCondSup != null) {
      postCond = new StringBuilder(postCondSup);
    } else {
      postCond = new StringBuilder();
    }
    
    // generate the constructor structure
    EClass ecls = null;
    if (consFieldsMap != null) {
      ecls = ecoreModel.getClass(dcls);
      genConstructorStruct(dcls, ecls, paramTypes, paramNames, refFieldNames, preCond, postCond, body, consFieldsMap, autoAttribGenOptMap);
    }
    
    // generate the constructor
    ConstructorDeclaration c = ParserToolkit.createConstructor(dcls,  
        paramTypes, paramNames, refFieldNames,  
        exceptions, body.toString(), Modifier.PUBLIC);
    
    // gen pre/post
    if (ecls != null) {
      EOperation eopt = ecoreModel.addOperation(ecls, dcls, c);
      if (preCond != null)
        ecoreModel.applyPreConditionOnOpt(ecls, eopt, preCond.toString());
      if (postCond != null)
        ecoreModel.applyPostConditionOnOpt(ecls, eopt, postCond.toString()); 
    }
    
    // generate annotations
    idx = 0;
    for (DOpt.Type consType : optTypes) {
      if (idx == 0) { // first optType: could have pre- post-conditions
        genMethodTypeAnnotation(c, consType, null, 
          (preCond != null) ? preCond.toString() : null, 
          (postCond != null) ? postCond.toString(): null);
      } else { // other optTypes: just add annotation
        genMethodTypeAnnotation(c, consType);        
      }
      idx++;
    }
    
    return c;
  }

  /**
   * @modifies clazz
   * @effects 
   *  generate in <tt>clazz</tt> a new constructor whose parameters are defined from <tt>consFields</tt> and 
   *  whose type is <tt>consType</tt>.
   *  
   *  <br>Return the constructo
   *  @version
   *  - 5.2: improved to support OCL pre- and post-conditions
   */
  private ConstructorDeclaration genConstructor(
      ClassAST dcls, 
      LinkedHashMap<DAttrDef,FieldDef> consFieldsMap,
      //LinkedHashMap<DAttrDef,FieldDef> allFieldsMap, 
      Map<LinkedHashMap<FieldDef, Type>, MethodDeclaration> autoAttribGenOptMap, 
      DOpt.Type consType) throws NotPossibleException {
    
//    Map<DAttrDef, FieldDef> allFieldsMap = dcls.getStateSpace();
    
    if (debug) {
      System.out.printf(
          "   genConstructor(...):%n   class:%s%n", dcls.getName()
          );
    }
    
    /*
     * gen method body: 
     *  for each field 
     *    if it is non-collection type
     *      if it is auto 
     *        call the corresponding auto-value-gen operation to get a value 
     *        assign field to value
     *      else
     *        assign field to param
     *    else if it is not optional
     *      init field to empty collection
     */
    StringBuilder body = new StringBuilder();
    Class[] exceptions = { ConstraintViolationException.class };
    //ParserToolkit.addImport(cu, exceptions);
    dcls.addImports(exceptions);
    
    List<Type> paramTypes = new ArrayList<>();
    //String[] paramNames = new String[consFieldsMap.size()];
    List<String> paramNames = new ArrayList<>(consFieldsMap.size());
    List<String> refFieldNames = new ArrayList<>(consFieldsMap.size());
    
    // v5.2: support pre-/post-condition
    StringBuilder preCond = new StringBuilder(), postCond = new StringBuilder();
    EClass ecls = ecoreModel.getClass(dcls);

    genConstructorStruct(dcls, ecls, paramTypes, paramNames, refFieldNames, preCond, postCond, body, consFieldsMap, autoAttribGenOptMap);

    /* moved to method
    
    Set<Entry<DAttrDef,FieldDef>> allFieldsSet = allFieldsMap.entrySet();
    Collection<FieldDef> consFields = consFieldsMap.values();
    
    Set<Entry<LinkedHashMap<FieldDef, Type>, MethodDeclaration>> attribGenOptEntrySet = autoAttribGenOptMap.entrySet();
         
    FieldDef field; 
    DAssoc assoc = null;
    DAttr dc;
    Type fieldType;
    String fieldName;
    Tuple2<LinkedHashMap<FieldDef,Type>, MethodDeclaration> valGenOptTuple = null;
    MethodDeclaration valGenOpt;
    StringBuilder valGenOptCall = null;
    Collection<FieldDef> optFieldSet;
    int consFieldIndex;
    String paramVal = null;     
    //int idx = 0;
    for (DAttr fdc : consFieldsMap.keySet()) {
      String attribName = fdc.name();
      // param name = attribute name
      String paramName = attribName;
      paramNames.add(paramName); //paramNames[idx++] = paramName; //fdc.name();
      
      // v5.2: create pre-condition for paramName
      String myPreCond = ecoreModel.createPreConditionOnOptParam(ecls, attribName, paramName);
      if (myPreCond != null) {
        if (preCond == null) {
          preCond = new StringBuffer(myPreCond);
        } else {
          preCond.append(" and ").append(myPreCond);
        }
      }
      
      // v5.2: create post-condition for paramName
      String myPostCond = ecoreModel.createPostConditionOnOptParam(dcls, ecls, attribName, paramName, consFieldsMap);
      if (myPostCond != null) {
        if (postCond == null) {
          postCond = new StringBuffer(myPostCond);
        } else {
          postCond.append(" and ").append(myPostCond);
        }
      }      
    }
    
    int edx = 0;
    for (Entry<DAttrDef,FieldDef> entry: allFieldsSet) {
      dc = entry.getKey();
      field = entry.getValue();
      fieldName = field.getName();
      
      // gen param types for fields in consFieldMap
      if (consFieldsMap.containsKey(dc)) {
        fieldType = field.getType();
        paramTypes.add(ParserToolkit.getObjectType(fieldType)); // getObjectType(fieldType);
        paramVal = fieldName; //"$"+consFieldIndex;
      } else { // field is not part of constructor fields
        paramVal = "null";
        
        // v5.2: if field is auto an attribute then generate a post-condition for it 
        // (because field was missed by the previous loop on consFieldsMap)
        if (dc.auto()) {
          String myPostCond = ecoreModel.createPostConditionOnOptParam(dcls, ecls, 
              fieldName, null, consFieldsMap);
          if (myPostCond != null) {
            if (postCond == null) {
              postCond = new StringBuffer(myPostCond);
            } else {
              postCond.append(" and ").append(myPostCond);
            }
          }  
        }
      }
      
      // update body
      if (!dc.type().isCollection()) {
        // non collection type
          assoc = null;
          assoc = (DAssoc) field.getAnnotation(ASSOC);
        
        if (dc.auto() && assoc == null) {
          // auto: call the corresponding auto-value-gen operation to get a value
          // look up the auto-value-gen opt for field
          for (Entry<LinkedHashMap<FieldDef, Type>, MethodDeclaration> ge : attribGenOptEntrySet) {
            if (ge.getKey().keySet().iterator().next().equals(field)) {
              // found: b/c field is the first param of the method
              valGenOptTuple = new Tuple2<>(ge.getKey(), ge.getValue());
            }
          }
          optFieldSet = valGenOptTuple.getFirst().keySet();
          valGenOpt = valGenOptTuple.getSecond();
          // create val-gen-opt call upon the parameters of this constructor whose corresponding fields are mapped to those of valGenOpt
          if (valGenOptCall == null) 
            valGenOptCall = new StringBuffer(); 
          else 
            valGenOptCall.delete(0, valGenOptCall.length());

          // first param is paramVal
          valGenOptCall.append(valGenOpt.getName()).append("(").append(paramVal);
          // other params are those among consFields that are mapped to those of valGenOptCall
          for (FieldDef ofield : optFieldSet) {
            if (!ofield.equals(field)) {
              consFieldIndex = 1;
              for (FieldDef cfield : consFields) {
                if (cfield.equals(ofield)) {
                  // found another field handle
                  valGenOptCall.append(",").append(cfield.getName());//append("$").append(consFieldIndex2);
                  break;
                }               
                consFieldIndex++;
              }
              
              if (consFieldIndex == consFields.size()+1) {
                // ofield is not found among consFields: use null
                valGenOptCall.append(",").append("null");
              } 
            }
          }
          
          valGenOptCall.append(")");
          body.append(edx>0 ? ";\n" : "").append("this.").append(fieldName).append("=").append(valGenOptCall);//.append("; \n");
        } else {
          // non auto
          body.append(edx>0 ? ";\n" : "").append("this.").append(fieldName + " = " + paramVal);// + "; \n");
        }
      } else if (!dc.optional()){
        // collection type & not optional: init field to empty collection
        body.append(edx>0 ? ";\n" : "").append("this.").append(fieldName + " = new ArrayList()"); //; \n");
      }
      
      edx++;
    } // end for

    // append ';' if needed
    if (body.charAt(body.length()-1) != ';') body.append(';');

    if (debug) {
//      StringBuffer paramNames = new StringBuffer();
//      for (DAttr fdc : consFieldsMap.keySet()) {
//        paramNames.append(fdc.type() + ": " + fdc.name()).append(",");
//      }
      System.out.printf("      parameters: %s%n      body: %s%n", paramNames, body);
    }
    */
    
    ConstructorDeclaration cons = ParserToolkit.createConstructor(dcls,  
        paramTypes, paramNames, refFieldNames, 
        exceptions, body.toString(), Modifier.PUBLIC);
    
    /* v5.2: added support for OCL pre-/post-conditions 
     */
    EOperation eopt = ecoreModel.addOperation(ecls, dcls, cons);
    if (preCond != null)
      ecoreModel.applyPreConditionOnOpt(ecls, eopt, preCond.toString());
    if (postCond != null)
      ecoreModel.applyPostConditionOnOpt(ecls, eopt, postCond.toString()); 
    
    genMethodTypeAnnotation(cons, consType, null, 
        (preCond != null) ? preCond.toString() : null, 
        (postCond != null) ? postCond.toString(): null);
    
    return cons;
  }


  /**
   * @param refFieldNames 
   * @modifies paramTypes, paramNames, refFieldNames, preCond, postCond, body
   * @effects 
   *  update the structure of a {@link ConstructorDeclaration} of <tt>dcls</tt> (which includes the params listed in @modifies) 
   *  using <tt>consFieldsMap, autoAttribGenOptMap</tt>
   */
  private void genConstructorStruct(final ClassAST dcls, final EClass ecls, 
      final List<Type> paramTypes, final List<String> paramNames, final List<String> refFieldNames, 
      final StringBuilder preCond, final StringBuilder postCond, StringBuilder body, 
      final HashMap<DAttrDef, FieldDef> consFieldsMap,
      final Map<LinkedHashMap<FieldDef, Type>, MethodDeclaration> autoAttribGenOptMap) {
    
    Collection<FieldDef> consFields = consFieldsMap.values();

    // param names
    //int idx = 0;
    for (DAttr fdc : consFieldsMap.keySet()) {
      String attribName = fdc.name();
      String paramName = attribName;
      paramNames.add(paramName); //fdc.name();
      refFieldNames.add(attribName);
      
      // v5.2: create pre-condition for paramName
      String myPreCond = ecoreModel.createPreConditionOnOptParam(ecls, attribName, paramName);
      if (myPreCond != null) {
        if (preCond.length() == 0) {
          preCond.append(myPreCond); //preCond = new StringBuilder(myPreCond);
        } else {
          preCond.append(" and ").append(myPreCond);
        }
      }
      
      // v5.2: create post-condition for paramName
      String myPostCond = ecoreModel.createPostConditionOnOptParam(dcls, ecls, attribName, paramName, consFieldsMap);
      if (myPostCond != null) {
        if (postCond.length() == 0) {
          postCond.append(myPostCond);
        } else {
          postCond.append(" and ").append(myPostCond);
        }
      }      
    }
    
    Map<DAttrDef, FieldDef> allFieldsMap = dcls.getStateSpace();

    if (allFieldsMap != null) { // state-space is not empty
      int edx = 0;
      String paramVal = null+"";
      DAssoc assoc = null;
      StringBuffer valGenOptCall = null;
//      Collection<FieldDef> optFieldSet;
      Tuple2<LinkedHashMap<FieldDef,Type>, MethodDeclaration> valGenOptTuple = null;
      int consFieldIndex;
      Set<Entry<DAttrDef,FieldDef>> allFieldsSet = allFieldsMap.entrySet();
      Set<Entry<LinkedHashMap<FieldDef, Type>, MethodDeclaration>> attribGenOptEntrySet = autoAttribGenOptMap.entrySet();

      for (Entry<DAttrDef,FieldDef> entry: allFieldsSet) {
        DAttrDef dc = entry.getKey();
        FieldDef field = entry.getValue();
        String fieldName = field.getName();
        
        // gen param types for fields in consFieldMap
        if (consFieldsMap.containsKey(dc)) {
          Type fieldType = field.getType();
          paramTypes.add(ParserToolkit.getObjectType(fieldType)); // getObjectType(fieldType);
          paramVal = fieldName; //"$"+consFieldIndex;
        } else { // field is not part of constructor fields
          paramVal = "null";
          
          // v5.2: if field is auto an attribute then generate a post-condition for it 
          // (because field was missed by the previous loop on consFieldsMap)
          if (dc.auto()) {
            String myPostCond = ecoreModel.createPostConditionOnOptParam(dcls, ecls, 
                fieldName, null, consFieldsMap);
            if (myPostCond != null) {
              if (postCond.length() == 0) {
                postCond.append(myPostCond);
              } else {
                postCond.append(" and ").append(myPostCond);
              }
            }  
          }
        }
        
        // update body
        if (!dc.type().isCollection()) {
          // non collection type
            assoc = null;
            assoc = (DAssoc) field.getAnnotation(ASSOC);
          
          if (dc.auto() && assoc == null) {
            // auto: call the corresponding auto-value-gen operation to get a value
            // look up the auto-value-gen opt for field
            for (Entry<LinkedHashMap<FieldDef, Type>, MethodDeclaration> ge : attribGenOptEntrySet) {
              if (ge.getKey().keySet().iterator().next().equals(field)) {
                // found: b/c field is the first param of the method
                valGenOptTuple = new Tuple2<>(ge.getKey(), ge.getValue());
              }
            }
            
            Set<FieldDef> optFieldSet = valGenOptTuple.getFirst().keySet();
            MethodDeclaration valGenOpt = valGenOptTuple.getSecond();
            // create val-gen-opt call upon the parameters of this constructor whose corresponding fields are mapped to those of valGenOpt
            if (valGenOptCall == null) 
              valGenOptCall = new StringBuffer(); 
            else 
              valGenOptCall.delete(0, valGenOptCall.length());

            // first param is paramVal
            valGenOptCall.append(valGenOpt.getName()).append("(").append(paramVal);
            // other params are those among consFields that are mapped to those of valGenOptCall
            for (FieldDef ofield : optFieldSet) {
              if (!ofield.equals(field)) {
                consFieldIndex = 1;
                for (FieldDef cfield : consFields) {
                  if (cfield.equals(ofield)) {
                    // found another field handle
                    valGenOptCall.append(",").append(cfield.getName());//append("$").append(consFieldIndex2);
                    break;
                  }               
                  consFieldIndex++;
                }
                
                if (consFieldIndex == consFields.size()+1) {
                  // ofield is not found among consFields: use null
                  valGenOptCall.append(",").append("null");
                } 
              }
            }
            
            valGenOptCall.append(")");
            body.append(edx>0 ? ";\n" : "").append("this.").append(fieldName).append("=").append(valGenOptCall);//.append("; \n");
          } else {
            // non auto
            body.append(edx>0 ? ";\n" : "").append("this.").append(fieldName + " = " + paramVal);// + "; \n");
          }
        } else if (!dc.optional()) {
          // collection type & not optional: init field to empty collection
          body.append(edx>0 ? ";\n" : "").append("this.").append(fieldName + " = new ArrayList<>()"); //; \n");
        }
        
        edx++;
      } // end for      
    } // end if (allFieldsMap)

    // append ';' if needed
    if (body.charAt(body.length()-1) != ';') body.append(';');    
  }

  /**
   * @modifies dcls 
   * @effects 
   *  Generate in <tt>dcls</tt> a setter operation for the field identified by <tt>field</tt>
   *  
   * @version 
   * - 5.2: support pre-/post-condition 
   */
  private void genSetter(ClassAST dcls, FieldDef field) {
    String fieldName = field.getName();

    String setterName = genMethodNameForField("set", fieldName);
    
    MethodDeclaration m = 
        ParserToolkit.createSingleParamMethod(dcls, ParserConstants.TypeVoid, setterName, 
        field,
        Modifier.PUBLIC);
    
    String mbody = "this.%s = %s;";
    mbody = String.format(mbody, fieldName, fieldName);
    
    ParserToolkit.createMethodBody(m, mbody);
    
    /* v5.2: added support for OCL pre-/post-conditions 
     */
    EClass ecls = ecoreModel.getClass(dcls);
    EOperation eopt = ecoreModel.addOperation(ecls, dcls, m);
    String preCond = ecoreModel.createAndApplyPreConditionSetter(ecls, eopt, fieldName); 
    String postCond = ecoreModel.createPostConditionSetter(ecls, eopt, fieldName); 
        
    //genMethodTypeAnnotation(m, DOpt.Type.Getter, fieldName);
    // end 5.2
    
    //TODO: if field (e.g. dob) has derived attributes (e.g. age) that depend ONLY on single deriving attribute 
    // then insert a call to each the auto-attrib-value-gen opt of those derived attributes
    
    genMethodTypeAnnotation(m, DOpt.Type.Setter, fieldName, preCond, postCond);
  }

  /**
   * @modifies dcls 
   * @effects 
   *  Generate in <tt>dcls</tt> a getter operation for the field identified by <tt>field</tt>
   * @version 
   * - 5.2: added support for OCL pre-/post-conditions
   */
  private void genGetter(ClassAST dcls, FieldDef field) {
    String fieldName = field.getName();
    String mname = genMethodNameForField("get", fieldName);
    
    Type fieldType = field.getType();
    
    MethodDeclaration m =
        dcls.createMethod(fieldType, mname, Modifier.PUBLIC);
        // ParserToolkit.createMethod(dcls, fieldType, mname, Modifier.PUBLIC);

    String mbody = "return this.%s;";
    mbody = String.format(mbody, fieldName);
    
    ParserToolkit.createMethodBody(m, mbody);
    
    /* v5.2: added support for OCL pre-/post-conditions 
     */
    EClass ecls = ecoreModel.getClass(dcls);
    EOperation eopt = ecoreModel.addOperation(ecls, dcls, m);
    String postCond = ecoreModel.createAndApplyPostConditionGetter(ecls, eopt, fieldName); 
        
    //genMethodTypeAnnotation(m, DOpt.Type.Getter, fieldName);
    // end 5.2
    
    genMethodTypeAnnotation(m, DOpt.Type.Getter, fieldName, null, postCond);
  }

  /**
   * @param fieldType 
   * @modifies <tt>dcls</tt>
   * @effects 
   *  generate in <tt>dcls</tt> association-link operations for the association <tt>assoc</tt> that is defined 
   *  for the field <tt>fieldName</tt> (whose domain constraint is <tt>dc</tt>)
   *  
   *   <p>Throws NotPossibleException if failed to create a method or failed to add a method to the class, 
   *   NotFoundException if field type is not found
   * @version 
   * - 5.2: improved to support OCL pre- and post-conditions
   */
  private void genAssocLinkOpts(ClassAST dcls, Type fieldType, String fieldName, DAttr dc, DAssoc assoc) throws NotFoundException, NotPossibleException {
    /***** association link operations
     *      if def(f.Association) 
     *       if f.Association.assocType="one-many" /\ f.Association.endType="one"
     *         create in c link-adder, link-adder-new, link-updater, and link-remover for f
     *         create in c link-count-getter, link-count-setter for f
     *       else if f.Association.assocType="one-one" /\ f.serialisable=true
     *         create in c link-adder-new for f
     */
    
    String mname = null, mbody;
    String paramName, linkCountFieldName;
    FieldDeclaration linkCountField;
    MethodDeclaration m;
    Class assocCls = assoc.associate().type();
    
    final Type cAssocCls = ParserToolkit.createClassOrInterfaceType(assocCls.getSimpleName());
    String assocClsSimpleName = assocCls.getSimpleName();
    String setterName = genMethodNameForField("set", fieldName);
    
    // v5.2: 
    String preCond = null, postCond = null;
    EClass ecls = ecoreModel.getClass(dcls); 
    EOperation eopt; 
        
    if (assoc.ascType().equals(AssocType.One2Many) && assoc.endType().equals(AssocEndType.One)) {
      /* link count field */
      linkCountFieldName = fieldName+"Count";
      linkCountField = genClassField(dcls, linkCountFieldName, ParserConstants.TypeInt , Modifier.PRIVATE);
      
      /* link-adder (1) */
      mname = genMethodNameForField("add", fieldName);
      paramName = "obj"; //"$1"; // $0 = this
      mbody =  "if (!%s.contains(%s)) {\n" +
                  "%s.add(%s); \n" +
                  "%s++; \n" +
               "} \n" +
               "return false;";
      mbody = String.format(mbody, fieldName, paramName, fieldName, paramName, linkCountFieldName);
      
      m = ParserToolkit.createSingleParamMethod(dcls, ParserConstants.TypeBool, mname, cAssocCls, paramName, Modifier.PUBLIC);
      ParserToolkit.createMethodBody(m, mbody);
      
      // v5.2: support pre-/post-conditions
      //genMethodTypeAnnotation(m, DOpt.Type.LinkAdder, fieldName);
      eopt = ecoreModel.addOperation(ecls, dcls, m); 
      preCond = ecoreModel.createPreConditionLinkAdderSingle(ecls, eopt, fieldName, paramName, linkCountFieldName);
      postCond = ecoreModel.createPostConditionLinkAdderSingle(ecls, eopt, fieldName, paramName, linkCountFieldName);
      ecoreModel.applyPreConditionOnOpt(ecls, eopt, preCond);
      ecoreModel.applyPostConditionOnOpt(ecls, eopt, postCond);
      
      genMethodTypeAnnotation(m, DOpt.Type.LinkAdder, fieldName, preCond, postCond);

      /* link-adder (2): overloads link-adder (1) for collection-typed param */
      mbody = 
        " for (%s o : %s) {\n" +
        "    if (!%s.contains(o)) { \n" +
        "      %s.add(o); \n" + 
        "      %s++; \n " +
        "    } \n " +
        "  } \n" +
        "  return false;";
      mbody = String.format(mbody, assocClsSimpleName, paramName, fieldName, fieldName, linkCountFieldName);
      m = ParserToolkit.createSingleParamMethod(dcls, ParserConstants.TypeBool, mname, fieldType, paramName, Modifier.PUBLIC);
      ParserToolkit.createMethodBody(m, mbody);       
      
      // v5.2: 
      //genMethodTypeAnnotation(m, DOpt.Type.LinkAdder, fieldName);
      eopt = ecoreModel.addOperation(ecls, dcls, m); 
      preCond = ecoreModel.createPreConditionLinkAdderCol(ecls, eopt, fieldName, paramName, linkCountFieldName);
      postCond = ecoreModel.createPostConditionLinkAdderCol(ecls, eopt, fieldName, paramName, linkCountFieldName);
      ecoreModel.applyPreConditionOnOpt(ecls, eopt, preCond);
      ecoreModel.applyPostConditionOnOpt(ecls, eopt, postCond);   
      genMethodTypeAnnotation(m, DOpt.Type.LinkAdder, fieldName, preCond, postCond);
      
      /* link-adder-new */
      mname = genMethodNameForField("addNew", fieldName);
      mbody = "%s.add(%s); \n" + 
              "%s++; \n" +
              "return false;";
      mbody = String.format(mbody, fieldName, paramName, linkCountFieldName);
      m = ParserToolkit.createSingleParamMethod(dcls, ParserConstants.TypeBool, mname, cAssocCls, paramName, Modifier.PUBLIC);
      ParserToolkit.createMethodBody(m, mbody);
      
      // v5.2:
      //genMethodTypeAnnotation(m, DOpt.Type.LinkAdderNew, fieldName);
      eopt = ecoreModel.addOperation(ecls, dcls, m); 
      preCond = ecoreModel.createPreConditionLinkAdderSingle(ecls, eopt, fieldName, paramName, linkCountFieldName);
      postCond = ecoreModel.createPostConditionLinkAdderSingle(ecls, eopt, fieldName, paramName, linkCountFieldName);
      ecoreModel.applyPreConditionOnOpt(ecls, eopt, preCond);
      ecoreModel.applyPostConditionOnOpt(ecls, eopt, postCond);   
      
      genMethodTypeAnnotation(m, DOpt.Type.LinkAdderNew, fieldName, preCond, postCond);
      
      /* link-updater */
      mname = genMethodNameForField("onUpdate", fieldName);
      mbody = "// TODO: implement this \n" +
               "  return false;";
      m = ParserToolkit.createSingleParamMethod(dcls, ParserConstants.TypeBool, mname, cAssocCls, paramName, Modifier.PUBLIC);
      ParserToolkit.createMethodBody(m, mbody);
      
      // v5.2:
      //genMethodTypeAnnotation(m, DOpt.Type.LinkUpdater, fieldName);
      eopt = ecoreModel.addOperation(ecls, dcls, m); 
      preCond = ecoreModel.createPreConditionLinkUpdaterSingle(ecls, eopt, fieldName, paramName, linkCountFieldName);
      postCond = ecoreModel.createPostConditionLinkUpdaterSingle(ecls, eopt, fieldName, paramName, linkCountFieldName);
      ecoreModel.applyPreConditionOnOpt(ecls, eopt, preCond);
      ecoreModel.applyPostConditionOnOpt(ecls, eopt, postCond);
      
      genMethodTypeAnnotation(m, DOpt.Type.LinkUpdater, fieldName, preCond, postCond);
      
      /* link-remover */
      mname = genMethodNameForField("onRemove", fieldName);
      mbody = "   boolean removed = %s.remove(%s); \n" +
              "   if (removed) %s--; \n" +
              "  return removed;";
      mbody = String.format(mbody, fieldName, paramName, linkCountFieldName);
      m = ParserToolkit.createSingleParamMethod(dcls, ParserConstants.TypeBool, mname, cAssocCls, paramName, Modifier.PUBLIC);
      ParserToolkit.createMethodBody(m, mbody);
      
      // v5.2:
      //genMethodTypeAnnotation(m, DOpt.Type.LinkRemover, fieldName);
      eopt = ecoreModel.addOperation(ecls, dcls, m); 
      preCond = ecoreModel.createPreConditionLinkRemoverSingle(ecls, eopt, fieldName, paramName, linkCountFieldName);
      postCond = ecoreModel.createPostConditionLinkRemoverSingle(ecls, eopt, fieldName, paramName, linkCountFieldName);
      ecoreModel.applyPreConditionOnOpt(ecls, eopt, preCond);
      ecoreModel.applyPostConditionOnOpt(ecls, eopt, postCond);
      
      genMethodTypeAnnotation(m, DOpt.Type.LinkRemover, fieldName, preCond, postCond);

      /* link-remover: overloaded for collection-typed parameter */
      mname = genMethodNameForField("onRemove", fieldName);
      mbody = "for (%s o : %s) {\n" +
                  " boolean removed = %s.remove(o); \n" +
                  " if (removed) %s--; \n" +
              " }\n "+
              " return false;";
      
      mbody = String.format(mbody, assocClsSimpleName, paramName, fieldName, linkCountFieldName);
      m = ParserToolkit.createSingleParamMethod(dcls, ParserConstants.TypeBool, mname, fieldType, paramName, Modifier.PUBLIC);
      ParserToolkit.createMethodBody(m, mbody);
      
      // v5.2:
      //genMethodTypeAnnotation(m, DOpt.Type.LinkRemover, fieldName);
      eopt = ecoreModel.addOperation(ecls, dcls, m); 
      preCond = ecoreModel.createPreConditionLinkRemoverCol(ecls, eopt, fieldName, paramName, linkCountFieldName);
      postCond = ecoreModel.createPostConditionLinkRemoverCol(ecls, eopt, fieldName, paramName, linkCountFieldName);
      ecoreModel.applyPreConditionOnOpt(ecls, eopt, preCond);
      ecoreModel.applyPostConditionOnOpt(ecls, eopt, postCond);
      
      genMethodTypeAnnotation(m, DOpt.Type.LinkRemover, fieldName, preCond, postCond);
      
      /* link-count-getter */
      mname = genMethodNameForField("get", linkCountFieldName);
      mbody = "return %s;";
      mbody = String.format(mbody, linkCountFieldName);
      m = ParserToolkit.createMethod(dcls, ParserConstants.TypeInteger, mname, Modifier.PUBLIC);
      ParserToolkit.createMethodBody(m, mbody);
      genMethodTypeAnnotation(m, DOpt.Type.LinkCountGetter, linkCountFieldName);//linkCountField.getName());
      
      /* link-count-setter */
      mname = genMethodNameForField("set", linkCountFieldName);
      mbody = "this.%s = %s;";
      mbody = String.format(mbody, linkCountFieldName, linkCountFieldName);
      m = ParserToolkit.createSingleParamMethod(dcls, ParserConstants.TypeVoid, mname, ParserConstants.TypeInt, linkCountFieldName, Modifier.PUBLIC);
      ParserToolkit.createMethodBody(m, mbody);
      genMethodTypeAnnotation(m, DOpt.Type.LinkCountSetter, linkCountFieldName);
    } else if (assoc.ascType().equals(AssocType.One2One)) {
      /*link-adder-new: delegate to setter method (above) */
      mname = genMethodNameForField("setNew", fieldName);
      paramName = "obj"; //"$1";
      mbody = "   %s(%s); \n" +
               "  return false;";
      mbody = String.format(mbody, setterName, paramName);
      m = ParserToolkit.createSingleParamMethod(dcls, ParserConstants.TypeBool, mname, cAssocCls, paramName, Modifier.PUBLIC);
      ParserToolkit.createMethodBody(m, mbody);
      genMethodTypeAnnotation(m, DOpt.Type.LinkAdderNew, fieldName);
    } // end association link operations    
  }

  /**
   * @modifies <tt>dcls, autoAttribGenOptMap</tt>
   * @effects 
   *  Generate in <tt>dcls</tt> a auto-attribute-value-generator for the auto field <tt>f</tt> and 
   *  add the generated method and its parameter map to <tt>autoAttribGenOptMap</tt>
   *  
   *  <p>throws NotFoundException if field type is not found
   * @version 
   * - 5.2: improved to support OCL pre- and post-conditions
   */
  private void genAutoAttribValueGen(ClassAST dcls, DAttr dc,
      FieldDef f,
      Map<LinkedHashMap<FieldDef, Type>, MethodDeclaration> autoAttribGenOptMap) throws NotFoundException {
    String fieldName = f.getName();
    String mname = genAutoAttributeValueGenMethodName(fieldName);
    String mbody; 
    
    Type fieldType = f.getType();
    
    LinkedHashMap<FieldDef, Type> paramTypeMap = 
        getAutoAttributeValueGenParamTypes(dcls, f, fieldType, dc);
    Type[] paramTypes = new Type[paramTypeMap.size()];
    String[] paramNames = new String[paramTypeMap.size()];
    int idx = 0;
    Type paramType;
    for (Entry<FieldDef,Type> e : paramTypeMap.entrySet()) {
      paramNames[idx] = e.getKey().getName();
      paramType = e.getValue();
      paramType = ParserToolkit.getObjectType(paramType); // use wrapper type if needed
      paramTypes[idx] = paramType;
      idx++;
    }
    
    /*
     * 2 cases of the body:
     *  if field's name is id and field is key and field.type is integer
     *    use standard body template for this field
     *  else
     *    body = null (to write manually)
     */
    // v5.2: support pre, post-conditions
    EClass ecls = ecoreModel.getClass(dcls);
    String preCond = null; 
    String postCond = null;

    if (isDefaultAutoIdField(dc)) {
      // field's name is id and field is key
      Type objectFieldType = ParserToolkit.getObjectType(fieldType);
      mbody =  "   %s val; \n" +
               "   if (%s == null) { \n " +
               "     %s++; \n " +
               "     val = %s; \n " +
               "   } else { \n" +
               "     if (%s > %s) { \n " +
               "       %s=%s; \n " +
               "     }    \n " +
               "     val = %s; \n " +
               "   } \n" +
               "   return val;";
      String firstParam = paramNames[0]; // $1
      mbody = String.format(mbody, 
          objectFieldType.toString(), firstParam, idCounterVar, 
          idCounterVar, firstParam, idCounterVar, idCounterVar, firstParam, firstParam);
      
      // v5.2:
      postCond = ecoreModel.createPostConditionAutoIdAttribValueGen(ecls, dcls, idCounterVar, firstParam);
    } else {
      // v5.0: improved to add a return statement after comment
      // mbody = "//TODO: implement this"; // to write manually
      mbody = "//TODO: implement this \n" + // to write manually
          "return " + ParserToolkit.getDefaultTypeValue(fieldType) + ";";

      // TODO: ? support post-condition for this type?
      /*
      String[] derivedParamNames = null; Type[] derivedParamTypes = null;
      if (paramNames.length > 1) {
        derivedParamNames = new String[paramNames.length-1]; // exclude the first param
        derivedParamTypes = new Type[paramTypes.length-1];
        System.arraycopy(paramNames, 1, derivedParamNames, 0, derivedParamNames.length);
        System.arraycopy(paramTypes, 1, derivedParamTypes, 0, derivedParamTypes.length);
      }
      postCond = ecoreModel.createPostConditionAutoNonIdAttribValueGen(ecls, dcls, derivedParamNames, derivedParamTypes);
      */
    }
    
    Class[] exceptions = null;
    MethodDeclaration m = ParserToolkit.createMethod(dcls, fieldType, mname, paramTypes, paramNames, exceptions, mbody, ParserConstants.modPrivateStatic);
    
    /* v5.2: added support for OCL pre-/post-conditions 
     */
    EOperation eopt = ecoreModel.addOperation(ecls, dcls, m);
    
    if (preCond != null)
      ecoreModel.applyPreConditionOnOpt(ecls, eopt, preCond);
    
    if (postCond != null) {
      ecoreModel.applyPostConditionOnOpt(ecls, eopt, postCond);
    }
    
    genMethodTypeAnnotation(m, DOpt.Type.AutoAttributeValueGen, fieldName, preCond, postCond);
    
    // add method to map
    autoAttribGenOptMap.put(paramTypeMap, m);    
  }

  /**
   * @requires <tt>FSA</tt> is not empty
   * @modifies <tt>dcls</tt>
   * 
   * @effects 
   *  create in <tt>dcls</tt> auto-attribute-value-synchroniser operation from the parameters specified by <tt>FSA</tt>
   *  <br>return the operation
   *  <p>throws CannotCompileException if failed 
   * 
   * @version
   * - 5.2: improved to support non-standard id field 
   * - 5.2: improved to support OCL pre- and post-conditions
   */
  private MethodDeclaration genAutoAttribValueSynch(ClassAST dcls, LinkedHashMap<DAttrDef, FieldDef> FSA) {
    String mname = "synchWithSource";
    String[] paramNames = {
        "attrib",
        "derivingValue",
        "minVal",
        "maxVal"
    };
    
    Type[] paramTypes = new Type[] {
        new ClassOrInterfaceType(DAttr.class.getSimpleName()),    // 1: attrib 
        new ClassOrInterfaceType(Tuple.class.getSimpleName()),    // 2: derivingValue
        new ClassOrInterfaceType(Object.class.getSimpleName()),   // 3: minVal
        new ClassOrInterfaceType(Object.class.getSimpleName()),   // 4: maxVal
      };
    
    //ParserToolkit.addImport(cu, Tuple.class);
    dcls.addImport(Tuple.class);
    
    /*
     * body consists of an if statement whose branches are mapped to the auto fields of the class
     *  - a special case is the first branch, which is mapped to the id field
     * e.g
        if (attrib.name().equals("id")) {
          int maxIdVal = (Integer) maxVal;
          if (maxIdVal > idCounter)  
            idCounter = maxIdVal;
          
        } else if (attrib.name().equals("code")) {
          // TODO: implement this
        }
     */
    StringBuffer mbody = new StringBuffer();
    String fieldName;
    DAttr dc;
    FieldDef field;

    mbody.append("String attribName = %s.name(); \n");
    
    // first add the if branch for the standard id field
    boolean hasDefaultIdField = false;
    for (Entry<DAttrDef,FieldDef> e : FSA.entrySet()) {
      dc = e.getKey();
      field = e.getValue();
      fieldName = field.getName();
      if (isDefaultAutoIdField(dc)) {
        hasDefaultIdField = true;
        mbody.append("if (attribName.equals(\"").append(fieldName).append("\")) { \n")
             .append("  int maxIdVal = (Integer) %s; \n")
             .append("  if (maxIdVal > ").append(idCounterVar).append(") \n")
             .append("    ").append(idCounterVar).append(" = maxIdVal; \n")
             .append("}");
      }
      break;
    }
    
    // then add other branches for other auto fields
    boolean firstField = true;
    for (Entry<DAttrDef,FieldDef> e : FSA.entrySet()) {
      dc = e.getKey();
      field = e.getValue();
      if (!isDefaultAutoIdField(dc)) {
        fieldName = field.getName();
        if (!hasDefaultIdField && firstField) {
          // first clause: use if
          mbody.append("if (attribName.equals(\"").append(fieldName).append("\")) { \n")
          .append("  //TODO: implement this \n")
          .append("}");
        } else {
          // not first clause: use else/if
          mbody.append("else if (attribName.equals(\"").append(fieldName).append("\")) { \n")
               .append("  //TODO: implement this \n")
               .append("}");
        }
        
        firstField = false;
      }
    }

    String mbodyStr = String.format(mbody.toString(), paramNames[0], paramNames[3]);

    //if (!mbodyStr.endsWith(";")) mbodyStr += ";";
    
    Class[] exceptions = { ConstraintViolationException.class }; 
    MethodDeclaration m = //CtNewMethod.make(modPublicStatic, CtClass.voidType, mname, paramTypes, exceptions, mbody.toString(), dcls);
        ParserToolkit.createMethod(dcls, ParserConstants.TypeVoid, mname, paramTypes, paramNames, exceptions, mbodyStr, ParserConstants.modPublicStatic);
    genMethodTypeAnnotation(m, DOpt.Type.AutoAttributeValueSynchroniser);
    
    return m;
  }

  /**
   * @effects 
   *   if the field whose domain constraint is <tt>dc</tt> is the <b>default</b> id field of a domain class
   *      return <tt>true</tt>
   *   else 
   *      return <tt>false</tt>
   */
  private boolean isDefaultAutoIdField(DAttr dc) {
    return dc.id() && dc.type().isInteger() && dc.name().equals("id");
  }

  /**
   * @modifies m
   *  Generate an instance of {@link DOpt} the value of whose type is <tt>optType</tt> and 
   *  attach it to <tt>m</tt>
   */
  private void genMethodTypeAnnotation(BodyDeclaration m, DOpt.Type optType) {
    genMethodTypeAnnotation(m, optType, null, null, null);
  }

  /**
   * @modifies m
   * @effects 
   *  Generate an instance of {@link DOpt} the value of whose type is <tt>optType</tt>
   *  if <tt>refAttribName != null</tt>  
   *    generate an instance of {@link AttrRef} for <tt>refAttribName</tt>
   *    
   *  attach both annotations to <tt>m</tt>
   */
  private void genMethodTypeAnnotation(BodyDeclaration m, DOpt.Type optType, String refAttribName) {
    /*5.2
    ParserToolkit.createMethodAnnotation(m, DOpt.class, 
        new String[] {"type", DOpt.class.getSimpleName()+"."+DOpt.Type.class.getSimpleName()+"."+optType.name()});
    
    // if refAttribName is specified then create member-ref annotation
    if (refAttribName != null) {
      ParserToolkit.createMethodAnnotation(m, AttrRef.class, 
          new String[] {"value", "\""+refAttribName+"\""});
    }
    */
    genMethodTypeAnnotation(m, optType, refAttribName, null, null);
  }
  
  /**
   * @requires <tt>preCond, postCond</tt>, if specified, then must be valid OCL expressions on <tt>m</tt>
   * 
   * @modifies m
   * @effects 
   *  Generate an instance of {@link DOpt} the value of whose type is <tt>optType</tt>
   *  if <tt>refAttribName != null</tt>  
   *    generate an instance of {@link AttrRef} for <tt>refAttribName</tt>
   *  
   *  If <tt>preCond, postCond</tt> are specified then add them to the DOpt.
   *   
   *  attach both annotations to <tt>m</tt>
   *  
   * @version 5.2
   */
  private void genMethodTypeAnnotation(BodyDeclaration m, DOpt.Type optType, 
      String refAttribName, String preCond, String postCond) {
    
    /*
    String[][] propValPairs = new String[][] {
      {"type", DOpt.class.getSimpleName()+"."+DOpt.Type.class.getSimpleName()+"."+optType.name() }
      ,{"requires", "\""+((preCond != null) ? preCond : MetaConstants.DEFAULT_DOPT_REQUIRES) + "\""}
      ,{ "effects", "\"" + ((postCond != null) ? postCond : MetaConstants.DEFAULT_DOPT_EFFECTS) + "\""}
        }
        ;
    */
    LinkedHashMap<String,Expression> propValPairLst = new LinkedHashMap<>();
    propValPairLst.put("type", 
            //v5.2: DOpt.class.getSimpleName()+"."+DOpt.Type.class.getSimpleName()+"."+optType.name()
            ParserToolkit.convertAnoPropVal2Expr(optType)
    );

    if (preCond != null) {
      propValPairLst.put("requires", ParserToolkit.convertAnoPropVal2Expr(preCond));
    }

    if (postCond != null) {
      propValPairLst.put("effects", ParserToolkit.convertAnoPropVal2Expr(postCond));
    }

    ParserToolkit.createMethodAnnotation(m, DOpt.class, propValPairLst);
    
    // if refAttribName is specified then create member-ref annotation
    if (refAttribName != null) {
      /*
      ParserToolkit.createMethodAnnotationSimple(m, AttrRef.class, 
          new String[] {"value", "\""+refAttribName+"\""});
          */
      propValPairLst = new LinkedHashMap<>();
      propValPairLst.put("value", 
              ParserToolkit.convertAnoPropVal2Expr(refAttribName)
      );
      ParserToolkit.createMethodAnnotation(m, AttrRef.class, propValPairLst);
    }
  }
}
