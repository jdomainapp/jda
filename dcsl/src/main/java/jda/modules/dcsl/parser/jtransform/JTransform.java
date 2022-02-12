package jda.modules.dcsl.parser.jtransform;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.parser.ClassAST;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.parser.ParserConstants;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.util.DClassTk;
/**
 * @overview 
 *  Represents a Java source code transformation action.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4
 */
public class JTransform {

  private Dom dom;
  // result nodes of the previous transformations
  private Map<Object, Node> transfCache;

  /**
   * @effects 
   *  initialises this with <code>dom</code>
   */
  public JTransform(Dom dom) {
    this.dom = dom;
  }

  /**
   * @effects return dom
   */
  public Dom getDom() {
    return dom;
  }


  /**
   * @effects 
   *  sets {@link #dom} = dom
   */
  public void setDom(Dom dom) {
    this.dom = dom;
  }
  
//  /**
//   * @modifies {@link #dom}
//   * @effects 
//   *  apply the specified transformation type <code>transfType</code> to the element named 
//   *  <code>srcElement</code> in {@link #dom} with arguments <code>args</code>.
//   *  <p>If succeeded then 
//   *    update {@link #dom} with the transformation result and 
//   *    return this object (to perform further transformation, if required).
//   *  <p>
//   *  Throws NotFoundException if a required element is not found; 
//   *  NotPossibleException if failed to perform the specified transformation for some reasons.
//   */
//  public JTransform apply(TransfAction transfType, 
//      String srcElement, Object...args) throws NotFoundException, NotPossibleException {
//    Transform transf = TransformFactory.createTransfObject(getDom(), transfType);
//    
//    Node eleNode = transf.apply(srcElement, args);
//    //TODO: temporarily record this node for subsequent transformation?
//    
//    return this;
//  }

  /**
   * @effects 
   *  marks the BEGIN of the transformation action, initialising necessary resources
   */
  public void begin() {
    transfCache = new HashMap<>();
  }
  
  /**
   * @effects 
   *  marks the END of the transformation action, releasing the resources that were used
   */
  public void end() {
    transfCache.clear();
    transfCache = null;
  }
  
  /**
   * @requires {@link #begin()} has been invoked and {@link #end()} has NOT been invoked.
   * @effects
   *  caches source code n with the specified key
   */
  private void cache(Object key, Node n) {
    if (transfCache == null) {
      return;
    }
    
    transfCache.put(key, n);
  }
  
  /**
   * @requires {@link #begin()} has been invoked and {@link #end()} has NOT been invoked.
   * @effects 
   *  retrieves the source code node mapped to key that was cached by {@link #cache(Object, Node)}.
   */
  private Node retrieveCache(Object key) {
    if (transfCache == null) {
      return null;
    }
   
    return transfCache.get(key);
  }
  

  /**
   * @effects 
   *  generates a unique key for use with {@link #cache(Object, Node)}
   */
  private String genKey(String...elements) {
    return String.join(".", elements);
  }

  /**
   * @modifies {@link #dom}
   * @requires class named <code>fqnClsName</code> exists in {@link #dom}
   * @effects 
   *  apply the <b>add-import</b> transformation to the class
   *  <code>fqnClsName</code> in {@link #dom} with arguments.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform mergeImport(String fqnClsName, Class<?>...classes) throws NotPossibleException {
    dom.getDClass(fqnClsName).addImports(classes);
    
    return this;
  }
  

  /**
   * @effects 
   *  
   */
  public JTransform mergeImport(String fqnClsName, String...importNames) {
    dom.getDClass(fqnClsName).addImports(importNames);
    
    return this;
  }
  
