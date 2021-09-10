package jda.modules.mccl.conceptualmodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.ClassAST;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.parser.ParserToolkit;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.mccl.util.DomainConstants.DomainMesg;
import jda.mosa.view.View;

/**
 * @overview Represents an MCC.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2
 */
/* TODO: MCC: to support MCC of a subclass
 * (1) extends MCC of the super-class
 * (2) createViewFields: only create view fields of the additional domain attributes of the sub-type (if any)
 */
public class MCC {
  
  //private static final Expression UNKNOWN_PROP_VAL = new StringLiteralExpr("?");

  private static final Class<ModuleDescriptor> ModuleDescCls = ModuleDescriptor.class;

  protected static final String DEF_IMAGE_ICON_EXT = ".png";

  private static final PrettyPrinterConfiguration printCfg;
  
  static {
    printCfg = new PrettyPrinterConfiguration();
    
    printCfg.setEndOfLineCharacter("\n");
    printCfg.setIndent("  ");
  }
  
  
  /** the AST of this class */
  private CompilationUnit ast;

  /** the domain class of this MCC */
  private ClassAST dcls;

  /**the MCC node in {@link #ast} */
  private ClassOrInterfaceDeclaration mccNode;

  /** the name of this (i.e. equals {@link #mccNode}.name) */
  private String name;

  /** the root output dir for saving {@link #getSourceCode()} */
  private File outputSrcFile;

  public MCC(String name, ClassAST dcls) {
    // get the shared class pool instance (singleton)
    ast = ParserToolkit.createJavaParserForClass(name, Modifier.PUBLIC);

    mccNode = ast.getClassByName(name).get();
    
    this.name = name;
    
    this.dcls = dcls;
  }

