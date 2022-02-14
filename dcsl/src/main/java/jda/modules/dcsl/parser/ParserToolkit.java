/**
 * 
 */
package jda.modules.dcsl.parser;

import static com.github.javaparser.ast.type.PrimitiveType.Primitive.BOOLEAN;
import static com.github.javaparser.ast.type.PrimitiveType.Primitive.CHAR;
import static com.github.javaparser.ast.type.PrimitiveType.Primitive.DOUBLE;
import static com.github.javaparser.ast.type.PrimitiveType.Primitive.FLOAT;
import static com.github.javaparser.ast.type.PrimitiveType.Primitive.INT;
import static com.github.javaparser.ast.type.PrimitiveType.Primitive.LONG;
import static com.github.javaparser.ast.type.PrimitiveType.Primitive.SHORT;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotImplementedException.Code;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.parser.behaviourspace.MethodSig;
import jda.modules.dcsl.parser.statespace.metadef.DAssocDef;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.parser.statespace.parser.AnnotationMerge;
import jda.modules.dcsl.parser.statespace.parser.AttribPropertyVisitor;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.util.DClassTk;

/**
 * @overview 
 *  A toolkit containing shared operations for JavaParser. 
 *  
 * @author dmle
 *
 * @version 1.1 (modified to work with JavaParser 3.2.5
 */
public class ParserToolkit {
  
  /**
   * @overview 
   *  Hierarchical type name constants.
   *  
   * @author Duc Minh Le (ducmle)
   */
  public interface TypeName {
    String name();
    int ordinal();
    /**
     * @effects 
     *  if this is not a {@link Collections} OR this is not {@link Collections#Any} 
     *    if {@link #name()} equals <tt>name</tt>
     *      return true
     *    else
     *      return false
     *  else
     *    if {@link #name()} is any of those non-any member in {@link Collections}
     *      return true
     *    else
     *      return false
     */
    boolean equalsByName(String name);
    
    enum Others implements TypeName {
      /** {@link Object} */
      Object;

      /* (non-Javadoc)
       * @see domainapp.modules.mccl.util.ParserToolkit.TypeName#equalsByName(java.lang.String)
       */
      @Override
      public boolean equalsByName(String name) {
        return name != null && name().equals(name);
      }
    }
    
    enum Collections implements TypeName {
      /** {@link Collection} */
      Collection,
      /** {@link java.util.Set} */
      Set,
      /** {@link List} */
      List,
      /**any of the types above*/
      Any,
      //TODO: add other types here
      ;
      
      /* (non-Javadoc)
       * @see domainapp.modules.mccl.util.ParserToolkit.TypeName#equalsByName(java.lang.String)
       */
      @Override
      public boolean equalsByName(String name) {
        if (name == null) return false;

        if (this.equals(Any)) {
          // any collection
          for (Collections c : values()) {
            if (!c.equals(Any) && c.name().equals(name)) {
              // name matches a non-any member
              return true;
            }
          }
          
          // name does not match with any non-any member
          return false;
        } else {
          // non-any collection
          return name().equals(name);
        }
      }
    }
  } /** end TypeName */

  /**
   * well-known constants for the wrapper types of {@link PrimitiveType}s
   */
  private static final Map<Primitive, ClassOrInterfaceType> wrapperTypeMap = new HashMap<>();
  static {
    // initialise wrapperTypeMap
    Primitive[] prims = PrimitiveType.Primitive.values();
    for (Primitive prim : prims) {
      wrapperTypeMap.put(prim, prim.toBoxedType());
    }
  }

//  /**
//   * well-known constants for the wrapper types of {@link PrimitiveType}s
//   */
//  private static final Map<Primitive, PrimitiveType> wrapperTypeRawMap = new HashMap<>();
//  static {
//    // initialise wrapperTypeMap
//    Primitive[] prims = PrimitiveType.Primitive.values();
//    for (Primitive prim : prims) {
//      try {
//        wrapperTypeRawMap.put(prim, (PrimitiveType) PrimitiveType.class.getMethod(prim.name().toLowerCase()+"Type").invoke(null));
//      } catch (IllegalAccessException | IllegalArgumentException
//          | InvocationTargetException | NoSuchMethodException
//          | SecurityException e) {
//        // should not happen
//        e.printStackTrace();
//      }
//    }
//  }
  
  //public static final Type cCollection = JavaParser.parseClassOrInterfaceType("Collection");//new ClassOrInterfaceType("Collection");

  /**{@link DAttr}*/
  private static final String DAttrName = "DAttr";

  /**{@link DAssoc}*/
  private static final String DAssocName = "DAssoc";

  /** {@link DOpt} */
  private static final String DOptName = "DOpt"; //DOpt.class.getSimpleName();

  /** {@link DOpt.Type} */
  private static final String DOptTypeName = DOpt.Type.class.getSimpleName();

  
  /** {@link AttrRef} */
  private static final String AttrRefName = "AttrRef";
  
  /** next line character */
  private static final String NL = System.lineSeparator(); //"\n";

  public static final String TypeName_Collection = Collection.class.getSimpleName();

  protected ParserToolkit() {} 
  
  /**
   * @effects 
   *  create a {@link CompilationUnit} used for parsing source code contained in <tt>javaSrcTxt</tt>
   */
  public static CompilationUnit createInlineJavaParser(String javaSrcTxt) throws ParseProblemException {
    CompilationUnit cu;
    // parse the source code text
    cu = JavaParser.parse(javaSrcTxt);
    return cu;
  }
  
  /**
   * @effects 
   *  create a {@link CompilationUnit} used for parsing source code file located at <tt>javaSrcFile</tt>
   */
  public static CompilationUnit createJavaParser(String javaSrcFile) throws FileNotFoundException {
    // creates an input stream for the file to be parsed
    FileInputStream in = new FileInputStream(javaSrcFile);

    return createJavaParser(in);
  }
  
  /**
   * @effects 
   *  create a {@link CompilationUnit} used for parsing source code file contained in <tt>javaSrcStream</tt>
   */
  public static CompilationUnit createJavaParser(InputStream javaSrcStream) throws ParseProblemException {
    CompilationUnit cu;
    try {
      // parse the file
      cu = JavaParser.parse(javaSrcStream);
      return cu;
    } finally {
      try { javaSrcStream.close(); } catch (IOException e) {}
    }
  }
  
  /**
   * @effects 
   *  create a {@link CompilationUnit} used for a class named <tt>name</tt> and with <tt>modifiers</tt>
   */
  public static CompilationUnit createJavaParserForClass(String name, Modifier...modifiers) {
    // create class
    CompilationUnit cu = new CompilationUnit();
    
    cu.addClass(name, modifiers);
    
    return cu;
  }
  
  /**
   * @effects 
   *  recursively print the tree-structure of a node
   */
  public static void printNodeStruc(Node theNode) {
    printNodeStruc(theNode, 0);
  }
  
  /**
   * @effects 
   *  recursively print the tree-structure of a node together with the level indicator, which 
   *  takes a simple form of an indentation (produced by {@link #indent(int)})
   */
  public static void printNodeStruc(Node theNode, int level) {
    String indent = indent(level);
    System.out.println(indent+"*");

    if (theNode instanceof MemberValuePair) {
      // member-value pair (e.g. annotation property)
      printMemberValueStruc((MemberValuePair) theNode, level);
    } else {
      // other node types: print structure
      if (hasActualChildren(theNode)) {
        List<Node> children = theNode.getChildNodes();
        for (Node c : children) { // recursive call
          printNodeStruc(c, level+1);
        }
      } else {
        // leaf node
        System.out.println(indent+ theNode);
      }        
    }
  }

  /**
   * @effects 
   *  if <tt>theNode</tt> actually has children nodes
   *    return true
   *  else
   *    return false
   *  
   *  <p>It is observed that although certain {@link Node}s in an AST are reported to have a single 
   *  child node whose content is empty. As far as this operation is concerned, 
   *  this type of nodes will be considered as 'leaf'.  
   */
  private static boolean hasActualChildren(Node theNode) {
    List<Node> children = theNode.getChildNodes();
    if (children != null && !children.isEmpty()) {
      if (children.size() == 1) {
        Node c = children.get(0);
        if (c.toString().equals("")) {
          // not an 'actual' child
          return false;
        } else {
          return true;
        }
      } else {
        return true;
      }
    } else {
      return false;
    }
  }

  /**
   * @effects 
   *  print member-value pair node
   */
  private static void printMemberValueStruc(MemberValuePair theNode, int level) {
    String indent = indent(level);
    String name = theNode.getName().asString();
    System.out.println(indent + name);
    
    Node valNode = theNode.getChildNodes().get(0);
    
    if (valNode instanceof AnnotationExpr) {
      // metadata extension
      printNodeStruc(valNode, level+1);
    } else {
      if (!(valNode instanceof ClassExpr) && 
          !valNode.getChildNodes().isEmpty()) {
        // valueNode may contain a structure (e.g. an enum like Type.Integer)
        printNodeStruc(valNode, level+1);
      } else {
        // normal value
        System.out.println(indent + valNode.toString());
      }
    }
  }

  /**
   * @effects 
   *  generate and return an indent consisting of <tt>level</tt> separator chars
   */
  private static String indent(int level) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < level; i++) {
      sb.append("-");
    }
    return sb.toString();
  }

  /**
   * @modifies propValMap
   * 
   * @effects 
   *  for each annotation element <tt>p</tt> of {@link DAttr}
   *    add entry <tt>(p.name, null)</tt> to <tt>propValMap</tt>
   */
  public static void populatePropertyValMapWithKeys(
      Map<String, Object> propValMap) {
    Class<DAttr> c = DAttr.class;
    
    Method[] methods = c.getDeclaredMethods();
    
    for (Method m : methods) {
      // add method name as key 
      propValMap.put(m.getName(), null);
    }
  }

  /**
   * @effects
   *  if exists import statements in <tt>cu</tt>
   *    extract the contents of each statement and return all in a {@link List}
   *  else
   *    return null 
   */
  public static List<String> getImports(CompilationUnit cu) {
    NodeList<ImportDeclaration> importNodes = cu.getImports();
    List<String> importStms = null;
    
    if (importNodes != null) {
      importStms = new ArrayList<>();
      String importStm;
      for (ImportDeclaration imp : importNodes) {
        importStm = imp.toString();
        // remove the trailing semicolon and '\n' (if included)
        if (importStm.endsWith("\n")) {
          importStm = importStm.substring(0, importStm.length()-1);
        }
        
        if (importStm.endsWith(";")) {
          importStm = importStm.substring(0, importStm.length()-1);
        }
        
        importStm = importStm.substring(importStm.indexOf(' ') + 1); // exclude the "import " prefix 
        importStms.add(importStm);
      }
    }
    
    return importStms;
  }

  /**
   * @effects 
   *  adds the specified imports into the specified compilation unit
   * @version 5.4
   */
  public static void addImports(CompilationUnit cu, List<String> imports) {
    if (imports == null) return;
    
    imports.forEach(imp -> {
      cu.addImport(imp);
    });
    
  }
  
  
  /**
   * @effects 
   *  add to <tt>cu</tt> import statements for each class in <tt>classes</tt> 
   */
  public static void addImport(CompilationUnit cu, Class... classes) {
    for (Class c : classes) {
      cu.addImport(c);
    }
  }

  /**
   * @effects 
   *  add to <tt>cu</tt> import statements for each class whose FQN is in <tt>clsFqns</tt>
   *  @version 5.2 
   */
  public static void addImport(CompilationUnit cu, String...clsFqns) {
    if (clsFqns == null) return;
    
    for (String fqn : clsFqns) {
      cu.addImport(fqn);
    }
  }
  

  /**
   * @modifies  cu
   * @effects 
   *  adds imports to cu
   * @version 5.4.1
   */
  public static void addImport(CompilationUnit cu,
      ImportDeclaration imports) {
    cu.addImport(imports);
  }
  
  /**
   * @effects 
   *  add to <tt>cu</tt> the package declaration for <tt>pkgName</tt>
   */
  public static void addPackage(CompilationUnit cu, String pkgName) {
//    Optional<PackageDeclaration> pkd = Optional.of(new PackageDeclaration(
//        new Name(pkgName)));
    cu.setPackageDeclaration(new PackageDeclaration(new Name(pkgName)));    
  }

  /**
   * @effects 
   *  create in <tt>cu</tt> a class definition whose name is <tt>name</tt> and whose modifiers are <tt>modifiers</tt>;
   *  return the created class.
   */
  public static ClassOrInterfaceDeclaration createClass(CompilationUnit cu,
      String name, Modifier...modifiers) {
    return cu.addClass(name, modifiers);
  }

  /**
   * @effects 
   *  create and return a {@link CompilationUnit} representing a new class specified in the arguments
   * @version 5.4 
   */
  public static CompilationUnit createClass(String className,
      Modifier... modifiers) {
    // e.g. "public class " + className + "{}"
    StringBuilder classDef = new StringBuilder();
    for (Modifier mod : modifiers) {
      classDef.append(mod.asString()).append(" ");
    }
    
    classDef.append("class ")
    .append(className)
    .append(" { } ");
    CompilationUnit cu = JavaParser.parse(classDef.toString());
    
    return cu;
  }

  /**
   * @effects 
   *  create and return a {@link CompilationUnit} from the content of the specified class declaration.
   * @version 5.4
   */
  public static CompilationUnit createCompilationUnit(
      ClassOrInterfaceDeclaration cd) {
    if (cd == null) return null;
    
    return JavaParser.parse(cd.toString());
  }
  

  /**
   * @effects 
   *  create and return a {@link ClassOrInterfaceType} whose name is <tt>typeSimpleName</tt>.
   *  
   *  Throws {@link ParseProblemException} if failed
   * @version 5.2 
   */
  public static Type createClassOrInterfaceType(String typeSimpleName) throws ParseProblemException {
    return JavaParser.parseClassOrInterfaceType(typeSimpleName);
  }

  /**
   * @effects 
   *  create and return a {@link ClassOrInterfaceType} whose name is <tt>typeSimpleName</tt>.
   *  
   *  Throws {@link ParseProblemException} if failed
   * @version 5.2 
   */
  public static Type createClassOrInterfaceType(Class cls) throws ParseProblemException {
    return createClassOrInterfaceType(cls.getSimpleName());
  }
 
  
  /**
   * @effects 
   *  create and return a new {@link ClassExpr} for <tt>cls</tt> 
   */
  public static ClassExpr createClassExprFor(Class cls) {
    ClassOrInterfaceType type = JavaParser.parseClassOrInterfaceType(cls.getName());
    
    return new ClassExpr(type);
  }

  /**
   * @requires A class whose simple name is <code>clsSimpleName</code> is accessible 
   * on the class path
   * 
   * @effects 
   *  create and return a new {@link ClassExpr} for <tt>clsSimpleName</tt> 
   */
  public static ClassExpr createClassExprFor(String clsSimpleName) {
    ClassOrInterfaceType type = 
        JavaParser.parseClassOrInterfaceType(clsSimpleName);
    
    return new ClassExpr(type);
  }
  
  /**
   * @effects 
   *  create and return a new {@link ClassExpr} for <tt>cls</tt> based on its simple name.
   */
  public static ClassExpr createSimpleClassExprFor(Class cls) {
    ClassOrInterfaceType type = JavaParser.parseClassOrInterfaceType(cls.getSimpleName());
    
    return new ClassExpr(type);
  }
  
  /**
   * @modifies dcls
   * @effects 
   *   invoke {@link #createMethod(ClassOrInterfaceDeclaration, Type, String, Modifier...)}
   *   for dcls.getCls()
   * @version 5.2
   */
  public static MethodDeclaration createMethod(ClassAST dcls, 
      Type returnType, String methodName, Modifier...modifiers) {
    return createMethod(dcls.getCls(), returnType, methodName, modifiers);
  }
  
  /**
   * @modifies clazz
   * @effects 
   *  create in <ttt>clazz</tt> a {@link MethodDeclaration) with <tt>(modifier, methodName, returnType)</tt>,
   *  return the method
   */
  public static MethodDeclaration createMethod(ClassOrInterfaceDeclaration clazz, Type returnType, String methodName, Modifier...modifiers) {
    
    EnumSet<Modifier> modifierSet = getModifierSet(modifiers);
    
    MethodDeclaration method = new MethodDeclaration(modifierSet, returnType, methodName);
    clazz.addMember(method);
    
    return method;
  }
  

  /**
   * @modifies dcls
   * @effects 
   *  invoke {@link #createSingleParamMethod(ClassOrInterfaceDeclaration, Type, String, FieldDef, Modifier...)}
   *  on dcls.cls
   * @version 5.2 
   */
  public static MethodDeclaration createSingleParamMethod(ClassAST dcls,
      Type returnType, String methodName, FieldDef field, Modifier...modifiers) {
    return createSingleParamMethod(dcls.getCls(), 
        returnType, methodName, field, modifiers);
  }
  
  /**
   * @modifies clazz
   * @effects 
   *  create in <tt>clazz</tt> and return a single-parameter method, the definition of the parameter is based on <tt>field</tt> (i.e. same name and type)
   */
  public static MethodDeclaration createSingleParamMethod(
      ClassOrInterfaceDeclaration clazz, Type returnType, String methodName,
      FieldDef field, Modifier...modifiers) {
    
    EnumSet<Modifier> modifierSet = getModifierSet(modifiers);
    
    MethodDeclaration method = new MethodDeclaration(modifierSet, returnType, methodName);
    
    // one parameter
    method.addParameter(field.getType(), field.getName());
    
    clazz.addMember(method);
    
    return method;
  }
  
  /**
   * @modifies dcls
   * @effects 
   *  invoke {@link #createSingleParamMethod(ClassOrInterfaceDeclaration, Type, String, Type, String, Modifier...)}
   *  for dcls.getCls()
   */
  public static MethodDeclaration createSingleParamMethod(
      ClassAST dcls, Type returnType, String methodName,
      Type paramType, String paramName, Modifier...modifiers) {
    return createSingleParamMethod(dcls.getCls(), 
        returnType, methodName, paramType, paramName, modifiers);
  }
  /**
   * @modifies clazz
   * @effects 
   *  create in <tt>clazz</tt> and return a single-parameter method, the definition of the parameter is based on <tt>(paramType, paramName)</tt>
   */
  public static MethodDeclaration createSingleParamMethod(
      ClassOrInterfaceDeclaration clazz, Type returnType, String methodName,
      Type paramType, String paramName, Modifier...modifiers) {
    
    EnumSet<Modifier> modifierSet = getModifierSet(modifiers);
    
    MethodDeclaration method = new MethodDeclaration(modifierSet, returnType, methodName);
    
    // one parameter
    method.addParameter(paramType, (paramName != null) ? paramName : "param");
    
    clazz.addMember(method);
    
    return method;
  }
  
  /**
   * @modifies dcls
   * @effects 
   *  invoke {@link #createMethod(ClassOrInterfaceDeclaration, Type, String, Type[], String[], Class[], String, Modifier...)}
   *  for dcls.getCls();
   * @version 5.2
   */
  public static <T extends Throwable> MethodDeclaration createMethod(
      ClassAST dcls, Type returnType, String methodName,
      Type[] paramTypes, String[] paramNames, Class<T>[] exceptions, String body, Modifier...modifiers) {
    return createMethod(dcls.getCls(), returnType, methodName, paramTypes, paramNames, exceptions, body, modifiers);
  }
  
  /**
   * @effects 
   *  create in <tt>clazz</tt> and return a {@link MethodDeclaration} from the input arguments
   */
  public static <T extends Throwable> MethodDeclaration createMethod(
      ClassOrInterfaceDeclaration clazz, Type returnType, String methodName,
      Type[] paramTypes, String[] paramNames, Class<T>[] exceptions, String body, Modifier...modifiers) {
    EnumSet<Modifier> modifierSet = getModifierSet(modifiers);

    MethodDeclaration method = new MethodDeclaration(modifierSet, returnType, methodName);
    
    // add parameters
    if (paramTypes != null) {
      int idx = 0;
      for (Type pt : paramTypes) {
        method.addParameter(pt, paramNames[idx++]);
      }
    }
    
    // add throws
    if (exceptions != null) {
      for (Class<T> t : exceptions) {
        method.addThrownException(t);
      }
    }
    
    // add body
    if (body != null) {
      BlockStmt bodyBlk = method.createBody();
      //bodyBlk.addStatement(body);
      addMethodStatements(bodyBlk, body);      
    }
    
    clazz.addMember(method);
    
    return method;
  }

  /**
   * @modifies dcls
   * @effects 
   *  invoke {@link #createConstructor(ClassOrInterfaceDeclaration, Type[], String[], Class[], String, Modifier...)}
   *  for dcls.getCls()
   * 
   * @version 5.2
   */
  public static <T extends Throwable> ConstructorDeclaration createConstructor(
      ClassAST dcls, 
      List<Type> paramTypes, List<String> paramNames, List<String> refFieldNames, 
      Class<T>[] exceptions, String body, Modifier...modifiers) throws NotPossibleException {
    return createConstructor(dcls, dcls.getCls(), paramTypes, paramNames, refFieldNames, exceptions, body, modifiers);
  }
  
  /**
   * @effects 
   *  create in <tt>clazz</tt> and return a {@link ConstructorDeclaration} from the input
   */
  public static <T extends Throwable> ConstructorDeclaration createConstructor(
      ClassAST dcls, 
      ClassOrInterfaceDeclaration clazz, 
      List<Type> paramTypes, List<String> paramNames, List<String> refFieldNames, 
      Class<T>[] exceptions, String body, Modifier...modifiers) throws NotPossibleException {

    EnumSet<Modifier> modifierSet = getModifierSet(modifiers);

    ConstructorDeclaration cons = new ConstructorDeclaration(modifierSet, clazz.getNameAsString());
    
    // add parameters
    if (paramTypes != null) {
      int idx = 0;
      boolean addedAttrRefImport = false;
      for (Type pt : paramTypes) {
        String paramName = paramNames.get(idx);
        String refFieldName = null;
        if (refFieldNames != null) {
          refFieldName = refFieldNames.get(idx);
        }
        idx++;
        Parameter param = cons.addAndGetParameter(pt, paramName);
        // add AttrRef to param
        if (refFieldName != null) {
          if (!addedAttrRefImport) { 
            dcls.addImport(AttrRef.class);
            addedAttrRefImport = true;
          }
          addParamAttrRef(param, refFieldName);
        }
      }
    }
    
    // add throws
    if (exceptions != null) {
      for (Class<T> t : exceptions) {
        cons.addThrownException(t);
      }
    }
    
    // add body
    if (body != null) {
      /*
      BlockStmt bodyBlk = cons.createBody();
      try {
        addMethodStatements(bodyBlk, body);
      } catch (ParseProblemException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PARSE_SOURCE_CODE, e);
      }
      */
      //BlockStmt bodyBlk = cons.createBody();
      try {
        addConstructorStatements(cons, body);
      } catch (ParseProblemException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PARSE_SOURCE_CODE, e);
      }
    }
    
    clazz.addMember(cons);
    
    return cons;
  }

  /**
   * @modifies param
   * @effects 
   *  Create annotation assignment: AttrRef(param) = refFieldName 
   */
  public static void addParamAttrRef(Parameter param, String refFieldName) {
    NormalAnnotationExpr anoExpr = param.addAndGetAnnotation(AttrRef.class);
    
    NodeList<MemberValuePair> pairs = new NodeList<>();
    MemberValuePair pair = new MemberValuePair("value", new StringLiteralExpr(refFieldName));
    pairs.add(pair);
    anoExpr.setPairs(pairs);
  }

  /**
   * @effects 
   *  create and return a {@link EnumSet} from <tt>modifier</tt> 
   */
  private static EnumSet<Modifier> getModifierSet(Modifier[] modifier) {
    EnumSet<Modifier> set = modifier[0].toEnumSet();
    
    if (modifier.length > 1) {
      for (int i = 1; i < modifier.length; i++) {
        set.add(modifier[i]);
      }
    }
    
    return set;
  }
  
  
  /**
   * @effects 
   *  create a return an array-type {@link Parameter} whose element type is <tt>elementType</tt> and whose name is 
   *  <tt>paramName</tt>  
   */
  public static Parameter createArrayParam(Type elementType, String paramName) {
    //ArrayType arrT = new ArrayType(elementType, new NodeList<AnnotationExpr>());
    //Parameter.create(arrT, paramName);
    Parameter param = new Parameter(elementType, paramName); 
    param.setVarArgs(true);
    
    return param;
  }

  /**
   * @effects 
   *  create a return a {@link Parameter} whose element type is <tt>elementType</tt> and whose name is 
   *  <tt>paramName</tt>  
   */
  public static Parameter createParam(Type elementType, String paramName) {
    Parameter param = new Parameter(elementType, paramName); 
    
    return param;
  }
  
  /**
   * @modifies m
   * @effects 
   *  add Exception <tt>e</tt> to <tt>m</tt>'s header.
   *  
   * @version 5.2c
   */
  public static void createMethodException(MethodDeclaration m,
      Class<Exception> e) {
    m.addThrownException(e);
  }
  
  /**
   * Use this method for Annotations that only have String-typed properties.
   * 
   * @effects 
   *  create for <tt>method</tt> an {@link Annotation} marker whose type is <tt>anoType</tt> and whose 
   *  property-value pairs are <tt>propValPairs</tt>
   * @deprecated as of v5.2 (use {@link #createMethodAnnotation(BodyDeclaration, Class, Map)}) instead.
   */
  public static void createMethodAnnotationSimple(BodyDeclaration method,
      Class<? extends Annotation> anoType, String[]...propValPairs) {
    NormalAnnotationExpr anoExpr = (NormalAnnotationExpr) method.addAndGetAnnotation(anoType);
    for (String[] pair : propValPairs) {
      anoExpr.addPair(pair[0], pair[1]);      
    }
  }

  /**
   * @effects 
   *  create for <tt>method</tt> an {@link Annotation} marker whose type is <tt>anoType</tt> and whose 
   *  property-value pairs are <tt>propValPairs</tt>
   */
  public static void createMethodAnnotation(BodyDeclaration method,
      Class<? extends Annotation> anoType, Map<String,Expression> propValPairs) {
    NormalAnnotationExpr anoExpr = (NormalAnnotationExpr) method.addAndGetAnnotation(anoType);
    
    NodeList<MemberValuePair> props = new NodeList<>();
    for (Entry<String,Expression> pair : propValPairs.entrySet()) {
      props.add(new MemberValuePair(pair.getKey(), pair.getValue()));
    }
    
    anoExpr.setPairs(props);
  }
  
  /**
   * @effects 
   *  create an array-typed {@link Parameter} for <tt>method</tt>, the parameter name is <tt>paramName</tt>, 
   *  and the element type is <tt>elementType</tt> 
   */
  public static void createArrayParamInMethod(MethodDeclaration method, 
      Type elementType, String paramName) {
    Parameter param = createArrayParam(elementType, paramName);
    method.addParameter(param);    
  }
  