  /**
   * @modifies {@link #dom}
   * @requires class named <code>fqnClsName</code> exists in {@link #dom}
   * @effects 
   *  apply the <b>add-import</b> transformation to the class
   *  <code>fqnClsName</code> in {@link #dom} with arguments and with the essential annotation classes.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform mergeEssentialImport(String fqnClsName, Class<?>...classes) throws NotPossibleException {
    dom.getDClass(fqnClsName).addImports(classes);

    // add essential annotations
    dom.getDClass(fqnClsName).addImports(new Class[] {
        DClass.class,
        DAttr.class, DAttr.Type.class, DOpt.class, AttrRef.class, 
        DAssoc.class, AssocType.class, AssocEndType.class, Associate.class, DCSLConstants.class
    });
       
    return this;
  }
  
  /**
   * @effects   
   *  add the import statements specified in <code>refCls</code> of <code>refDom</code>
   *  to the class <code>fqnClsName</code>
   */
  public JTransform mergeImport(String fqnClsName, Dom refDom, String refCls) {
    // extract source code of the method
    List<String> imports = refDom.getDClass(refCls).getImport();
    
    dom.getDClass(fqnClsName).addImport(imports);
    
    return this;
  }
  
  /**
   * @effects 
   *  create a domain class named <code>fqnClsName</code> with the specified modifier.
   */
  public JTransform mergeDClass(String fqnClsName, Modifier mod) {
    return 
    addClassIfNotExists(fqnClsName, Modifier.PUBLIC)
      .addClassAnoIfNotExists(fqnClsName, DClass.class);
  }

  /**
   * @modifies {@link #dom}
   * @effects 
   *  apply the <b>add-class</b> transformation to the element named 
   *  <code>fqnClsName</code> in {@link #dom} with arguments <code>args</code>.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform addClass(String fqnClsName, Modifier...mods) throws NotPossibleException {
//    Transform transf = TransformFactory.createTransfObject(getDom(), AddClass.class, true);
//    Node n = transf.apply(fqnClsName, args);
    
    Node n = dom.addClass(fqnClsName, mods);
    
    cache(fqnClsName, n);
    
    return this;
  }

  /**
   * @modifies {@link #dom}
   * @effects 
   *  if the class whose FQN is <code>fqnClsName</code> does not exist in {@link #dom}
   *    apply {@link #addClass(String, Modifier...)} to it
   *  otherwise
   *    do nothing
   *    
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */

  public JTransform addClassIfNotExists(String fqnClsName, Modifier...mods) 
      throws NotPossibleException {
    
    // try to load
    String clsName = DClassTk.getClassNameFromFqn(fqnClsName);
    ClassAST ast;
    try {
      ast = dom.loadClass(clsName, fqnClsName);
      // class exists
    } catch (NotFoundException ex) {
      // not found in the source code folder: create new
      addClass(fqnClsName, mods);
//      ast = dom.getDClass(fqnClsName);
//      ast.setSrcFile();
    }
    return this;
  }
 
  /**
   * @modifies <code>fqnClsName</code> in {@link #dom}
   * @effects 
   *  let <code>s</code> = supertype declaration of <code>refClsFQN</code> in <code>refDom</code>, 
   *  merges <code>s</code> with <code>c</code>'s class header
   * @version 5.4
   * 
   */
  public JTransform mergeSuperTypes(String fqnClsName, Dom refDom,
      String refClsFQN) throws NotPossibleException {

    ClassAST refCls = refDom.getDClass(refClsFQN);
    ClassOrInterfaceType superCls = refCls.getSuperClsType();
    Collection<ClassOrInterfaceType> superIntfs = refCls.getSuperIntfs();
    
    ClassAST cls = dom.getDClass(fqnClsName);
    if (superCls != null) {
      cls.setSuperCls(superCls);
    }
    
    if (superIntfs != null) {
      //superIntfs.forEach(cls::addClassImplement);
      cls.addClassImplement(superIntfs.toArray(new ClassOrInterfaceType[superIntfs.size()]));
    }
    
    // no need to cache
    return this;
  }
  
  /**
   * @effects 
   *  update class <code>fqnClsName</code> with an <code>implements</code> clause
   *  for <code>intfClasses</code>
   *  
   */
  public JTransform mergeClassImplement(String fqnClsName, Class<?>...intfClasses) 
      throws NotFoundException, NotPossibleException {

    dom.getDClass(fqnClsName).addClassImplement(intfClasses);
    
    // no need to cache
    return this;
  }

