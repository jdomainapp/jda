package jda.modules.dcsl.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import javax.json.JsonObject;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.parser.statespace.parser.StateSpaceVisitor;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DOpt;

/**
 * @overview 
 *  Wraps a {@link CompilationUnit} of the AST of a Java class. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ClassAST {

  /** the simple name of this class */
  private String className;

  /** the path to source code file of this class */
  private String srcFile;
  
  /** the AST of this class */
  private CompilationUnit ast;
  
  /**the class node in {@link #ast}*/
  private ClassOrInterfaceDeclaration cls;

  private String fqn;

  /**
   * the {@link ClassOrInterfaceType} representation of {@link #cls}
   */
  private ClassOrInterfaceType type;

  /**
   * the {@link ClassOrInterfaceType} representation of {@link #cls} based on its simple name
   */
  private ClassOrInterfaceType simpleType;

  
  /**
   * the {@link ClassExpr} of {@link #cls}
   */
  private ClassExpr clsExpr;

  /**
   * the simple {@link ClassExpr} of {@link #cls}
   */
  private ClassExpr simpleClsExpr;

  /** the state space of this. State space contains more than just the field declarations. 
   * It contains also DAttr definitions of those fields. 
   * The simple list of domain fields can be obtained via {@link #getDomainFields()}. 
   */
  private LinkedHashMap<DAttrDef, FieldDef> stateSpace;

  private Collection<MethodDeclaration> domainMethods;

  private Collection<FieldDeclaration> domainFields;
  
