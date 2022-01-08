package jda.modules.dcsltool.transform.ecore;

import static jda.modules.dcsl.parser.ParserToolkit.getReferencedType;
import static jda.modules.dcsl.parser.ParserToolkit.getReferencedTypeName;
import static jda.modules.dcsl.parser.ParserToolkit.getTypeName;
import static jda.modules.dcsl.parser.ParserToolkit.isTypeBoolean;
import static jda.modules.dcsltool.transform.ecore.EcoreOCLToolkit.Ocllib;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.types.CollectionType;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;

import javafx.beans.NamedArg;
import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.parser.ClassAST;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.statespace.metadef.AssociateDef;
import jda.modules.dcsl.parser.statespace.metadef.DAssocDef;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DCSLToolkit;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsltool.exceptions.OCLException;

/**
 * @overview 
 *  Represents sets of Ecore's elements forming a model.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2 
 */
public class EcoreModel {
  private Map<Object, ENamedElement> ecoreMap;

  public static final EcoreFactory Ecoref = EcoreFactory.eINSTANCE;

  
  /**
   * An {@link OCLHelper} used for processing OCL constraints generated during the execution of this
   * @version 5.2
   */
  private static OCLHelper oclHelper;

  static {
    oclHelper = EcoreOCLToolkit.Ocl.createOCLHelper();
  }
  
  public EcoreModel() {
    ecoreMap = new HashMap<>();
  }
  
//  /**
//   * @effects 
//   * 
//   * @version 
//   * 
//   */
//  public ENamedElement get(Object key) {
//    return ecoreMap.get(key);
//  }
//
//  /**
//   * @effects 
//   * 
//   */
//  public void put(Object key, ENamedElement element) {
//    ecoreMap.put(key, element);
//  }

  /**
   * @effects 
   *  sets this to empty
   */
  public void clear() {
    ecoreMap.clear();
  }

  /**
   * @effects 
   *  if exists <tt>(fqn: String, ec: EClass)</tt> in this
   *    return ec
   *  else 
   *    transform <tt>dcls</tt> into an ec: EClass and add (fqn = dcls.getFqn(), ec) to this.
   *  
   *  return ec
   */
  public EClass addClass(ClassAST dcls) {
    String fqn = dcls.getFqn();
    
    EClass eclazz = (EClass) ecoreMap.get(fqn);
    
    if (eclazz == null) {
      // create it
      eclazz = transfClassSrc2EClass(dcls);
      ecoreMap.put(fqn, eclazz);
    }
    
    return eclazz;
  }

  /**
   * @effects 
   *  if exists <tt>(cls, ec: EClass)</tt> in this
   *    return ec
   *  else 
   *    transform <tt>cls</tt> into an ec: EClass and add (cls, ec) to this.
   *  
   *  return ec
   */
  public EClass addClass(Class cls) {
    EClass ecls = (EClass) ecoreMap.get(cls); 
    if (ecls == null) {
      ecls = Ecoref.createEClass();
      ecls.setName(cls.getSimpleName());
      
      /* TODO: create package
      Package pkg = cls.getPackage();
      EPackage epkg = EPackage.Registry.INSTANCE.get(pkg); // look up package...
      if (epkg = null) {
        ecoref.createEPackage();
      }
      */
      ecoreMap.put(cls, ecls);
    }
    
    return ecls;
  }
  
  public EClass addType(Type type) {
    String typeName = ParserToolkit.getReferencedTypeName(type);
    EClass ecls = (EClass) ecoreMap.get(typeName); 

    if (ecls == null) {
      ecls = Ecoref.createEClass();
      
      ecls.setName(typeName);
      
      /* TODO: create package
      Package pkg = cls.getPackage();
      EPackage epkg = EPackage.Registry.INSTANCE.get(pkg); // look up package...
      if (epkg = null) {
        ecoref.createEPackage();
      }
      */
      ecoreMap.put(typeName, ecls);
    }
    
    return ecls;
  }
  