  /**
   * @effects 
   *  if the specified annotation does not exist in class <code>fqnClsName</code> then
   *    adds it
   *  else
   *    do nothing
   */
  public <T extends Annotation> JTransform addClassAnoIfNotExists(String fqnClsName, Class<T> anoCls) {
    String key = genKey(fqnClsName, anoCls.getSimpleName());
    
    Node n = retrieveCache(key);
    
    if (n == null) {
      // try looking in the dom
      ClassAST ast = dom.getDClass(fqnClsName);
      if (ast != null && ast.hasAnnotation(DClass.class)) {
        // annotation exists
        return this;
      }

      // create it
      addClassAno(fqnClsName, anoCls);
    } 
    
    return this;
  }


  /**
   * @modifies {@link #dom}
   * @requires class named <code>fqnClsName</code> exists in {@link #dom}
   * @effects 
   *  apply the <b>add-class-annotation</b> transformation to the class
   *  <code>fqnClsName</code> in {@link #dom} with no arguments.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform addClassAno(String fqnClsName, Class<? extends Annotation> anoCls) 
  throws NotPossibleException {
    return addClassAno(fqnClsName, anoCls, null);
  }
  
  /**
   * @modifies {@link #dom}
   * @requires class named <code>fqnClsName</code> exists in {@link #dom}
   * @effects 
   *  apply the <b>add-class-annotation</b> transformation to the class
   *  <code>fqnClsName</code> in {@link #dom} with arguments <code>args</code.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform addClassAno(String fqnClsName, Class<? extends Annotation> anoCls,
      String anoPropVals) {
    Node n = dom.getDClass(fqnClsName).addClsAno(anoCls, anoPropVals);
    
    String key = genKey(fqnClsName, anoCls.getSimpleName());
    cache(key, n);
    
    return this;
  }


  /**
   * @effects 
   *  execute a template for a combination of primitive transformations that add a standard getter method 
   *  for the specified <tt>fieldName</tt>.
   *  
   *  <p>Throws NotPossibleException if fails.
   */
  public JTransform mergeDGetterMethod(String fqClassName, String fieldName, Type type) throws NotPossibleException {
    String getter = DClassTk.getGetterNameFor(fieldName);
    addMethod(fqClassName, getter, type, Modifier.PUBLIC)
      .addMethodAno(fqClassName, getter, DOpt.class, "type: DOpt.Type.Getter")
      .addMethodAno(fqClassName, getter, AttrRef.class, "value: " + fieldName)    
      .addMethodStmt(fqClassName, getter, "return "+fieldName+";");
    return this;
  }
  
  /**
   * @effects 
   *  execute a template for a combination of primitive transformations that add a standard getter method 
   *  for the specified <tt>fieldName</tt>.
   *  
   *  <p>Throws NotPossibleException if fails.
   */
  public JTransform mergeDSetterMethod(String fqClassName, String fieldName, Type type) throws NotPossibleException {
    String setter = DClassTk.getSetterNameFor(fieldName);
    addMethod(fqClassName, setter, ParserConstants.TypeVoid, Modifier.PUBLIC)
      .addMethodParam(fqClassName, setter, fieldName, type)
      .addMethodAno(fqClassName, setter, DOpt.class, "type: DOpt.Type.Setter")
      .addMethodAno(fqClassName, setter, AttrRef.class, "value: " + fieldName)    
      .addMethodStmt(fqClassName, setter, "this."+fieldName+" = "+fieldName+";");
    return this;
  }

  
  /**
   * @effects 
   *  adds the exception classes specified in <code>throwClasses</code> to the specified method.
   */
  public JTransform addMethodThrows(String fqnClsName, String methodName,
      Class... throwClasses) {
    dom.getDClass(fqnClsName).addMethodThrows(methodName, throwClasses);
    
    // no need to cache
    return this;
  }