//  /**
//   * @effects 
//   *  create a return a <b>variable</b> array-type {@link Parameter} whose element type is <tt>elementType</tt> and whose name is 
//   *  <tt>paramName</tt>  
//   */
//  public static Parameter createVarArrayParam(Type elementType, String paramName) {
//    Parameter param = Parameter.create(elementType, paramName);
//    param.setVarArgs(true);
//    
//    return param;
//  }
  
  /**
   * @effects 
   *  create and return in <tt>method</tt> an empty block 
   */
  public static BlockStmt createMethodBodyBlock(MethodDeclaration method) {
    BlockStmt block = new BlockStmt();
    method.setBody(block);
    
    return block;
  }
  
  /**
   * @modifies block
   * 
   * @effects 
   *  Parse (if necessary) and add <tt>stmtText</tt> to the end of <tt>block</tt>, 
   *  return {@link Statement} object that is created as the result 
   */
  public static Statement addMethodStatement(BlockStmt block, String stmtText) {
    if (stmtText == null || block == null) return null;
    
    Statement stmt;
//    if (stmtText.endsWith(";")) {
//      // ends with ";"
//      stmt = JavaParser.parseStatement(stmtText);
//      block.addStatement(stmt);
//    } else {
//      // does not end with ";"
//      stmt = block.addStatement(stmtText);
//    }
    stmt = block.addStatement(stmtText);
    return stmt;
  }
  
  /**
   * @modifies block
   * @effects
   * <pre>
   *  if stmtText contains multiple lines separated by '\n'
   *    parse stmtText and add each line to block as a statement
   *  else
   *    call {@link #addMethodStatement(BlockStmt, String)} (block, stmtText)
   *  </pre>  
   *  @version 
   *  - 5.4.1: added a check to add '{}' around code statement only if it does not have one
   */
  public static Node addMethodStatements(BlockStmt block, String stmtText) throws ParseProblemException {
    if (stmtText == null || block == null) return null;
    
    Node result;
    if (stmtText.indexOf(NL) > -1) {
      // has multiple lines
      // NOTE: needs to put stmtText in-between a pair of { } before parsing
      String blockStmtTxt = stmtText.startsWith("{") ? stmtText : "{"+stmtText+"}";
      BlockStmt stmtTextBlock = JavaParser.parseBlock(blockStmtTxt);
      result= stmtTextBlock;
      stmtTextBlock.getStatements().forEach(block::addStatement);
    } else {
      // has a single line
      result = addMethodStatement(block, stmtText);
    }
    
    return result;
  }
  
  /**
   * The reason that we have this method (rather than using {@link #addMethodStatements(BlockStmt, String)}) 
   * is because the current JavaParser version (3.2.5) failed to parse 
   * constructor body that contains "super(...);" as the first statement.
   * 
   * @modifies block
   * @effects
   * <pre>
   *  if stmtText contains multiple lines separated by '\n'
   *    parse stmtText and add each line to block as a statement
   *  else
   *    call {@link #addMethodStatement(BlockStmt, String)} (block, stmtText)
   *  </pre>  
   */
  public static void addConstructorStatements(ConstructorDeclaration cons, String bodyStmtBlock) throws ParseProblemException {
    if (bodyStmtBlock == null || cons == null) return;
    
    String consTxt = cons.toString();
    // remove the empty body {}
    consTxt = consTxt.substring(0, consTxt.lastIndexOf("{"));
    // replace it with the bodyStmtBlock
    consTxt += "{\n" + bodyStmtBlock + "\n}";
    ConstructorDeclaration decl = (ConstructorDeclaration) JavaParser.parseClassBodyDeclaration(consTxt);
    
    cons.setBody(decl.getBody());
  }
  
  /**
   * @modifies cons
   * @effects
   *  add each statement in blockStmt to <code>cons</code>
   * @version 5.4
   */
  public static void addConstructorStatements(ConstructorDeclaration cons, 
      BlockStmt blockStmt) throws ParseProblemException {
    if (blockStmt == null || cons == null) return;
    
    BlockStmt body = cons.getBody();

    blockStmt.getStatements().forEach(s -> body.addStatement(s));
  }
  
  /**
   * @effects 
   *  create and return a {@link MethodCallExpr} that calls a method named <tt>methodNameStr</tt> with 
   *  arguments <tt>argExps</tt>
   */
  public static MethodCallExpr createMethodCall(String methodNameStr, Expression...argExps) {
    MethodCallExpr call = new MethodCallExpr();
    NodeList<Expression> args = new NodeList<>();
    if (argExps.length > 0) {
      for (Expression exp : argExps) args.add(exp);
    }
    
    //QualifiedNameExpr methodName = new QualifiedNameExpr();
    //methodName.setName(methodNameStr);
    call.setName(methodNameStr);
    call.setArguments(args);
    
    return call;
  }

  /**
   * @effects 
   *  create body of <tt>m</tt> to contain <tt>mbody</tt> 
   */
  public static void createMethodBody(MethodDeclaration m, String mbody) {
    //m.createBody().addStatement(mbody);
    BlockStmt bodyBlk = m.createBody();
    addMethodStatements(bodyBlk, mbody);
  }

  /**
   * @effects 
   *  if <tt>fieldType</tt> represents a primitive type
   *    return the wrapper type equivalence
   *  else
   *    return <tt>fieldType</tt>
   *    
   */
  public static Type getObjectType(Type fieldType) {
    if (fieldType instanceof PrimitiveType) {// && !fieldType.equals(CtClass.voidType)) {
      // a primitive type
      Primitive ptype = ((PrimitiveType) fieldType).getType();
      return wrapperTypeMap.get(ptype);
    } else {
      return fieldType;
    }
  }

//  /**
//   * @effects 
//   *  if <tt>fieldType</tt> represents a primitive type
//   *    return the wrapper type equivalence
//   *  else
//   *    return <tt>fieldType</tt>
//   *    
//   */
//  public static Type getObjectTypeRaw(Type fieldType) {
//    if (fieldType instanceof PrimitiveType) {// && !fieldType.equals(CtClass.voidType)) {
//      // a primitive type
//      Primitive ptype = ((PrimitiveType) fieldType).getType();
//      return wrapperTypeRawMap.get(ptype);
//    } else {
//      return fieldType;
//    }
//  }

  /**
   * @effects 
   *  if package declaration is defined in <tt>cu</tt>
   *    return it as {@link String}
   *  else
   *    return null 
   */
  public static String getPackageDeclaration(CompilationUnit cu) {
    Optional<PackageDeclaration> pkg = cu.getPackageDeclaration();
    
    if (pkg != null && pkg.isPresent()) {
      return pkg.get().getNameAsString();
    } else {
      return null;
    }
  }

  /**
   * @effects <pre>
   *  let wordName = getStandardWordName(rawWord)
   *  if <tt>name contains wordName</tt>
   *    for each word in <tt>name</tt> that matches wordName
   *      replace it by <tt>replacement</tt>
   *    return the result
   *  else
   *    return null
   *  </pre>
   */
  public static String replaceNameWords(String name, String rawWord, String replacement) {
    String wordName = getStandardWordName(rawWord);
    if (name.indexOf(wordName) > -1) {
      String replacementName = getStandardWordName(replacement);
      return name.replaceAll(wordName, replacementName);      
    } else {
      // no need to replace
      return null;
    }
  }

  /**
   * @effects 
   *  if rawWord does not start with a capital letter
   *    capitalise first letter of rawWord and return the result
   *  else
   *    return rawWord
   */
  public static String getStandardWordName(String rawWord) {
    if (rawWord == null || rawWord.length() == 0) return null;
    
    char first = rawWord.charAt(0);
    
    if (Character.isLowerCase(first)) {
      // capitalise first and return result
      return Character.toUpperCase(first) + rawWord.substring(1);
    } else {
      return rawWord;
    }
  }
  
  /**
   * @effects 
   *  return the value of <tt>expr</tt> 
   */
  public static Object expressionValue(LiteralExpr expr) {
    Object val = null;
    if (expr instanceof BooleanLiteralExpr) {
      val = ((BooleanLiteralExpr) expr).getValue();
    } else if (expr instanceof DoubleLiteralExpr) {
      val = Double.parseDouble(((DoubleLiteralExpr) expr).getValue());
    } else if (expr instanceof LongLiteralExpr) {
      val = Long.parseLong(((LongLiteralExpr) expr).getValue());
    } else if (expr instanceof IntegerLiteralExpr) {
      val = Integer.parseInt(((IntegerLiteralExpr) expr).getValue());
    } else if (expr instanceof StringLiteralExpr) {
      val = ((StringLiteralExpr) expr).getValue();
    } 
    
    return val;
  }

  /**
   * @effects 
   *  set expr.value = newVal (with conversion if required)
   */
  public static void setExpressionValue(LiteralExpr expr, String newVal) {
    if (expr instanceof BooleanLiteralExpr) {
      ((BooleanLiteralExpr) expr).setValue(Boolean.parseBoolean(newVal));
    } else if (expr instanceof DoubleLiteralExpr) {
      ((DoubleLiteralExpr) expr).setValue(newVal);
    } else if (expr instanceof LongLiteralExpr) {
      ((LongLiteralExpr) expr).setValue(newVal);
    } else if (expr instanceof IntegerLiteralExpr) {
      ((IntegerLiteralExpr) expr).setValue(newVal);
    } else if (expr instanceof StringLiteralExpr) {
      ((StringLiteralExpr) expr).setValue(newVal);
    } 
  }

