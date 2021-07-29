package jda.modules.dcsl.util.parser;

import java.io.InputStream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

/**
 * @overview 
 *  This program prints the abstract syntax tree of a given Java program.
 *  
 * @author ducmle <br>
 *  adapted from code at {@link https://github.com/javaparser/javaparser/issues/538#issuecomment-276155353} 
 *  by Danny van Bruggen (matozoid).
 *  
 *  <p><b>Extension</b>: print node type and, if node type is non-composite (i.e. not specified among {@link #CompositeNodeClasses}),
 *                then also print the node value 
 * 
 * @example <pre>
 *  // from code string
 *  String code = ...
 *  ASTPrinter.print(code);
 *  
 *  // from code stream
 *  InputStream codeStream = ...
 *  ASTPrinter.print(code);
 *  
 *  // from a {@link CompilationUnit}
 *  CompilationUnit cu = ...
 *  ASTPrinter.print(cu);
 * </pre>
 *  
 * @version
 */
public class ASTPrinter {
  
  private static final Class[] CompositeNodeClasses = {
    CompilationUnit.class,
    ClassOrInterfaceDeclaration.class,
    MethodDeclaration.class,
    BlockStmt.class,
    IfStmt.class,
    WhileStmt.class,
    ForeachStmt.class,
    ForStmt.class,
    DoStmt.class,
    SwitchStmt.class, SwitchEntryStmt.class,
    TryStmt.class, CatchClause.class,    
  };
  
  /**
   * @effects 
   *  Parse <tt>code</tt> and print its abstract syntax tree (AST) out on the standard output.
   *  
   * @author
   *  adapted from code at {@link https://github.com/javaparser/javaparser/issues/538#issuecomment-276155353} 
   *  by Danny van Bruggen (matozoid)
   */
  public static void print(CompilationUnit cu) {
    cu.accept(new TreeStructVisitor() {
      @Override
      public void out(Node n, int indentLevel) {
        printNode(n, indentLevel);
      }
    }, 0);    
  }
  
  /**
   * @effects 
   *  Parse <tt>code</tt> and print its abstract syntax tree (AST) out on the standard output.
   *  
   * @author
   *  adapted from code at {@link https://github.com/javaparser/javaparser/issues/538#issuecomment-276155353} 
   *  by Danny van Bruggen (matozoid)
   */
  public static void print(String code) {
    CompilationUnit cu = JavaParser.parse(code);
    
    cu.accept(new TreeStructVisitor() {
      @Override
      public void out(Node n, int indentLevel) {
        printNode(n, indentLevel);
      }
    }, 0);    
  }

  /**
   * @effects 
   *  Parse <tt>codeStream</tt> and print its abstract syntax tree (AST) out on the standard output.
   *  
   * @author
   *  adapted from code at {@link https://github.com/javaparser/javaparser/issues/538#issuecomment-276155353} 
   *  by Danny van Bruggen (matozoid)
   */
  public static void print(InputStream codeStream) {
    CompilationUnit cu = JavaParser.parse(codeStream);
    
    cu.accept(new TreeStructVisitor() {

      @Override
      public void out(Node n, int indentLevel) {
        printNode(n, indentLevel);
      }
    }, 0);    
  }
  
  /**
   * @effects 
   *  Print to the standard output the user-friendly content of <tt>n</tt>
   *  
   * @version 
   */
  protected static void printNode(Node n, int indentLevel) {
    Class nodeType = n.getClass();
    
    if (isComposite(n)) {
      // n is a composite node -> print the type name only
      System.out.println(indent("  ", indentLevel) + nodeType.getSimpleName());
    } else {
      // no is non-composite -> print the type name + content
      String content = n.toString(); //n.toStringWithoutComments();
      System.out.println(indent("  ", indentLevel) + nodeType.getSimpleName() + ": " + content);
    }
  }

  /**
   * @effects 
   *  if n is a composite node (i.e. its type is one of those specified in {@link #CompositeNodeClasses}
   *    return true
   *  else
   *    return false
   * @version 
   * 
   */
  private static boolean isComposite(Node n) {
    Class nodeType = n.getClass();
    
    for (Class c : CompositeNodeClasses) {
      if (c.equals(nodeType))
        return true;
    }
    
    // non-composite
    return false;
  }

  /**
   * @effects 
   *  generate and return an indent consisting of <tt>level</tt> separator chars
   * @author ducmle
   */
  private static String indent(String indentChar, int level) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < level; i++) {
      sb.append(indentChar);
    }
    return sb.toString();
  }
}