//  /** (derived from {@link #cls}): fully qualified name of class*/
//  private String fqn;
  
  /**
   * @effects 
   *  initialise this from content of <tt>srcFile</tt>
   * @version 
   */
  public ClassAST(String className, String srcFile) throws NotFoundException {
    //  get the shared class pool instance (singleton)
    this.className = className;
    this.srcFile = srcFile;

    try {
      ast = ParserToolkit.createJavaParser(srcFile);
    } catch (FileNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, e, new Object[] {srcFile});
    }
    
    Optional<ClassOrInterfaceDeclaration> opt = ast.getClassByName(className);
    if (opt.isPresent()) {
      cls = opt.get();
    } else {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, new Object[] {className + " (not a class?)"});      
    }

  }

  /**
   * NOTE: Use this method for the case that class <tt>clsName</tt> is an inner class of 
   * the one specified in <tt>outerSrcFile</tt>. 
   * 
   * @requires <tt>clsName = innerCls.getNameAsString()</tt> /\ 
   *  <tt>topLevelClass(outerAst).nodes</tt> contains <tt>innerCls</tt> as an inner class ( 
   *  which is named <tt>clsName</tt>) 
   * 
   * @effects 
   *  initialise this with pre-defined state
   * @version 5.2
   */
  public ClassAST(String clsName, 
      String outerSrcFile, CompilationUnit outerAst, ClassOrInterfaceDeclaration innerCls) {
    this.className = clsName;
    this.srcFile = outerSrcFile;
    this.ast = outerAst;
    this.cls = innerCls;
  }


  /**
   * @effects 
   *  initialises this as a {@link ClassAST} for an existing class represented by <code>cu</code>
   *  
   * @version 5.4
   */
  public ClassAST(String clsName, CompilationUnit cu) {
    this.className = clsName;
    this.srcFile = null;
    
    ast = cu;
    cls = ast.getClassByName(className).get();
  }

  /**
   * @effects 
   *  sets {@link #srcFile} to <code>srcFilePath</code>
   * @version 5.4
   */
  public void setSrcFile(String srcFilePath) {
    this.srcFile = srcFilePath;
  }
  
  /**
   * @effects 
   *  changes this.{@link #className} and update {@link #ast} accordingly
   *   
   * @version 5.4.1
   */
  public void setSimpleName(String newName) {
    this.className = newName;
    
    int lastDot = (fqn != null) ? fqn.lastIndexOf(".") : -1;
    String pkgName = (lastDot > -1) ? fqn.substring(0, lastDot) : null;

    if (pkgName != null) {
      this.fqn = pkgName + "." + newName;
      
    } else {
      this.fqn = newName;
    }
    
    if (srcFile != null) {
      srcFile = srcFile.substring(0,
          srcFile.lastIndexOf(File.separator)) + File.separator + newName;
    }
    
    ParserToolkit.setName(cls, newName);
  }


  /**
   * @effects 
   *  return {@link #srcFile}
   * @version 5.4.1
   * 
   */
  public String getSrcFile() {
    return srcFile;
  }
  
  /**
   * @requires {@link #srcFile} is not null
   * @modifies this
   * @effects 
   *    reload {@link #ast} from {@link #srcFile} and update this accordingly.
   */
  public void reload() throws NotFoundException {
    if (srcFile == null) return;
    
    reset();
    
    try {
      ast = ParserToolkit.createJavaParser(srcFile);
    } catch (FileNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, e, new Object[] {srcFile});
    }
    
    cls = ast.getClassByName(className).get();
  }
  
  /**
   * @effects 
   *  reset this to the initial state
   */
  private void reset() {
    fqn = null;
    stateSpace = null;
    domainMethods = null;
    domainFields = null;
  }

  /**
   * @effects 
   *  return the FQN of the class represented by this. 
   */
  public String getFqn() {
    /*
    String pkgOpt = getPackageDeclaration();
    
    String fqn;
    if (pkgOpt != null) {
      fqn = pkgOpt + "." + cls.getNameAsString();
    } else {
      fqn = cls.getNameAsString();
    }
    
    return fqn;
    */
    return getFqn(false);
  }

  public String getFqn(boolean refresh) {
    if (fqn == null || refresh) {
      String pkgOpt = getPackageDeclaration();
      
      if (pkgOpt != null) {
        fqn = pkgOpt + "." + cls.getNameAsString();
      } else {
        fqn = cls.getNameAsString();
      }
    }
    
    return fqn;
  }
  
  /**
   * @requires m is a member of {@link #cls}
   * 
   * @effects 
   *  generate and return FQN for <tt>m</tt>
   * @version 5.2
   * 
   */
  public String getMethodFqn(CallableDeclaration m) {
    
    String fqn = getFqn();
    
    String mfqn = fqn + "." + m.getNameAsString() + m.getBegin();
    
    return mfqn;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return String.format("ClassAST(%s): %n %s", cls.getNameAsString(), ast.toString());    
  }

  /**
   * @effects 
   *  return {@link ClassOrInterfaceType} representation of this.
   */
  public ClassOrInterfaceType getType() {
    if (type == null)
      type = JavaParser.parseClassOrInterfaceType(getFqn());
    
    return type;
  }

  /**
   * @effects 
   *  return {@link ClassOrInterfaceType} representation of this, based on the simple name.
   */
  public ClassOrInterfaceType getSimpleType() {
    if (simpleType == null)
      simpleType = JavaParser.parseClassOrInterfaceType(getName());
    
    return simpleType;
  }
  
  /**
   * @effects 
   *  return a {@link ClassExpr} whose name is {@link #getType()}. 
   */
  public ClassExpr getClassExpr() {
    if (clsExpr == null)
      clsExpr = new ClassExpr(getType());

    
    return clsExpr;
  }


  /**
   * @effects 
   *  return a {@link ClassExpr} whose name is {@link #getSimpleType()}. 
   */
  public ClassExpr getSimpleClassExpr() {
    if (simpleClsExpr == null)
      simpleClsExpr = new ClassExpr(getSimpleType());

    
    return simpleClsExpr;
  }
  
  /**
   * @effects 
   *  return {@link #cls}.name
   */
  public String getName() {
    return cls.getNameAsString();
  }

  /**
   * @effects 
   *  copy the specified field to {@link #ast} and return the newly-created field
   * @version 5.4
   */
  public FieldDeclaration addField(FieldDeclaration refField) {
    return ParserToolkit.addFieldCopy(cls, refField);
  }
  
  /**
   * @effects 
   *  if exists {@link FieldDeclaration}s in this
   *    return them as List
   *  else
   *    return null 
   * @version 5.4 
   */
  public List<FieldDeclaration> getFields() {
    return ParserToolkit.getFields(cls);
  }
  
  /**
   * @effects 
   *  if exists {@link FieldDeclaration} in {@link #cls} that is assigned with {@link DAttr}
   *    return a {@link Collection} of them (preserving order of the fields in {@link #cls}
   *  else
   *    return null 
   */
  public Collection<FieldDeclaration> getDomainFields() {
    //return ParserToolkit.getDomainFields(cls);
    return getDomainFields(false);
  }

  /**
   * @effects 
   *  if exists {@link FieldDeclaration} in {@link #cls} that is assigned with {@link DAttr}
   *    return a {@link Collection} of them (preserving order of the fields in {@link #cls}
   *  else
   *    return null 
   */
  public Collection<FieldDeclaration> getDomainFields(boolean refresh) {
    if (domainFields == null || refresh) {
      domainFields = ParserToolkit.getDomainFields(cls);
    }
    
    return domainFields;
  }
  

  /**
   * @effects 
   *   if exists {@link FieldDeclaration} in {@link #cls} that is assigned with {@link DAttr}
   *   and whose names are among <tt>fieldNames</tt>
   *    return a {@link Collection} of them (preserving order of the fields in {@link #cls}
   *  else
   *    return null 
   */
  public Collection<FieldDeclaration> getDomainFieldsByName(
      Collection<String> fieldNames) {
    return ParserToolkit.getDomainFieldsByName(cls, fieldNames);
  }

  /**
   * @effects 
   *  if exists  in this a {@link MethodDeclaration} <tt>d</tt> of a domain method <tt>m</tt> such that
   *  <tt>DOpt(m).type = optType /\ AttrRef(m).value = attribName</tt>
   *    return <tt>d</tt>
   *  else
   *    return null
   *    
   * @version 5.2
   */
  public MethodDeclaration getDomainMethodByAttrRef(
      DOpt.Type optType,
      String attribName) {
    return ParserToolkit.getDomainMethodByAttrRef(cls, optType, attribName);
  }
  
  /**
   * @effects 
   *  if exists domain methods of this
   *    return them as {@link Collection} (in definition order)
   *  else
   *    return null
   * @version 5.2
   */
  public Collection<MethodDeclaration> getDomainMethods() {
    return getDomainMethods(false);
  }

  /**
   * @effects 
   *  if exists {@link MethodDeclaration}s in this
   *    return them as List
   *  else
   *    return null 
   * @version 5.4 
   */
  public List<MethodDeclaration> getMethods() {
    return ParserToolkit.getDeclaredMethods(cls);
  }
  
  /**
   * @effects 
   *  if the specified method is declared in {@link #ast}
   *    return it
   *  else
   *    return null
   * @version 5.4
   */
  public MethodDeclaration getMethod(String methodName) {
    return ParserToolkit.getMethodByName(cls, methodName);
  }
  
  /**
   * @effects 
   *  if refresh = true || not exists domain methods of this
   *    extract them from {@link #cls} into {@link #domainMethods} (in definition order)
   *    
   *  return {@link #domainMethods}
   * @version 5.2
   */
  public Collection<MethodDeclaration> getDomainMethods(boolean refresh) {
    if (domainMethods == null || refresh) {
      domainMethods = ParserToolkit.getDomainMethods(cls);
    }
    
    return domainMethods;
  }