// This does not work properly!!!
//  /**
//   * @param class1 
//   * @effects 
//   *  if exists {@link FieldDeclaration}s that are declared in <tt>cd</tt> (i.e. not belong to 
//   *  any inner classes)
//   *    return them
//   *  else
//   *    return null
//   */
//  public static <M extends BodyDeclaration> List<M> getMyMembers(
//      ClassOrInterfaceDeclaration cd, Class<M> memberType) {
//    List<M> allFields = cd.getChildNodesByType(memberType);
//    
//    List<M> myFields = new ArrayList<>();
//    
//    for (M m : allFields) {
//      if (m.getParentNode().get().getParentNode().get() == cd) {
//        // direct field
//        myFields.add(m);
//      }
//    }
//    
//    if (myFields.isEmpty())
//      return null;
//    else
//      return myFields;
//  }

  /**
   * @param class1 
   * @effects 
   *  if exists {@link FieldDeclaration}s that are declared in <tt>cd</tt> (i.e. not belong to 
   *  any inner classes)
   *    return them
   *  else
   *    return null
   */
  public static <M extends BodyDeclaration> List<M> getMyMembers(
      ClassOrInterfaceDeclaration cd, Class<M> memberType) {
    List<M> allMembers = cd.getChildNodesByType(memberType);
    
    List<M> myMembers = new ArrayList<>();
    
    for (M m : allMembers) {
      if (m.getParentNode().get().getParentNode().get() == cd) {
        // direct field
        myMembers.add(m);
      }
    }
    
    if (myMembers.isEmpty())
      return null;
    else
      return myMembers;
  }

  /**
   * @effects 
   *  return all declared {@link MethodDeclaration}s in <code>cd</code> or null of no methods are found
   * @version 5.4
   */
  public static List<MethodDeclaration> getDeclaredMethods(
      ClassOrInterfaceDeclaration cd) {
    if (cd == null) return null;
    
    return cd.getMethods();
  }
  
  /**
   * @effects 
   *  if exists {@link MethodDeclaration}s in <tt>cd</tt>
   *    return them as {@link Collection} (in definition order)
   *  else
   *    return null 
   * @version 5.2
   */
  public static Collection<MethodDeclaration> getDomainMethods(
      ClassOrInterfaceDeclaration cd) {
    Collection<MethodDeclaration> methods = cd.getMethods();
    
    List<MethodDeclaration> dmethods = null;
    if (methods != null) {
      for (MethodDeclaration md : methods) {
        if (isDomainMethod(md)) {
          if (dmethods == null) dmethods = new ArrayList<>();
          dmethods.add(md);
        }
      }
    }
    
    return dmethods;
  }

  /**
   * @effects 
   *  if exists in <tt>cd</tt> a {@link MethodDeclaration} <tt>d</tt> of a domain method <tt>m</tt> such that
   *  <tt>DOpt(m).type = optType /\ AttrRef(m).value = attribName</tt>
   *    return <tt>d</tt>
   *  else
   *    return null
   *    
   * @version 5.2
   */
  public static MethodDeclaration getDomainMethodByAttrRef(
      ClassOrInterfaceDeclaration cd,
      DOpt.Type optType, String attribName) {
    Collection<MethodDeclaration> methods = cd.getMethods();
    
    if (methods != null) {
      for (MethodDeclaration md : methods) {
        Optional<AnnotationExpr> doptOpt = md.getAnnotationByClass(DOpt.class);
        Optional<AnnotationExpr> attrRefOpt = md.getAnnotationByClass(AttrRef.class);
        if (doptOpt != null && doptOpt.isPresent() && 
            attrRefOpt != null && attrRefOpt.isPresent()) {
          NormalAnnotationExpr dopt = (NormalAnnotationExpr) doptOpt.get();
          NormalAnnotationExpr attrRef = (NormalAnnotationExpr) attrRefOpt.get();
          if (isDOptType(dopt, optType) && isAttrRef(attrRef, attribName)) {
            // found it
            return md;
          }
        }
      }
    }
    
    // not found
    return null;
  }
  
  /**
   * @effects 
   *  if exists in <tt>cd</tt> {@link MethodDeclaration}s <tt>d</tt> of a domain method <tt>m</tt> such that
   *  <tt>DOpt(m).type = optType /\ AttrRef(m).value = attribName</tt>
   *    return <b>all</b> such <tt>d</tt>
   *  else
   *    return null
   *    
   * @version 5.2<br>
   * - 5.4.1: improved to allow nullable optType
   */
  public static Collection<MethodDeclaration> getDomainMethodsByAttrRef(
      ClassOrInterfaceDeclaration cd,
      DOpt.Type optType, String attribName) {
    Collection<MethodDeclaration> methods = cd.getMethods();
    
    if (methods != null) {
      List<MethodDeclaration> match = null;
      for (MethodDeclaration md : methods) {
        Optional<AnnotationExpr> doptOpt = md.getAnnotationByClass(DOpt.class);
        Optional<AnnotationExpr> attrRefOpt = md.getAnnotationByClass(AttrRef.class);
        /* v5.4.1: 
         if (doptOpt != null && doptOpt.isPresent() && 
            attrRefOpt != null && attrRefOpt.isPresent()) {
         */
        if ((optType == null || (doptOpt != null && doptOpt.isPresent())) && 
            attrRefOpt != null && attrRefOpt.isPresent()) {
          NormalAnnotationExpr dopt = (optType!= null) ? (NormalAnnotationExpr) doptOpt.get() : null; // v5.4.1
          NormalAnnotationExpr attrRef = (NormalAnnotationExpr) attrRefOpt.get();
          /* v5.4.1: 
           if (isDOptType(dopt, optType) && isAttrRef(attrRef, attribName)) {
           */
          if ((optType == null || isDOptType(dopt, optType)) && isAttrRef(attrRef, attribName)) {
            // found it
            if (match == null) match = new ArrayList<>();
            match.add(md);
          }
        }
      }
      
      if (match != null) {
        return match;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if exists in <tt>cd</tt> a {@link MethodDeclaration} <tt>d</tt> of a domain method <tt>m</tt> such that
   *  <tt>DOpt(m).type = optType</tt>
   *    return <tt>d</tt>
   *  else
   *    return null
   *    
   * @version 5.2
   */
  public static MethodDeclaration getDomainMethodByOptType(
      ClassOrInterfaceDeclaration cd,
      DOpt.Type optType) {
    Collection<MethodDeclaration> methods = cd.getMethods();
    
    if (methods != null) {
      for (MethodDeclaration md : methods) {
        Optional<AnnotationExpr> doptOpt = md.getAnnotationByClass(DOpt.class);
        if (doptOpt != null && doptOpt.isPresent()) {
          NormalAnnotationExpr dopt = (NormalAnnotationExpr) doptOpt.get();
          if (isDOptType(dopt, optType)) {
            // found it
            return md;
          }
        }
      }
    }
    
    // not found
    return null;
  }
  
  /**
   * @effects 
   *  if exists in <tt>cd</tt> {@link MethodDeclaration}s <tt>d</tt> of a domain method <tt>m</tt> such that
   *  <tt>DOpt(m).type = optType</tt>
   *    return all such <tt>d</tt>
   *  else
   *    return null
   *    
   * @version 5.2
   */
  public static Collection<MethodDeclaration> getDomainMethodsByOptType(
      ClassOrInterfaceDeclaration cd,
      DOpt.Type optType) {
    Collection<MethodDeclaration> methods = cd.getMethods();
    
    if (methods != null) {
      List<MethodDeclaration> match = null;

      for (MethodDeclaration md : methods) {
        Optional<AnnotationExpr> doptOpt = md.getAnnotationByClass(DOpt.class);
        if (doptOpt != null && doptOpt.isPresent()) {
          NormalAnnotationExpr dopt = (NormalAnnotationExpr) doptOpt.get();
          if (isDOptType(dopt, optType)) {
            // found it
            if (match == null) match = new ArrayList<>();
            match.add(md);
          }
        }
      }
      
      if (match != null) {
        return match;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if exists in <tt>cd</tt> a {@link ConstructorDeclaration} <tt>d</tt> of a domain method <tt>m</tt> such that
   *  <tt>DOpt(m).type = optType</tt>
   *    return <tt>d</tt>
   *  else
   *    return null
   *    
   * @version 5.2
   */
  public static ConstructorDeclaration getDomainConstructorByOptType(
      ClassOrInterfaceDeclaration cd,
      DOpt.Type optType) {
    Collection<ConstructorDeclaration> methods = cd.getConstructors();
    
    if (methods != null) {
      for (ConstructorDeclaration md : methods) {
        Optional<AnnotationExpr> doptOpt = md.getAnnotationByClass(DOpt.class);
        if (doptOpt != null && doptOpt.isPresent()) {
          NormalAnnotationExpr dopt = (NormalAnnotationExpr) doptOpt.get();
          if (isDOptType(dopt, optType)) {
            // found it
            return md;
          }
        }
      }
    }
    
    // not found
    return null;
  }
  
  /**
   * @effects 
   *  if exists in <tt>cd</tt> {@link ConstructorDeclaration}s <tt>d</tt> of some domain method(s) <tt>m</tt> such that
   *  <tt>DOpt(m).type in optTypes</tt>
   *    return <tt>d</tt>
   *  else
   *    return null
   *    
   * @version 5.2
   */
  public static Collection<ConstructorDeclaration> getDomainConstructorsByOptType(
      ClassOrInterfaceDeclaration cd,
      DOpt.Type...optTypes) {
    Collection<ConstructorDeclaration> allConsts = cd.getConstructors();
    
    if (allConsts != null) {
      Collection<ConstructorDeclaration> consts = new ArrayList<>();
      for (ConstructorDeclaration md : allConsts) {
        NodeList<AnnotationExpr> dopts = md.getAnnotations();
        if (dopts != null) {
          OUTER: for (AnnotationExpr doptexp : dopts) {
            NormalAnnotationExpr dopt = (NormalAnnotationExpr) doptexp;
            for (DOpt.Type type : optTypes) {
              if (isDOptType(dopt, type)) {
                // match
                consts.add(md);
                break OUTER;
              }
            }
          }
        }
      }
      
      if (consts.isEmpty())
        return null;
      else
        return consts;
    }
    
    // not found
    return null;
  }
  
  /**
   * @effects 
   *  if attrRef has value = <tt>attribName</tt>
   *    return true
   *  else
   *    return false
   * @version 5.2
   */
  public static boolean isAttrRef(NormalAnnotationExpr attrRef,
      String attribName) {
    NodeList<MemberValuePair> propVals = attrRef.getPairs();
    
    for (MemberValuePair propVal : propVals) {
      String prop = propVal.getNameAsString();
      if (prop.equals("value")) { // DAttr.value
        Expression valExpr = propVal.getValue();
        String val = expressionValue((LiteralExpr)valExpr) + "";
        if (val.equals(attribName)) { // match
          return true;
        } else {
          return false;
        }
      }
    }
    
    return false;
  }

  /**
   * @effects 
   *  if <tt>dopt</tt> has type = <tt>optType</tt>
   *    return true
   *  else
   *    return false
   * @version 5.2
   */
  public static boolean isDOptType(NormalAnnotationExpr dopt,
      DOpt.Type optType) {
    NodeList<MemberValuePair> propVals = dopt.getPairs();
    
    DOpt.Type myOptType;
    
    for (MemberValuePair propVal : propVals) {
      String prop = propVal.getNameAsString();
      Expression valExpr = propVal.getValue();
      if (prop.equals("type")) { // DOpt.type
        String typeName;
        if (valExpr instanceof FieldAccessExpr) { 
          // e.g. DOpt.Type.Getter: convert to the proper enum
          FieldAccessExpr fieldExpr = (FieldAccessExpr) valExpr;
          typeName = fieldExpr.getNameAsString(); 
        } else { // e.g. Getter (without the "DOpt.Type." prefix)
          typeName = expressionValue((LiteralExpr)valExpr) + "";
        }
        myOptType = DOpt.Type.valueOf(typeName);
        
        if (myOptType.equals(optType)) { // match
          return true;
        } else {
          return false;
        }
      }
    }
    
    return false;
  }

  /**
   * @effects 
   *  if md is a domain method (i.e. attached to DOpt)
   *    return true
   *  else
   *    return false
   * @version 5.2
   * 
   */
  public static boolean isDomainMethod(MethodDeclaration md) {
    if (md != null) {
      Optional<AnnotationExpr> ano = md.getAnnotationByClass(DOpt.class);
      return ano != null && ano.isPresent();
    } else {
      return false;
    }
  }

  /**
   * @effects 
   *  if exists (dc : DAttrDef, f : FieldDef) in cxtFieldsMap s.t. dc.name().equals(attribName)
   *    return true
   *  else 
   *    return false 
   *  @version 5.2
   */
  public static boolean isAttribInCxt(String attribName,
      Map<DAttrDef, FieldDef> cxtFieldsMap) {
    if (cxtFieldsMap == null || attribName == null)
      return false;
    
    for (Entry<DAttrDef, FieldDef> e : cxtFieldsMap.entrySet()) {
      if (e.getKey().name().equals(attribName)) {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * @effects 
   *  if exists {@link FieldDeclaration} in {@link #cls}
   *    return a {@link Collection} of them (preserving order of the fields in {@link #cls}
   *  else
   *    return null 
   */
  public static List<FieldDeclaration> getFields(
      ClassOrInterfaceDeclaration cls) {
    if (cls == null) return null;
    
    return cls.getFields();
  }
  
  /**
   * @effects 
   *   if exists {@link FieldDeclaration} in <tt>cd</tt> that is assigned with {@link DAttr}
   *    return a {@link List} of them (preserving order of the fields in <tt>cd</tt>
   *  else
   *    return <tt>null</tt> 
   */
  public static List<FieldDeclaration> getDomainFields(
      ClassOrInterfaceDeclaration cd) {
    List<FieldDeclaration> fields = cd.getFields();
    
    List<FieldDeclaration> dfields = null;
    if (fields != null) {
      for (FieldDeclaration fd : fields) {
        if (isDomainField(fd)) {
          if (dfields == null) dfields = new ArrayList<>();
          dfields.add(fd);
        }
      }
    }
    
    return dfields;
  }

  /**
   * @effects 
   *   if exists {@link FieldDeclaration} in <tt>cd</tt> that is assigned with {@link DAttr}
   *   and whose names are among <tt>fieldNames</tt>
   *    return a {@link List} of them (preserving order of the fields in <tt>cd</tt>
   *  else
   *    return <tt>null</tt> 
   */
  public static List<FieldDeclaration> getDomainFieldsByName(
      ClassOrInterfaceDeclaration cd, Collection<String> fieldNames) {
    List<FieldDeclaration> fields = cd.getFields();
    
    List<FieldDeclaration> dfields = null;
    if (fields != null) {
      String fname;
      for (FieldDeclaration fd : fields) {
        fname = getFieldName(fd);  
        if (fieldNames.contains(fname) && isDomainField(fd)) {
          if (dfields == null) dfields = new ArrayList<>();
          dfields.add(fd);
        }
      }
    }
    
    return dfields;
  }
  
  /**
   * @effects 
   * 
   * @version 5.4
   */
  public static FieldDeclaration getFieldByName(ClassOrInterfaceDeclaration cd, 
      String fieldName) {
    List<FieldDeclaration> fields = cd.getFields();
    
    if (fields != null) {
      for (FieldDeclaration fd : fields) {
        if (getFieldName(fd).equals(fieldName)) {
          return fd;
        }
      }
    }
    
    return null;
  }
  
  /**
   * @effects 
   *  if fd is a domain field 
   *    return true
   *  else
   *    return false 
   */
  public static boolean isDomainField(FieldDeclaration fd) {
    Optional<AnnotationExpr> ano = fd.getAnnotationByClass(DAttr.class);
    return ano != null && ano.isPresent();
  }
  
  /**
   * @effects 
   *  extract and return {@link FieldDef} from {@link FieldDeclaration} 
   */
  public static FieldDef getFieldDef(FieldDeclaration fd) {
    NodeList<VariableDeclarator> vars = fd.getVariables();

    // TODO (if needed): can a field have multiple vars?
    VariableDeclarator n  = vars.get(0);
    //VariableDeclaratorId id = n.getId();
    Type type = n.getType();
    
    String fieldName = n.getNameAsString(); //id.getName();

    FieldDef fieldDef = new FieldDef();
    fieldDef.setName(fieldName);
    fieldDef.setType(type);

    return fieldDef;
  }


  /**
   * @effects 
   *  Convert <tt>field</tt> to suitable {@link FieldDef}.
   *  
   *  If <tt>field</tt> is a domain field then also add suitable {@link DAttrDef} to the result field def.
   * @version 5.2
   */
  public static FieldDef getFieldDefFull(FieldDeclaration field) {
    FieldDef fieldDef = getFieldDef(field);
    
    Optional<AnnotationExpr> anoExprOpt = field.getAnnotationByClass(DAttr.class);
    if (anoExprOpt.isPresent()) {
      // field is a domain field
      AnnotationExpr anoExpr = anoExprOpt.get();
      
      DAttrDef attr = null;
      
      AttribPropertyVisitor propVisit = new AttribPropertyVisitor();
      
      if (anoExpr != null) {
        // extract field declaration 
        attr = new DAttrDef();
        // register attr to field as annotation 
        fieldDef.addAnnotation(DAttr.class, attr);
        
        anoExpr.accept(propVisit, attr);
      }

      if (attr != null) {
        // extract DAssocDefs (if any)
        anoExprOpt = field.getAnnotationByClass(DAssoc.class);
        DAssocDef assoc = null;
        
        if (anoExprOpt.isPresent()) {
          anoExpr = anoExprOpt.get();
          assoc = new DAssocDef();
          anoExpr.accept(propVisit, assoc);
          
          // register assoc to field as annotation
          fieldDef.addAnnotation(DAssoc.class, assoc);
        }
      }
    }
    
    return fieldDef;
  }
  
  /**
   * @effects 
   *  return the name of <tt>fd</tt>
   */
  public static String getFieldName(FieldDeclaration fd) {
    NodeList<VariableDeclarator> vars = fd.getVariables();

    // TODO (if needed): can a field have multiple vars?
    VariableDeclarator n  = vars.get(0);
//    VariableDeclaratorId id = n.getId();
//    String fieldName = id.getName();
    String fieldName = n.getNameAsString();
    
    return fieldName;
  }

  /**
   * @effects 
   *  return the type of <tt>fd</tt>
   */
  public static Type getFieldType(FieldDeclaration fd) {
    NodeList<VariableDeclarator> vars = fd.getVariables();

    VariableDeclarator fieldVar  = vars.get(0);

    Type type = fieldVar.getType();
    
    return type;
  }
  
  /**
   * @effects 
   *  sets the type of <tt>fd</tt> to <code>newType</code>
   * @version 5.4.1
   */
  public static void setFieldType(FieldDeclaration fd, Type newType) {
    NodeList<VariableDeclarator> vars = fd.getVariables();

    VariableDeclarator fieldVar  = vars.get(0);

//    Type type = fieldVar.getType();
//    return type;
    
    fieldVar.setType(newType);
  }
  
  /**
   * @effects 
   *  if fd.type neq newType
   *    set fd.type = newType
   *  else
   *    do nothing  
   */
  public static void setDomainFieldType(FieldDeclaration fd,
      Type newType) {
    NodeList<VariableDeclarator> vars = fd.getVariables();

    VariableDeclarator fieldVar  = vars.get(0);
    
    if (!fieldVar.getType().equals(newType))
      fieldVar.setType(newType);
  }
  
  /**
   * @effects 
   *  change the name and the value of property <tt>DAttr.name</tt> of <tt>fd</tt> to <tt>newName</tt> 
   */
  public static void setDomainFieldName(FieldDeclaration fd, String newName) {
    NodeList<VariableDeclarator> vars = fd.getVariables();

    // TODO (if needed): can a field have multiple vars?
    VariableDeclarator n  = vars.get(0);
    n.setName(newName);
    
    // if field has DAttr annotation then change its name property 
    // v5.4: change AttributeDesc -> DAttr
//    Optional<AnnotationExpr> anoOpt = fd.getAnnotationByClass(AttributeDesc.class);
    Optional<AnnotationExpr> anoOpt = fd.getAnnotationByClass(DAttr.class);
    if (anoOpt.isPresent()) {
      NormalAnnotationExpr ano = (NormalAnnotationExpr) anoOpt.get();
      List<MemberValuePair> targetPairs = ano.getPairs();
      for (MemberValuePair tpair : targetPairs) {
        // tpair is a property of ano
        if (tpair.getNameAsString().equals("name")) { 
          // property "name"
          // update tpair.value 
          StringLiteralExpr newNameVal = new StringLiteralExpr(newName);
          tpair.setValue(newNameVal);
          break;
        }
      }
    }
  }

  /**
   * @effects 
   *  return a value that would be as the default value assigned to an object variable of <tt>type</tt>
   * @version 1.1
   */
  public static String getDefaultTypeValue(Type type) {
    if (isTypeNumeric(type)) {
      return "0";
    } else if (isPrimitiveType(type, Primitive.BOOLEAN)) {
      return "false";
    } else if (isPrimitiveType(type, Primitive.CHAR)) {
      return "\u0000";
    } else if (isTypeReference(type) || isTypeObject(type)) {
      return "null";
    } else {
      throw new IllegalArgumentException("Not supported type: " + type);
    }
  }

  
  /**
   * @modifies fd
   * @effects 
   *  rename <tt>fd</tt> to the lower-case version of its data type 
   */
  public static void renameDomainFieldUsingDataType(FieldDeclaration fd) {
    Type fieldType = getFieldType(fd);
    
    String typeName = fieldType.toString();
    
    // remove generic symbols if any
    if (typeName.indexOf("<") > -1)
      typeName = typeName.replaceAll(">", "");
    
    if (typeName.indexOf(">") > -1)
      typeName = typeName.replaceAll(">", "");
    
    typeName = (typeName.charAt(0)+"").toLowerCase() + typeName.substring(1);
    
    setDomainFieldName(fd, typeName);
  }
  
  /**
   * @effects 
   *  return <b>the first</b> top-level {@link ClassOrInterfaceDeclaration} of <tt>cu</tt>
   */
  public static ClassOrInterfaceDeclaration getTopLevelClass(CompilationUnit cu) {
    List<ClassOrInterfaceDeclaration> classes = getTopLevelClasses(cu);
    
    if (classes != null) {
      return classes.get(0);          
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if exist {@link ClassOrInterfaceDeclaration}s in <tt>cu</tt>
   *    return them
   *  else
   *    return null 
   */
  public static List<ClassOrInterfaceDeclaration> getTopLevelClasses(
      CompilationUnit cu) {
    List<ClassOrInterfaceDeclaration> classes = cu.getChildNodesByType(ClassOrInterfaceDeclaration.class);
    
    List<ClassOrInterfaceDeclaration> tops = new ArrayList<>(); 
    if (classes != null) {
      for (ClassOrInterfaceDeclaration cd : classes) {
        if (
            //v5.2: fixed: cd.getParentNode().get().getParentNode().get() == cu
            cd.getParentNode().get() == cu
            ) {
          tops.add(cd);
        }
      }
    }

    if (tops.isEmpty())
      return null;
    else
      return tops;
  }
  
  /**
   * @effects 
   *  Merge property-value pairs in <tt>anoSrc</tt> into those of <tt>anoTarget</tt>  
   */
  public static void mergeAnotation(AnnotationExpr anoSrc, NormalAnnotationExpr anoTarget) {
    List<MemberValuePair> srcPairs = anoSrc.getChildNodesByType(MemberValuePair.class);
    List<MemberValuePair> targetPairs = anoTarget.getChildNodesByType(MemberValuePair.class);
    NodeList<MemberValuePair> newPairs = new NodeList<>();
    for (MemberValuePair spair : srcPairs) {
      if (spair.getParentNode().get().getParentNode().get() == anoSrc) {
        // only add member pairs of ano (i.e. exclude those of any nested
        // annotations)
        boolean foundMatch = false;
        for (MemberValuePair tpair : targetPairs) {
          if (tpair.getParentNode().get().getParentNode().get() == anoTarget) {
            // only add member pairs of ano (i.e. exclude those of any nested
            // annotations)
            if (spair.getName().equals(tpair.getName())) {
              // a matching pair: update tpair.value if it differs
              Expression sval = spair.getValue();
              if (!tpair.getValue().equals(sval)) {
                tpair.setValue(sval);
              }
              foundMatch = true;
              break;
            }
          }
        }
        
        if (!foundMatch) {
          // new pair: to add
          newPairs.add(spair);
        }
      }
    } // end for
    
    if (!newPairs.isEmpty()) {
      // add new pairs to anoTarget
      //targetPairs.forEach(newPairs::add);
      NodeList<MemberValuePair> updatedPairs = new NodeList<>();
      targetPairs.forEach(updatedPairs::add);
      newPairs.forEach(updatedPairs::add);
      
      anoTarget.setPairs(updatedPairs);
    }
  }

  /**
   * @effects 
   *  create and return a new {@link AnnotationExpr} of the same type and content as <tt>ano</tt>
   *  and add it to <tt>classMember</tt>.  
   */
  public static AnnotationExpr addNewAnnotation(BodyDeclaration classMember, AnnotationExpr ano) {
    String anoType = ano.getName().toString();
    
    NormalAnnotationExpr anoExp = null;
    switch (anoType) {
    case DAttrName:
      anoExp = (NormalAnnotationExpr) classMember.addAnnotation(DAttr.class);
      break;
    case DAssocName:
      anoExp = (NormalAnnotationExpr) classMember.addAnnotation(DAssoc.class);
      break;
    case DOptName:
      anoExp = (NormalAnnotationExpr) classMember.addAnnotation(DOpt.class);
      break;      
    case AttrRefName:
      anoExp = (NormalAnnotationExpr) classMember.addAnnotation(AttrRef.class);
      break;      
    // TODO (if needed): add other ano-types here
    default:
      // not supported
    }

    if (anoExp != null) {
      // copy values of member-value-pairs from expandedField over to anoExp
      List<MemberValuePair> pairs = ano.getChildNodesByType(MemberValuePair.class);
      NodeList<MemberValuePair> newPairs = new NodeList<>();
      for (MemberValuePair pair : pairs) {
        if (pair.getParentNode().get().getParentNode().get() == ano) {
          // only add member pairs of ano (i.e. exclude those of any nested
          // annotations)
          newPairs.add(pair);
        }
      }
      anoExp.setPairs(newPairs);
    }
    
    return anoExp;
  }

  /**
   * @effects
   *  if <tt>type</tt> references a data type named <tt>typeName</tt> either directly or indirectly via
   *  generics
   *    return true
   *  else
   *    return false
   */
  public static boolean references(Type type, String typeName) {
    if (type instanceof ClassOrInterfaceType) {
      // a reference type
      ClassOrInterfaceType ci = (ClassOrInterfaceType) type;
      Optional<NodeList<Type>> boundTypes = ci.getTypeArguments();
      ClassOrInterfaceType t = null;
      if (boundTypes != null && boundTypes.isPresent()) {
        // generic type
        // TODO (if needed): can there be more than one bounded types?
        Type bt = boundTypes.get().get(0);
        if (bt instanceof ClassOrInterfaceType) {
          t = (ClassOrInterfaceType) bt;
        }
      } else {
        // non-generic type
        t = ci;
      }
      
      if (t != null && t.getName().equals(typeName)) {
        // true
        return true;
      }
    }
    
    // all other cases: false
    return false;
  }

  /**
   * @effects 
   *  if type is a generic type
   *    return the referenced (bound) type
   *  else
   *    return type
   */
  public static Type getReferencedType(Type type) {
    Type t = type;

    if (type instanceof ClassOrInterfaceType) {
      // a reference type
      ClassOrInterfaceType ci = (ClassOrInterfaceType) type;
      Optional<NodeList<Type>> boundTypes = ci.getTypeArguments();
      if (boundTypes != null && boundTypes.isPresent()) {
        // generic type
        // TODO (if needed): can there be more than one bounded types?
        Type bt = boundTypes.get().get(0);
        if (bt instanceof ClassOrInterfaceType) {
          t = (ClassOrInterfaceType) bt;
        }
      }
    }
    
    return t;
  }

  /**
   * @effects 
   *  return the name of the type referenced by <tt>type</tt>
   */
  public static String getReferencedTypeName(Type type) {
    Type t = getReferencedType(type);
    
    if (t instanceof ClassOrInterfaceType) {
      return ((ClassOrInterfaceType)t).getNameAsString();
    } else {
      //TODO: support other types
      return type.toString();
    }
  }

  /**
   * @effects 
   *  return the name of <tt>type</tt>
   */
  public static String getTypeName(Type type) {
    if (type instanceof ClassOrInterfaceType) {
      return ((ClassOrInterfaceType) type).getNameAsString();
    } else {
      //TODO: support other types
      return type.toString();
    }
  }
  

  /**
   * @effects 
   *  return the name of the specified type declaration.
   *  
   * @version 5.4
   */
  public static String getName(TypeDeclaration<?> td) {
    return td.getNameAsString();
  }
  
  /**
   * @effects 
   *  add <tt>field</tt> to <tt>cd</tt>
   */
  public static void addField(ClassOrInterfaceDeclaration cd,
      FieldDeclaration field) {
    cd.addMember(field);
  }

  /**
   * @effects 
   *  add <tt>field.clone</tt> to <tt>cd</tt>
   */
  public static FieldDeclaration addFieldCopy(ClassOrInterfaceDeclaration cd,
      FieldDeclaration field) {
    FieldDeclaration fd = field.clone();
    
    cd.addMember(fd);
    
    return fd;
  }
  
  /**
   * @effects 
   *  if <tt>type</tt> represents <tt>Object</tt>
   *    return true
   *  else
   *    return false
   */
  public static boolean isTypeObject(Type type) {
    return isType(type, TypeName.Others.Object);
  }

  /**
   * @effects 
   *  if <tt>type</tt> represents a {@link ClassOrInterfaceType} whose name is or matches 
   *  with <tt>targetTypeName</tt>
   *    return true
   *  else
   *    return false
   */
  public static boolean isType(Type type, TypeName targetTypeName) {
    if (type == null) return false;
    
    if (type instanceof ClassOrInterfaceType) {
      ClassOrInterfaceType t = (ClassOrInterfaceType) type;
      return targetTypeName.equalsByName(t.getName().asString());
    } else {
      return false;
    }
  }
  
  /**
   * @effects 
   *  if <tt>type</tt> represents a numeric type
   *    return true
   *  else
   *    return false
   */
  public static boolean isTypeNumeric(Type type) {
    /*
    if (type instanceof PrimitiveType) {
      PrimitiveType ptype = (PrimitiveType) type;
      Primitive t = ptype.getType();
      switch (t) {
        case DOUBLE:
        case FLOAT:
        case INT:
        case LONG:
        case SHORT:
          return true;
        default:
          return false;
      }
    } else {
      return false;
    }*/
    return isTypeIntegral(type) || isTypeReal(type);
  }
  
  /**
   * @effects 
   *  if <tt>type</tt> represents an integral type
   *    return true
   *  else
   *    return false
   */
  public static boolean isTypeIntegral(Type type) {
    /*
    if (type instanceof PrimitiveType) {
      PrimitiveType ptype = (PrimitiveType) type;
      Primitive t = ptype.getType();
      switch (t) {
        case INT:
        case LONG:
        case SHORT:
          return true;
        default:
          return false;
      }
    } else {
      return false;
    }
    */
    String tname = null;
    if (type instanceof PrimitiveType) {
      tname = ((PrimitiveType)type).asString();
    } else if (type instanceof ClassOrInterfaceType) {
      tname = ((ClassOrInterfaceType) type).getNameAsString();
    }

    if (tname != null) {
      tname = tname.toLowerCase();
      return (tname.equals(INT.asString()) || tname.equals("integer") 
          || tname.equals(LONG.asString())
          || tname.equals(SHORT.asString()));
    } else {
      return false;
    }
  }
  
  /**
   * @effects 
   *  if <tt>type</tt> represents a real type
   *    return true
   *  else
   *    return false
   */
  public static boolean isTypeReal(Type type) {
    /*
    if (type instanceof PrimitiveType) {
      PrimitiveType ptype = (PrimitiveType) type;
      Primitive t = ptype.getType();
      switch (t) {
        case DOUBLE:
        case FLOAT:
          return true;
        default:
          return false;
      }
    } else {
      return false;
    }
    */
    String tname = null;
    if (type instanceof PrimitiveType) {
      tname = ((PrimitiveType)type).asString();
    } else if (type instanceof ClassOrInterfaceType) {
      tname = ((ClassOrInterfaceType) type).getNameAsString();
    }

    if (tname != null) {
      tname = tname.toLowerCase();
      return (tname.equals(DOUBLE.asString()) 
          || tname.equals(FLOAT.asString())); 
    } else {
      return false;
    }    
  }
  
  /**
   * @effects 
   *  if <tt>type</tt> represents the boolean type
   *    return true
   *  else
   *    return false
   */
  public static boolean isTypeBoolean(Type type) {
    /*
    if (type instanceof PrimitiveType) {
      PrimitiveType ptype = (PrimitiveType) type;
      Primitive t = ptype.getType();
      switch (t) {
        case BOOLEAN:
          return true;
        default:
          return false;
      }
    } else {
      return false;
    } */
    String tname = null;
    if (type instanceof PrimitiveType) {
      tname = ((PrimitiveType)type).asString();
    } else if (type instanceof ClassOrInterfaceType) {
      tname = ((ClassOrInterfaceType) type).getNameAsString();
    }

    if (tname != null) {
      tname = tname.toLowerCase();
      return (tname.equals(BOOLEAN.asString()));
    } else {
      return false;
    }
  }
  
  /**
   * @effects 
   *  if <tt>type</tt> represents a character type
   *    return true
   *  else
   *    return false
   */
  public static boolean isTypeCharacter(Type type) {
    /*
    Primitive t = null;
    if (type instanceof PrimitiveType) {
      PrimitiveType ptype = (PrimitiveType) type;
      t = ptype.getType();
    } 
    
    if (t != null) {
      switch (t) {
        case CHAR:
          return true;
        default:
          return false;
      }
    } else {
      return false;
    } */
    String tname = null;
    if (type instanceof PrimitiveType) {
      tname = ((PrimitiveType)type).asString();
    } else if (type instanceof ClassOrInterfaceType) {
      tname = ((ClassOrInterfaceType) type).getNameAsString();
    }

    if (tname != null) {
      tname = tname.toLowerCase();
      return (tname.equals(CHAR.asString()));
    } else {
      return false;
    }
  } 

  /**
   * @effects 
   *  if type is a primitive type (incl. int, boolean, short)
   *    return true
   *  else
   *    return false
   * @version 5.2
   * 
   */
  public static boolean isTypePrimitive(Type type) {
    return (type != null && (type instanceof PrimitiveType));
  }
  
  /**
   * @effects 
   *  if <tt>type</tt> represents primitive type <tt>ptype</tt>
   *    return true
   *  else
   *    return false
   */
  public static boolean isPrimitiveType(Type type, Primitive ptype) {
    if (type instanceof PrimitiveType) {
      PrimitiveType mytype = (PrimitiveType) type;
      Primitive t = mytype.getType();
      return t.equals(ptype);
    } else {
      return false;
    }
  }
  
  /**
   * @effects 
   *  if <tt>type</tt> represents a referenced type <tt>reftype</tt>
   *    return true
   *  else
   *    return false
   */
  public static boolean isTypeReference(Type type) {
    if (type == null) return false;
    
    return (type instanceof ReferenceType);
  }
  
  /**
   * @effects 
   *  if <tt>type</tt> represents a collection type
   *    return true
   *  else
   *    return false
   */
  public static boolean isTypeCollection(Type type) {
    if (type == null) return false;

    return isType(type, TypeName.Collections.Any);
  }
  
  /**
   * @requires <tt>type</tt> is either a primitive type or a reference type
   * @effects 
   *  if type is a Java's built-in type (incl. primitive, String) or an {@link ArrayType} of a built-in type
   *    return true
   *  else
   *    return false 
   * @version 5.2 
   */
  public static boolean isJavaBuiltInType(Type type) {
    if (type == null) return false;
    
    return 
//        isTypeNumeric(type) || 
//        isTypeBoolean(type) ||
//        isTypeCharacter(type) ||
        isTypePrimitive(type) || 
        isJavaBuiltInArrayType(type) ||
        isJavaBuiltInRefType((ClassOrInterfaceType) type);
  }
  
  /**
   * @effects 
   *  if <tt>type</tt> is an {@link ArrayType} whose element type is a Java's built-in type (i.e. satisfies {@link #isJavaBuiltInType(Type)})
   *    return true
   *  else
   *    return false
   */
  public static boolean isJavaBuiltInArrayType(Type type) {
    if (type == null) return false;
    
    if (type instanceof ArrayType) {
      ArrayType atype = (ArrayType) type;
      
      Type etype = atype.getElementType();
      
      return isJavaBuiltInType(etype);
    } else {
      return false;
    }
  }

  /**
   * @effects 
   *  if <tt>type</tt> represents a Java's built-in reference type 
   *  (i.e. <tt>n = type.name /\ "java.lang".n</tt> is a valid class) 
   *    return true
   *  else
   *    return false
   * @version 5.2
   */
  public static boolean isJavaBuiltInRefType(ClassOrInterfaceType type) {
    if (type == null) return false;
    
    String tname = ((ClassOrInterfaceType) type).getNameAsString();
    
    String fqn = "java.lang." + tname;
    
    try {
      Class c = Class.forName(fqn);
      return true;
      // built-in reference type
    } catch (ClassNotFoundException e) {
      // not a built-in reference type
      return false;
    }
  }

  /**
   * @requires <tt>type, newType</tt> are compatible
   * 
   * @modifies type
   * @effects 
   *  switch the type definition of <tt>type</tt> to that of <tt>newType</tt>
   */
  public static void switchType(Type type, Type newType) {
    if (type instanceof ClassOrInterfaceType) {
      ClassOrInterfaceType t = (ClassOrInterfaceType) type;
      
      ClassOrInterfaceType tn = (ClassOrInterfaceType) newType;
      
      t.setName(tn.getName());
      t.setScope(tn.getScope().get());
      t.setTypeArguments(tn.getTypeArguments().get());
    } else if (type instanceof PrimitiveType) {
      PrimitiveType t = (PrimitiveType) type;
      
      PrimitiveType tn = (PrimitiveType) newType;
      
      t.setType(tn.getType());
    }
    // TODO (if needed): support other types here
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
  public static ConstructorDeclaration getDefaultConstructor(ClassOrInterfaceDeclaration cls) {
    Optional<ConstructorDeclaration> opt = cls.getDefaultConstructor();
    return opt.isPresent() ? opt.get() : null;
  }

  /**
   * @effects 
   *  creates in {@link #cls} a default constructor whose body is copied from <code>constr</code>; 
   *  return the constructor declaration.
   * @version 5.4
   */
  public static ConstructorDeclaration addDefaultConstructor(ClassOrInterfaceDeclaration cls, ConstructorDeclaration constr) {
    ConstructorDeclaration myConstr = 
        cls.addConstructor(constr.getModifiers().toArray(new Modifier[0]));
    
    myConstr.setBody(constr.getBody().clone());
    
    return myConstr;
  }

  /**
   * @effects 
   *  if exists in <code>cd</code> a {@link ConstructorDeclaration} whose parameter types
   *  match <code>paramTypes</code> (in that order)
   *    return it
   *  else
   *    throw return null
   * @version 5.4
   */
  public static ConstructorDeclaration getConstructorByParamTypes(
      ClassOrInterfaceDeclaration cd, 
      String...paramTypes) {
    Collection<ConstructorDeclaration> methods = cd.getConstructors();
    
    if (methods != null) {
      for (ConstructorDeclaration md : methods) {
        List<Parameter>  params = md.getParameters();
        int numParams = params.size();
        if (numParams == paramTypes.length) {
          boolean match = true;
          for (int i = 0; i < numParams; i++) {
            Parameter param = params.get(i);
            String paramName = getTypeName(param.getType());
            if (!paramName.equals(paramTypes[i])) {
              // does not match
              match = false;
              break;
            }
          }
          
          if (match) {
            // found
            return md;
          }
        }
      }
    }
    
    // not found
    return null;
  }
  
  /**
   * @effects 
   *  if <tt>constructor</tt> is defined with {@link DOpt} meta-attribute
   *    return the value of its <tt>type</tt> property
   *  else
   *    return null
   */
  public static Expression getConstructorType (
      ConstructorDeclaration constructor) {
    Optional<AnnotationExpr> ano = constructor.getAnnotationByClass(DOpt.class);
    
    if (ano != null && ano.isPresent()) {
      NodeList<MemberValuePair> pairs = ((NormalAnnotationExpr)ano.get()).getPairs();
      for (MemberValuePair pair : pairs) {
        if (pair.getName().equals("type")) {
          return pair.getValue();
        }
      }
    }
    
    return null;
  }

  /**
   * @modifies <tt>targetAst</tt>
   * @effects 
   *  transfer all relevant {@link ImportDeclaration}(s) in srcAst for <tt>type</tt> to <tt>targetAst</tt>.
   *  
   *  <p>Note: transfer must 
   *  (i) exclude built-in Java types (e.g. String) and  
   *  (ii) handles the case that <tt>srcAst</tt> is in same package as <tt>type</tt> but 
   *  <tt>targetAst</tt> is not.
   * @version 
   * - 5.2: improved to handle the case mentioned in the "Note" statement above
   */
  public static void transferImportsForType(final CompilationUnit srcAst, final Type type,
      final CompilationUnit tgtAst) {
    if (type == null || !(type instanceof ClassOrInterfaceType)) return;
    
    // exclude built-in type
    if (isJavaBuiltInType(type)) { // built-in type: ignore
      return;
    }
    
    ClassOrInterfaceType clsType = (ClassOrInterfaceType) type;
    String typeName = clsType.getNameAsString();
    ImportDeclaration imp = null;
    
    if (typeName.indexOf(".") > -1) {
      // FQN -> create import
      imp = JavaParser.parseImport("import " + typeName + ";");
      
      if (!hasImport(tgtAst, imp)) tgtAst.addImport(imp);
    } else { // simple name
      // handle the case that clsType is in the same package as srcAst: in this case no import exists in srcAst
      // so we need to generate an ImportDeclaration for tgtAst
      boolean isCollectionType = isType(clsType, TypeName.Collections.Any);
      if (!isCollectionType) { // non collection type: get or create import
        imp = getOrCreateImportForClassType(srcAst, clsType);
      } else { // collection type: get import only 
        imp = getImportForClassType(srcAst, clsType);
      }
      
      if (//isType(clsType, TypeName.Collections.Any)
          isCollectionType
          ) {
        // collection type: import both Collection type and the generic type referenced by the collection
        // get import for clsType
        //imp = getImportForClassType(srcAst, clsType);
        if (imp != null && !hasImport(tgtAst, imp)) 
          tgtAst.addImport(imp);
        
        ClassOrInterfaceType refType = (ClassOrInterfaceType) getReferencedType(clsType);        
        if (refType != null && !isJavaBuiltInType(refType)) {
          // refType is not a Java's built-in type
          imp = getOrCreateImportForClassType(srcAst, refType); //getImportForClassType(srcAst, refType);
          if (imp != null && !hasImport(tgtAst, imp)) tgtAst.addImport(imp);
        }
      } else {
        // non collection type
        // imp = getImportForClassType(srcAst, clsType);
        if (imp != null && !hasImport(tgtAst, imp)) tgtAst.addImport(imp);
      }
    }
  }

  /**
   * @effects 
   *  if <tt>tgtAst</tt> has <tt>imp</tt>
   *    return true
   *  else
   *    return false 
   */
  public static boolean hasImport(CompilationUnit tgtAst, ImportDeclaration imp) {
    return (tgtAst != null && imp != null && tgtAst.getImports().contains(imp));
  }

  /**
   * @requires <tt>clsType</tt> is not a collection type
   * 
   * @effects
   *  if exists in <tt>ast</tt> {@link ImportDeclaration} for <tt>clsType</tt>
   *    return it
   *  else if <tt>clsType</tt> is in the same package as <tt>ast</tt> 
   *    create and return an {@link ImportDeclaration} for it
   *  else 
   *    return null  
   * @version 5.2
   */
  public static ImportDeclaration getOrCreateImportForClassType(
      CompilationUnit ast, ClassOrInterfaceType clsType) {
    ImportDeclaration imp = getImportForClassType(ast, clsType);
    
    if (imp != null) {
      return imp;
    } else {
      // srcAst has no import declaration -> type is (assumed) to be in the same package
      String pkg = getPackageDeclaration(ast);
      if (pkg != null) {
        String clsName = clsType.getNameAsString();
        String fqn = pkg + "." + clsName;
        Name name = new Name(fqn);
        boolean isStatic = false;
        boolean isAsterisk = false;
        imp = new ImportDeclaration(name, isStatic, isAsterisk);
      }
      
      return imp;
    }
  }
  
  /**
   * @effects 
   *  if exists in <tt>ast</tt> {@link ImportDeclaration} for <tt>clsType</tt>
   *    return it
   *  else return null  
   */
  public static ImportDeclaration getImportForClassType(CompilationUnit ast, ClassOrInterfaceType clsType) {
    //  search among import statements
    String typeName = clsType.getNameAsString();
    
    NodeList<ImportDeclaration> imports = ast.getImports();
    String impStr;
    if (imports != null) {
      for (ImportDeclaration imp : imports) {
        impStr = imp.toString();
        if (impStr.endsWith(NL)) 
          impStr = impStr.substring(0, impStr.length()-NL.length());
        
        if (impStr.endsWith("."+typeName + ";")) {  // e.g. a.b.c.Student;
          return imp;
        }
      }
    }
    
    return null;
  }

  /**
   * @effects 
   *  return FQN of the class represented by <tt>clsExpr</tt> in <tt>ast</tt>
   */
  public static String getFqn(CompilationUnit ast, ClassExpr clsExpr) {
    ImportDeclaration imp = getImportForClassType(ast, (ClassOrInterfaceType) clsExpr.getType());
    
    if (imp != null) {
      String fqn = imp.getName().asString();
      return fqn;
    } else {
      return null;
    }
  }

  /**
   * @requires ast != null, type != null
   * 
   * @effects 
   *   If exists a {@link ClassOrInterfaceDeclaration} unit corresponding to <tt>type</tt> that is either 
   *   in the same package as or written in an import statement in <tt>ast</tt>
   *    construct and return its FQN 
   *   else
   *    return null
   * @version 5.2c
   */
  public static String getFqnFor(CompilationUnit ast, ClassOrInterfaceType type) {
    ImportDeclaration imp = getOrCreateImportForClassType(ast, type);
    
    if (imp == null) return null;
    
    String clsFqn = imp.getNameAsString();
    
    return clsFqn;
  }

  /**
   * @effects 
   *  create and return a {@link NormalAnnotationExpr} from <code>jsonObj</code>, which holds the 
   *  data structure of the annotation.
   *  
   * @version 5.4
   */
  public static NormalAnnotationExpr createAnoPropValExpr(final JsonObject jsonObj, 
      final Class<? extends Annotation> anoCls) {
    /* v5.4.1: call method
    final Map<String, Expression> propValMap = new LinkedHashMap<>();
    jsonObj.keySet().forEach(prop -> {
      Class<?> valType = null;
      try {
        valType = anoCls.getDeclaredMethod(prop).getReturnType();
      } catch (NoSuchMethodException | SecurityException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, new Object[] {"getDeclaredMethod", prop, e.getMessage()}, e);
      }
      
      Expression val = createAnoPropValExpr(jsonObj, valType, prop);
      
      
      propValMap.put(prop, val);
    });
    
    NormalAnnotationExpr expr = createAnnotationExpr(anoCls, propValMap);
    
    return expr;
    */
    return createAnoPropValExpr(null, jsonObj, anoCls);
  }

  /**
   * @modifies ast
   * @requires 
   *  if <code>ast != null</code> then it must be the one to which the result {@link NormalAnnotationExpr} is attached.
   *  
   * @effects 
   *  create and return a {@link NormalAnnotationExpr} from <code>jsonObj</code>, which holds the 
   *  data structure of the annotation.
   *  
   *  <p>if <code>ast</code> is specified then updates it with imports of all the value types encountered.
   *  
   * @version 5.4.1
   */
  public static NormalAnnotationExpr createAnoPropValExpr(
      final CompilationUnit ast,
      final JsonObject jsonObj, 
      final Class<? extends Annotation> anoCls) {
    final Map<String, Expression> propValMap = new LinkedHashMap<>();
    jsonObj.keySet().forEach(prop -> {
      Class<?> valType = null;
      try {
        valType = anoCls.getDeclaredMethod(prop).getReturnType();
      } catch (NoSuchMethodException | SecurityException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, new Object[] {"getDeclaredMethod", prop, e.getMessage()}, e);
      }
      
      // parse value using the property type in anoCls
      // debug
//      System.out.printf("createAnoPropValExpr: %s => ", prop);

      Expression val = createAnoPropValExpr(ast, jsonObj, valType, prop);
      
      // debug
//      System.out.printf(" %s%n", val);
      
      propValMap.put(prop, val);
    });
    
    NormalAnnotationExpr expr = createAnnotationExpr(anoCls, propValMap);
    
    if (ast != null) {
      addImport(ast, anoCls);
    }
    
    return expr;
  }
  
  /**
   * @effects 
   * 
   * @version 5.4
   */
  public static NormalAnnotationExpr createAnnotationExpr(
      Class<? extends Annotation> anoCls, Map<String, Expression> propValMap) {
    NodeList<MemberValuePair> props = new NodeList<>();
    
    propValMap.forEach((prop, val) -> props.add(new MemberValuePair(prop, val)));
    
    String anoName = anoCls.getSimpleName();
    NormalAnnotationExpr anoExpr = new NormalAnnotationExpr(
        new Name(anoName), props);
    return anoExpr;
  }

  /**
   * @modifies ownerAst
   * @requires 
   *  if <code>ownerAst != null</code> then it must be the one that will contain the result.
   *  
   * @effects 
   *  convert value of <code>prop</code> in <code>jsonObj</code> 
   *  to an {@link Expression}, suitable for being set to value of a 
   *  {@link MemberValuePair} of an {@link AnnotationExpr}.
   *  
   *  <p>If <code>ownerAst != null</code> then updates it with imports of any new value types that are encountered. 
   *  
   *  Return the expression.
   *   
   * @version 5.4.1
   */
  public static Expression createAnoPropValExpr(
      final CompilationUnit ownerAst,
      final JsonObject jsonObj, 
      final Class<?> valType, 
      String prop) throws NotImplementedException {
    Expression val;
    
    JsonValue jsonVal = jsonObj.get(prop);
    if (JsonArray.class.isAssignableFrom(jsonVal.getClass())) { 
      // array expression
      // ducmle: fixed 20210127
//    Class<?> eleType = valType.getComponentType() != null)
      Class<?> eleType = (valType.getComponentType() != null) ? valType.getComponentType() : valType;
//      val = createAnoPropValArrayExpr(eleType, ((JsonArray)jsonVal));
      JsonArray valArr = (JsonArray) jsonVal;
      ArrayInitializerExpr expr = new ArrayInitializerExpr();
      int index = 0;
      NodeList<Expression> values = new NodeList<>();
      for(JsonValue v : valArr) {
        Expression exprEle;
        if (v instanceof JsonObject) {
          // object array element -> sub-annotation element
          Class<? extends Annotation> anoEleType = (Class<? extends Annotation>) eleType;
          exprEle = createAnoPropValExpr(ownerAst, (JsonObject) v, anoEleType);
        } else {
          // non-object array element
          exprEle = createAnoPropValExprWithImports(ownerAst, eleType, v.toString());
        }
        values.add(exprEle);
      }
      expr.setValues(values);
      val = expr;
    } else if (jsonVal instanceof JsonObject) { // array
      // annotation expression 
      Class<? extends Annotation> subAnoType = (Class<? extends Annotation>) valType;
      val = createAnoPropValExpr(ownerAst, (JsonObject) jsonVal, subAnoType);
      //Map<String, Expression> propValMap = parseAnoPropSpec(subAnoType, (JsonObject) jsonVal);
      //val = createAnnotationExpr(subAnoType, propValMap);
    } else {  
      // atomic value expression
      String jsonValStr = jsonVal.toString().replaceAll("\"", "");//jsonObj.getString(prop);
      val = createAnoPropValExprWithImports(ownerAst, valType, jsonValStr);
    }
    
    return val;
  }
  
  /**
   * @effects 
   *  convert value of <code>prop</code> in <code>jsonObj</code> 
   *  to an {@link Expression}, suitable for being set to value of a 
   *  {@link MemberValuePair} of an {@link AnnotationExpr}.
   *  
   *  Return the expression.
   *   
   * @version 5.4
   * @param valType 
   */
  public static Expression createAnoPropValExpr(final JsonObject jsonObj, 
      final Class<?> valType, 
      String prop) throws NotImplementedException {
    Expression val;
    
    JsonValue jsonVal = jsonObj.get(prop);
    if (JsonArray.class.isAssignableFrom(jsonVal.getClass())) { 
      // array expression
      Class<?> eleType = valType.getComponentType();
//      val = createAnoPropValArrayExpr(eleType, ((JsonArray)jsonVal));
      JsonArray valArr = (JsonArray) jsonVal;
      ArrayInitializerExpr expr = new ArrayInitializerExpr();
      int index = 0;
      NodeList<Expression> values = new NodeList<>();
      for(JsonValue v : valArr) {
        Expression exprEle;
        if (v instanceof JsonObject) {
          // object array element -> sub-annotation element
          Class<? extends Annotation> anoEleType = (Class<? extends Annotation>) eleType;
          exprEle = createAnoPropValExpr((JsonObject) v, anoEleType);
        } else {
          // non-object array element
          exprEle = createAnoPropValExpr(eleType, v.toString());
        }
        values.add(exprEle);
      }
      expr.setValues(values);
      val = expr;
    } else if (jsonVal instanceof JsonObject) { // array
      // annotation expression 
      Class<? extends Annotation> subAnoType = (Class<? extends Annotation>) valType;
      val = createAnoPropValExpr((JsonObject) jsonVal, subAnoType);
      //Map<String, Expression> propValMap = parseAnoPropSpec(subAnoType, (JsonObject) jsonVal);
      //val = createAnnotationExpr(subAnoType, propValMap);
    } else {  
      // atomic value expression
      String jsonValStr = jsonVal.toString().replaceAll("\"", "");//jsonObj.getString(prop);
      val = createAnoPropValExpr(valType, jsonValStr);
    }
    
    return val;
  }
  
  /**
   * @effects 
   *  convert val to a {@link Expression}, suitable for being set to value of a 
   *  {@link MemberValuePair} of an {@link AnnotationExpr}.
   *  
   *  Return the expression.
   *   
   * @version 
   * - 5.4: created
   * - 5.4.1: improved
   */
  public static Expression createAnoPropValExpr(Class<?> valType, 
      String val) throws NotImplementedException {
    Expression expr;
    /*
    String[] valEles = val.split("\\.");
    if (valEles.length > 1) {
      // compound element
      // needs to use FieldAccessExpr
      FieldAccessExpr fae = null;
      
      for (int i = 0; i < valEles.length; i++) {
        String ele = valEles[i];
        if (fae == null) {
          fae = new FieldAccessExpr();
          fae.setScope(new NameExpr(ele));
          fae.setName(valEles[++i]);
        } else {
          FieldAccessExpr old = fae;
          fae = new FieldAccessExpr(old, ele);
        }
      }
      
      expr = fae;
    } else {
      // single element
      if (valType.equals(String.class)) {
        expr = createStringExpr(val);
      } else if (valType.equals(Boolean.class) || valType.equals(boolean.class)) {
        expr = createBooleanExpr(Boolean.parseBoolean(val));
      } else if (valType.equals(Integer.class) || valType.equals(int.class)) {
        expr = createIntegerExpr(Integer.parseInt(val));
      } else if (valType.equals(Long.class) || valType.equals(long.class)) {
        expr = createLongExpr(Long.parseLong(val));
      } else if (valType.equals(Float.class) || valType.equals(float.class)) {
        expr = createFloatExpr(Float.parseFloat(val));
      } else if (valType.equals(Double.class) || valType.equals(double.class)) {
        expr = createDoubleExpr(Double.parseDouble(val));
      } else if (valType.equals(Class.class)) {
        expr = createClassExprFor(val);
      } else {
        throw new NotImplementedException(Code.DATA_TYPE_NOT_SUPPORTED, 
            new Object[] {valType});
      }
    }
    */
    return createAnoPropValExprWithImports(null, valType, val);
  }

  /**
  * @modifies ownerAst
   * @requires 
   *  if <code>ownerAst != null</code> then it must be the one that will contain the result.
   *  
    * @effects 
   *  convert val to a {@link Expression}, suitable for being set to value of a 
   *  {@link MemberValuePair} of an {@link AnnotationExpr}.
   *  
   *  <p>If <code>ownerAst != null</code> then updates it with imports of any new value types that are encountered. 
   *  
   *  Return the expression.
   *   
   * @version 5.4.1
   */
  public static Expression createAnoPropValExprWithImports(
      final CompilationUnit ownerAst, 
      Class<?> valType, 
      String val) throws NotImplementedException {
    Expression expr;
    /*
    String[] valEles = val.split("\\.");
    if (valEles.length > 1) {
      // compound element
      // needs to use FieldAccessExpr
      FieldAccessExpr fae = null;
      
      for (int i = 0; i < valEles.length; i++) {
        String ele = valEles[i];
        if (fae == null) {
          fae = new FieldAccessExpr();
          fae.setScope(new NameExpr(ele));
          fae.setName(valEles[++i]);
        } else {
          FieldAccessExpr old = fae;
          fae = new FieldAccessExpr(old, ele);
        }
      }
      
      expr = fae;
    } else {
      // single element
      if (valType.equals(String.class)) {
        expr = createStringExpr(val);
      } else if (valType.equals(Boolean.class) || valType.equals(boolean.class)) {
        expr = createBooleanExpr(Boolean.parseBoolean(val));
      } else if (valType.equals(Integer.class) || valType.equals(int.class)) {
        expr = createIntegerExpr(Integer.parseInt(val));
      } else if (valType.equals(Long.class) || valType.equals(long.class)) {
        expr = createLongExpr(Long.parseLong(val));
      } else if (valType.equals(Float.class) || valType.equals(float.class)) {
        expr = createFloatExpr(Float.parseFloat(val));
      } else if (valType.equals(Double.class) || valType.equals(double.class)) {
        expr = createDoubleExpr(Double.parseDouble(val));
      } else if (valType.equals(Class.class)) {
        expr = createClassExprFor(val);
      } else {
        throw new NotImplementedException(Code.DATA_TYPE_NOT_SUPPORTED, 
            new Object[] {valType});
      }
    }
    */
    
    /* ducmle: 20220206: fixed to add field access check to some cases
    String[] valEles = val.split("\\.");
    final boolean fieldAccessFormat = (valEles.length > 1);
    
    if (valType.equals(String.class)) {
      expr = createStringExpr(val);
    } else if (valType.equals(Boolean.class) || valType.equals(boolean.class)) {
      expr = createBooleanExpr(Boolean.parseBoolean(val));
    } else if (valType.equals(Integer.class) || valType.equals(int.class)) {
      expr = createIntegerExpr(Integer.parseInt(val));
    } else if (valType.equals(Long.class) || valType.equals(long.class)) {
      expr = createLongExpr(Long.parseLong(val));
    } else if (valType.equals(Float.class) || valType.equals(float.class)) {
      expr = createFloatExpr(Float.parseFloat(val));
    } else if (valType.equals(Double.class) || valType.equals(double.class)) {
      expr = createDoubleExpr(Double.parseDouble(val));
    } else if (!valType.isEnum() && valType.equals(Class.class)) {
      expr = createClassExprFor(val);
    } else {
//      String[] valEles = val.split("\\.");
//      if (valEles.length > 1) {
      if (fieldAccessFormat) {
        // compound element
        // needs to use FieldAccessExpr
        FieldAccessExpr fae = null;
        
        for (int i = 0; i < valEles.length; i++) {
          String ele = valEles[i];
          if (fae == null) {
            fae = new FieldAccessExpr();
            fae.setScope(new NameExpr(ele));
            fae.setName(valEles[++i]);
          } else {
            FieldAccessExpr old = fae;
            fae = new FieldAccessExpr(old, ele);
          }
        }
        
        expr = fae;
      } else { 
        throw new NotImplementedException(Code.DATA_TYPE_NOT_SUPPORTED, 
          new Object[] {valType});
      }
    }
    */
    
    String[] valEles = val.split("\\.");
    final boolean fieldAccessVal = (valEles.length > 1);
    
    if (valType.equals(String.class)) {
      expr = createStringExpr(val);
    } else if (valType.equals(Boolean.class) || valType.equals(boolean.class)) {
      expr = createBooleanExpr(Boolean.parseBoolean(val));
    } else if (!fieldAccessVal && (valType.equals(Integer.class) || valType.equals(int.class))) {
      expr = createIntegerExpr(Integer.parseInt(val));
    } else if (valType.equals(Long.class) || valType.equals(long.class)) {
      expr = createLongExpr(Long.parseLong(val));
    } else if (valType.equals(Float.class) || valType.equals(float.class)) {
      expr = createFloatExpr(Float.parseFloat(val));
    } else if (valType.equals(Double.class) || valType.equals(double.class)) {
      expr = createDoubleExpr(Double.parseDouble(val));
    } else if (!valType.isEnum() && valType.equals(Class.class)) {
      expr = createClassExprFor(val);
    } else {
//      String[] valEles = val.split("\\.");
//      if (valEles.length > 1) {
      if (fieldAccessVal) {
        // compound element
        // needs to use FieldAccessExpr
        FieldAccessExpr fae = null;
        
        for (int i = 0; i < valEles.length; i++) {
          String ele = valEles[i];
          if (fae == null) {
            fae = new FieldAccessExpr();
            fae.setScope(new NameExpr(ele));
            fae.setName(valEles[++i]);
          } else {
            FieldAccessExpr old = fae;
            fae = new FieldAccessExpr(old, ele);
          }
        }
        
        expr = fae;
      } else { 
        throw new NotImplementedException(Code.DATA_TYPE_NOT_SUPPORTED, 
          new Object[] {valType});
      }
    }
    return expr;
  }
  
//  /**
//   * @effects 
//   *  convert val to a {@link Expression}, suitable for being set to value of a 
//   *  {@link MemberValuePair} of an {@link AnnotationExpr}.
//   *  
//   *  Return the expression.
//   *   
//   * @version 5.4
//   * @param valType 
//   */
//  public static Expression createAnoPropValExpr(Class<?> valType, 
//      JsonValue val) throws NotImplementedException {
//    Expression expr;
//    
//    if (Enum.class.isAssignableFrom(valType)) {
//      String valStr = val.toString();
//      String[] valEles = valStr.split("\\.");
//      if (valEles.length > 1) {
//        // compound element
//        // needs to use FieldAccessExpr
//        FieldAccessExpr fae = null;
//        
//        for (int i = 0; i < valEles.length; i++) {
//          String ele = valEles[i];
//          if (fae == null) {
//            fae = new FieldAccessExpr();
//            fae.setScope(new NameExpr(ele));
//            fae.setName(valEles[++i]);
//          } else {
//            FieldAccessExpr old = fae;
//            fae = new FieldAccessExpr(old, ele);
//          }
//        }
//        
//        expr = fae;
//      } 
//    } else {
//      // single element
//      if (valType.equals(String.class)) {
//        expr = createStringExpr(val);
//      } else if (valType.equals(Boolean.class) || valType.equals(boolean.class)) {
//        expr = createBooleanExpr(Boolean.parseBoolean(val));
//      } else if (valType.equals(Integer.class) || valType.equals(int.class)) {
//        expr = createIntegerExpr(Integer.parseInt(val));
//      } else if (valType.equals(Long.class) || valType.equals(long.class)) {
//        expr = createLongExpr(Long.parseLong(val));
//      } else if (valType.equals(Float.class) || valType.equals(float.class)) {
//        expr = createFloatExpr(Float.parseFloat(val));
//      } else if (valType.equals(Double.class) || valType.equals(double.class)) {
//        expr = createDoubleExpr(Double.parseDouble(val));
//      } else if (valType.equals(Class.class)) {
//        expr = createClassExprFor(val);
//      } else {
//        throw new NotImplementedException(Code.DATA_TYPE_NOT_SUPPORTED, 
//            new Object[] {valType});
//      }
//    }
//    
//    return expr;
//  }
  
  
  /**
   * @effects 
   *  convert val to a {@link Expression}, suitable for being set to value of a 
   *  {@link MemberValuePair} of an {@link AnnotationExpr}.
   *  
   *  Return the expression.
   *   
   * @version 5.2
   * @deprecated since 5.4, use {@link #createAnoPropValExpr(String)} instead
   */
  public static Expression convertAnoPropVal2Expr(String val) {
    StringLiteralExpr strExpr = new StringLiteralExpr(val);
    
    return strExpr;
  }

  /**
   * @effects 
   *  convert val to an {@link Expression}, suitable for being set to value of a 
   *  {@link MemberValuePair} of an {@link AnnotationExpr}.
   *  
   *  Return the expression.
   *   
   * @version 5.2
   * @deprecated since 5.4, use {@link #createAnoPropValExpr(String)} instead
   * 
   */
  public static Expression convertAnoPropVal2Expr(DOpt.Type val) {
    // DOpt.Type
    FieldAccessExpr doptTypeExpr = new FieldAccessExpr();
    doptTypeExpr.setScope(new NameExpr(DOptName));
    doptTypeExpr.setName(new SimpleName(DOptTypeName));
    
    // DOpt.Type.val
    FieldAccessExpr expr = new FieldAccessExpr();
    expr.setScope(doptTypeExpr);
    expr.setName(val.name());
    
    return expr;
  }
  
  /**
   * @effects 
   *  create and return a suitable String representation of value 
   * @version 5.2 
   */
  public static String convertPropValuetoString(Object value) {
    if (value == null) return null;
    
    Class cls = value.getClass();
    if (cls.isArray()) {
      // array type
      Object[] arr = (Object[]) value;
      StringBuffer sb = new StringBuffer();
      int numEls = arr.length, index = 0;
      for (Object v : arr) {
        sb.append(v.toString());
        if (index < numEls - 1) sb.append(",");
        index++;
      }
      
      return sb.toString();
    }
    // support other types here
    else {
      return value+"";
    }
  }

  /**
   * @requires anoPropVals != null
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public static Map<String, Expression> parseAnoPropSpec(Class<? extends Annotation> anoCls, String anoPropVals) 
      throws NotPossibleException {
    String[] pairs = anoPropVals.split(",");
    Map<String, Expression> propValMap = new LinkedHashMap<>();
    for (String pairStr : pairs) {
      String[] pair = pairStr.split(":");
      String prop = pair[0].trim();
      String valStr = pair[1].trim();
      
      // parse value using the property type in anoCls
      try {
        Class<?> valType = anoCls.getDeclaredMethod(prop).getReturnType();
        Expression val = createAnoPropValExpr(valType, valStr);
        propValMap.put(prop, val);
      } catch (NoSuchMethodException | SecurityException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, new Object[] {"getDeclaredMethod", prop, e.getMessage()}, e);
      }
    }
    
    return propValMap;
  }
  
  /**
   * @effects 
   *  Parse <tt>pair.value</tt> to the suitable type and return it.
   *  
   * @version 5.2c 
   */
  public static Object parseAnoMemberValue(MemberValuePair pair) {
    String name = pair.getNameAsString();
    Node valNode = pair.getValue();
    Object val;
    
    if (!(valNode instanceof AnnotationExpr)) { // normal property
      if (valNode instanceof FieldAccessExpr) { // e.g. Format.Nil, Type.Integer: convert to the proper enum
        FieldAccessExpr fieldExpr = (FieldAccessExpr) valNode;
        Expression scopeExp = fieldExpr.getScope();
        String scope;
        if (scopeExp instanceof NameExpr) {
          scope = ((NameExpr) scopeExp).getName().asString();
        } else {
          scope = ((NameExpr)((FieldAccessExpr) scopeExp).getScope()).getName().asString();
        }
        
        String typeName = fieldExpr.getNameAsString(); //.getField().asString();
        
        switch (scope) {
          case "Type":
            // DAttr.Type
            val = DAttr.Type.valueOf(typeName);
            break;
          case "Format":
            // DAttr.Format
            val = DAttr.Format.valueOf(typeName);
            break;
          case "AssocType":
            // DAssoc.AssocType
            val = DAssoc.AssocType.valueOf(typeName);
            break;
          case "AssocEndType":
            // DAssoc.AssocEndType
            val = DAssoc.AssocEndType.valueOf(typeName);
            break;
          case DOptName:
            // DOpt.Type
            val = DOpt.Type.valueOf(typeName);
            break;
          default:
            val = null;
        }
      } else if (valNode instanceof LiteralExpr) { // wrapper types (e.g. String, Integer, etc.): to convert
        val = ParserToolkit.expressionValue((LiteralExpr)valNode);
      } else if (valNode instanceof ArrayInitializerExpr) { // array type
        //TODO: check the correct element type and use it to initialise the array
        NodeList<Expression> elements = ((ArrayInitializerExpr) valNode).getValues();
        if (elements.isEmpty()) { // empty array
          val = new String[] {};
        } else { // non-empty array
          String[] valArray = new String[elements.size()];
          int idx = 0;
          for (Expression eexpr : elements) {
            if (eexpr instanceof LiteralExpr) {
              valArray[idx++] = ParserToolkit.expressionValue((LiteralExpr)eexpr).toString();
            }
          }
          val = valArray;
        }
      } else if (valNode instanceof ClassExpr) {  // class value (e.g. Student.class)
        throw new NotImplementedException("Class-typed property is not supported");
        /* TODO: support class-typed property value
        ClassOrInterfaceType classType = (ClassOrInterfaceType) ((ClassExpr) valNode).getType();
        String clsName = classType.getNameAsString();
        String fqn = getFQN(clsName);
        Class clazz;
        try {
          clazz = Class.forName(fqn);
          val = clazz;
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
          
          val = null;
        }
        */
      }
      // add more special cases here
      else {  // non-of-the above
        val = valNode;          
      }
      //attrDef.setPropertyValue(name, val);
    } else { // metadata extension
      throw new NotImplementedException("Annotation-typed property is not supported");
      /* TODO: support annotation-typed property
      switch (name) {
        case "filter": // DAttr.filter
          SelectDef selectDef = new SelectDef();
          valNode.accept(this, selectDef);
          
          attrDef.setPropertyValue(name, selectDef);
          break;
          
        case "associate": // DAssoc.associate
          AssociateDef associateDef = new AssociateDef();
          valNode.accept(this, associateDef);
          
          attrDef.setPropertyValue(name, associateDef);
          break;
          
        // add other cases here
      }
      */
    }  
    
    return val;
  }
  
  /**
   * @effects 
   *  return a {@link DoubleLiteralExpr} representing <tt>val</tt>
   *   
   * @version 5.2c
   */
  public static DoubleLiteralExpr createDoubleExpr(double val) {
    return new DoubleLiteralExpr(val);
  }

  /**
   * @effects 
   *  return a {@link DoubleLiteralExpr} representing <tt>val</tt> suitable for use as float value.
   *  
   *  <p>This differs from {@link #createDoubleExpr(double)} in that the expression's value has the suffix <tt>'f'</tt>.
   *   
   * @version 5.2c
   */
  public static DoubleLiteralExpr createFloatExpr(float val) {
    DoubleLiteralExpr expr = new DoubleLiteralExpr(val);
    // IMPORTANT: this is needed!
    expr.setValue(val+"f");
    
    return expr;
  }

  public static IntegerLiteralExpr createIntegerExpr(int val) {
    IntegerLiteralExpr expr = new IntegerLiteralExpr(val);
    
    return expr;
  }
  
  public static LongLiteralExpr createLongExpr(long val) {
    LongLiteralExpr expr = new LongLiteralExpr(val);
    
    return expr;
  }
  
  /**
   * @effects 
   *  return a {@link BooleanLiteralExpr} representing <tt>val</tt>
   *   
   * @version 5.2c
   */
  public static BooleanLiteralExpr createBooleanExpr(Boolean tf) {
    return new BooleanLiteralExpr(tf);
  }

  /**
   * @effects 
   *  return a {@link StringLiteralExpr} representing <tt>val</tt>
   *   
   * @version 5.2c
   */
  public static StringLiteralExpr createStringExpr(String val) {
    //ducmle: 20220127: remove quotes if present
    val = val.trim();
    if (val.startsWith("\"")) val = val.substring(1);
    if (val.endsWith("\"")) val = val.substring(0, val.length()-1);
    
    return new StringLiteralExpr(val);
  }

  /**
   * @modifies <tt>cls</tt>
   * @effects 
   *  Remove all comments in <tt>cls</tt>.
   *  
   * @version 
   */
  public static void removeAllComments(ClassOrInterfaceDeclaration cls) {
    // header comments
    cls.removeJavaDocComment();
    
    List<Comment> comments = cls.getAllContainedComments();
    if (comments != null) {
      for (Comment c : comments) {
        //cls.remove(c);
        c.removeForced();
      }
    }
    
    List<MethodDeclaration> methods = cls.getMethods();
    
    for (MethodDeclaration m : methods) {
      m.removeComment();
      m.removeJavaDocComment();
      
      Optional<BlockStmt> bodyOpt = m.getBody();
      
      if (bodyOpt.isPresent()) {
        BlockStmt body = bodyOpt.get();
        
        /* does not work!: body.remove() does not work
        comments = body.getAllContainedComments();
        
        if (comments != null) {
          for (Comment c : comments) {
            boolean removed = body.remove(c);
            if (removed) {
              System.out.println();
            }
          }
        }
        */
        removeAllNodeComments(body);
      }
    }
    
    List<FieldDeclaration> fields = cls.getFields();
    
    for (FieldDeclaration f : fields) {
      f.removeComment();
      f.removeJavaDocComment();
    }
  }

  /**
   * @effects 
   *   
   */
  private static void removeAllNodeComments(Node node) {
    
    removeThisNodeComment(node);
    
    for (Node child : node.getChildNodes()) {
      removeThisNodeComment(child);
      
      List<Node> descendants = child.getChildNodes();
      
      if (descendants != null) {
        for (Node d : descendants) {
          removeAllNodeComments(d);
        }
      }
    }
  }

  /**
   * @modifies <tt>node</tt>
   * @effects 
   *   remove the comment and all orphaned comments that are attached directly to <tt>node</tt>  
   * @version 
   */
  private static void removeThisNodeComment(Node node) {
    Optional<Comment> copt = node.getComment();
    if (copt.isPresent())
      copt.get().removeForced();

    List<Comment> orphaned = node.getOrphanComments();
    if (orphaned != null) {
      for (Comment c : orphaned) {
        c.removeForced();
      }
    }
  }

  /**
   * @effects 
   *  count and return number of lines-of-code (locs) of class <tt>cls</tt>, excluding the 
   *  members whose names are in <tt>exclMemberNames</tt> (if specified).
   *  
   *  If the class is empty then return 0.
   */
  public static int countLocsWithExclusion(ClassOrInterfaceDeclaration cls,
      Collection<String> exclMemberNames, StringBuilder cleanedSrc) {
    //TODO: is there a better way than cloning?
    ClassOrInterfaceDeclaration clsc = cls.clone();
    
    if (exclMemberNames != null) {
      // remove opeations (if any)
      removeOperations(clsc, exclMemberNames);
      
      // remove inner classes (if any)
      removeInnerClasses(clsc, exclMemberNames);
    }
    
    String src = clsc.toString();
    
    if (cleanedSrc != null) {
      cleanedSrc.append(src);
    }

    // debug
//    String src = clsc.toString();
    // System.out.printf("%ncountLocsWithExclusion(%s): src = %n %s %n%n", cls.getName(), src);

    // count lines
    int lines = src.split("\\n").length;
    
    return lines;
  }

  /**
   * @modifies cls
   * @effects 
   *  if <tt>cls</tt> has inner classes then remove those whose name are in <tt>memberNames</tt>
   *  
   * @version 5.2
   */
  public static void removeInnerClasses(ClassOrInterfaceDeclaration cls, Collection<String> memberNames) {
    List<ClassOrInterfaceDeclaration> nodes = 
        cls.getChildNodesByType(ClassOrInterfaceDeclaration.class);
    
    if (nodes != null) {
      for (ClassOrInterfaceDeclaration inner : nodes) {
        if (memberNames.contains(inner.getNameAsString())) {
          inner.removeForced();
        }
      }
    }
  }

  /**
   * @modifies cls
   * 
   * @effects  
   *  remove from <tt>cls</tt> the methods whose names are in <tt>methodNames</tt>
   * @version 5.2
   */
  public static void removeOperations(ClassOrInterfaceDeclaration cls,
      Collection<String> methodNames) {
    //
    List<MethodDeclaration> methods = cls.getMethods();
    
    if (methods != null) {
      for (MethodDeclaration m : methods) {
        String mname = m.getNameAsString();
        
        if (methodNames.contains(mname)) {
          m.removeForced();
        }
      }
    }
    
    // constructors
    // TODO: compare constructs by parameter list
    List<ConstructorDeclaration> consts = cls.getConstructors();
    if (consts != null) {
      for (ConstructorDeclaration cons : consts) {
        String cname = cons.getNameAsString();
        
        if (methodNames.contains(cname)) {
          cons.removeForced();
        }
      }
    }
  }

  /**
   * @effects 
   *  if exists member(s) of <tt>cls</tt> whose name(s) are <tt>name</tt>
   *    return the first of such member
   *  else
   *    return null 
   * @version 5.2
   */
  public static ClassOrInterfaceDeclaration getInnerClsByName(
      ClassOrInterfaceDeclaration cls, 
      String name) {
    if (cls == null || name == null) return null;
    
    List<Node> nodes = cls.getChildNodes();
    if (nodes != null) {
      for (Node n : nodes) {
        if (n instanceof ClassOrInterfaceDeclaration) {
          ClassOrInterfaceDeclaration ncls = (ClassOrInterfaceDeclaration) n;
          if (ncls.getNameAsString().equals(name)) {
            return ncls; 
          } 
        }
      }
    }
    
    // not found
    return null;
  }

  /**
   * @effects 
   *  if cls has a field named <tt>name</tt>, typed <tt>declaredType</tt> and has <tt>modifier</tt>
   *    return true
   *  else
   *    return false
   * @version 5.2 
   */
  public static boolean hasField(final ClassOrInterfaceDeclaration cls, String name, Type declaredType, Modifier... modifier) {
    Optional<FieldDeclaration> optF = cls.getFieldByName(name);
    
    if (optF.isPresent()) {
      FieldDeclaration f = optF.get();
      Type ftype = getFieldType(f);
      if (!ftype.equals(declaredType)) { 
        return false;
      } 
      
      EnumSet<Modifier> mods = f.getModifiers();
      if (mods.size() != modifier.length)
        return false;
      
      for (Modifier m : modifier) {
        if (!mods.contains(m)) {
          return false;
        }
      }
      
      // found a match
      return true;
    } else {
      return false;
    }
  }
 
  /**
   * This works the same as {@link #createGenericType(Class, String...)} except that 
   * it takes the {@link Class} objects as input.
   * 
   * @effects 
   *  create and return a generic {@link Type} suitable for the specified Java's generic type specification.
   *  
   * @version 5.4
   */
  public static Type createGenericType(Class<?> cls,
      Class... typeParams) {
    String simpleName = cls.getSimpleName();
    ClassOrInterfaceType type = (ClassOrInterfaceType) createClassOrInterfaceType(simpleName);
    NodeList<Type> typeArgs = new NodeList<>(type);
    for(Class typeParamCls : typeParams) {
      String typeParam = typeParamCls.getSimpleName();
      ClassOrInterfaceType typeArg = (ClassOrInterfaceType) createClassOrInterfaceType(typeParam);
      typeArgs.add(typeArg);
    }
    type.setTypeArguments(typeArgs);
    
    return type;
  }
  
  /**
   * @effects 
   *  create and return a generic {@link Type} suitable for the specified Java's generic type specification.
   *  
   * @version 5.4
   */
  public static Type createGenericType(Class<?> cls,
      String... typeParams) {
    String simpleName = cls.getSimpleName();
    ClassOrInterfaceType type = (ClassOrInterfaceType) createClassOrInterfaceType(simpleName);
    NodeList<Type> typeArgs = new NodeList<>(type);
    for(String typeParam : typeParams) {
      ClassOrInterfaceType typeArg = (ClassOrInterfaceType) createClassOrInterfaceType(typeParam);
      typeArgs.add(typeArg);
    }
    type.setTypeArguments(typeArgs);
    
    return type;
  }
  
  /**
   * @param name: optional
   * @param paramNames: optional
   * @param exceptions: optional
   * 
   * @effects 
   *   if cls has a method with the specified <tt>methodSig</tt> and is annotated with {@link DOpt}
   *   whose type = <tt>optType</tt>
   *    return true
   *   else
   *    return false
   *  
   *  <p>Throws NotFoundException if a referenced thrown exception cannot be found in the class path
   *  of <tt>cls</tt>'s compilation unit.
   * @version 5.2
   */
  public static boolean hasMethod(ClassOrInterfaceDeclaration cls, 
      DOpt.Type optType, MethodSig methodSig) throws NotFoundException {
    // look up method with the specified optType
    Collection<MethodDeclaration> methods = getDomainMethodsByOptType(cls, optType);
    
    if (methods == null) 
      return false;

    // check that method has the correct signature
    CompilationUnit cu = (CompilationUnit) cls.getParentNode().get();
    
    try {
      for (MethodDeclaration method : methods) {
        boolean match = methodSig.equals(method.getType(), 
          method.getNameAsString(), 
          null,
          getParameterTypes(method),
          getMethodThrowsClause(cu, method), 
          method.getModifiers().toArray(new Modifier[0])
          );
        
        if (match) return true;
      }
      
      return false;
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, 
          new String[] {e.getMessage()});
    }
  }

  /**
   * This is a variation of {@link #hasMethod(ClassOrInterfaceDeclaration, DOpt.Type, MethodSig)}.
   * 
   * @param name: optional
   * @param paramNames: optional
   * @param exceptions: optional
   * 
   * @effects 
   *   if cls has a method with the specified <tt>optType</tt> and signature elements
   *    return true
   *   else
   *    return false
   *  
   *  <p>Throws NotFoundException if a referenced thrown exception cannot be found in the class path
   *  of <tt>cls</tt>'s compilation unit.
   *  
   * @version 5.2
   */
  public static boolean hasMethod(ClassOrInterfaceDeclaration cls,
      DOpt.Type optType, Type returnType,
      String name, String[] paramNames, Type[] paramTypes, 
      Class[] exceptions, Modifier...modifiers) throws NotFoundException {
    // look up method with the specified optType
    Collection<MethodDeclaration> methods = getDomainMethodsByOptType(cls, optType);
    
    if (methods == null) 
      return false;

    for (MethodDeclaration method : methods) {
      boolean match = 
          matchMethod(cls, method, returnType, name, paramNames, paramTypes, exceptions, modifiers);
      if (match) return true;
    }
    
    // no match 
    return false;
  }
  

  /**
   * This is a variation of {@link #hasMethod(ClassOrInterfaceDeclaration, DOpt.Type, Type, String, String[], Type[], Class[], Modifier...)}.
   * 
   * @param name: optional
   * @param paramNames: optional
   * @param exceptions: optional
   * 
   * @effects 
   *   if cls has a method with the specified <tt>optType</tt>, referencing <tt>refAttribName</tt>
   *   and whose signature elements match rest of the parameters
   *    return true
   *   else
   *    return false
   *  
   *  <p>Throws NotFoundException if a referenced thrown exception cannot be found in the class path
   *  of <tt>cls</tt>'s compilation unit.
   *  
   * @version 5.2 
   */
  public static boolean hasMethod(ClassOrInterfaceDeclaration cls,
      DOpt.Type optType, String refAttribName,
      Type returnType, String name, String[] paramNames,
      Type[] paramTypes, Class[] exceptions, Modifier...modifiers) {
    Collection<MethodDeclaration> methods = getDomainMethodsByAttrRef(cls, optType, refAttribName);
    
    if (methods == null) 
      return false;
    
    for (MethodDeclaration method : methods) {
      boolean match = 
          matchMethod(cls, method, returnType, name, paramNames, paramTypes, exceptions, modifiers);
      if (match) return true;
    }
    
    // no match 
    return false;
  }
  
  /**
   * @effects 
   *  if method's signature matches other parameters
   *    return true
   *  else
   *    return false
   * @version 5.2
   */
  private static boolean matchMethod(ClassOrInterfaceDeclaration cls, MethodDeclaration method, Type returnType,
      String name, String[] paramNames, Type[] paramTypes, Class[] exceptions,
      Modifier[] modifiers) throws NotFoundException {
    // check that method has the correct signature
    CompilationUnit cu = (CompilationUnit) cls.getParentNode().get();
    
    try {
      return MethodSig.equals(
          // input signature
          returnType, name, paramNames, paramTypes, exceptions, modifiers,
          // method's signature
          method.getType(), 
          method.getNameAsString(), 
          null,
          getParameterTypes(method),
          getMethodThrowsClause(cu, method), 
          method.getModifiers().toArray(new Modifier[0])
          );
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, 
          new String[] {e.getMessage()});
    }    
  }

  /**
   * @effects 
   *   return method's thrown exceptions or null if method has no throws clause.
   *   
   *   <p>Throws ClassNotFoundException if the specified exception class cannot be
   *   looked up in the class path.
   */
  public static Class[] getMethodThrowsClause(CompilationUnit cu, 
      MethodDeclaration method) throws ClassNotFoundException {
    List<ReferenceType> exps = method.getThrownExceptions();
    
    if (exps != null && !exps.isEmpty()) {
      Class[] throwables = new Class[exps.size()];
      
      int idx = 0;
      for (ReferenceType t : exps) {
        ClassOrInterfaceType ct = (ClassOrInterfaceType) t;
        ImportDeclaration importd = getImportForClassType(cu, ct);
        
        if (importd == null) // no suitable import: cannot find it 
          return null;
        
        String fqn = importd.getNameAsString();
        
        Class c = Class.forName(fqn);
        
        throwables[idx] = c;
        idx++;
      }
      
      return throwables;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  return opt's parameter types or null if method has no parameters.
   */
  public static Type[] getParameterTypes(CallableDeclaration opt) {
    List<Parameter> params = opt.getParameters();
    if (params != null && !params.isEmpty()) {
      Type[] paramTypes = new Type[params.size()];
      
      int idx = 0;
      for (Parameter param : params) {
        paramTypes[idx] = param.getType();
        idx++;
      }
      
      return paramTypes;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if <tt>m</tt> has the specified annotation
   *    return true
   *  else
   *    return false
   * @version 5.2
   */
  public static boolean hasMethodAnnotation(CallableDeclaration m, DOpt.Type optType) {
    NodeList<AnnotationExpr> anos = m.getAnnotations();
    
    if (anos != null && !anos.isEmpty()) {
      final String DOptName = DOpt.class.getSimpleName();
      for (AnnotationExpr anox : anos) {
        NormalAnnotationExpr ano = (NormalAnnotationExpr) anox;
        if (ano.getNameAsString().equals(DOptName)) {
          if (isDOptType(ano, optType)) {
            // found it
            return true;
          }          
        }
      }
    } 
    
//    Optional<AnnotationExpr> doptOpt = m.getAnnotationByClass(DOpt.class);
//    if (doptOpt != null && doptOpt.isPresent()) {
//      NormalAnnotationExpr dopt = (NormalAnnotationExpr) doptOpt.get();
//      if (isDOptType(dopt, optType)) {
//        // found it
//        return true;
//      }
//    }
    
    return false;
  }

  /**
   * @effects 
   *  if <tt>opt</tt>'s signature has the same parameter types as specified by <tt>paramTypes</tt>
   *    return true
   *  else
   *    return false
   * @version 5.2
   */
  public static boolean hasParameterTypes(CallableDeclaration opt,
      Type[] paramTypes) {
    List<Parameter> params = opt.getParameters();
    
    if (paramTypes == null) {
      if (params != null && !params.isEmpty()) {
        return false;
      } else {
        return true;
      }
    } else {
      if(params == null || params.isEmpty()) {
        return false;
      } else {
        Type[] mparamTypes = getParameterTypes(opt);
        
        return Arrays.equals(paramTypes, mparamTypes);
      }
    }
  }

  /**
   * @effects 
   *  create and return a {@link FieldAccessExpr} for <tt>enu</tt> 
   * @version 5.2
   */
  public static FieldAccessExpr createFieldAccessExprFor(Enum enu) {
    FieldAccessExpr expr = new FieldAccessExpr();

    String enumClsName = enu.getDeclaringClass().getName();
    // replace '$' by '.', in case enumCls is an inner class
    if (enumClsName.indexOf('$') > -1) {
      enumClsName = enumClsName.replaceAll("\\$", ".");
    }
    String enumVal = enu.name();
    
    NameExpr scope = new NameExpr();
    scope.setName(enumClsName);
    
    expr.setScope(scope);
    expr.setName(enumVal);
    
    return expr;
  }

  /**
   * @effects 
   *  create and return a {@link FieldAccessExpr} for <tt>enu</tt> based on the Enum's simple name.
   * @version 5.2
   */
  public static FieldAccessExpr createSimpleFieldAccessExprFor(Enum enu) {
    FieldAccessExpr expr = new FieldAccessExpr();

    String enumClsName = enu.getDeclaringClass().getSimpleName();
    String enumVal = enu.name();
    
    NameExpr scope = new NameExpr();
    scope.setName(enumClsName);
    
    expr.setScope(scope);
    expr.setName(enumVal);
    
    return expr;
  }

  /**
   * @effects 
   *  create and return a {@link NormalAnnotationExpr} for <tt>anoType</tt> that consists of a single member-value pair specified
   *  by the input
   * @version 5.ec
   */
  public static NormalAnnotationExpr createSimpleAnnotationExpr(Class<? extends Annotation> anoType,
      String memberName, Expression memberVal) {
    NodeList<MemberValuePair> memberValPairs = new NodeList<>();
    memberValPairs.add(new MemberValuePair(memberName, memberVal));

    NormalAnnotationExpr anoExpr = new NormalAnnotationExpr(
        parseName(anoType.getSimpleName()), memberValPairs);

    return anoExpr;
  }
  
  /**
   * @effects 
   *  parse <tt>qualifiedName</tt> (import it if needed) and 
   *  return the {@link Name} object of <tt>qualifiedName</tt>
   */
  public static Name parseName(String qualifiedName) {
    return JavaParser.parseName(qualifiedName);
  }

  /**
   * @requires anoPropVals != null
   * @effects 
   * 
   * @version 5.4
   * 
   */
//  public static Map<String, Expression> parseAnoPropSpec(Class<? extends Annotation> anoCls, 
//      final JsonObject anoPropVals) 
//      throws NotPossibleException {
    
//    final Map<String, Expression> propValMap = new LinkedHashMap<>();
//    anoPropVals.keySet().forEach(prop -> {
//      Class<?> valType = null;
//      try {
//        valType = anoCls.getDeclaredMethod(prop).getReturnType();
//      } catch (NoSuchMethodException | SecurityException e) {
//        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, new Object[] {"getDeclaredMethod", prop, e.getMessage()}, e);
//      }
//      
//      // parse value using the property type in anoCls
//      Expression val = createAnoPropValExpr(anoPropVals, valType, prop);
//      
//      propValMap.put(prop, val);
//    });
//    
//    return propValMap;
//  }
  

  /**
   * @effects 
   *  return all {@link DOpt.Type} attached to <tt>opt</tt> or null of no such annotation attachment is found
   *  
   * @version 5.2c
   */
  public static Collection<DOpt.Type> getOptTypes(CallableDeclaration opt) {
    List<AnnotationExpr> anos = opt.getAnnotations();
    Collection<DOpt.Type> typeCol;
    if (anos != null) {
      typeCol = new ArrayList<>();
      for (AnnotationExpr anoexp : anos) {
        NormalAnnotationExpr ano = (NormalAnnotationExpr) anoexp;
        String name = ano.getNameAsString();
        if (name.equals(DOptName)) {
          // dopt
          DOpt.Type type = (DOpt.Type) parseAnoMemberValue(ano.getPairs().get(0)); //((StringLiteralExpr) ano.getPairs().get(0).getValue()).getValue();
          typeCol.add(type);
        }
      }
      
      if (typeCol.isEmpty())
        return null;
      else
        return typeCol;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if <tt>param</tt> is annoated with {@link AttrRef}
   *    return its value
   *  else
   *    return null
   * @version 5.2c
   */
  public static String getAttrRefValue(Parameter param) {
    Optional<AnnotationExpr> optAno = param.getAnnotationByClass(AttrRef.class);
    if (optAno != null && optAno.isPresent()) {
      NormalAnnotationExpr ano = (NormalAnnotationExpr) optAno.get();
      
      return ((StringLiteralExpr) ano.getPairs().get(0).getValue()).getValue();
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *   if exists a {@link NormalAnnotationExpr} attached to member whose type is <tt>anoType</tt> 
   *    return it
   *   else
   *    return null
   * @version 20210310
   */
  public static NormalAnnotationExpr getAnnotation(BodyDeclaration member, Class<? extends Annotation> anoType) {
    Optional<AnnotationExpr> anoOpt = member.getAnnotationByClass(anoType);
    if (anoOpt != null && anoOpt.isPresent()) {
      NormalAnnotationExpr ano = (NormalAnnotationExpr) anoOpt.get();
      return ano;
    } else {
      return null;
    }
  }

  /**
   * @modifies md
   * @effects 
   *  create and add to <code>md</code> a <code>Parameter(paramName: paramType) </code>
   * @version 5.4
   */
  public static Node addMethodParam(MethodDeclaration md, String paramName,
      Type paramType) {
    Parameter param = createParam(paramType, paramName);
    md.addParameter(param);
    return param;
  }

  /**
   * @effects 
   *   if exists a {@link MethodDeclaration} in <code>cd</code> whose name is <code>methodName</code>
   *    return the first of such element
   *   else
   *    return null
   *    
   * @version 5.4
   * 
   */
  public static MethodDeclaration getMethodByName(ClassOrInterfaceDeclaration cd, String methodName) {
    Collection<MethodDeclaration> methods = cd.getMethods();
    
    if (methods != null) {
      for (MethodDeclaration md : methods) {
        if (md.getNameAsString().equals(methodName)) {
          return md;
        }
      }
    }
    
    // not found
    return null;
  }

  /**
   * @requires stmt is a single-line code statement
   * @modifies body
   * @effects 
   *  push <code>stmt</code> to the top of <code>body</code>. 
   * @version 5.4
   */
  public static Node pushMethodStmt(BlockStmt body, String stmtText) {
    if (stmtText == null || body == null) return null;

    NodeList<Statement> stmtBlock = body.getStatements();
    Statement stmt = JavaParser.parseStatement(stmtText);
    
    stmtBlock.add(0,stmt);
    return stmt;
  }

  /**
   * @effects 
   *  return the {@link BlockStmt} represent the body the {@link MethodDeclaration} in <code>cd</code>
   *  whose method is <code>methodName</code> or null if no such method exists or the method body
   *  has not been created.
   *  
   * @version 5.4
   * 
   */
  public static BlockStmt getMethodBody(ClassOrInterfaceDeclaration cd, String methodName) {
    MethodDeclaration md = getMethodByName(cd, methodName);
    
    if (md != null) {
      return md.getBody().get();
    } else {
      return null;
    }
  }

 /**
  * @effects 
  *  return the {@link BlockStmt} represent the body in <code>opt</code>
  *  
  * @version 5.4
  * 
  */
  public static BlockStmt getOperationBody(
      CallableDeclaration<?> opt) {
    if (opt instanceof MethodDeclaration) {
      Optional<BlockStmt> bodyOpt = ((MethodDeclaration) opt).getBody();
      return (bodyOpt.isPresent()) ? bodyOpt.get() : null;    
    } else {
      return ((ConstructorDeclaration) opt).getBody();
    }
  }
  
  /**
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public static Node addClassAno(ClassOrInterfaceDeclaration cls,
      Class<? extends Annotation> anoCls, String anoPropVals) throws NotPossibleException {
    NormalAnnotationExpr anoExpr = (NormalAnnotationExpr) 
        cls.addAndGetAnnotation(anoCls);
    
    if (anoPropVals != null) {
      Map<String, Expression> propValMap = parseAnoPropSpec(anoCls, anoPropVals);
      NodeList<MemberValuePair> props = new NodeList<>();
      
      propValMap.forEach((prop, val) -> props.add(new MemberValuePair(prop, val)));
      
      anoExpr.setPairs(props);
    }
    
    return anoExpr;
  }


  /**
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public static Node addFieldAno(FieldDeclaration fd,
      Class<? extends Annotation> anoCls, String anoPropVals) {
    NormalAnnotationExpr anoExpr = (NormalAnnotationExpr) 
        fd.addAndGetAnnotation(anoCls);
    
    if (anoPropVals != null) {
      Map<String, Expression> propValMap = parseAnoPropSpec(anoCls, anoPropVals);
      NodeList<MemberValuePair> props = new NodeList<>();
      
      propValMap.forEach((prop, val) -> props.add(new MemberValuePair(prop, val)));
      
      anoExpr.setPairs(props);
    }
    
    return anoExpr;
  }

  /**
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public static Node addFieldAno(FieldDeclaration fd,
      Class<? extends Annotation> anoCls, JsonObject anoPropValJson) {
    NormalAnnotationExpr anoExpr = createAnoPropValExpr(anoPropValJson, anoCls);
    fd.addAnnotation(anoExpr);
    return anoExpr;
  }
  
  /**
   * @effects 
   * 
   * @version 5.4
   * 
   */
  public static Node addMethodAno(MethodDeclaration md,
      Class<? extends Annotation> anoCls, String anoPropVals) {
    NormalAnnotationExpr anoExpr = (NormalAnnotationExpr) 
       md.addAndGetAnnotation(anoCls);
    
    if (anoPropVals != null) {
      Map<String, Expression> propValMap = parseAnoPropSpec(anoCls, anoPropVals);
      NodeList<MemberValuePair> props = new NodeList<>();
      
      propValMap.forEach((prop, val) -> props.add(new MemberValuePair(prop, val)));
      
      anoExpr.setPairs(props);
    }
    
    return anoExpr;
  }

  /**
   * @modifies cls
   * @effects 
   *  update class <code>cls</code> with an <code>implements</code> clause
   *  for <code>intfClasses</code>
   * @version 5.4
   */
  public static void addClassImplement(ClassOrInterfaceDeclaration cls,
      Class<?>...intfClasses) {
    for(Class<?> intfCls : intfClasses) {
      cls.addImplementedType(intfCls);      
    }
  }

  /**
   * @modifies cls
   * @effects 
   *  update class <code>cls</code> with an <code>implements</code> clause
   *  for <code>intfClsses</code>
   * @version 5.4.1
   */
  public static void addClassImplement(ClassOrInterfaceDeclaration cls,
      ClassOrInterfaceType...intfClasses) {
    for(ClassOrInterfaceType intfCls : intfClasses) {
      Collection<ClassOrInterfaceType> superIntfs = getSuperIntfs(cls);
      if (superIntfs == null || !superIntfs.contains(intfCls)) {
        cls.addImplementedType(intfCls);              
      }
    }
  }
  

  /**
   * @effects 
   *  if <code>cls</code> implements interfaces
   *    return them as {@link ClassOrInterfaceType}s
   *  else
   *    return null 
   * @version 5.4.1
   */
  public static Collection<ClassOrInterfaceType> getSuperIntfs(final ClassOrInterfaceDeclaration cls) {
    NodeList<ClassOrInterfaceType> supIntfs = cls.getImplementedTypes();
    if (supIntfs == null || supIntfs.size() == 0) {
      // not a sub-type
      return null;
    } else {
      // a sub-type
      return supIntfs;
    }
  }
  
  /**
   * @effects 
   *  adds the exception classes specified in <code>throwClasses</code> to the specified method.
   * 
   * @version 5.4
   */
  public static <T extends Throwable> void addMethodThrows(
      ClassOrInterfaceDeclaration cls, String methodName,
      Class<T>...throwClasses) {
    MethodDeclaration md = getMethodByName(cls, methodName);
    for(Class<T> tc : throwClasses) {
      md.addThrownException(tc);
    }
  }

  /**
   * @effects 
   *  adds <code>stmt</code> to the body of the constructor of class <code>cls</code>
   *  whose parameter types are <code>paramTypes</code>
   * 
   * @version 5.4
   */
  public static void addConstructorStatement(ClassOrInterfaceDeclaration cls,
      String[] paramTypes, String stmt) throws NotFoundException {
    ConstructorDeclaration cons = getConstructorByParamTypes(cls, paramTypes);
    
    if (cons == null) {
      throw new NotFoundException(NotFoundException.Code.CONSTRUCTOR_METHOD_NOT_FOUND, 
          new Object[] {cls.getNameAsString(), Arrays.toString(paramTypes)});
    }
    
    addMethodStatement(cons.getBody(), stmt);
  }

  /**
   * @effects 
   *  copy the specified method to class <code>cls</code> and return the newly-created method
   * @version 5.4
   */
  public static MethodDeclaration addMethod(ClassOrInterfaceDeclaration cls,
      MethodDeclaration method) {
    MethodDeclaration md = method.clone();
    cls.addMember(md);
    
    return md;
  }

  /**
   * @effects 
   *  Return the {@link DAttr.Type} equivalence of <code>type</code>.
   *  Throws NotFoundException if not found
   *  
   * @version 5.4
   */
  public static DAttr.Type lookUpDAttrType(Type type) throws NotFoundException {
    if (type == null) return null;
    String typeName = getTypeName(type);
    DAttr.Type dtype;
    
    try {
      dtype = Enum.valueOf(DAttr.Type.class, typeName);
    } catch (IllegalArgumentException e) {
      if (type instanceof ClassOrInterfaceType) {
        dtype = DAttr.Type.Domain;
      }
      // TODO ? add more special cases here:
      else {
        throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, 
          e, new Object[] {DAttr.Type.class, ""});
      }
    }
    
    return dtype;
  }

  /**
   * @effects 
   *  if <code>cls</code> is defined with annotation <code>anoCls</code>
   *    return true
   *  else
   *    return false
   * @version 5.4
   */
  public static boolean hasAnnotation(ClassOrInterfaceDeclaration cls,
      Class<? extends Annotation> anoCls) {
    if (cls == null || anoCls == null) return false;
    
    return cls.getAnnotationByClass(anoCls).isPresent();
  }

  /**
   * @modifies <code>cls</code> in this
   * @effects 
   *  update <code>cls</code> by replacing all type name
   *  references to <code>name</code> by <code>newName</code>.
   * @version 5.4
   */
  public static void updateTypeNameRef(ClassOrInterfaceDeclaration cls, String name,
      String newName) {
    updateTypeNameRefOnNode(cls, name, newName);
  }

  /**
   * @modifies node
   * @effects 
   *  update all type name references to <code>name</code> to <code>newName</code> in all code nodes inside <code>node</code>
   *   
   * @version 5.4
   */
  public static void updateTypeNameRefOnNode(Node node,
      String name, String newName) {
    List<Node> children = node.getChildNodes();
    children.forEach(child -> {
      if (child instanceof ClassOrInterfaceType){
        ClassOrInterfaceType ct = (ClassOrInterfaceType) child;
        if (ct.getNameAsString().equals(name)) {
          // found a match -> change the name
          ct.getName().setIdentifier(newName);
        }
      }
      
      // recursive
      updateTypeNameRefOnNode(child, name, newName);
    });
  }
  
  /**
   * @modifies <code>node</code>
   * @effects 
   *  update all {@link MethodCallExpr}s in <code>node</code>'s syntax tree that 
   *  uses <code>oldMethodName</code> to use <code>newMethodName</code>. 
   *  
   * @version 5.4.1
   */
  public static void updateMethodCallsInNode(Node node,
      String oldMethodName, String newMethodName) {
    List<Node> children = node.getChildNodes();
    children.forEach(child -> {
      if (child instanceof MethodCallExpr){
        MethodCallExpr ct = (MethodCallExpr) child;
        if (ct.getNameAsString().equals(oldMethodName)) {
          // found a match -> change the name
          ct.getName().setIdentifier(newMethodName);
        }
      }

      // recursive
      updateMethodCallsInNode(child, oldMethodName, newMethodName);
    });
  }
  
  /**
   * @requires 
   *  the specified opts have the same signature (as validated by {@link #isSameMethodSignature(CallableDeclaration, CallableDeclaration)})  
   *  
   * @modifies <code>opt</code>
   * @effects 
   *  merge declaration elements of <code>otherOpt</code> into <code>opt</code>
   *   
   * @version 5.4.1
   * 
   */
  public static void mergeDOpts(CallableDeclaration<?> opt,
      CallableDeclaration<?> otherOpt) {
    // merges fd.type, modifier, annotation elements
    Type ft, otherFt;
    EnumSet<Modifier> mods = opt.getModifiers(), otherMods = otherOpt.getModifiers();
    
    // merges modifiers
    if (otherMods.contains(Modifier.PUBLIC)) {
      if (mods.contains(Modifier.PRIVATE))
        mods.remove(Modifier.PRIVATE);
      else if (mods.contains(Modifier.PROTECTED))
        mods.remove(Modifier.PROTECTED);
      
      
      mods.add(Modifier.PUBLIC);
    } else if (otherMods.contains(Modifier.PROTECTED)) {
      if (mods.contains(Modifier.PRIVATE))
        mods.remove(Modifier.PRIVATE);
      
      mods.add(Modifier.PROTECTED);
    } 
    
    // merges return type (for methods only)
    if (opt instanceof MethodDeclaration) {
      MethodDeclaration md = (MethodDeclaration) opt;
      MethodDeclaration otherMd = (MethodDeclaration) otherOpt;
      
      ft = md.getType();
      otherFt = otherMd.getType();
      
      if (!ft.equals(otherFt)) {
        md.setType(otherFt);
      }
    } 
    
    // merges annotation elements
    // scope: DOpt & AttrRef
    Optional<AnnotationExpr> otherAnoOpt = otherOpt.getAnnotationByClass(DOpt.class);
    if (otherAnoOpt.isPresent()) {
      // otherOpt is a domain method
      AnnotationExpr otherAno = otherAnoOpt.get();
      Optional<AnnotationExpr> anoOpt = opt.getAnnotationByClass(DOpt.class);

      AnnotationMerge anoMerge = new AnnotationMerge();

      if (!anoOpt.isPresent()) {
        // fd is not yet defined with DAttr -> add new
        opt.addAnnotation(otherAno.clone());
      } else {
        // fd has DAttr: merge
        // merge field declaration 
        anoOpt.get().accept(anoMerge, otherAno);
      }
      
      // merge AttrRef (if any)
      otherAnoOpt = otherOpt.getAnnotationByClass(AttrRef.class);
      if (otherAnoOpt.isPresent()) {
        otherAno = otherAnoOpt.get();
        anoOpt = opt.getAnnotationByClass(AttrRef.class);

        if (!anoOpt.isPresent()) {
          // opt does not have AttrRef: to add
          opt.addAnnotation(otherAno.clone());
        } else {
          // opt has it: to merge
          anoOpt.get().accept(anoMerge, otherAno);
        }
      }
    }
    
    // merge body
    BlockStmt otherBody = getOperationBody(otherOpt);
    if (otherBody != null) {
      mergeOptBody(opt, otherBody);
    }
  }

  /**
   * @effects 
   *   merges <code>otherBody</code> into <code>opt</code>'s body.
   *    
   * @version 5.4.1
   */
  public static void mergeOptBody(CallableDeclaration<?> opt,
      BlockStmt otherBody) {
    BlockStmt body;
    if (opt instanceof MethodDeclaration) {
      body = ((MethodDeclaration) opt).getBody().get();
    } else {
      body = ((ConstructorDeclaration) opt).getBody();
    }
    otherBody.getStatements().forEach(body::addStatement);
  }

  /**
   * @requires
   *  the specified fields have the same name 
   *  
   * @modifies <code>fd</code>
   * @effects 
   *  merge declaration elements of <code>otherFd</code> into <code>fd</code>
   *   
   * @version 5.4.1
   * 
   */
  public static void mergeDField(FieldDeclaration fd,
      FieldDeclaration otherFd) {
    // merges fd.type, modifier, annotation elements
    Type ft = getFieldType(fd), otherFt = getFieldType(otherFd);
    EnumSet<Modifier> mods = fd.getModifiers(), otherMods = otherFd.getModifiers();
    
    // merges fd.type
    if (!ft.equals(otherFt)) {
      setFieldType(fd, otherFt);
    }
    
    // merges modifiers
    if (!mods.contains(Modifier.PRIVATE) && otherMods.contains(Modifier.PRIVATE)) {
      if (mods.contains(Modifier.PUBLIC))
        mods.remove(Modifier.PUBLIC);
      
      mods.add(Modifier.PRIVATE);
    }
    
    // merges annotation elements
    // scope: DAttr & DAssoc
    Optional<AnnotationExpr> otherAnoOpt = otherFd.getAnnotationByClass(DAttr.class);
    if (otherAnoOpt.isPresent()) {
      // otherFd is a domain field
      AnnotationExpr otherAno = otherAnoOpt.get();
      Optional<AnnotationExpr> anoOpt = fd.getAnnotationByClass(DAttr.class);

      AnnotationMerge anoMerge = new AnnotationMerge();

      if (!anoOpt.isPresent()) {
        // fd is not yet defined with DAttr -> add new
        fd.addAnnotation(otherAno.clone());
      } else {
        // fd has DAttr: merge
        // merge field declaration 
        anoOpt.get().accept(anoMerge, otherAno);
      }
      
      // merge DAssoc (if any)
      otherAnoOpt = otherFd.getAnnotationByClass(DAssoc.class);
      if (otherAnoOpt.isPresent()) {
        otherAno = otherAnoOpt.get();
        anoOpt = fd.getAnnotationByClass(DAssoc.class);

        if (!anoOpt.isPresent()) {
          // fd does not have DAssoc: to add
          fd.addAnnotation(otherAno.clone());
        } else {
          // fd has DAssoc: to merge
          anoOpt.get().accept(anoMerge, otherAno);
        }
      }
    }
  }

  
  /**
   * @effects 
   *  return <code>true</code> if <tt>opt</tt>'s signature matches <tt>otherOpt</tt>
   *  or return false if otherwise.
   *  
   *  <p>A {@link CallableDeclaration} is either a method or a constructor.
   * @version 5.4.1
   */
  public static boolean isSameMethodSignature(CallableDeclaration<?> opt,
      CallableDeclaration<?> otherOpt) {
    boolean match = true;
    if (opt.getNameAsString().equals(otherOpt.getNameAsString())) {
      // found a match by name, check parameters
      List<Parameter> params = opt.getParameters();
      List<Parameter> otherParams = otherOpt.getParameters();
      if (params.size() == otherParams.size()) {
        // same length, check parameter pairs
        for (int i = 0; i < params.size(); i++) {
          Parameter param = params.get(i);
          Parameter otherParam = otherParams.get(i);
          if (!matchParam(param, otherParam)) {
            // no match
            match = false;
            break;
          }
        }
      } else {
        match = false;
      }
    } else {
      match = false;
    }
    
    return match;
  }

  /**
   * @effects 
   *  if param.type matches otherParam.type
   *    return true
   *  else
   *    return false
   */
  public static boolean matchParam(Parameter param,
      Parameter otherParam) {
    Type ptype = param.getType();
    Type otherPType = otherParam.getType();
    
    return ptype.equals(otherPType);
    
//    String paramType = ptype.asString();
//    String otherParamType = otherPType.asString();
//    
//    boolean match = paramType.equals(otherParamType); // || execParamType.equals("Object");
    
//    if (!match) {
//      // paramType could be generic
//      Type genType = otherParam.getParameterizedType();
//      if (genType instanceof ParameterizedType) {
//        ParameterizedType parGenType = (ParameterizedType) genType;
//        match = matchGenericType(ptype, parGenType);
//      }
//    }
//    return match;
  }

  /**
   * @effects 
   *  change the element name to <code>newName</code>
   * @version 5.4.1
   * 
   */
  public static void setName(ClassOrInterfaceDeclaration cls, String newName) {
    // cls.setName(newName);
    cls.getName().setIdentifier(newName);
  }

  /**
   * @requires all referenced classes of this are available in the class path /\
   *  cu contains cls
   *   
   * 
   * @effects 
   *  if cls is a sub-class
   *    return the super-class declaration in {@link #ast} as {@link Class} (using {@link Class#forName(String)} to load it)
   *    
   *    Throws NotFoundException if the specified class cannot be found by {@link Class#forName(String).
   *  else
   *    return null 
   */
  public static Class getSuperCls(CompilationUnit cu, ClassOrInterfaceDeclaration cls) throws NotFoundException {
    NodeList<ClassOrInterfaceType> sups = cls.getExtendedTypes();
    if (sups == null || sups.size() == 0) {
      // not a sub-class
      return null;
    } else {
      // a sub-class
      ClassOrInterfaceType supType = sups.get(0);
      
      // load the class itself
      String fqn = getFqnFor(cu, supType);
      
      Class supCls;
      try {
        supCls = Class.forName(fqn);
        
        return supCls;
      } catch (ClassNotFoundException e) { // should not happen
        throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e, new String[] {fqn});
      }
    }
  }

  /**
   * @modifies cls
   * 
   * @requires cls is not a sub-class of another class (use {@link #getSuperCls()} to check)
   * 
   * @effects 
   *  makes cls the subclass (i.e. <code>extends</code>) <code>superCls</code>  
   * @version 5.4.1
   */
  public static void setSuperCls(ClassOrInterfaceDeclaration cls, ClassOrInterfaceType superCls) {
    cls.setExtendedType(0, superCls);
//    cls.addExtendedType(superCls);
  }
  
  /**
   * @effects 
   *  if cls is a sub-class
   *    return the super-class declaration of cls as {@link ClassOrInterfaceType}
   *  else
   *    return null 
   * @version 5.4.1
   */
  public static ClassOrInterfaceType getSuperClsType(ClassOrInterfaceDeclaration cls) {
    NodeList<ClassOrInterfaceType> sups = cls.getExtendedTypes();
    if (sups == null || sups.size() == 0) {
      // not a sub-class
      return null;
    } else {
      // a sub-class
      ClassOrInterfaceType supType = sups.get(0);
      return supType;
    }
  }

  /**
   * @modifies <code>cls</code>
   * @effects 
   *  refactor <code>cls</code> st. the field named <code>currFieldName</code> is renamed to <code>newName</code>. 
   *  This also rename all methods whose name reference this field (e.g. getters, setters) and 
   *  all references to this field in method bodies.   
   *  
   * @version 5.4.1
   * 
   */
  public static void renameField(ClassOrInterfaceDeclaration cls, 
      String currFieldName, String newName) {
    // record (n,oldName) for nodes that have been renamed (to update references later, if necessary)
    // predominantly record the operations (other nodes and their references will have already been updated) 
    final Map<Node,String> changeLog = new HashMap<>();
    
    cls.getChildNodes().forEach(n -> {
      if (n instanceof FieldDeclaration) {
        FieldDeclaration fd = (FieldDeclaration) n;
        if (getFieldName(fd).equals(currFieldName)) {
          renameField(cls, fd, currFieldName, newName, changeLog);
        }
      }
    });
    
    if (!changeLog.isEmpty()) {
      changeLog.forEach((node, oldNodeName) -> {
        if (node instanceof MethodDeclaration) {
          // update method calls that reference this method
          String newMethodName = ((MethodDeclaration) node).getNameAsString();
          updateMethodCallsInNode(cls, oldNodeName, newMethodName);
        }
      });
    }
  }

  /**
   * @modifies <code>cls</code>
   * @effects 
   *  refactor <code>cls</code> st. the field <code>fd</code> is renamed to <code>newName</code>. 
   *  This also rename all methods whose name reference this field (e.g. getters, setters) and 
   *  all references to this field in method bodies.  
   *  
   * @version 5.4.1
   * 
   */
  public static void renameField(ClassOrInterfaceDeclaration cls, FieldDeclaration fd, 
      String currFieldName, String newName, Map<Node, String> changeLog) {
    // rename the field 
    setDomainFieldName(fd, newName);
    
    // rename all methods whose name reference this field (e.g. getters, setters)
    // scope: assume methods use AttrRef
    Collection<MethodDeclaration> opts = getDomainMethodsByAttrRef(cls, null, currFieldName);
    if (opts != null) {
      opts.forEach(opt -> {
        renameMethodRefField(opt, currFieldName, newName, changeLog);
      });
    }
    
  }

  /**
   * @modifies opt
   * @effects 
   *   refactor opt st. all references to the field named <code>currFieldName</code> are replaced 
   *   by <code>newName</code>. 
   *   These include references in annotation elements in the declaration and 
   *   references in method body statements.
   *   
   * @version 5.4.1
   * 
   */
  public static void renameMethodRefField(MethodDeclaration opt,
      String currFieldName, String newName, Map<Node, String> changeLog) {
    // rename opt
    String currCamelName =  DClassTk.toCamelCase(currFieldName);
    String newCamelName = DClassTk.toCamelCase(newName);
    final String oldOptName = opt.getNameAsString();
    StringBuilder optName = new StringBuilder(oldOptName);
    int index = optName.indexOf(currCamelName);
    if (index > -1) {
      optName.replace(index, index+currCamelName.length(), newCamelName);
      opt.setName(optName.toString());
      
      changeLog.put(opt, oldOptName); // record this in log (for method call update later) 
    }
    
    // rename AttrRef
    // scope: assumes domain method that uses @AttrRef
    Optional<AnnotationExpr> anoOpt = opt.getAnnotationByClass(AttrRef.class);
    if (anoOpt.isPresent()) {
      NormalAnnotationExpr ano = (NormalAnnotationExpr) anoOpt.get();
      MemberValuePair pair = ano.getPairs().get(0);
      StringLiteralExpr newNameVal = new StringLiteralExpr(newName);
      pair.setValue(newNameVal);
    }
    
    // rename field references in method body
    BlockStmt body = opt.getBody().orElse(null);
    if (body != null) {
      BlockStmt newBody = renameVarRefsInCode(body, currFieldName, newName);
      opt.setBody(newBody);
    }
  }

  /**
   * @effects 
   *  rename all references to <code>currVar</code> in <code>code</code> to <code>newVar</code>.
   *  Return a new {@link BlockStmt} that contains the replacements. 
   *  
   * @version 5.4.1
   */
  public static BlockStmt renameVarRefsInCode(BlockStmt code, String currVar,
      String newVar) {
    if (code == null) return null;
    /*
    code.getChildNodes().forEach(n -> {
      Expression exp;
      
      if (n instanceof VariableDeclarationExpr) {
        
      }
    });
    */
    String codeStr = code.toString();
    codeStr = codeStr.replaceAll(currVar, newVar);
    BlockStmt newCode = new BlockStmt();
    addMethodStatements(newCode, codeStr);
    
    return newCode;
  }

//  /**
//   * @effects 
//   *  if <tt>srcType</tt> matches the definition of <tt>genType</tt>
//   *    return true
//   *  else
//   *    return false
//   */
//  private static boolean matchGenericType(Type srcType,
//      ParameterizedType genType) {
//    String genTypeName = ((Class)genType.getRawType()).getSimpleName();
//    
//    java.lang.reflect.Type[] typeVars = genType.getActualTypeArguments();
//    
//    List<Node> typeElements = srcType.getChildNodes();
//    String parentType = typeElements.get(0).toString();
//    
//    boolean match = true;
//    if (parentType.equals(genTypeName)) {
//      // matching the parent type
//      // match the type arguments
//      int typeElIndex = 1;  // excluding parentType (see above)
//      for (java.lang.reflect.Type t : typeVars) {
//        if (typeElIndex >= typeElements.size()) {
//          // no match
//          match = false;
//          break;
//        }
//        Node typeEl = typeElements.get(typeElIndex);
//        if (t instanceof Class) {
//          Class tcls = (Class) t;
//          String tname = tcls.getSimpleName();
//          if (!tname.equals(typeEl.toString())) {
//            // no match
//            match = false; 
//            break;
//          }
//        } else {
//          // TODO: any other cases?
//          match = false; 
//          break;
//        }
//        typeElIndex++;
//      }
//    } else {
//      match = false;
//    }
//    
//    return match;
//  }
}