  /**
   * @effects 
   *  initialise this from content of <tt>srcFile</tt>
   */
  public MCC(String clsName, String srcFile, ClassAST dcls) throws NotFoundException {
    //  get the shared class pool instance (singleton)
    try {
      ast = ParserToolkit.createJavaParser(srcFile);
      
      mccNode = ast.getClassByName(clsName).get();
      
      this.name = mccNode.getNameAsString(); 
      
      this.dcls = dcls;
      
      this.outputSrcFile = new File(srcFile); 
          
    } catch (FileNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, e, new Object[] {srcFile});
    }
  }

  /**
   * @modifies this.{@link #ast}
   * @effects 
   *  creates in {@link #ast} a {@link ModuleDescriptor} for the module class represented by this.
   *  The model of this descriptor is set to {@link #dcls} 
   */
  public void createModuleDesc() {
    /**
     *  let n = dcls.name
     *   
     *  create ModuleDesc(
     *      modelDesc=ModelDesc(model=dcls),
     *      viewDesc=ViewDesc(formTitle="Form: n",domainClassLabel=n,imageIcon="n.png",view=View),
     *      controllerDesc=ControllerDesc())
     */
    
    // first add all necessary imports
    Class[] libClasses = {
        ModelDesc.class, 
        ViewDesc.class,
        View.class,
        RegionName.class,
        RegionType.class,
        ControllerDesc.class
    };
    String[] domClasses = {
        dcls.getFqn()
    };
    
    ParserToolkit.addImport(ast, libClasses);
    ParserToolkit.addImport(ast, domClasses);
    
    NormalAnnotationExpr moduleDesc = mccNode.addAndGetAnnotation(ModuleDescriptor.class);
    
    NodeList<MemberValuePair> props = new NodeList<>();
    moduleDesc.setPairs(props);
    
    // prop: name
    props.add(new MemberValuePair("name", new StringLiteralExpr(name)));
    
    String clsName = dcls.getName();
    
    // prop: modelDesc
    NodeList<MemberValuePair> modelDescProps = new NodeList<>();
    ClassExpr dclsExpr = dcls.getSimpleClassExpr();//dcls.getClassExpr();
    modelDescProps.add(
        new MemberValuePair("model", dclsExpr)
    );
    NormalAnnotationExpr modelDesc = new NormalAnnotationExpr(
        parseName(ModelDesc.class.getSimpleName()), modelDescProps);
    
    MemberValuePair modelDescProp = new MemberValuePair("modelDesc", modelDesc);
    props.add(modelDescProp);
    
    // prop: viewDesc
    NodeList<MemberValuePair> viewDescProps = new NodeList<>();
    viewDescProps.add(new MemberValuePair("formTitle", new StringLiteralExpr("Module: " + clsName)));
    viewDescProps.add(new MemberValuePair("imageIcon", new StringLiteralExpr(clsName+DEF_IMAGE_ICON_EXT)));
    viewDescProps.add(new MemberValuePair("domainClassLabel", new StringLiteralExpr(clsName)));
    viewDescProps.add(new MemberValuePair("view", ParserToolkit.createSimpleClassExprFor(View.class)));
    viewDescProps.add(new MemberValuePair("viewType", ParserToolkit.createSimpleFieldAccessExprFor(RegionType.Data)));
    viewDescProps.add(new MemberValuePair("parent", ParserToolkit.createSimpleFieldAccessExprFor(RegionName.Tools)));
            
    NormalAnnotationExpr viewDesc = new NormalAnnotationExpr(
        parseName(ViewDesc.class.getSimpleName()), viewDescProps);
    
    MemberValuePair viewDescProp = new MemberValuePair("viewDesc", viewDesc);
    props.add(viewDescProp);
    
    // prop: controllerDesc
    NodeList<MemberValuePair> controllerDescProps = new NodeList<>();
    NormalAnnotationExpr controllerDesc = new NormalAnnotationExpr(
        parseName(ControllerDesc.class.getSimpleName()), controllerDescProps);
    
    MemberValuePair controllerDescProp = new MemberValuePair("controllerDesc", controllerDesc);
    props.add(controllerDescProp);
    
    // isPrimary
    MemberValuePair isPrimaryProp = new MemberValuePair("isPrimary", new BooleanLiteralExpr(Boolean.TRUE));
    props.add(isPrimaryProp);    
  }

  /**
   * @effects 
   *  if this has view fields
   *    return them as {@link Collection}, in the declaration order
   *  else
   *    return null
   *    
   * @version 20210310
   */
  public Collection<FieldDeclaration> getViewFields() {
    return mccNode.getFields();
  }
  
  /**
   * @modifies this.{@link #ast}
   * @effects 
   *  creates in {@link #ast} the view fields that reflect each domain field of {@link #dcls}. 
   */
  public void createViewFields() {
    // create title view field
    addTitleViewField(dcls.getName());
    
    // create other view fields
    Collection<FieldDeclaration> domainFields = dcls.getDomainFields();
    
    if (domainFields != null) {
      addViewFields(domainFields);
    }
  }

  /**
   * @modifies {@link #mccNode}
   * @effects 
   *    create in {@link #mccNode} a view field to reflect each domain field in <tt>domainFields</tt>
   */
  public void addViewFields(Collection<FieldDeclaration> domainFields) {
    String name;
    Type dtype;
    VariableDeclarator var;
    for (FieldDeclaration field: domainFields) {
      var = field.getVariable(0); // domain field statement has one var
      name = var.getNameAsString();
      dtype = var.getType();
      
      dcls.transferImportsFor(dtype, ast);
      
      addViewField(name, dtype);
    }
  }

  /**
   * @requires 
   *  <tt>updatedFieldsNameMap</tt> map new-field-name -> old-field-name for <tt>updatedFields</tt>
   *  
   * @modifies {@link #mccNode}
   * @effects 
   *  update the view fields of {@link #mccNode} that reflect <tt>updatedFields</tt>  
   *  such that they have new names as specified in <tt>updatedFieldsNameMap</tt> and 
   *  if their data types also differ then these are also changed. 
   */
  public void updateViewFields(Collection<FieldDeclaration> updatedFields,
      Map<String, String> updatedFieldsNameMap) {
    for (FieldDeclaration df : updatedFields) {
      String dfName = ParserToolkit.getFieldName(df);
      Type dtype = ParserToolkit.getFieldType(df);
      
      String oldName = updatedFieldsNameMap.get(dfName);
      
      Optional<FieldDeclaration> oldFieldOpt = mccNode.getFieldByName(oldName);
      if (oldFieldOpt.isPresent()) {
        FieldDeclaration oldField = oldFieldOpt.get();
        
        // update oldField with new name
        ParserToolkit.setViewFieldName(oldField, dfName);
        
        // update oldField with new type
        ParserToolkit.setDomainFieldType(oldField, dtype);
      }
    }
  }
  
  /**
   * @modifies {@link #mccNode}
   * @effects 
   *    update {@link #mccNode} to remove the view fields that reflect the domain fields whose names are in <tt>fieldNames</tt>.
   */
  public void deleteViewFields(Collection<String> fieldNames) throws NotFoundException, NotPossibleException {
    for (String fieldName : fieldNames) {
      Optional<FieldDeclaration> fieldOpt = mccNode.getFieldByName(fieldName);
      if (fieldOpt.isPresent()) {
        FieldDeclaration oldField = fieldOpt.get();
        
        // remove oldField
        boolean removed = mccNode.remove(oldField);
        if (!removed) {
          // something wrong
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, new Object[] {"MCC<ClassOrInterfaceDeclaration>", "remove", oldField});
        }
      } else {
        // raise a NotFoundException here
        throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND, new Object[] {fieldName, getName()});
      }
    }
  }
  
  /**
   * @modifies {@link #mccNode}
   * @effects 
   *    create in {@link #mccNode} a view field whose name is <tt>name</tt> and data type is <tt>dataType</tt>
   */
  private void addViewField(String name, Class dataType) {
    FieldDeclaration field = mccNode.addField(dataType, name, Modifier.PRIVATE);
    
    addViewFieldDesc(field, name);
  }
  
  /**
   * @modifies {@link #mccNode}
   * @effects 
   *    create in {@link #mccNode} a view field whose name is <tt>name</tt> and data type is <tt>dataType</tt>
   */
  private void addViewField(String name, Type dataType) {
    FieldDeclaration field = mccNode.addField(dataType, name, Modifier.PRIVATE);
    
    addViewFieldDesc(field, name);
  }

  /**
   * @modifies {@link #mccNode}
   * @effects 
   *    create in {@link #mccNode} a title view field whose label <tt>label</tt>
   */
  private void addTitleViewField(String label) {
    FieldDeclaration field = mccNode.addField(String.class, "title", Modifier.PRIVATE);
    
    addViewFieldDesc(field, label);
  }
  
  /**
   * @modifies field
   * @effects 
   *  assign a default {@link AttributeDesc} to to <tt>field</tt> whose label is <tt>label</tt>.
   */
  private void addViewFieldDesc(FieldDeclaration field, String label) {
    // create descriptor
    NormalAnnotationExpr desc = field.addAndGetAnnotation(AttributeDesc.class);
    NodeList<MemberValuePair> props = new NodeList<>();
    props.add(new MemberValuePair("label", new StringLiteralExpr(label)));
    desc.setPairs(props);    
  }
  
  /**
   * @effects 
   *  parse <tt>qualifiedName</tt> (import it if needed) and 
   *  return the {@link Name} object of <tt>qualifiedName</tt>
   */
  protected Name parseName(String qualifiedName) {
    return JavaParser.parseName(qualifiedName);
  }

  /**
   * @effects 
   *  return simple name of this
   */
  public String getName() {
    return name;
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
    return String.format("MCC(%s): %n %s", mccNode.getNameAsString(), ast.toString(printCfg));    
  }

  /**
   * @modifies this.{@link #ast}
   * @effects 
   *  changes package declaration of {@link #ast} to pkgName.
   */
  public void setPackageName(String pkgName) {
    ast.setPackageDeclaration(pkgName);
  }


  /**
   * @effects 
   *  return package name of this 
   * @version 5.4.1
   */
  public String getPackage() {
    return ParserToolkit.getPackageDeclaration(ast);
  }
  
  /**
   * @effects 
   *  return {@link #ast}.toString
   */
  public String getSourceCode() {
    return ast.toString();
  }

  /**
   * @modifies this.{@link #outputSrcFile}
   * @effects 
   *  write source code of {@link #ast} to a file in a sub-package directory of <tt>mccOutputRootDir</tt> 
   *  that corresponds to the package name of this (overwritting existing content, if any).
   *  
   *  <p>sets this.{@link #outputSrcFile} = the file.
   */
  public void save(String mccOutputRootDir) throws NotPossibleException {
    //this.mccOutputRootDir = mccOutputRootDir;
    
    String mccSrc = getSourceCode();
    String mccPkg = getPackage(); //ParserToolkit.getPackageDeclaration(ast);
    String mccFQN = mccPkg + "." + name;
    
    // write to file
    boolean overwrite = true;
    outputSrcFile = ToolkitIO.writeJavaSourceFile(mccOutputRootDir, mccFQN, mccSrc, overwrite); 
  }

  /**
   * @effects 
   *  if {@link #outputSrcFile} != null
   *    write source code of {@link #ast} to a file specified in {@link #outputSrcFile} 
   *    (overwritting existing content, if any).
   *  else
   *    do nothing
   */
  public void save() {
    if (outputSrcFile != null) {
      String mccSrc = getSourceCode();
      
      boolean overwrite = true;
      ToolkitIO.writeTextFileWithEncoding(outputSrcFile, mccSrc, "UTF-8", overwrite);
    }
  }


  /**
   * @requires {@link #outputSrcFile} is set by a constructor or 
   *  by invoking it {@link #save()} 
   *  
   * @effects 
   *  return the file to which this was saved.
   *  
   * @version 5.4.1
   */
  public File getOutputSrcFile() {
    return outputSrcFile;
  }
  
  /**
   * @effects 
   *  return {@link #dcls} 
   */
  public ClassAST getDomainClass() {
    return dcls;
  }

  /**
   * @requires 
   *  <tt>propChain.length >= 1</tt>
   *  
   * @effects
   *   return the value of property in <tt>propChain</tt> relative to the {@link ModuleDescriptor} of this.
   *   
   *   If some property in the chain is not found, return <tt>null</tt>
   *   
   *   <p>Throws  
   *   NotPossibleException if the property chain is ill-specified
   */
  public Expression getPropertyVal(String...propChain) throws NotPossibleException {
    if (propChain == null || propChain.length < 1) return null;

    // ModuleDescriptor(mcc)
    NormalAnnotationExpr ano = (NormalAnnotationExpr) 
        mccNode.getAnnotationByClass(ModuleDescCls).get();
    
    Expression val;
    int idx = 0, chainLen = propChain.length;
    boolean hasMore;
    for (String prop : propChain) {
      // get prop.val
      val = getPropertyVal(ano, prop);
      
      if (val == null) return null;
      
      hasMore = chainLen > idx+1;
      if (hasMore) {
        // val needs to be an Annotation element
        if (val instanceof NormalAnnotationExpr) {
          ano = (NormalAnnotationExpr) val;
        } else {
          // error!
          throw new NotPossibleException(DomainMesg.INVALID_NONE_ANNOTATION_TYPED_PROPERTY_VALUE, new Object[] {prop, val});
        }
      } else {
        // prop is last in propChain: return value
        return val;
      }
      idx++;
    }
    
    // should not happen
    return null;
  }

  /**
   * @effects 
   *  if exists  in <tt>ano</tt> a property named <tt>prop</tt>
   *    return its value as {@link Expression}
   *  else
   *    return null
   */
  private Expression getPropertyVal(NormalAnnotationExpr ano, String prop) {
    NodeList<MemberValuePair> propVals = ano.getPairs();
    
    for (MemberValuePair propVal : propVals) {
      if (propVal.getName().asString().equals(prop)) {
        // found it
        return propVal.getValue();
      }
    }
    
    // not found
    return null;
    
    //throw new NotFoundException(NotFoundException.Code.PROPERTY_NOT_FOUND, new Object[] {prop});
  }

  /**
   * @effects 
   *  return FQN of the class represented by <tt>clsExpr</tt> in this
   */
  public String getFqn(ClassExpr clsExpr) {
    // find among the imports...
    String fqn = ParserToolkit.getFqn(ast, clsExpr);
    
    return fqn;
  }

  /**
   * @effects 
   *  return FQN of this
   */
  public String getFqn() {
    String pkg = ParserToolkit.getPackageDeclaration(ast);
    String fqn = pkg + "." + name;
    
    return fqn;
  }
  
  /**
   * @effects 
   *    return {@link #ast} 
   */
  protected final CompilationUnit getAst() {
    return ast;
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
   * @effects 
   *    return {@link #mccNode} 
   */
  protected final ClassOrInterfaceDeclaration getMccNode() {
    return mccNode;
  }
  
//  /**
//   * @effects 
//   * 
//   * @version 
//   * 
//   */
//  public void setPropertyVal(String string, String string2, String newScope) {
//    
//  }

  /**
   * @modifies this
   * 
   * @effects 
   *  if state scope is specified
   *    add <tt>newFields</tt> to the end of it
   *    return true
   *  else
   *    return false
   */
  public boolean addToStateScope(ClassAST c, Collection<String> newFields) {
    ArrayInitializerExpr stateScope = (ArrayInitializerExpr) getPropertyVal("containmentTree", "stateScope");
    if (stateScope != null && !isDefaultScope(stateScope)) {
      // containmentTree.stateScope is specified
      addToScope(stateScope, newFields);
      return true;
    } else {
      return false;
    }
  }

  /**
   * @modifies this
   * @effects <pre> 
   *  for each containment edge in the containment tree of this whose property child equals c.fqn
   *    if stateScope of the edge's scope-desc is specified
   *      add newFields to end of it
   *  
   *  if containment tree was changed
   *    return true
   *  else
   *    return false
   *      
   *  </pre>
   *  
   *  @version 
   *  - 5.1c: improved to support an alternative design: CTree.edges
   */
  public boolean addToContainmentScopes(ClassAST c, Collection<String> newFields) {
    /* v5.1c:
    ArrayInitializerExpr treeExpr = (ArrayInitializerExpr) getPropertyVal("containmentTree", "subtrees");
    if (treeExpr == null) {
      // no containment tree
      return false;
    }
    
    NodeList<Expression> tree = treeExpr.getValues();
    
    if (tree.isEmpty()) {
      // tree is not specified
      return false;
    }
    
    // sub-tree is specified
    String myFqn = c.getFqn();
    boolean updated = false;
    NormalAnnotationExpr subTree1L;
    for (Expression subTreeExpr : tree) {
      subTree1L = (NormalAnnotationExpr) subTreeExpr;
      ArrayInitializerExpr childrenExpr = (ArrayInitializerExpr) getPropertyVal(subTree1L, "children");
      NodeList<Expression> children = childrenExpr.getValues();
      
      for (Expression childExpr : children) {
        NormalAnnotationExpr child = (NormalAnnotationExpr) childExpr;
        ClassExpr childCls = (ClassExpr) getPropertyVal(child, "cname");
        if (getFqn(childCls).equals(myFqn)) {
          // child node matches c
          // update scope (if specified)
          ArrayInitializerExpr contScopeExpr = (ArrayInitializerExpr) getPropertyVal(child, "scope");
          if (!isDefaultScope(contScopeExpr)) {
            addToScope(contScopeExpr, newFields);
            
            if (!updated) updated = true;
          }
        }
      }
    }
        
    return updated;
    */
    
    ArrayInitializerExpr treeExpr = (ArrayInitializerExpr) getPropertyVal("containmentTree", "edges");
    if (treeExpr != null) {
      // new method: using edges
      return addToContainmentScopesByEdges(c, newFields, treeExpr);
    } else {  // try the old, obsolete method (using subtrees)
      treeExpr = (ArrayInitializerExpr) getPropertyVal("containmentTree", "subtrees");
      if (treeExpr == null) {
        // no containment tree
        return false;
      } else {
        // old (obsolete) method: using subtrees
        return addToContainmentScopesBySubTrees(c, newFields, treeExpr);
      }
    }
  }

  /**
   * @requires 
   *  <tt>treeExpr</tt> is the value of the property "edges".  
   *  
   * @effects 
   *  performs as specified by {@link #addToContainmentScopes(ClassAST, Collection)} but for the new design that uses
   *  {@link CTree#edges()} to customise module containments.
   * 
   * @version 5.1c
   * 
   */
  private boolean addToContainmentScopesByEdges(ClassAST c,
      Collection<String> newFields, ArrayInitializerExpr treeExpr) {
    NodeList<Expression> edges = treeExpr.getValues();
    
    if (edges.isEmpty()) {
      // tree is not specified
      return false;
    }
    
    // edges are specified
    String myFqn = c.getFqn();
    boolean updated = false;
    NormalAnnotationExpr edge;
    for (Expression edgeExpr : edges) {
      edge = (NormalAnnotationExpr) edgeExpr;
      ClassExpr childCls = (ClassExpr) getPropertyVal(edge, "child");
      
      if (getFqn(childCls).equals(myFqn)) {
        // child node matches c
        // update edge's scope (if specified)
        NormalAnnotationExpr scopeDesc = (NormalAnnotationExpr) getPropertyVal(edge, "scopeDesc");
        ArrayInitializerExpr contScopeExpr = (ArrayInitializerExpr) getPropertyVal(scopeDesc, "stateScope");
        if (!isDefaultScope(contScopeExpr)) {
          addToScope(contScopeExpr, newFields);
          
          if (!updated) updated = true;
        }
      }
    }
        
    return updated;    
  }

  /**
   * @requires 
   *  <tt>treeExpr</tt> is the value of the property "subtrees".  
   *  
   * @effects 
   *  performs as specified by {@link #addToContainmentScopes(ClassAST, Collection)} but for the old design that uses
   *  {@link CTree#subtrees()} to customise module containments.
   * 
   * @version 5.1c
   */
  private boolean addToContainmentScopesBySubTrees(ClassAST c,
      Collection<String> newFields, ArrayInitializerExpr treeExpr) {
    NodeList<Expression> tree = treeExpr.getValues();
    
    if (tree.isEmpty()) {
      // tree is not specified
      return false;
    }
    
    // sub-tree is specified
    String myFqn = c.getFqn();
    boolean updated = false;
    NormalAnnotationExpr subTree1L;
    for (Expression subTreeExpr : tree) {
      subTree1L = (NormalAnnotationExpr) subTreeExpr;
      ArrayInitializerExpr childrenExpr = (ArrayInitializerExpr) getPropertyVal(subTree1L, "children");
      NodeList<Expression> children = childrenExpr.getValues();
      
      for (Expression childExpr : children) {
        NormalAnnotationExpr child = (NormalAnnotationExpr) childExpr;
        ClassExpr childCls = (ClassExpr) getPropertyVal(child, "cname");
        if (getFqn(childCls).equals(myFqn)) {
          // child node matches c
          // update scope (if specified)
          ArrayInitializerExpr contScopeExpr = (ArrayInitializerExpr) getPropertyVal(child, "scope");
          if (!isDefaultScope(contScopeExpr)) {
            addToScope(contScopeExpr, newFields);
            
            if (!updated) updated = true;
          }
        }
      }
    }
        
    return updated;    
  }

  /**
   * @modifies this
   * 
   * @effects 
   *  if state scope is specified
   *    remove <tt>delFields</tt> (if used at all) from it 
   *    return true
   *  else
   *    return false
   */
  public boolean removeFromStateScope(ClassAST c,
      Collection<String> delFields) {
    ArrayInitializerExpr stateScope = (ArrayInitializerExpr) getPropertyVal("containmentTree", "stateScope");
    if (stateScope != null && !isDefaultScope(stateScope)) {
      // containmentTree.stateScope is specified
      removeFromScope(stateScope, delFields);
      return true;
    } else {
      return false;
    }
  }

  /**
   * @modifies this
   * @effects <pre> 
   *  for each containment edge in containment tree of this whose property child equals c.fqn
   *    if stateScope of the edge's scope-desc is specified
   *      remove from it <tt>delFields</tt> (if used at all)
   *  
   *  if containment tree was changed
   *    return true
   *  else
   *    return false
   *      
   *  </pre>
   *  @version 
   *  - 5.1c: improved to support an alternative design: CTree.edges
   */
  public boolean removeFromContainmentScopes(ClassAST c,
      Collection<String> delFields) {
    /* v5.1c: 
    ArrayInitializerExpr treeExpr = (ArrayInitializerExpr) getPropertyVal("containmentTree", "subtrees");
    if (treeExpr == null) {
      // no containment tree
      return false;
    }
    
    NodeList<Expression> tree = treeExpr.getValues();
    
    if (tree.isEmpty()) {
      // subtree is not specified
      return false;
    }
    
    // sub-tree is specified
    String myFqn = c.getFqn();
    boolean updated = false;
    NormalAnnotationExpr subTree1L;
    for (Expression subTreeExpr : tree) {
      subTree1L = (NormalAnnotationExpr) subTreeExpr;
      ArrayInitializerExpr childrenExpr = (ArrayInitializerExpr) getPropertyVal(subTree1L, "children");
      NodeList<Expression> children = childrenExpr.getValues();
      
      for (Expression childExpr : children) {
        NormalAnnotationExpr child = (NormalAnnotationExpr) childExpr;
        ClassExpr childCls = (ClassExpr) getPropertyVal(child, "cname");
        if (getFqn(childCls).equals(myFqn)) {
          // child node matches c
          // update scope (if specified)
          ArrayInitializerExpr contScopeExpr = (ArrayInitializerExpr) getPropertyVal(child, "scope");
          if (!isDefaultScope(contScopeExpr)) {
            removeFromScope(contScopeExpr, delFields);
            
            if (!updated) updated = true;
          }
        }
      }
    }
    
    return updated;
    */
    ArrayInitializerExpr treeExpr = (ArrayInitializerExpr) getPropertyVal("containmentTree", "edges");
    if (treeExpr != null) {
      // new method: using edges
      return removeFromContainmentScopesByEdges(c, delFields, treeExpr);
    } else {  // try the old, obsolete method (using subtrees)
      treeExpr = (ArrayInitializerExpr) getPropertyVal("containmentTree", "subtrees");
      if (treeExpr == null) {
        // no containment tree
        return false;
      } else {
        // old (obsolete) method: using subtrees
        return removeFromContainmentScopesBySubTrees(c, delFields, treeExpr);
      }
    }
  }
  
  /**
   * @effects 
   *  performs as specified in {@link #removeFromContainmentScopes(ClassAST, Collection)} but for the new design 
   *  that uses {@link CTree#edges()}.
   *  
   * @version 5.1c
   * 
   */
  private boolean removeFromContainmentScopesByEdges(ClassAST c,
      Collection<String> delFields, ArrayInitializerExpr treeExpr) {
    NodeList<Expression> edges = treeExpr.getValues();
    
    if (edges.isEmpty()) {
      // tree is not specified
      return false;
    }
    
    // sub-tree is specified
    String myFqn = c.getFqn();
    boolean updated = false;
    NormalAnnotationExpr edge;
    for (Expression edgeExpr : edges) {
      edge = (NormalAnnotationExpr) edgeExpr;
      ClassExpr childCls = (ClassExpr) getPropertyVal(edge, "child");
      
      if (getFqn(childCls).equals(myFqn)) {
        // child node matches c
        // update scope (if specified)
        NormalAnnotationExpr scopeDesc = (NormalAnnotationExpr) getPropertyVal(edge, "scopeDesc");
        ArrayInitializerExpr contScopeExpr = (ArrayInitializerExpr) getPropertyVal(scopeDesc, "stateScope");
        if (!isDefaultScope(contScopeExpr)) {
          removeFromScope(contScopeExpr, delFields);
          
          if (!updated) updated = true;
        }        
      }
    }
    
    return updated;
  }

  /**
   * @effects 
   *  performs as specified in {@link #removeFromContainmentScopes(ClassAST, Collection) but for the old design 
   *  that uses {@link CTree#subtrees()}.
   *  
   * @version 5.1c
   * 
   */
  private boolean removeFromContainmentScopesBySubTrees(ClassAST c,
      Collection<String> delFields, ArrayInitializerExpr treeExpr) {
    NodeList<Expression> tree = treeExpr.getValues();
    
    if (tree.isEmpty()) {
      // tree is not specified
      return false;
    }
    
    // sub-tree is specified
    String myFqn = c.getFqn();
    boolean updated = false;
    NormalAnnotationExpr subTree1L;
    for (Expression subTreeExpr : tree) {
      subTree1L = (NormalAnnotationExpr) subTreeExpr;
      ArrayInitializerExpr childrenExpr = (ArrayInitializerExpr) getPropertyVal(subTree1L, "children");
      NodeList<Expression> children = childrenExpr.getValues();
      
      for (Expression childExpr : children) {
        NormalAnnotationExpr child = (NormalAnnotationExpr) childExpr;
        ClassExpr childCls = (ClassExpr) getPropertyVal(child, "cname");
        if (getFqn(childCls).equals(myFqn)) {
          // child node matches c
          // update scope (if specified)
          ArrayInitializerExpr contScopeExpr = (ArrayInitializerExpr) getPropertyVal(child, "scope");
          if (!isDefaultScope(contScopeExpr)) {
            removeFromScope(contScopeExpr, delFields);
            
            if (!updated) updated = true;
          }
        }
      }
    }
    
    return updated;
  }

  /**
   * @modifies <tt>scope</tt>
   * @effects
   *  add <tt>newFields</tt> to the end of <tt>scope</tt> 
   */
  private void addToScope(ArrayInitializerExpr scope,
      Collection<String> newFields) {
    NodeList<Expression> vals = scope.getValues();
    StringLiteralExpr fexp;
    
    for (String f : newFields) {
      fexp = new StringLiteralExpr(f);
      vals.add(fexp);
    }
  }

  /**
   * @modifies <tt>scope</tt>
   * @effects
   *  remove <tt>delFields</tt> from <tt>scope</tt> 
   */
  private void removeFromScope(ArrayInitializerExpr scope,
      Collection<String> delFields) {
    NodeList<Expression> vals = scope.getValues();
    
    for (String fname : delFields) {
      for (int i = 0; i < vals.size(); i++) {
        Expression val = vals.get(i);
        if (val instanceof FieldAccessExpr) { // e.g. Student.A_id
          FieldAccessExpr fval = (FieldAccessExpr) val;
          if (fval.getNameAsString().equals(fname)) {
            vals.remove(fval);
            i--;            
            break;  // expects only one match 
          }
        } else if (val instanceof StringLiteralExpr) { // e.g. "id"
          StringLiteralExpr fexp = (StringLiteralExpr) val; 
          if (fexp.getValue().equals(fname)) {
            // fname is in val: delete it
            vals.remove(fexp);
            i--;
            break;  // expects only one match 
          }
        }
        //TODO: any other cases for val?
      }
    }
  }
  
  /**
   * @requires 
   *  <tt>updatedFieldsNameMap</tt> map new-field-name -> old-field-name of <tt>c</tt>
   *  
   * @modifies this
   * 
   * @effects 
   *  if state scope is specified and contains some old name in <tt>updatedFieldsNameMap.values</tt>
   *    replace all old names in state scope with new names in <tt>updatedFieldsNameMap.keys</tt>
   *    return true
   *  else
   *    return false 
   * 
   */
  public boolean updateStateScope(ClassAST c,
      Map<String, String> updatedFieldsNameMap) {
    ArrayInitializerExpr stateScope = (ArrayInitializerExpr) getPropertyVal("containmentTree", "stateScope");
    boolean updated = false;
    if (stateScope != null && !isDefaultScope(stateScope)) {
      // containmentTree.stateScope is specified
      updated = updateScope(stateScope, updatedFieldsNameMap);
    }
    
    return updated;
  }

  /**
   * @requires 
   *  <tt>updatedFieldsNameMap</tt> map new-field-name -> old-field-name of <tt>c</tt>
   *  
   * @modifies this
   * @effects <pre> 
   *  for each containment edge in the containment tree of this whose property child equals c.fqn
   *    if stateScope of the edge's scope-desc is specified and contains some old name in <tt>updatedFieldsNameMap.values</tt>
   *      replace all old names in scope with new names in <tt>updatedFieldsNameMap.keys</tt>
   *  
   *  if containment tree was changed
   *    return true
   *  else
   *    return false
   *      
   *  </pre>
   *  @version 
   *  - 5.1c: improved to support an alternative design: CTree.edges
   *  
   */
  public boolean updateContainmentScopes(ClassAST c,
      Map<String, String> updatedFieldsNameMap) {
    /* v5.1c:
    ArrayInitializerExpr treeExpr = (ArrayInitializerExpr) getPropertyVal("containmentTree", "subtrees");
    
    if (treeExpr == null) {
      // no containment tree
      return false;
    }
    
    NodeList<Expression> tree = treeExpr.getValues();
    
    if (tree.isEmpty()) {
      // tree is not specified
      return false;
    }
    
    // sub-tree is specified
    String myFqn = c.getFqn();
    boolean updated = false;
    NormalAnnotationExpr subTree1L;
    for (Expression subTreeExpr : tree) {
      subTree1L = (NormalAnnotationExpr) subTreeExpr;
      ArrayInitializerExpr childrenExpr = (ArrayInitializerExpr) getPropertyVal(subTree1L, "children");
      NodeList<Expression> children = childrenExpr.getValues();
      
      for (Expression childExpr : children) {
        NormalAnnotationExpr child = (NormalAnnotationExpr) childExpr;
        ClassExpr childCls = (ClassExpr) getPropertyVal(child, "cname");
        if (getFqn(childCls).equals(myFqn)) {
          // child node matches c
          // update scope (if specified)
          ArrayInitializerExpr contScopeExpr = (ArrayInitializerExpr) getPropertyVal(child, "scope");
          if (!isDefaultScope(contScopeExpr)) {
            updateScope(contScopeExpr, updatedFieldsNameMap);
            
            if (!updated) updated = true;
          }
        }
      }
    }
    
    return updated;
    */
    ArrayInitializerExpr treeExpr = (ArrayInitializerExpr) getPropertyVal("containmentTree", "edges");
    if (treeExpr != null) {
      // new method: using edges
      return updateContainmentScopesByEdges(c, updatedFieldsNameMap, treeExpr);
    } else {  // try the old, obsolete method (using subtrees)
      treeExpr = (ArrayInitializerExpr) getPropertyVal("containmentTree", "subtrees");
      if (treeExpr == null) {
        // no containment tree
        return false;
      } else {
        // old (obsolete) method: using subtrees
        return updateContainmentScopesBySubTrees(c, updatedFieldsNameMap, treeExpr);
      }
    }
  }

  /**
   * @effects 
   *  performs as specified in {@link #updateContainmentScopes(ClassAST, Map)} but for the new design 
   *  that uses {@link CTree#edges()}.
   *  
   * @version 5.1c
   */
  private boolean updateContainmentScopesByEdges(ClassAST c,
      Map<String, String> updatedFieldsNameMap, ArrayInitializerExpr treeExpr) {
    NodeList<Expression> edges = treeExpr.getValues();
    
    if (edges.isEmpty()) {
      // tree is not specified
      return false;
    }
    
    // edges are specified
    String myFqn = c.getFqn();
    boolean updated = false;
    NormalAnnotationExpr edge;
    for (Expression edgeExpr : edges) {
      edge = (NormalAnnotationExpr) edgeExpr;
      ClassExpr childCls = (ClassExpr) getPropertyVal(edge, "child");
      
      if (getFqn(childCls).equals(myFqn)) {
        // child node matches c
        // update scope (if specified)
        NormalAnnotationExpr scopeDesc = (NormalAnnotationExpr) getPropertyVal(edge, "scopeDesc");
        ArrayInitializerExpr contScopeExpr = (ArrayInitializerExpr) getPropertyVal(scopeDesc, "stateScope");
        if (!isDefaultScope(contScopeExpr)) {
          updateScope(contScopeExpr, updatedFieldsNameMap);
          
          if (!updated) updated = true;          
        }
      }
    }
    
    return updated;
  }

  /**
   * @effects 
   *  performs as specified in {@link #updateContainmentScopes(ClassAST, Map)} but for the old design 
   *  that uses {@link CTree#subtrees()}.
   *  
   * @version 5.1c
   */
  private boolean updateContainmentScopesBySubTrees(ClassAST c,
      Map<String, String> updatedFieldsNameMap, ArrayInitializerExpr treeExpr) {
    NodeList<Expression> tree = treeExpr.getValues();
    
    if (tree.isEmpty()) {
      // tree is not specified
      return false;
    }
    
    // sub-tree is specified
    String myFqn = c.getFqn();
    boolean updated = false;
    NormalAnnotationExpr subTree1L;
    for (Expression subTreeExpr : tree) {
      subTree1L = (NormalAnnotationExpr) subTreeExpr;
      ArrayInitializerExpr childrenExpr = (ArrayInitializerExpr) getPropertyVal(subTree1L, "children");
      NodeList<Expression> children = childrenExpr.getValues();
      
      for (Expression childExpr : children) {
        NormalAnnotationExpr child = (NormalAnnotationExpr) childExpr;
        ClassExpr childCls = (ClassExpr) getPropertyVal(child, "cname");
        if (getFqn(childCls).equals(myFqn)) {
          // child node matches c
          // update scope (if specified)
          ArrayInitializerExpr contScopeExpr = (ArrayInitializerExpr) getPropertyVal(child, "scope");
          if (!isDefaultScope(contScopeExpr)) {
            updateScope(contScopeExpr, updatedFieldsNameMap);
            
            if (!updated) updated = true;
          }
        }
      }
    }
    
    return updated;
  }

  /**
   * @modifies scope
   * @effects
   *  if <tt>scope</tt> contains some old name in <tt>updatedFieldsNameMap.values</tt>
   *    replace all old names in <tt>scope</tt> with new names in <tt>updatedFieldsNameMap.keys</tt>
   *    return true
   *  else
   *    return false 
   */
  private boolean updateScope(ArrayInitializerExpr scope,
      Map<String, String> updatedFieldsNameMap) {
    NodeList<Expression> vals = scope.getValues();
    
    String newName, oldName;
    boolean updated = false;

    for (Entry<String,String> e : updatedFieldsNameMap.entrySet()) {
      newName = e.getKey();
      oldName = e.getValue();
      for (Expression val : vals) {
        if (val instanceof FieldAccessExpr) { // e.g. Student.A_id
          FieldAccessExpr fval = (FieldAccessExpr) val;
          if (fval.getNameAsString().equals(oldName)) {
            // a match: replace with new name
            fval.setName(newName);
            if (!updated) updated = true;
          }
        } else if (val instanceof StringLiteralExpr) { // e.g. "id"
          StringLiteralExpr fval = (StringLiteralExpr) val;
          if (fval.getValue().equals(oldName)) {
            // a match: replace with new name
            fval.setValue(newName);
            if (!updated) updated = true;
          }
        }
        //TODO: any other cases for val?
      }
    }
    
    return updated;
  }
  
  /**
   * @effects 
   *  if <tt>scopeExpr</tt> represents the default scope
   *    return true
   *  else
   *    return false 
   */
  private boolean isDefaultScope(ArrayInitializerExpr scopeExpr) {
    NodeList<Expression> vals = scopeExpr.getValues();
    if (vals.isEmpty() || (vals.size() == 1 && vals.get(0).equals("*"))) {
      // default scope
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * @effects 
   *  if ModelDesc(this).model matches c.fqn
   *    return true
   *  else
   *    return false 
   */
  public boolean isModelMatching(ClassAST c) {
    String myFqn = c.getFqn();
    ClassExpr modelExpr = (ClassExpr) getPropertyVal("modelDesc", "model");
    if (modelExpr != null && getFqn(modelExpr).equals(myFqn)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @requires 
   *  <tt>supName</tt> has been imported into {@link #ast}
   *  
   * @modifies {@link #ast}, {@link #mccNode}
   * @effects 
   *  update {@link #mccNode} to extend <tt>supName</tt> as the super class 
   */
  public void setSuperType(String supName) {
//    ClassOrInterfaceType supType = (ClassOrInterfaceType) ParserToolkit.createClassOrInterfaceType(supName);
    mccNode.addExtendedType(supName);
  }

}