  /**
   * @modifies <code>fqnClsName</code> in {@link #dom}
   * @effects 
   *  let <code>c'</code> = default constructor of <code>refClsFQN</code> in <code>refDom</code>, 
   *  if <code>fqnClsName</code> has a default constructor <code>c</code>
   *    append <code>c'</code> body to <code>c</code> 
   *  else
   *    copy <code>c'</code> to create <code>c</code>
   * @version 5.4
   * 
   */
  public JTransform mergeDefaultConstructor(String fqnClsName, Dom refDom,
      String refClsFQN) throws NotPossibleException {
    ConstructorDeclaration constr = refDom.getDClass(refClsFQN).getDefaultConstructor();
    
    ClassAST classAst = dom.getDClass(fqnClsName);
    ConstructorDeclaration myConstr = classAst.getDefaultConstructor();

    if (myConstr == null) {
      // create new
      classAst.addDefaultConstructor(constr);
    } else {
      // update
      classAst.addConstructorStmtBlock(myConstr, constr.getBody());
    }
    
    return this;
  }

  /**
   * @effects 
   *  adds <code>stmt</code> to the body of the constructor of the class 
   *  <code>fqnClsName</code> whose parameter types are <code>paramTypes</code>
   */
  public JTransform addConstructorStmt(String fqnClsName, 
      String[] paramTypes,
      String stmt) {
    dom.getDClass(fqnClsName).addConstructorStmt(paramTypes, stmt);
    
    // no need to cache
    return this;
  }

  /**
   * @effects 
   *  extract source code of the method <code>refCls.methodName</code>, which is 
   *  kept in <code>refDom</code> and copies it to the class <code>fqnClsName</code> in {@link #dom}
   * 
   */
  public JTransform addMethod(String fqnClsName, String methodName, 
      Dom refDom, String refCls) {
    // extract source code of the method
    MethodDeclaration refMethod = refDom.getDClass(refCls).getMethod(methodName);
    
    Node n = dom.getDClass(fqnClsName).addMethod(refMethod);
    
    String methodKey = genKey(fqnClsName, methodName);
    cache(methodKey, n);
    
    return this;
  }