// should not do this  
//  /**
//   * @effects 
//   *  return {@link #ast} 
//   */
//  public CompilationUnit getAst() {
//    return ast;
//  }

  /**
   * @modifies <tt>targetAst</tt>
   * @effects 
   *  transfer all relevant {@link ImportDeclaration}(s) in {@link #ast} for <tt>type</tt> to <tt>targetAst</tt>.
   */
  public void transferImportsFor(Type type, CompilationUnit targetAst) {
    ParserToolkit.transferImportsForType(ast, type, targetAst);
  }

  /**
   * @effects 
   *  return full package declaration of {@link #ast} 
   */
  public String getPackageDeclaration() {
    return ParserToolkit.getPackageDeclaration(ast);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cls == null) ? 0 : cls.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ClassAST other = (ClassAST) obj;
    if (cls == null) {
      if (other.cls != null)
        return false;
    } else if (!cls.equals(other.cls))
      return false;
    return true;
  }


  /**
   * @effects 
   *  if <tt>pkg != null</tt>
   *    registers to {@link #ast} <tt>pkg</tt> as an import  
   *  else 
   *    do nothing
   * 
   * @version 5.2
   */
  public void addImport(String pkg) {
    if (pkg != null) {
      ast.addImport(pkg);
    }
  }
  
  /**
   * @effects 
   *  if <tt>pkgs != null</tt>
   *    registers to {@link #ast} each <tt>pkg</tt> in <tt>pkgs</tt> as an import  
   *  else 
   *    do nothing
   * @version 5.2
   */
  public void addImports(String[] pkgs) {
    if (pkgs != null) {
      for (String pkg : pkgs) {
        ast.addImport(pkg);
      }
    }
  }

  /**
   * @modifies this
   * @effects 
   *  add each c in <tt>clses</tt> to this as an import
   * @version 5.2
   * 
   */
  public void addImports(Class[] clses) {
    if (clses != null) {
      for (Class c : clses) {
        ast.addImport(c);
      }
    }
  }
  
  /**
   * @effects 
   *  add <tt>cls</tt> to this as an import.
   * @version 5.2
   */
  public void addImport(Class cls) {
    if (cls != null) {
      ast.addImport(cls);
    }
  }
  
  /**
   * @effects 
   *  return the <b>current</b> state space of this (the state space may be changed 
   *  by methods such as {@link #addField(Type, String, Modifier...)}). 
   *  
   *  <p>If state space is empty (i.e. containing no domain fields) then return null.
   * @version 5.2
   */
  public Map<DAttrDef, FieldDef> getStateSpace() {
    return getStateSpace(false);
  }
  
  /**
   * @effects 
   *  Read the state space of {@link #cls} and records it into {@link #stateSpace}.
   *  If <tt>restart = true</tt> then re-read the state space (regardless of whether 
   *  {@link #stateSpace} has been initialised)
   *  
   *  <p>If state space is empty (i.e. containing no domain fields) then return null.
   *  
   * @version 
   * - 5.2<br>
   * - 5.2c: return null instead of throwing NotFoundException 
   */
  public Map<DAttrDef, FieldDef> getStateSpace(boolean restart) {
    if (stateSpace == null || restart) {
      // read state space spec
      // a map to record the state space elements
      stateSpace = new LinkedHashMap<>();
      List<String> imports = ParserToolkit.getImports(ast);
      
      // walk the AST to extract the state space elements into stateSpace
      String pkgName = getPackageDeclaration();
      
      new StateSpaceVisitor(pkgName, imports).visit(ast, stateSpace);
      
      if (stateSpace.isEmpty()) {
        stateSpace = null;
        // no domain attributes defined
        //v5.2c: throw new NotFoundException(NotFoundException.Code.ATTRIBUTES_NOT_FOUND, new Object[] {srcFile});
      }    
    }
    
    return stateSpace;
  }

  /**
   * @effects 
   *   return the source code text of this 
   * @version 5.2
   */
  public String getSourceCode() {
    return ast.toString();
  }

  /**
   * @modifies this
   * @effects 
   *   create in {@link #cls} a {@link FieldDeclaration} from the input parameters, 
   *   return the result.
   *    
   * @version 5.2
   */
  public FieldDeclaration addField(Type declaredType, String name,
      Modifier... modifier) {
    return cls.addField(declaredType, name, modifier);
  }

  /**
   * @effects 
   *  invoke {@link #createMethod(Type, String, Modifier...)}
   *  
   * @version 5.4 
   */
  public MethodDeclaration addMethod(Type type, String methodName,
      Modifier[] mods) {
    return createMethod(type, methodName, mods);
  }
  
  /**
   * @modifies this
   * @effects 
   *   create in {@link #cls} a {@link MethodDeclaration} from input parameters, 
   *   return the result.
   *     
   * @version 5.2
   */
  public MethodDeclaration createMethod(Type returnType, String mname,
      Modifier...modifiers) {
    return ParserToolkit.createMethod(cls, returnType, mname, modifiers);
  }

  /**
   * @effects 
   *  if dcls has a field named <tt>name</tt>, typed <tt>declaredType</tt> and has <tt>modifier</tt>
   *    return true
   *  else
   *    return false
   * @version 5.2 
   */
  public boolean hasField(String name, Type declaredType, Modifier... modifier) {
    return ParserToolkit.hasField(cls, name, declaredType, modifier);
  }
  

  /**
   * @effects 
   *  if exists a field with the specified name
   *    return {@link FieldDeclaration}
   *  else
   *    return null
   * @version 5.4.1
   * 
   */
  public FieldDeclaration getField(String fieldName) {
    return ParserToolkit.getFieldByName(cls, fieldName);
  }
  
  /**
   * @effects 
   *  return {@link #cls}
   * @version 5.2
   * 
   */
  public ClassOrInterfaceDeclaration getCls() {
    return cls;
  }

  /**
   * NOTE: try to avoid processing the returned {@link CompilationUnit} directly. 
   * Use this class's methods to do so. 
   * 
   * @effects 
   *  return {@link #ast} 
   * @version 5.2
   */
  public CompilationUnit getAst() {
    return ast;
  }

  /**
   * @requires all referenced classes of this are available in the class path 
   * 
   * @effects 
   *  if this is a sub-class
   *    return the super-class declaration in {@link #ast} as {@link Class} (using {@link Class#forName(String)} to load it)
   *    
   *    Throws NotFoundException if the specified class cannot be found by {@link Class#forName(String).
   *  else
   *    return null 
   */
  public ClassAST getSuperClass(
// v5.4.1     SourceModel model
      Dom model
      ) throws NotFoundException {
    NodeList<ClassOrInterfaceType> sups = cls.getExtendedTypes();
    if (sups == null || sups.size() == 0) {
      // not a sub-class
      return null;
    } else {
      // a sub-class
      ClassOrInterfaceType supType = sups.get(0);
      
      // load the class itself
      String clsName = supType.getNameAsString();
      String fqn = ParserToolkit.getFqnFor(ast, supType);
      
      ClassAST supCls = model.loadClass(clsName, fqn);
      
      return supCls;
    }
  }
  
  /**
   * @requires all referenced classes of this are available in the class path 
   * 
   * @effects 
   *  if this is a sub-class
   *    return the super-class declaration in {@link #ast} as {@link Class} (using {@link Class#forName(String)} to load it)
   *    
   *    Throws NotFoundException if the specified class cannot be found by {@link Class#forName(String).
   *  else
   *    return null 
   */
  public Class getSuperCls() throws NotFoundException {
    /*
    NodeList<ClassOrInterfaceType> sups = cls.getExtendedTypes();
    if (sups == null || sups.size() == 0) {
      // not a sub-class
      return null;
    } else {
      // a sub-class
      ClassOrInterfaceType supType = sups.get(0);
      
      // load the class itself
      String fqn = ParserToolkit.getFqnFor(ast, supType);
      
      Class supCls;
      try {
        supCls = Class.forName(fqn);
        
        return supCls;
      } catch (ClassNotFoundException e) { // should not happen
        throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e, new String[] {fqn});
      }
    }
    */
    return ParserToolkit.getSuperCls(ast, cls);
  }

  /**
   * @modifies this
   * 
   * @requires this is not a sub-class of another class (use {@link #getSuperCls()} to check)
   * 
   * @effects 
   *  makes this the subclass (i.e. <code>extends</code>) <code>superCls</code>  
   * @version 5.4.1
   */
  public void setSuperCls(ClassOrInterfaceType superCls) {
    //cls.addExtendedType(superCls);
    ParserToolkit.setSuperCls(cls, superCls);
  }
  
  /**
   * @effects 
   *  if this is a sub-class
   *    return the super-class declaration in {@link #ast} as {@link ClassOrInterfaceType}
   *  else
   *    return null 
   * @version 5.4.1
   */
  public ClassOrInterfaceType getSuperClsType() {
    /* 
    NodeList<ClassOrInterfaceType> sups = cls.getExtendedTypes();
    if (sups == null || sups.size() == 0) {
      // not a sub-class
      return null;
    } else {
      // a sub-class
      ClassOrInterfaceType supType = sups.get(0);
      return supType;
    }
    */
    return ParserToolkit.getSuperClsType(cls);
  }
  

  /**
   * @effects 
   *  if this implements interfaces
   *    return them as {@link ClassOrInterfaceType}s
   *  else
   *    return null 
   * @version 5.4.1
   */
  public Collection<ClassOrInterfaceType> getSuperIntfs() {
    return ParserToolkit.getSuperIntfs(cls);
  }
  
  /**
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addMethodParam(MethodDeclaration md, String paramName,
      Type paramType) {
    return ParserToolkit.addMethodParam(md, paramName, paramType);
  }

  /**
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addMethodParam(String methodName, String paramName,
      Type paramType) {
    return addMethodParam(ParserToolkit.getMethodByName(cls, methodName), paramName, paramType);
  }

  /**
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addMethodBody(MethodDeclaration md) {
    return ParserToolkit.createMethodBodyBlock(md);
  }

  /**
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addMethodBody(String methodName) {
    return addMethodBody(ParserToolkit.getMethodByName(cls, methodName));
  }

  /**
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addMethodStmt(BlockStmt body, String stmt) {
    return ParserToolkit.addMethodStatement(body, stmt);
  }

  /**
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addMethodStmt(String methodName, String stmt) {
    return addMethodStmt(ParserToolkit.getMethodBody(cls, methodName), stmt);
  }

  /**
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node pushMethodStmt(BlockStmt body, String stmt) {
    return ParserToolkit.pushMethodStmt(body, stmt);
  }

  /**
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node pushMethodStmt(String methodName, String stmt) {
    return pushMethodStmt(ParserToolkit.getMethodBody(cls, methodName), stmt);
  }

  /**
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addMethodStmtBlock(BlockStmt body, String stmtBlock) {
    return ParserToolkit.addMethodStatements(body, stmtBlock);
  }

  /**
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addMethodStmtBlock(String methodName, String stmtBlock) {
    return addMethodStmtBlock(ParserToolkit.getMethodBody(cls, methodName), stmtBlock);
  }

  /**
   * @requires <code>anoPropVals</code> is written in the <b>simplified, single-line Json syntax</b> 
   *  (i.e. without the outer curly brackets {})
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addClsAno(Class<? extends Annotation> anoCls, String anoPropVals) {
    return ParserToolkit.addClassAno(cls, anoCls, anoPropVals);
  }

  /**
   * @requires <code>anoPropVals</code> is written in the <b>simplified, single-line Json syntax</b> 
   *  (i.e. without the outer curly brackets {})
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addFieldAno(FieldDeclaration fd,
      Class<? extends Annotation> anoCls, String anoPropVals) {
    return ParserToolkit.addFieldAno(fd, anoCls, anoPropVals);
  }

  /**
   * @requires <code>anoPropVals</code> is written in the <b>simplified, single-line Json syntax</b> 
   *  (i.e. without the outer curly brackets {})
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addFieldAno(String fieldName, Class<? extends Annotation> anoCls,
      String anoPropVals) {
    return addFieldAno(ParserToolkit.getFieldByName(cls, fieldName), 
        anoCls, anoPropVals);
  }

  /**
   * @requires <code>anoPropVals</code> is written in the <b>simplified, single-line Json syntax</b> 
   *  (i.e. without the outer curly brackets {})
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addFieldAno(FieldDeclaration fd,
      Class<? extends Annotation> anoCls, JsonObject anoPropValJson) {
    return ParserToolkit.addFieldAno(fd, anoCls, anoPropValJson);
  }

  /**
   * @requires <code>anoPropVals</code> is written in the <b>simplified, single-line Json syntax</b> 
   *  (i.e. without the outer curly brackets {})
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addFieldAno(String fieldName, Class<? extends Annotation> anoCls,
      JsonObject anoPropValJson) {
    return addFieldAno(ParserToolkit.getFieldByName(cls, fieldName), 
        anoCls, anoPropValJson);
  }
  
  /**
   * @requires <code>anoPropVals</code> is written in the <b>simplified, single-line Json syntax</b> 
   *  (i.e. without the outer curly brackets {})
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addMethodAno(MethodDeclaration md,
      Class<? extends Annotation> anoCls, String anoPropVals) {
    return ParserToolkit.addMethodAno(md, anoCls, anoPropVals);
  }

  /**
   * @requires <code>anoPropVals</code> is written in the <b>simplified, single-line Json syntax</b> 
   *  (i.e. without the outer curly brackets {})
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public Node addMethodAno(String methodName,
      Class<? extends Annotation> anoCls, String anoPropVals) {
    return addMethodAno(ParserToolkit.getMethodByName(cls, methodName), anoCls, anoPropVals);
  }

  /**
   * @modifies cls
   * @effects 
   *  update this with an <code>implements</code> clause
   *  for <code>intfClasses</code>
   * @version 5.4
   */
  public void addClassImplement(Class<?>...intfCls) {
    ParserToolkit.addClassImplement(cls, intfCls);
  }

  /**
   * @modifies cls
   * @effects 
   *  update this with <code>implements</code> clause
   *  for <code>intfCls</code>
   * @version 5.4.1
   */
  public void addClassImplement(ClassOrInterfaceType...intfClsses) {
    ParserToolkit.addClassImplement(cls, intfClsses);
  }
  
  /**
   * @effects 
   *  adds the exception classes specified in <code>throwClasses</code> to the specified method.
   * 
   * @version 5.4
   * @param methodName 
   */
  public <T extends Throwable> void addMethodThrows(String methodName, Class<T>...throwClasses) {
    ParserToolkit.addMethodThrows(cls, methodName, throwClasses);
  }

  /**
   * @effects 
   *  adds <code>stmt</code> to the body of the constructor 
   *  whose parameter types are <code>paramTypes</code>
   * 
   * @version 5.4
   */
  public void addConstructorStmt(String[] paramTypes, String stmt) {
    ParserToolkit.addConstructorStatement(cls, paramTypes, stmt);
  }

  /**
   * @effects 
   *  copy the specified method to {@link #ast} and return the newly-created method
   * @version 5.4
   */
  public Node addMethod(MethodDeclaration method) {
    return ParserToolkit.addMethod(cls, method);
  }

  /**
   * @effects 
   *  return all the import statements in this or null of none is found
   * @version 5.4
   */
  public List<String> getImport() {
    return ParserToolkit.getImports(ast);
  }

  /**
   * @effects 
   *  adds the specified imports into this
   * @version 5.4
   */
  public void addImport(List<String> imports) {
    ParserToolkit.addImports(ast, imports);
  }

  /**
   * @effects 
   *  if <code>cls</code> is defined with annotation <code>anoCls</code>
   *    return true
   *  else
   *    return false
   * @version 5.4
   */
  public boolean hasAnnotation(Class<? extends Annotation> anoCls) {
    return ParserToolkit.hasAnnotation(cls, anoCls);
  }

  /**
   * @effects 
   *  if {@link #cls} has a default constructor
   *    return its declaration
   *  else
   *    return null
   * @version 5.4
   * 
   */
  public ConstructorDeclaration getDefaultConstructor() {
    return ParserToolkit.getDefaultConstructor(cls);
  }

  /**
   * @effects 
   *  creates in {@link #cls} a default constructor whose body is copied from <code>constr</code>
   * @version 5.4
   */
  public void addDefaultConstructor(ConstructorDeclaration constr) {
    ParserToolkit.addDefaultConstructor(cls, constr);
  }

  /**
   * @effects 
   *  adds to the constructor <code>constr</code> the specified statement block
   * @version 5.4
   */
  public void addConstructorStmtBlock(ConstructorDeclaration constr,
      BlockStmt body) {
    ParserToolkit.addConstructorStatements(constr, body);
  }

  /**
   * @modifies <code>cls</code> in this
   * @effects 
   *  update <code>cls</code> by replacing all type name references to <code>name</code> by <code>newName</code>.
   * @version 5.4
   */
  public void updateTypeNameRef(String name, String newName) {
    ParserToolkit.updateTypeNameRef(cls, name, newName);
  }


  /**
   * @requires 
   *  this and <code>otherAst</code> represent a single class compilation unit
   *  
   * @modifies this
   *  
   * @effects 
   *  merges elements of <code>otherAst</code> to <code>this</code>
   *  
   * @version 5.4.1
   */
  public void mergeWith(ClassAST otherAst) {
    final List<Node> children = cls.getChildNodes();
    final ClassOrInterfaceDeclaration otherCls = otherAst.getCls();
    final List<Node> otherChildren = otherCls.getChildNodes();
    final Stack<BodyDeclaration<?>> newNodes = new Stack<>();
    
    final boolean[] matchNode = {false};
    
    // merge imports
    List<String> imports = otherAst.getImport();
    addImport(imports);
    
    // merge class header
    mergeClassHeader(otherAst);
    
    // merge class content
    otherChildren.forEach(otherChild -> {
      matchNode[0] = false;
      if (otherChild instanceof FieldDeclaration) {
        FieldDeclaration otherFd = (FieldDeclaration) otherChild;
        final String otherFdName = ParserToolkit.getFieldName(otherFd);
        for (Node child : children) {
          if (child instanceof FieldDeclaration) {
            FieldDeclaration fd = (FieldDeclaration) child;
            String fdName = ParserToolkit.getFieldName(fd);
            if (fdName.equals(otherFdName)) {
              // matching field -> merge
              ParserToolkit.mergeDField(fd, otherFd);
              matchNode[0] = true;
              break;
            }
          }
        } 
        
        // if no match found then fd is new -> add it
        if (!matchNode[0]) {
          newNodes.push(otherFd);
        }
      } else if (otherChild instanceof CallableDeclaration) {
        CallableDeclaration<?> otherMd = (CallableDeclaration<?>) otherChild;
        for (Node child : children) {
          if (child instanceof CallableDeclaration) {
            CallableDeclaration<?> md = (CallableDeclaration<?>) child;
            if (ParserToolkit.isSameMethodSignature(md, otherMd)) {
              // matching method signature -> merge
              ParserToolkit.mergeDOpts(md, otherMd);
              matchNode[0] = true;
              break;
            }
          }
        } 
        
        // if no match found then fd is new -> add it
        if (!matchNode[0]) {
          newNodes.push(otherMd);
        } 
      }
    }); // end otherChildren
    
    // if there are new nodes then add them 
    if (!newNodes.isEmpty()) {
      newNodes.forEach(cls::addMember);
    }
  }

  
  /**
   * @modifies {@link #cls}
   * @effects 
   *  merges <code>otherAst</code>'s class header with {@link #cls}'s class header
   * @version 5.4.1
   */
  public void mergeClassHeader(ClassAST otherAst) throws NotPossibleException {
    // for now, interested in supertypes
    mergeSuperTypes(otherAst);
  }
  
  /**
   * @modifies {@link #cls}
   * @effects 
   *  merges all supertypes of <code>otherAst</code> (if any) with {@link #cls}'s class header
   * @version 5.4.1
   */
  public void mergeSuperTypes(ClassAST otherAst) throws NotPossibleException {

    ClassOrInterfaceType superCls = otherAst.getSuperClsType();
    Collection<ClassOrInterfaceType> superIntfs = otherAst.getSuperIntfs();
    
    if (superCls != null) {
      ParserToolkit.setSuperCls(cls, superCls);
    }
    
    if (superIntfs != null) {
      ParserToolkit.addClassImplement(cls, 
          superIntfs.toArray(new ClassOrInterfaceType[superIntfs.size()]));
    }
  }
  

  /**
   * @effects 
   *  if {@link #srcFile} is specified
   *    save the content of {@link #ast} to {@link #srcFile} in <tt>outputDir</tt>
   *    to the package path of this.
   *  else
   *    throws NotPossibleException
   *   
   * @version 5.4
   * @param outputDirName 
   */
  public void save(String outputDir) throws NotPossibleException {
    if (srcFile == null) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_SAVE_FILE, 
          new Object[] {getName()+ ": File path not specified"});
    }
    
    String pkgPath = ToolkitIO.getPackagePath(outputDir, getPackageDeclaration());
    ToolkitIO.touchPath(pkgPath);
    String clsName = getName();
    String fileName = clsName + ToolkitIO.FILE_JAVA_EXT;
    String filePath = pkgPath + File.separator + fileName;
    ToolkitIO.writeUTF8TextFile(
        new File(filePath), ast.toString(), true);
  }
  
  @Override
  public ClassAST clone() {
    CompilationUnit newAst = ast.clone();
    ClassAST newClsAst = new ClassAST(this.className, this.srcFile, 
        newAst,
        newAst.getClassByName(className).get()
        );
    
    newClsAst.fqn = fqn;
    
    return newClsAst;
  }

  /**
   * @modifies this
   * @effects 
   *  changes the package declaration of ast to pkg
   *  
   * @version 5.4.1
   */
  public void setPackage(String pkg) {
    ParserToolkit.addPackage(ast, pkg);
  }

  /**
   * @modifies this
   * @effects 
   *   refactor {@link #cls} to have newName
   *    
   * @version 5.4.1
   * 
   */
  public void rename(String newName) {
    setSimpleName(newName);
    
    // rename constructor declarations
    cls.getChildNodes().forEach(n -> {
      if (n instanceof ConstructorDeclaration) {
        ((ConstructorDeclaration) n).setName(newName);
      }
    });
  }

  /**
   * @modifies this
   * @effects 
   *  refactor this st. the field named <code>currFieldName</code> is renamed to <code>newName</code>. 
   *  This also rename all methods whose name reference this field (e.g. getters, setters) and 
   *  all references to this field in method bodies.   
   *  
   * @version 5.4.1
   * 
   */
  public void renameField(String currFieldName, String newName) {
    ParserToolkit.renameField(cls, currFieldName, newName);
  }
}