  /**
   * @effects 
   *  add to {@link #ecoreMap} an {@link EGenericType} representing <tt>type</tt>, 
   *  whose bound parameter is set to <tt>refType</tt>
   *  
   *  return the {@link EClassifier} of this type.
   */
  public EClassifier addGenericType(Type type, Type refType) {
    String typeName = getTypeName(type);
    String refTypeName = getTypeName(refType);
    
    String genTypeName = typeName + "<"+refTypeName+">";
    
    EClassifier ecls = (EClass) ecoreMap.get(genTypeName); 
    EClass refCls = (EClass) ecoreMap.get(refTypeName);
    
    if (refCls == null) { // ASSUME: refType is not generic!
      refCls = addType(refType);
    }
    
    if (ecls == null) {
      //EGenericType genType = Ecoref.createEGenericType();
      //genType.setEClassifier(eCls);

      EClass eCls = Ecoref.createEClass();
      eCls.setName(typeName);

      ETypeParameter tparam = Ecoref.createETypeParameter();
      //tparam.getEBounds().add(genType);
      
      ecls.getETypeParameters().add(tparam);
      
      //genType.setETypeParameter(tparam);
      
      /* TODO: create package
      Package pkg = cls.getPackage();
      EPackage epkg = EPackage.Registry.INSTANCE.get(pkg); // look up package...
      if (epkg = null) {
        ecoref.createEPackage();
      }
      */
      ecoreMap.put(genTypeName, ecls);
    }
    
    return ecls;
  }

  
  /**
   * @requires domain classes referenced by dcls have been added to this 
   * @effects 
   *  transform each method in <tt>dcls</tt> into an {@link EOperation} and add it into this.
   *  
   *  Throws NotFoundException if a referenced domain class is not in this.
   */
  public void addOperationsOf(ClassAST dcls) throws NotFoundException {
    // add dcls's methods to this as EOperations
    Collection<MethodDeclaration> methods = dcls.getDomainMethods();
    if (methods != null) {
      EClass ecls = getClass(dcls);
      if (ecls == null)
        throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, new String[] {dcls.getFqn()});
      
      for (MethodDeclaration m : methods) {
        addOperation(ecls, dcls, m);
      }
    }
  }

  /**
   * @requires domain classes referenced by dcls have been added to this 
   * @effects 
   *  transform each method in <tt>dcls</tt> into an {@link EOperation} and add it into this.
   *  
   *  Throws NotFoundException if a referenced domain class is not in this.
   */
  public void addFieldsOf(ClassAST dcls, EClass ecls) throws NotFoundException {
    // add dcls's methods to this as EOperations
    Map<DAttrDef,FieldDef> fields = dcls.getStateSpace();
    if (fields != null) {
      for (Entry<DAttrDef,FieldDef> e : fields.entrySet()) {
        FieldDef f = e.getValue();
        addField(ecls, dcls, f);
      }
    }
  }
  
  /**
   * @requires (ecls, dcls) in {@link #ecoreMap} /\ f is a field of dcls /\   
   *  data type referenced by f (if any) has been added to this 
   * @effects 
   *  transform <tt>f</tt> into an {@link EAttribute} and add it into this.
   *  
   *  Throws NotFoundException if a referenced domain class is not in this.
   */
  public EAttribute addField(EClass ecls, ClassAST dcls, FieldDef field) {
    EAttribute attr = Ecoref.createEAttribute();
    attr.setName(field.getName());
    
    // field type
    Type ftype = field.getType();
    EClassifier etype = transfTypeSrc2EClass(ftype);
    attr.setEType(etype);
    
    // field constraints
    // DAttr
    DAttrDef dattr = (DAttrDef) field.getAnnotation(DAttr.class);
    if (dattr != null) {
      EAnnotation eattr = Ecoref.createEAnnotation();
      eattr.setEModelElement(attr);
      attr.getEAnnotations().add(eattr);
      
      Collection<Entry<String,Object>> props = dattr.getProperties();
      
      EMap<String,String> eprops = eattr.getDetails();
      for (Entry<String,Object> prop : props) {
        eprops.put(prop.getKey(), ParserToolkit.convertPropValuetoString(prop.getValue()));        
      }
    }

    DAssocDef dassoc = (DAssocDef) field.getAnnotation(DAssoc.class);
    if (dassoc != null) {
      EAnnotation eattr = Ecoref.createEAnnotation();
      eattr.setEModelElement(attr);
      attr.getEAnnotations().add(eattr);
      
      Collection<Entry<String,Object>> props = dassoc.getProperties();
      
      EMap<String,String> eprops = eattr.getDetails();
      for (Entry<String,Object> prop : props) {
        Object propVal = prop.getValue();
        
        String propValStr;
        if (propVal instanceof AssociateDef) {
          // associate: merge key properties into eprops
          AssociateDef assocDef = (AssociateDef) propVal;
          Collection<Entry<String,Object>> assocProps = assocDef.getProperties();
          for (Entry<String,Object> aprop : assocProps) {
            String pkey = aprop.getKey();
            Object pval = aprop.getValue();
            if (DCSLToolkit.isEssentialAssociateProperty(pkey)) {
              propValStr = ParserToolkit.convertPropValuetoString(pval);//pval + "";
              eprops.put(pkey, propValStr);                  
            }
          }
        } else {
          propValStr = ParserToolkit.convertPropValuetoString(propVal);//propVal + "";
          eprops.put(prop.getKey(), propValStr);                  
        }
      }
    }
    
    // add attr to ecls
    ecls.getEStructuralFeatures().add(attr);
    
    return attr;
  }


  /**
   * A variant of {@link #addField(EClass, ClassAST, FieldDef)}.
   * 
   * @effects 
   *  see {@link #addField(EClass, ClassAST, FieldDef)} 
   */
  public EAttribute addField(EClass ecls, ClassAST dcls, FieldDeclaration field) {

    //TODO: create a FieldDef from field
    FieldDef fieldDef = ParserToolkit.getFieldDefFull(field);
    
    EAttribute eattrib = addField(ecls, dcls, fieldDef);
    
    // TODO: if field is static the set eattrib accordingly!
    if (field.isStatic()) {
      // eattrib...
    }
    
    return eattrib;
  }
  
  /**
   * @requires m is a method of dcls /\ (ecls, dcls) in {@link #ecoreMap} /\  
   *  domain classes referenced by m's parameters have been added to this 
   * @effects 
   *  transform <tt>m</tt> into an {@link EOperation} and add it into this.
   *  
   *  Throws NotFoundException if a referenced domain class is not in this.
   */
  public EOperation addOperation(EClass ecls, ClassAST dcls, CallableDeclaration m) {
    EOperation opt = transfMethodSrc2EOperation(ecls, m);
    
    String mfqn = dcls.getMethodFqn(m);
    ecoreMap.put(mfqn, opt);
    
    return opt;
  }


  /**
   * @modifies {@link #ecoreMap}
   * @effects 
   *  create and return an {@link EOperation} in <tt>ecls</tt> for <tt>m</tt>
   */
  public EOperation addOperation(EClass ecls, Method m) {
    EOperation opt = transfMethod2EOperation(ecls, m);
    
    ecoreMap.put(m, opt);
    
    return opt;
  }
  
  /**
   * @modifies cls
   * @effects 
   *  create and add to <tt>cls</tt> an {@link EOperation} from the input parameters
   */
  public void createEOperation(EClassifier cls, EClassifier returnType,
      String name) {
    EOperation opt = Ecoref.createEOperation();
    cls.eContents().add(opt);
    
    opt.setName(name);
    opt.setEType(returnType);
  }
  
  /**
   * @effects 
   *   if exists (ec: {@link EClass}, dcsl) in {@link #ecoreMap}
   *    return dc
   *   else
   *    return null
   */
  public EClass getClass(ClassAST dcls) {
    String fqn = dcls.getFqn();
    
    return (EClass) ecoreMap.get(fqn);
  }

  /**
   * @effects 
   *  if exists (em : EOperation, m) in {@link #ecoreMap}
   *    return em
   *  else
   *    return null
   */
  public EOperation getOperation(ClassAST dcls, MethodDeclaration m) {
    String mfqn = dcls.getMethodFqn(m);
    
    return (EOperation) ecoreMap.get(mfqn);
  }


  /**
   * @requires <tt>ecls</tt> is an EClass corresponding to <tt>dcls</tt>
   * @effects 
   *  if exists in <tt>ecls</tt> {@link EOperation} corresponding to an auto-attribute-gen operation 
   *  of attribute named <tt>attribName</tt>
   *    return it
   *  else
   *    return null
   */
  public EOperation getAutoAttribGenOperationFor(ClassAST dcls,
      EClassifier ecls, String attribName) {
    MethodDeclaration m = dcls.getDomainMethodByAttrRef(DOpt.Type.AutoAttributeValueGen, 
        attribName);
    
    if (m == null) return null;

    EOperation opt = getOperation(dcls, m);
    
    return opt;
  }
  
  /**
   * @effects 
   *   Use {@link #oclHelper} to create and return a pre-condition for the current context.
   *  
   *  Throws OCLException if fails, NotFoundException if no attribute in <tt>ecls</tt> has 
   *  the same name as <tt>fieldName</tt>.
   *   
   */
  public String createAndApplyPreConditionSetter(EClass ecls, EOperation eopt,
      String fieldName) throws OCLException, NotFoundException {
    // read field's domain constraint and use it to write an OCL on eopt's parameter.
    // Generally, the OCL expression is an infix expression, whose operator depends on 
    // the field's data type
    // e.g.: if fieldName = "name" (typed: String, length=30) /\ eopt's parameter = n
    //  -> OCL: n.size() <= 30
    
    //TODO: assume setter eopt has one parameter
    String paramName = eopt.getEParameters().get(0).getName();

    EAttribute attrb = (EAttribute) ecls.getEStructuralFeature(fieldName);
    
    if (attrb == null) {
      // should not happen
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND, new String[] {fieldName, ecls.getName()});
    }
    
    String ocl = createPreConditionOnOptParam(ecls, attrb, paramName);
    
    if (ocl != null) { 
      applyPreConditionOnOpt(ecls, eopt, ocl);
      return ocl;
    } else
      return null;
  }

  /**
   * @effects 
   *    create and return a getter's OCL post-condition based on <tt>fieldName</tt>
   *    suitable for <tt>eopt</tt>.
   *    
   *  Throws OCLException if fails.
   *    
   */
  public String createAndApplyPostConditionGetter(EClass ecls, EOperation eopt,
      String fieldName) throws OCLException {
    
    String ocl = "result = " + fieldName;

    applyPostConditionOnOpt(ecls, eopt, ocl);
    
    return ocl;
  }
  
  /**
   * @effects 
   *  if max cardinality (maxCard) of Field(attribName) is specified
   *    create and return an OCL expression that Field(linkCountFieldName) + 1 <= maxCard
   *  else
   *    return null
   *     
   * @example: <pre>
   * eopt = addEnrolments, attribName = enrolments, paramName = obj, linkCountFieldName = enrolmentsCount;
   * 
   * let maxCard = DAssoc(Field(enrolments)).associate.maxCard
   * if maxCard is specified
   *  result = "if enrolments->excludes(obj) then enrolmentsCount + 1 <= maxCard else true endif"
   * else
   *  result = null ("true") 
   * </pre>
   */
  public String createPreConditionLinkAdderSingle(EClass ecls, EOperation eopt,
      String attribName, String paramName, String linkCountFieldName) {
    EAttribute attrib = (EAttribute) ecls.getEStructuralFeature(attribName);

    // read maxCard = DAssoc(Field(fieldName)).associate.maxCard
    EAnnotation dassoc = attrib.getEAnnotations().get(1);
    //EClassifier attrType = attrib.getEType();
    EMap<String,String> attrProps = dassoc.getDetails();
    String ocl = null;

    String cardMaxStr = attrProps.get("cardMax");
    if (cardMaxStr != null && !DCSLToolkit.isCardMore(cardMaxStr)) {
      // cardMax is specified
      int cardMax = Integer.parseInt(cardMaxStr);
      
      // result = "enrolmentsCount + 1 <= maxCard"
      ocl = "if %s->excludes(%s) then %s + 1 <= %s else true endif"; 
      ocl = String.format(ocl, 
          attribName, paramName, linkCountFieldName, cardMax+"");
    }
    
    return ocl;
  }
  
  /**
   * @effects 
   *  if max cardinality (maxCard) of Field(attribName) is specified
   *    create and return an OCL expression that 
   *      Field(linkCountFieldName) + Parameter(paramName).size() <= maxCard
   *  else
   *    return null
   *     
   * @example: <pre>
   * eopt = addEnrolments, attribName = enrolments,
   * paramName = enrols,  
   * linkCountFieldName = enrolmentsCount;
   * 
   * let maxCard = DAssoc(Field(enrolments)).associate.maxCard
   * if maxCard is specified
   *  result = "enrolmentsCount + enrols->select(o | enrolments->excludes(o)).size() <= maxCard"
   * else
   *  result = null ("true") 
   * </pre>
   */
  public String createPreConditionLinkAdderCol(EClass ecls, EOperation eopt,
      String attribName, String paramName, String linkCountFieldName) {
    EAttribute attrib = (EAttribute) ecls.getEStructuralFeature(attribName);

    // read maxCard = DAssoc(Field(fieldName)).associate.maxCard
    EAnnotation dassoc = attrib.getEAnnotations().get(1);
    //EClassifier attrType = attrib.getEType();
    EMap<String,String> attrProps = dassoc.getDetails();
    String ocl = null;

    String cardMaxStr = attrProps.get("cardMax");
    if (cardMaxStr != null && !DCSLToolkit.isCardMore(cardMaxStr)) {
      // cardMax is specified
      int cardMax = Integer.parseInt(cardMaxStr);
      
      // result = "enrolmentsCount + enrols->size() <= maxCard"
      ocl = "%s + %s->select(o | %s->excludes(o))->size() <= %s"; 
      ocl = String.format(ocl, 
          linkCountFieldName, paramName, attribName, cardMax+"");
    }
    
    return ocl;
  }

  /**
   * @effects 
   *  return domain-specific OCL constraint for pre-condition
   */
  public String createPreConditionLinkUpdaterSingle(EClass ecls,
      EOperation eopt, String attribName, String paramName,
      String linkCountFieldName) {
    //TODO: for domain-specific rules
    String ocl = null;
    
    return ocl;
  }
  
  /**
   * @effects 
   *  if min cardinality (minCard) of Field(attribName) is specified
   *    create and return an OCL expression that 
   *      if Field(attribName) contains Parameter(paramName) then 
   *        Field(linkCountFieldName) - 1 >= minCard
   *  else
   *    return null
   *     
   * @example: <pre>
   * eopt = removeEnrolments, attribName = enrolments, paramName = obj, linkCountFieldName = enrolmentsCount;
   * 
   * if minCard of Field(enrolments) is specified
   *  result = "if enrolments->includes(obj) then enrolmentsCount - 1 >= minCard else true endif"
   * else
   *  result = null ("true") 
   * </pre>
   */
  public String createPreConditionLinkRemoverSingle(EClass ecls,
      EOperation eopt, String attribName, String paramName,
      String linkCountFieldName) {
    EAttribute attrib = (EAttribute) ecls.getEStructuralFeature(attribName);

    // read minCard = DAssoc(Field(fieldName)).associate.minCard
    EAnnotation dassoc = attrib.getEAnnotations().get(1);
    //EClassifier attrType = attrib.getEType();
    EMap<String,String> attrProps = dassoc.getDetails();
    String ocl = null;

    String cardMinStr = attrProps.get("cardMin");
    int cardMin = Integer.parseInt(cardMinStr);
    // result = "if enrolments->includes(obj) then enrolmentsCount - 1 >= minCard else true endif"
    ocl = "if %s->includes(%s) then %s - 1 >= %s else true endif"; 
    ocl = String.format(ocl, 
        attribName, paramName, linkCountFieldName, cardMin+"");
    
    return ocl;
  }


  /**
   * @effects 
   *  if min cardinality (minCard) of Field(attribName) is specified
   *    create and return an OCL expression that 
   *      if Field(attribName) contains some objects in Parameter(paramName) then 
   *        and Field(linkCountFieldName) - no of those objects >= minCard
   *  else
   *    return null
   *     
   * @example: <pre>
   * eopt = removeEnrolments, attribName = enrolments, paramName = enrols, linkCountFieldName = enrolmentsCount;
   * 
   * if minCard of Field(enrolments) is specified
   *  result = "enrolmentsCount - enrols->select(o | enrolments->includes(o))->size() >= minCard"
   * else
   *  result = null ("true") 
   * </pre>
   */
  public String createPreConditionLinkRemoverCol(EClass ecls, EOperation eopt,
      String attribName, String paramName, String linkCountFieldName) {
    EAttribute attrib = (EAttribute) ecls.getEStructuralFeature(attribName);

    // read minCard = DAssoc(Field(fieldName)).associate.minCard
    EAnnotation dassoc = attrib.getEAnnotations().get(1);
    //EClassifier attrType = attrib.getEType();
    EMap<String,String> attrProps = dassoc.getDetails();
    String ocl = null;

    String cardMinStr = attrProps.get("cardMin");
    int cardMin = Integer.parseInt(cardMinStr);
    // result = "enrolmentsCount - enrols->select(o | enrolments->includes(o)).size() >= minCard"
    ocl = "%s - %s->select(o | %s->includes(o))->size() >= %s"; 
    ocl = String.format(ocl, 
        linkCountFieldName, paramName, attribName, cardMin+"");
    
    return ocl;
  }

  /**
   * @effects 
   *  Create an OCL pre-condition for parameter whose name is <tt>paramName</tt>
   *  based on the constraints of the referenced attribute named <tt>attribName</tt> in <tt>ecls</tt>.
   *  If such pre-condition exists then return it, else return null. 
   */
  public String createPreConditionOnOptParam(EClass ecls, String attribName, String paramName) {
    EAttribute attrib = (EAttribute) ecls.getEStructuralFeature(attribName);
    
    return createPreConditionOnOptParam(ecls, attrib, paramName);
  }
  
  /**
   * @requires parameter named <tt>paramName</tt> references attribute <tt>attrib</tt>
   * @effects 
   *   create and return OCL expression expressing the pre-condition for the parameter named <tt>paramName</tt>
   */
  public String createPreConditionOnOptParam(EClassifier ecls, EAttribute attrib, String paramName) {
    EAnnotation dattr = attrib.getEAnnotations().get(0);
    EClassifier attrType = attrib.getEType();
    EMap<String,String> attrProps = dattr.getDetails();
    String ocl = null;

    if (this.isTypeObject(attrType)) {
      boolean optional = Boolean.parseBoolean(attrProps.get("optional"));
      
      if (!optional) {
        String notOpt;
        if (this.isTypeCollection(attrType)) { // collection
          notOpt = "not " + paramName + ".isEmpty()";
        } else {  // non-collection
          notOpt = paramName + " <> null"; // "not " + paramName + ".oclIsUndefined()";
        }
        
        ocl = (ocl != null ? ocl + " and " + notOpt : notOpt);
      }
    }
    
    // length, min, max
    if (this.isTypeString(attrType)) {
      int len = Integer.parseInt(attrProps.get("length"));
      if (len != DCSLConstants.DEFAULT_DATTR_LENGTH) {
        String lenOcl = paramName + ".size() <= " + len;
        ocl = (ocl != null ? ocl + " and " + lenOcl : lenOcl); 
      }
    } else if (this.isTypeNumeric(attrType)) {
      String minVal = attrProps.get("min");
      String maxVal = attrProps.get("max");
      double min, max;
      boolean isIntegral = this.isTypeIntegral(attrType);
      
      if (minVal != null) {
        min = Double.parseDouble(minVal);
      } else {
        min = CommonConstants.DEFAULT_MIN_VALUE;
      }
          
      if (maxVal != null) {
        max = Double.parseDouble(maxVal);
      } else {
        max = CommonConstants.DEFAULT_MAX_VALUE;
      }
      
      String minStr = null, maxStr = null;
      if (min != CommonConstants.DEFAULT_MIN_VALUE) {
        if (isIntegral) {
          minStr = paramName + " >= " + ((int) min);          
        } else {
          minStr = paramName + " >= " + min;                    
        }
      }

      if (max != CommonConstants.DEFAULT_MAX_VALUE) {
        if (isIntegral) {
          maxStr = paramName + " <= " + ((int) max);          
        } else {
          maxStr = paramName + " <= " + max;          
        }
      }
      
      String mxOcl = null;
      if (minStr != null && maxStr != null) {
        mxOcl = minStr + " and " + maxStr;
      } else if (minStr != null) {
        mxOcl = minStr;
      } else if (maxStr != null) {
        mxOcl = maxStr;
      }
      
      if (mxOcl != null) {
        ocl = (ocl != null ? ocl + " and " + mxOcl : mxOcl);
      }
    } 
    
    // TODO: support other types here
    
    return ocl;
  }

  /**
   * @effects 
   *  Create an OCL post-condition for parameter whose name is <tt>paramName</tt>
   *  based on the constraints of the referenced attribute named <tt>attribName</tt> in <tt>ecls</tt>.
   *  If such pre-condition exists then return it, else return null. 
   */
  public String createPostConditionOnOptParam(ClassAST dcls, EClass ecls, String attribName, String paramName,
      Map<DAttrDef, FieldDef> cxtFieldsMap) {
    EAttribute attrib = (EAttribute) ecls.getEStructuralFeature(attribName);
    
    return createPostConditionOnOptParam(dcls, ecls, attrib, paramName, cxtFieldsMap);
  }
  
  /**
   * @effects 
   *  Create an OCL post-condition for parameter whose name is <tt>paramName</tt>
   *  based on the constraints of the referenced attribute <tt>attrib</tt>.
   *  If such pre-condition exists then return it, else return null. 
   */
  public String createPostConditionOnOptParam(ClassAST dcls, EClassifier ecls,
      EAttribute attrib, String paramName, Map<DAttrDef, FieldDef> cxtFieldsMap) {
    EAnnotation dattr = attrib.getEAnnotations().get(0);
    EMap<String,String> attrProps = dattr.getDetails();
    String ocl = null;
    
    String attribName = attrib.getName();
    
    // (1) DAttr(attrib).auto = true then use the auto-gen operation in ecls
    // e.g. self.id = genId()
    boolean auto = Boolean.parseBoolean(attrProps.get("auto"));
    if (auto) {
      // auto attribute
      // Note: if there are derived attributes then add them to the operation call
      String derivedFrom = (String) attrProps.get("derivedFrom"); // comma-separated
//      String[] derivedAttribs = null;
//      if (derivedFrom != null) {
//        derivedAttribs = derivedFrom.split(",");
//      }
      EOperation autoGenOp = getAutoAttribGenOperationFor(dcls, ecls, attribName);
      if (autoGenOp != null) {
        if (paramName != null) { // normal case
          ocl = "self." + attribName + " = " + autoGenOp.getName() + "("+paramName;
        } else { // exception case (for auto attribute that is updated by autoGenOp)
          ocl = "self." + attribName + " = " + autoGenOp.getName() + "(null";          
        }
        if (derivedFrom != null) {
          String[] derivedAttribs = derivedAttribs = derivedFrom.split(",");
          for (String derAttrib : derivedAttribs) {
            // if derAttrib is in cxtFieldsMap then adds it to ocl as parameter, otherwise uses null
            if (ParserToolkit.isAttribInCxt(derAttrib, cxtFieldsMap)) {
              ocl += "," + derAttrib;  
            } else {
              ocl += "," + null;
            }
          }
        }
        ocl +=")";
      } else { // no auto-gen method
        ocl = null; // TODO: what's a better alternative ?
      }
    } else { // non-atuo attribute
      // (2) e.g. self.name = n
      if (paramName != null) { // normal case
        ocl = "self." + attribName + " = " + paramName;
      } else { // exceptional case
        ocl = null; // not specified
      }
      
    }
    
    return ocl;
  }

  /**
   * @effects 
   *  create and return a setter's OCL post-condition based on <tt>fieldName</tt>
   *    suitable for <tt>eopt</tt>.
   *    
   *  Throws OCLException if fails.
   */
  public String createPostConditionSetter(EClass ecls, EOperation eopt,
      String fieldName) throws OCLException {
    
    //TODO: assume setter eopt has one parameter
    String paramName = eopt.getEParameters().get(0).getName();
    
    String ocl = "self."+fieldName+" = " + paramName;
    
    applyPostConditionOnOpt(ecls, eopt, ocl);
    
    return ocl;
  }

  /**
   * @effects 
   *  create and return an OCL expression suitable for automatically updating the <tt>idCounterVar</tt>
   *  of <tt>ecls</tt> 
   */
  public String createPostConditionAutoIdAttribValueGen(EClass ecls,
      ClassAST dcls, String idCounterVar, String paramName) {
    String clsName = ecls.getName();
    String idCountVarFull = idCounterVar; //clsName+"::"+idCounterVar;
    
    // e.g. 
    /* 
     if id = null then 
       Student::idCount = Student::idCount +1 and result = Student::idCount"
     else if id > Student::idCount then 
       Student::idCount = id and result = id
     else
       result = id
     endif endif
     */
    String ocl = "if %s = null then "
                  + "%s = %s + 1 and result = %s "
                  + "else if %s > %s then "
                  + "%s = %s and result = %s "
                  + "else "
                  + "result = %s "
                  + "endif endif";
    ocl = String.format(ocl, 
        paramName,
        idCountVarFull, idCountVarFull, idCountVarFull,
        paramName, idCountVarFull,
        idCountVarFull, paramName, paramName,
        paramName
        );
    
    return ocl;
  }
  
  /**
   * @effects 
   *  Create and return an OCL expression that 
   *    Field(attribName) has been added with a new element specified by paramName and 
   *    Field(linkCountFieldName) has been incremented by 1 
   *    
   *  else
   *    return null
   *     
   * @example: <pre>
   * eopt = addEnrolments, attribName = enrolments, paramName = e, linkCountFieldName = enrolmentsCount;
   * 
   *  result = 
   *    "enrolments = enrolments@pre->asSet()->union(Set{e}) and 
   *      enrolmentCount = enrolmentCount@pre + 1"
   * </pre>
   */
  public String createPostConditionLinkAdderSingle(EClass ecls, EOperation eopt,
      String attribName, String paramName, String linkCountFieldName) {
    /* TODO: this is the expected (short) OCL but fails in Ecore because it does 
     * does not support Collection->asSet()
     String ocl = "%s = %s@pre->asSet()->union(Set{%s}) and %s = %s@pre + 1";
     ocl = String.format(ocl, attribName, attribName, paramName, linkCountFieldName, linkCountFieldName);
    */

    // alternative OCL (same semantics)
    /*
     * enrolments->forAll(o | enrolments@pre->includes(o) or obj = o) and 
        enrolmentsCount = enrolmentsCount@pre + (enrolments->size() - enrolments@pre->size())
     */
    String ocl = 
        "%s->forAll(o | %s@pre->includes(o) or %s = o) and " + 
            "%s = %s@pre + (%s->size() - %s@pre->size())";
    ocl = String.format(ocl, 
        attribName, attribName, paramName,
        linkCountFieldName, linkCountFieldName, attribName, attribName);

    return ocl;
    
  }
  
  /**
   * @effects 
   *  Create and return an OCL expression that 
   *    Field(attribName) has been added with a new elements specified by paramName and 
   *    Field(linkCountFieldName) has been incremented by the number of those elements
   *    
   *  else
   *    return null
   *     
   * @example: <pre>
   * eopt = addEnrolments, attribName = enrolments, paramName = enrols, linkCountFieldName = enrolmentsCount;
   * 
   *  result = 
   *    enrolments = enrolments@pre->asSet()->union(enrols->asSet()) and 
   *    enrolmentCount = enrolmentCount@pre + enrols->size()
   * 
   * </pre>
   */
  public String createPostConditionLinkAdderCol(EClass ecls, EOperation eopt,
      String attribName, String paramName, String linkCountFieldName) {
    /* TODO: this is the expected (short) OCL but fails in Ecore because it does 
     * does not support Collection->asSet() 
     String ocl = "%s = %s@pre->asSet()->union(%s->asSet()) and %s = %s@pre + %s->size()";
     ocl = String.format(ocl, 
         attribName, attribName, paramName, 
         linkCountFieldName, linkCountFieldName, 
         paramName);
     */
    // alternative OCL (same semantics)
    /*
     * enrolments->forAll(o | enrolments@pre->includes(o) or obj->includes(o)) and 
        enrolmentsCount = enrolmentsCount@pre + (enrolments->size() - enrolments@pre->size())
     */    
    String ocl = 
            "%s->forAll(o | %s@pre->includes(o) or %s->includes(o)) and " + 
            "%s = %s@pre + (%s->size() - %s@pre->size())";
    ocl = String.format(ocl, 
        attribName, attribName, paramName
        , linkCountFieldName, linkCountFieldName, attribName, attribName);
     
    return ocl;    
  }

  /**
   * @effects 
   *  for domain-specific rules
   */
  public String createPostConditionLinkUpdaterSingle(EClass ecls,
      EOperation eopt, String attribName, String paramName,
      String linkCountFieldName) {
    //TODO: for domain-specific rules
    String ocl = null;
    
    return ocl;

  }

  /**
   * @effects 
   *  if Field(attribName).contains(Parameter(paramName)) is specified
   *    create and return an OCL expression that Parameter(paramName) has been removed from Field(attribName)
   *    and Field(linkCountFieldName) has been decremented by 1
   *  else
   *    return null
   *     
   * @example: <pre>
   * eopt = removeEnrolments, attribName = enrolments, paramName = obj, linkCountFieldName = enrolmentsCount;
   * 
   * result = 
   *    "enrolments = enrolments@pre->asSet()->excluding(e) and 
   *      enrolmentCount = enrolmentCount@pre - 1"
   * 
   * </pre>
   */
  public String createPostConditionLinkRemoverSingle(EClass ecls,
      EOperation eopt, String attribName, String paramName,
      String linkCountFieldName) {
    String ocl = null;
    
    // alternative OCL:
    /*
     * enrolments->forAll(o | enrolments@pre->includes(o) and obj <> o) and 
        enrolmentsCount = enrolmentsCount@pre - (enrolments@pre->size() - enrolments->size())
     */
    ocl = "%s->forAll(o | %s@pre->includes(o) and %s <> o) and " + 
                  "%s = %s@pre - (%s@pre->size() - %s->size())";
    
    ocl = String.format(ocl, 
        attribName, attribName, paramName,
        linkCountFieldName, linkCountFieldName, attribName, attribName
        );
    
    return ocl;
  }
  
  /**
   * @effects 
   *  if Field(attribName).contains(Parameter(paramName)) is specified
   *    create and return an OCL expression that Parameter(paramName) has been removed from Field(attribName)
   *    and Field(linkCountFieldName) has been decremented by 1
   *  else
   *    return null
   *     
   * @example: <pre>
   * eopt = removeEnrolments, attribName = enrolments, paramName = enrols, linkCountFieldName = enrolmentsCount;
   * 
   * result = 
   *    "enrolments = enrolments@pre->asSet() - enrols->asSet() and 
   *      enrolmentCount = enrolmentCount@pre - (enrolments@pre->size() - enrolments->size())"
   * 
   * </pre>
   */
  public String createPostConditionLinkRemoverCol(EClass ecls, EOperation eopt,
      String attribName, String paramName, String linkCountFieldName) {
    String ocl = null;
    
    // alternative OCL:
    /*
     * enrolments->forAll(o | enrolments@pre->includes(o) and enrols->excludes(o)) and 
        enrolmentsCount = enrolmentsCount@pre - (enrolments@pre->size() - enrolments->size())
     */
    ocl = "%s->forAll(o | %s@pre->includes(o) and %s->excludes(o)) and " + 
                  "%s = %s@pre - (%s@pre->size() - %s->size())";
    
    ocl = String.format(ocl, 
        attribName, attribName, paramName,
        linkCountFieldName, linkCountFieldName, attribName, attribName
        );
    
    return ocl;
  }
  
  
  /**
   * @requires {@link #oclHelper} was set to a relevant context
   * @effects 
   *  use {@link #oclHelper} to create and return a pre-condition for the current context. 
   */
  public Constraint applyPreConditionOnCurrContext(String ocl) throws OCLException {
    try {
      return (Constraint) oclHelper.createPrecondition(ocl);
      //  ocl is good for the context
    } catch (ParserException e) {
      // ocl is not good for the context
      throw new OCLException(NotPossibleException.Code.FAIL_TO_CREATE_OCL_CONSTRAINT, e, 
          new String[] {"Pre-condition OCL is not valid for the current context: " + ocl});
    }
  }
  
  /**
   * @effects 
   *  apply <tt>ocl</tt> as pre-condition on <tt>eopt</tt> of <tt>ecls</tt>.
   *  Throws OCLException if fails
   */
  public void applyPreConditionOnOpt(EClass ecls, EOperation eopt,
      String ocl) throws OCLException {
    try {
      oclHelper.setOperationContext(ecls, eopt);
      
      Constraint post = (Constraint) oclHelper.createPrecondition(ocl);
      
      // ocl is good for the context
    } catch (ParserException e) {
      // ocl is not good for the context
      throw new OCLException(NotPossibleException.Code.FAIL_TO_CREATE_OCL_CONSTRAINT, e, 
          new String[] {"Method: "+ eopt.getName() + "\nPre-condition OCL: " + ocl});
    }    
  }
  
  /**
   * @effects 
   *  apply <tt>ocl</tt> as post-condition on <tt>eopt</tt> of <tt>ecls</tt>.
   *  Throws OCLException if fails
   */
  public void applyPostConditionOnOpt(EClass ecls, EOperation eopt,
      String ocl) throws OCLException {
    try {
      oclHelper.setOperationContext(ecls, eopt);
      
      Constraint post = (Constraint) oclHelper.createPostcondition(ocl);
      
      // ocl is good for the context
    } catch (ParserException e) {
      // ocl is not good for the context
      throw new OCLException(NotPossibleException.Code.FAIL_TO_CREATE_OCL_CONSTRAINT, e, 
          new String[] {"Method: "+ eopt.getName() + "\nPost-condition OCL: " + ocl});
    }    
  }

  /**
   * @effects 
   * 
   * @version 
   * @return 
   * 
   */
  public static void setOclOperationContext(EClass cls, EOperation opt) {
    oclHelper.setOperationContext(cls, opt);
  }
  
  /**
   * @modifies ecoreModel
   * @effects 
   * 
   * @version 
   * 
   */
  public EClassifier transfClass2EClass(Class cls) {
    if (cls.isPrimitive()) {
      // built-in 
      if (cls == Integer.class || cls == int.class ||
          cls == Long.class || cls == long.class ||
          cls == Byte.class || cls == byte.class
          ) {
        return Ocllib.getInteger();
      } else if (cls == Float.class || cls == float.class ||
          cls == Double.class || cls == double.class 
          ) {
        return Ocllib.getReal();
      } else if (cls == Boolean.class || cls == boolean.class) {
        return Ocllib.getBoolean();
      } 
      // TODO: add others here...
      else {
        throw new IllegalArgumentException("Type not supported: " + cls);
      }
    } else if (cls.equals(String.class)) {
      // built-in 
      return Ocllib.getString();
    }  else {
      // not built-in: create new
      EClass ecls = addClass(cls);
      
      return ecls;
    }
  }

  /**
   * @modifies ecoreModel
   * @effects 
   *  
   */
  public EClass transfClassSrc2EClass(
      ClassAST dcls) {
    //TODO: support built-in types
    
    // not built-in: create new
    EClass ecls = Ecoref.createEClass();
    ecls.setName(dcls.getName());
    
    /* TODO: create package
    Package pkg = cls.getPackage();
    EPackage epkg = EPackage.Registry.INSTANCE.get(pkg); // look up package...
    if (epkg = null) {
      ecoref.createEPackage();
    }
    */
    
    return ecls;
  }

  /**
   * @effects 
   *  create (if not already) and return an {@link EClassifier} for <tt>type</tt>
   */
  public EClassifier transfTypeSrc2EClass(Type type) {
    EClassifier ecls = null;
    
    if (ParserToolkit.isTypeCharacter(type)) {
      // numeric 
      ecls = Ocllib.getString();
    } else if (isTypeBoolean(type)) {
        ecls = Ocllib.getBoolean();
    } else if (ParserToolkit.isTypeIntegral(type)) {
      ecls = Ocllib.getInteger();
    } else if (ParserToolkit.isTypeReal(type)) {
      ecls = Ocllib.getReal();
    } else if (ParserToolkit.isTypeReference(type)) {
      // reference type
      /*
      Type refType = getReferencedType(type);
      if (refType != type) {
        // type is generic
        ecls = addGenericType(type, refType);
        
      } else {
      */
      if (ParserToolkit.isTypeCollection(type)) { // collection-type
        ecls = Ocllib.getCollection();
        // check if generic type
        Type refType = getReferencedType(type);
        if (refType != type) {
          // type is generic
          EClass refCls = addType(refType);  // ASSUME: refType is not generic!
          CollectionType colEls = (CollectionType) ecls;
          colEls.setElementType(refCls);
        }
      } else {  // non-collection type
        String typeName = getReferencedTypeName(type);
        if (typeName.equals("String")) {
          // string
          ecls = Ocllib.getString();
        } 
        // TODO: other built-in types ?
        else { 
          ecls = addType(type);
        }
      }
    } 
    // TODO: support other type here
    else {
      // create new
      ecls = addType(type);
    }
    
    return ecls;
  }
  
  /**
   * @modifies ecls
   * @effects 
   *  create an EOperation for <tt>m</tt> and adds it to <tt>ecls</tt> 
   */
  public EOperation transfMethod2EOperation(
      EClass ecls, Method m) {
    EOperation eopt = Ecoref.createEOperation();
    eopt.setName(m.getName());
    
    Class retType = m.getReturnType();
    if (retType != void.class) {
      EClassifier eretType = (EClassifier) ecoreMap.get(retType);
      if (eretType == null) {
        eretType = transfClass2EClass(retType);
        ecoreMap.put(retType, eretType);
      }
      
      eopt.setEType(eretType);
    }

    // TODO: 
    // eopt.visibility 
    
    Parameter[] params = m.getParameters();
    if (params.length > 0) {
      for (Parameter param : params) {
        EParameter eparam = transfParam2EParam(param);
        eopt.getEParameters().add(eparam);
      }
    }
    
    // register eopt to ecls
    ecls.getEOperations().add(eopt);

    return eopt;
  }



  /**
   * @requires <tt>(ecls,clazz)</tt> is in <tt>ecoreModel</tt> /\ 
   *  m is a member of <tt>clazz</tt>
   *  
   * @modifies ecoreModel, <tt>ecls</tt>
   * @effects 
   *  create an {@link EOperation} of <tt>m</tt> and return it.
   *  Registers it to {@link EClass} of <tt>ecls</tt> 
   *  
   */
  public EOperation transfMethodSrc2EOperation( 
      EClass ecls, CallableDeclaration m) {
    EOperation eopt = Ecoref.createEOperation();
    eopt.setName(m.getNameAsString());
    
    if (m instanceof MethodDeclaration) { // convert return type
      Type retType = ((MethodDeclaration)m).getType();
      if (!retType.equals(VoidType.class)) {
        EClassifier eretType = transfTypeSrc2EClass(retType);//addType(retType);
        
        eopt.setEType(eretType);
      }
    }

    // TODO: 
    // eopt.visibility 
    
    NodeList<com.github.javaparser.ast.body.Parameter> params = m.getParameters();
    if (params.size() > 0) {  // convert parameters
      for (com.github.javaparser.ast.body.Parameter param : params) {
        EParameter eparam = transfParamSrc2EParam(param);
        eopt.getEParameters().add(eparam);
      }
    }
    
    // register eopt to ecls
    ecls.getEOperations().add(eopt);
    
    return eopt;
  }

  /**
   * @modifies ecoreModel
   * @effects 
   * 
   * @version 
   * 
   */
  public EParameter transfParam2EParam(Parameter param) {
    EParameter eparam = Ecoref.createEParameter();
    // need to use NamedArg
    String paramName = param.getAnnotation(NamedArg.class).value();
    eparam.setName(paramName);
    
    //TODO: support generic type?
    eparam.setEType(transfClass2EClass(param.getType()));
    
    return eparam;
  }
  
  /**
   * @modifies ecoreModel
   * @effects 
   *  create and return an {@link EParameter} for <tt>param</tt>.
   * 
   */
  public EParameter transfParamSrc2EParam(com.github.javaparser.ast.body.Parameter param) {
    EParameter eparam = Ecoref.createEParameter();
    // need to use NamedArg
    String paramName = param.getNameAsString();
    eparam.setName(paramName);
    
    //TODO: support generic type?
    //TODO: assume parameter type is ClassOrInterfaceType
    
    EClassifier paramType = transfTypeSrc2EClass(param.getType());
    
    eparam.setEType(paramType);
    
    return eparam;
  }

  /**
   * @effects 
   *  if type is String
   *    return true
   *  else
   *    return false
   */
  public boolean isTypeString(EClassifier type) {
    return (type == Ocllib.getString()); 
  }

  /**
   * @effects 
   *  if type is numeric
   *    return true
   *  else
   *    return false
   */
  public boolean isTypeNumeric(EClassifier type) {
    return (type == Ocllib.getInteger() || 
        type == Ocllib.getReal()
        ); 
  }
  
  /**
   * @effects 
   *  if type is numeric and integral
   *    return true
   *  else
   *    return false
   */
  public boolean isTypeIntegral(EClassifier type) {
    return (type == Ocllib.getInteger()); 
  }

  /**
   * @effects 
   *  if type is numeric and real
   *    return true
   *  else
   *    return false
   */
  public boolean isTypeReal(EClassifier type) {
    return (type == Ocllib.getReal()); 
  }
  
  /**
   * @effects 
   *  if type is an object type
   *    return true
   *  else
   *    return false
   */
  public boolean isTypeObject(EClassifier type) {
    return (type instanceof EClass || type.getName().equals("String")); 
  }
  
  /**
   * @effects 
   *  if type is a collection type
   *    return true
   *  else
   *    return false
   */
  public boolean isTypeCollection(EClassifier type) {
    EClassifier colType = Ocllib.getCollection();
    return (type.eClass().getESuperTypes().contains(colType.eClass())); 
  }
}