  /**
   * @modifies {@link #dom}
   * @effects 
   *  apply the <b>add-method</b> transformation to the method
   *  <code>fqnClsName.methodName</code> in {@link #dom} with arguments <code>args</code>.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform addMethod(String fqnClsName, String methodName, Type type, Modifier...mods) throws NotPossibleException {
//    Transform transf = TransformFactory.createTransfObject(getDom(), AddMethod.class, true);
//    Node n = transf.apply(fqnClsName, methodName, args);

    Node n = dom.addMethod(fqnClsName, methodName, type, mods);
    
    String methodKey = genKey(fqnClsName, methodName);
    cache(methodKey, n);
    
    return this;
  }

  /**
   * @modifies {@link #dom}
   * @effects 
   *  apply the <b>add-method-parameter</b> transformation to the method
   *  <code>fqnClsName.methodName</code> in {@link #dom} with arguments <code>args</code>.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform addMethodParam(String fqnClsName, String methodName, 
      String paramName, Type paramType) throws NotPossibleException {
    String methodKey = genKey(fqnClsName, methodName);
    MethodDeclaration md = (MethodDeclaration) retrieveCache(methodKey);
    Node n;
    if (md != null) {
      n = dom.getDClass(fqnClsName).addMethodParam(md, paramName, paramType);
    } else {
      n = dom.getDClass(fqnClsName).addMethodParam(methodName, paramName, paramType);
    }
    
    String key = genKey(methodKey, paramName);
    cache(key, n);
    
    return this;
  }

  /**
   * @modifies {@link #dom}
   * @effects 
   *  apply the <b>add-method-body</b> transformation to the method
   *  <code>fqnClsName.methodName</code> in {@link #dom} with arguments <code>args</code>.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform addMethodBody(String fqnClsName, String methodName) throws NotPossibleException {
//    Transform transf = TransformFactory.createTransfObject(getDom(), AddMethodBody.class, true);
    String methodKey = genKey(fqnClsName, methodName);
    MethodDeclaration md = (MethodDeclaration) retrieveCache(methodKey);
    Node n;
    if (md != null) {
//      n = transf.apply(fqnClsName, md);
      n = dom.getDClass(fqnClsName).addMethodBody(md);
    } else {
//      n = transf.apply(fqnClsName, methodName);      
      n = dom.getDClass(fqnClsName).addMethodBody(methodName);
    }
    
    String key = genKey(methodKey,"body");
    cache(key, n);
    
    return this;
  }

  /**
   * @modifies {@link #dom}
   * @effects 
   *  apply the <b>add-method-statement</b> transformation to the method
   *  <code>fqnClsName.methodName</code> in {@link #dom} with arguments <code>args</code>.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform addMethodStmt(String fqnClsName, String methodName, String stmt) throws NotPossibleException {
//    Transform transf = TransformFactory.createTransfObject(getDom(), AddMethodStmt.class, true);
    String methodKey = genKey(fqnClsName, methodName, "body");
    BlockStmt body = (BlockStmt) retrieveCache(methodKey);
    Node n;
    if (body != null) {
//      n = transf.apply(fqnClsName, md, stmt);
      n = dom.getDClass(fqnClsName).addMethodStmt(body, stmt);
    } else {
//      n = transf.apply(fqnClsName, methodName, stmt);      
      n = dom.getDClass(fqnClsName).addMethodStmt(methodName, stmt);
    }
    
    /* TODO ? do we need to cache this
    String key = fqnClsName+ "." + methodName + "stmt";
    cache(key, n);
    */
    return this;
  }

  /**
   * @modifies {@link #dom}
   * @effects 
   *  apply the <b>push-method-statement</b> transformation to the method
   *  <code>fqnClsName.methodName</code> in {@link #dom} with arguments <code>args</code>.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform pushMethodStmt(String fqnClsName, String methodName, String stmt) throws NotPossibleException {
//    Transform transf = TransformFactory.createTransfObject(getDom(), PushMethodStmt.class, true);
    String methodKey = genKey(fqnClsName, methodName, "body");
    BlockStmt body = (BlockStmt) retrieveCache(methodKey);
    Node n;
    if (body != null) {
//      n = transf.apply(fqnClsName, md, stmt);
      n = dom.getDClass(fqnClsName).pushMethodStmt(body, stmt);
    } else {
//      n = transf.apply(fqnClsName, methodName, stmt);      
      n = dom.getDClass(fqnClsName).pushMethodStmt(methodName, stmt);
    }
    
    /* TODO ? do we need to cache this
    String key = fqnClsName+ "." + methodName + "stmt";
    cache(key, n);
    */
    return this;
  }

  /**
   * @modifies {@link #dom}
   * @effects 
   *  apply the <b>add-method-statement-block</b> transformation to the method
   *  <code>fqnClsName.methodName</code> in {@link #dom} with arguments <code>args</code>.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform addMethodBlock(String fqnClsName, String methodName, String stmtBlock) throws NotPossibleException {
//    Transform transf = TransformFactory.createTransfObject(getDom(), AddMethodBlock.class, true);
    String methodKey = genKey(fqnClsName, methodName, "body");
    BlockStmt body = (BlockStmt) retrieveCache(methodKey);
    Node n;
    if (body != null) {
//      n = transf.apply(fqnClsName, md, stmt);
      n = dom.getDClass(fqnClsName).addMethodStmtBlock(body, stmtBlock);
    } else {
//      n = transf.apply(fqnClsName, methodName, stmt);      
      n = dom.getDClass(fqnClsName).addMethodStmtBlock(methodName, stmtBlock);
    }
    
    /* TODO ? do we need to cache this
    String key = fqnClsName+ "." + methodName + "stmt";
    cache(key, n);
    */
    return this;
  }
  
  /**
   * @modifies {@link #dom}
   * @requires method <code>fqnClsName.methodName</code> exists in {@link #dom} && 
   *  <code>anoPropVals</code> is written in the <b>simplified, single-line Json syntax</b> 
   *  (i.e. without the outer curly brackets {})
   * @effects 
   *  apply the <b>add-method-annotation</b> transformation to the method
   *  <code>fqnClsName.methodName</code> in {@link #dom} with arguments.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform addMethodAno(String fqnClsName, String methodName, 
      Class<? extends Annotation> anoCls,
      String anoPropVals) throws NotPossibleException {
    String key = genKey(fqnClsName, methodName);
    MethodDeclaration md = (MethodDeclaration) retrieveCache(key);
    Node n;
    if (md != null) {
      n = dom.getDClass(fqnClsName).addMethodAno(md, anoCls, anoPropVals);
    } else {
      n = dom.getDClass(fqnClsName).addMethodAno(methodName, anoCls, anoPropVals);
    }
    
    key = genKey(key, anoCls.getSimpleName());
    cache(key, n);
    
    return this;
  }

  /**
   * @modifies <code>fqnClsName</code> in {@link #dom}
   * @effects 
   *  add all non-constructor methods from <code>refClsFQN</code> in <code>refDom</code> to 
   *  <code>fqnClsName</code> in {@link #dom}
   */
  public JTransform mergeMethods(String fqnClsName, Dom refDom, String refClsFQN) throws NotPossibleException {
    List<MethodDeclaration> refMethods = refDom.getDClass(refClsFQN).getMethods();
    
    ClassAST cls = dom.getDClass(fqnClsName);
    refMethods.forEach(refMethod -> cls.addMethod(refMethod));
    return this;
  }
  
  /**
   * @modifies <code>fqnClsName</code> in {@link #dom}
   * @effects 
   *  add all declared fields from <code>refClsFQN</code> in <code>refDom</code> to 
   *  <code>fqnClsName</code> in {@link #dom}
   */
  public JTransform mergeFields(String fqnClsName, Dom refDom, String refClsFQN) throws NotPossibleException {
    List<FieldDeclaration> refFields = refDom.getDClass(refClsFQN).getFields();
    
    ClassAST cls = dom.getDClass(fqnClsName);
    refFields.forEach(refField -> cls.addField(refField));
    return this;
  }
  
  /**
   * @effects 
   *  if class(<code>fqnClsName</code>) has a filed named <code>fieldName</code>
   *    merges it with the specified field declaration elements
   *  else 
   *    create domain field <code>fqnClsName.fieldName</code> with the 
   *    constraints specified in <code>constraintSpec</code>
   */
  public JTransform mergeDField(String fqnClsName, String fieldName,
      Type type, Modifier mod, String constraintSpec) throws NotFoundException, NotPossibleException {
    dom.removeFieldIfExists(fqnClsName, fieldName);
    
    DAttr.Type t = ParserToolkit.lookUpDAttrType(type);
    return 
      addField(fqnClsName, fieldName, type, mod)
        .addFieldAno(fqnClsName, fieldName, DAttr.class, 
            "name: "+fieldName+", type: Type." +t+", " + constraintSpec);
  }

  
  /**
   * @effects 
   *  create domain field <code>fqnClsName.fieldName</code> with the 
   *  constraints specified in <code>constraintSpec</code>
   */
  public JTransform mergeDField(String fqnClsName, String fieldName,
      Type type, String constraintSpec, Modifier...mod) throws NotFoundException, NotPossibleException {
    DAttr.Type t = ParserToolkit.lookUpDAttrType(type);
    
    JTransform transf = addField(fqnClsName, fieldName, type, mod);
    if (constraintSpec != null) {
        transf.addFieldAno(fqnClsName, fieldName, DAttr.class, 
            "name: "+fieldName+", type: Type." +t+", " + constraintSpec);
    }
    
    return transf;
  }
  
  /**
   * @modifies {@link #dom}
   * @effects 
   *  apply the <b>add-field</b> transformation to the field  
   *  <code>fqnClsName.fieldName</code> in {@link #dom} with arguments <code>args</code>.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform addField(String fqnClsName, String fieldName, Type type, Modifier...mods) throws NotPossibleException {
//    Transform transf = TransformFactory.createTransfObject(getDom(), AddField.class, true);
//    Node n = transf.apply(fqnClsName, name, args);
    Node n = dom.addField(fqnClsName, fieldName, type, mods);
    
    String key = genKey(fqnClsName, fieldName);
    cache(key, n);
    
    return this;
  }

  /**
   * @param assocCls 
   * @effects 
   *  
   */
  public JTransform mergeAssocOneMany(String fqClassName, String fieldName, 
      String fqnAssocClsName, 
      AssocEndType thisEnd) {
    String thisName = DClassTk.getClassNameFromFqn(fqClassName);
    String thisNameCamel = DClassTk.toCamelCase(thisName);
    String assocClsName = DClassTk.getClassNameFromFqn(fqnAssocClsName);
    String assocName = DClassTk.getAutoAssociationName(thisName, assocClsName);
    String ascType = DClassTk.getFieldAccessString(AssocType.One2Many);
    String endType = DClassTk.getFieldAccessString(thisEnd);
    JsonValue cardMax = 
        thisEnd.equals(AssocEndType.Many) ? Json.createValue(1) : Json.createValue(DCSLConstants.CARD_MORE_STRING) ;
    JsonObject assocJson = Json.createObjectBuilder()
        .add("ascName", assocName)
        .add("role", thisNameCamel)
        .add("ascType", ascType)
        .add("endType", endType)
        .add("associate", 
            Json.createObjectBuilder()
            .add("type", assocClsName)
            .add("cardMin", 1)
            .add("cardMax", cardMax)
            .build()
            )
//        .add("dependsOn", true)
        .build()
        ;
    mergeFieldAno(fqClassName, fieldName, DAssoc.class, 
        assocJson
        );
    char c = 'C';
    return this;
  }
  
  /**
   * A short-cut to {@link #mergeAssocOneMany(String, String, String, AssocEndType)}.
   * 
   * @param assocCls 
   * @effects 
   *  
   */
  public JTransform mergeAssocOneMany(String fqClassName, String fieldName, 
      Class assocCls, 
      AssocEndType thisEnd) {
    return mergeAssocOneMany(fqClassName, fieldName, assocCls.getName(), thisEnd);
  }

  /**
   * @effects 
   * 
   */
  public JTransform mergeAssocManyMany(String fqClassName, String fieldName, Class assocCls, String normAttrib) {
    String thisName = DClassTk.getClassNameFromFqn(fqClassName);
    String thisNameCamel = DClassTk.toCamelCase(thisName);
    String assocClsName = assocCls.getSimpleName();
    String assocName = DClassTk.getAutoAssociationName(thisName, assocClsName);
    String ascType = DClassTk.getFieldAccessString(AssocType.Many2Many);
    String endType = DClassTk.getFieldAccessString(AssocEndType.Many);
    String cardMax = DCSLConstants.CARD_MORE_STRING;
    JsonObject assocJson = Json.createObjectBuilder()
        .add("ascName", assocName)
        .add("role", thisNameCamel)
        .add("ascType", ascType)
        .add("endType", endType)
        .add("associate", 
            Json.createObjectBuilder()
            .add("type", assocClsName)
            .add("cardMin", 1)
            .add("cardMax", cardMax)
            .build()
            )
        .add("normAttrib", normAttrib)
        .build()
        ;
    mergeFieldAno(fqClassName, fieldName, DAssoc.class, 
        assocJson
        );
    
    return this;
  }

  /**
   * @modifies {@link #dom}
   * @requires field <code>fqnClsName.fieldName</code> exists in {@link #dom}
   * @effects 
   *  apply the <b>add-field-annotation</b> transformation to the field
   *  <code>fqnClsName.fieldName</code> in {@link #dom} with arguments.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform addFieldAno(String fqnClsName, String fieldName, 
      Class<? extends Annotation> anoCls,
      String anoPropVals) throws NotPossibleException {
    String key = genKey(fqnClsName, fieldName);
    FieldDeclaration fd = (FieldDeclaration) retrieveCache(key);
    Node n;
    if (fd != null) {
      n = dom.getDClass(fqnClsName).addFieldAno(fd, anoCls, anoPropVals);
    } else {
      n = dom.getDClass(fqnClsName).addFieldAno(fieldName, anoCls, anoPropVals);
    }
    
    key = genKey(key, anoCls.getSimpleName());
    cache(key, n);
    
    return this;
  }

  /**
   * @modifies {@link #dom}
   * @requires field <code>fqnClsName.fieldName</code> exists in {@link #dom}
   * @effects 
   *  apply the <b>add-field-annotation</b> transformation to the field
   *  <code>fqnClsName.fieldName</code> in {@link #dom} with arguments.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return this object (to perform further transformation, if required).
   *  <p>
   *  Throws NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public JTransform mergeFieldAno(String fqnClsName, String fieldName, 
      Class<? extends Annotation> anoCls, JsonObject anoPropValsJson) throws NotPossibleException {
    String key = genKey(fqnClsName, fieldName);
    FieldDeclaration fd = (FieldDeclaration) retrieveCache(key);
    Node n;
    if (fd != null) {
      n = dom.getDClass(fqnClsName).addFieldAno(fd, anoCls, anoPropValsJson);
    } else {
      n = dom.getDClass(fqnClsName).addFieldAno(fieldName, anoCls, anoPropValsJson);
    }
    
    key = genKey(key, anoCls.getSimpleName());
    cache(key, n);
    
    return this;
  }
 
  /**
   * @effects 
   *  if a source element named <code>fqElementName</code> exists in this or in {@link #dom}
   *    print its content
   *  else
   *    print error
   */
  public void print(String fqElementName) {
    Node n = retrieveCache(fqElementName);
    if (n != null) {
      doPrint(fqElementName, n);
    } else {
      // look up in the model
      printAst(fqElementName);
    }
  }

  /**
   * @effects 
   *  if a source element named <code>fqElementName</code> exists in {@link #dom}
   *    print its content
   *  else
   *    print error
   */
  public void printAst(String fqElementName) {
    // look up in the model
    ClassAST clsAst = dom.getDClass(fqElementName);
    doPrint(fqElementName, clsAst);
  }
  
  /**
   * @effects 
   *  if element != null
   *    print its content
   *  else
   *    print error
   */
  private void doPrint(String fqElementName, Object element) {
    if (element == null) {
      System.out.println("Not found: perhaps wrong name: " + fqElementName);
    } else {
      System.out.println(element);
    }    
  }

  /**
   * @modifies {@link #dom}
   * 
   * @effects 
   *  merges <code>other</code>'s elements to this.{@link #dom}
   *  
   * @version 5.4.1
   */
  public void mergeModel(Dom other) {
    /*
     * for each domain class c in other
     *   find matching class c' in this 
     *    mergeDClass(c', c)
     */
    if (other == null || other.isEmpty()) return;
    
    other.forEach((otherFqn, otherAst) -> {
      ClassAST ast = dom.getDClassByName(DClassTk.getClassNameFromFqn(otherFqn));
      if (ast != null) {
        mergeDClass(ast, otherAst);
      } else {
        // new class: add to model
        ClassAST newAst = otherAst.clone();
        // set package name to the same as the existing classes in dom
        newAst.setPackage(dom.getAnyPackage());
        dom.addClass(newAst);
      }
      
    });
  }

  /**
   * @modifies ast
   *  
   * @effects 
   *  merges elements of <code>otherAst</code> to <code>ast</code>
   *  
   * @version 5.4.1
   */
  public void mergeDClass(ClassAST ast, ClassAST otherAst) {
    ast.mergeWith(otherAst);
  }
}
