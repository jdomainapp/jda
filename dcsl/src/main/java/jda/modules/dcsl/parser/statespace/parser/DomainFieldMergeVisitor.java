/**
 * 
 */
package jda.modules.dcsl.parser.statespace.parser;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import jda.modules.common.exceptions.NotImplementedException;

/**
 * @overview 
 *  A {@link VoidVisitor} that walks the AST of a field to merge its elements with another field. 
 *  
 * @author dmle
 *
 * @version 5.4.1
 */
public class DomainFieldMergeVisitor extends VoidVisitorAdapter<AnnotationExpr> {
  
  private String pkgName;
  private List<String> imports;
  
  private static Logger logger = LoggerFactory.getLogger(DomainFieldMergeVisitor.class.getSimpleName());
  
  /**
   * @effects 
   *  initialise this with the arguments
   */
  public DomainFieldMergeVisitor(String pkgName, List<String> imports) {
    this.pkgName = pkgName;
    this.imports = imports;
  }

  /**
   * @effects 
   *  initialise this with {@link #pkgName} = null, {@link #imports} = null
   */
  public DomainFieldMergeVisitor() {
    this(null, null);
  }

  /**
   * @effects 
   *  analyse <tt>pair</tt> to merge with <tt>anoExpr</tt>.
   */
  @Override
  public void visit(MemberValuePair pair, AnnotationExpr anoExpr) {
    if (anoExpr == null) {
      return;
    }
    
    /* todo: when needed 
    // an annotation property (element)
    String name = pair.getNameAsString();
    Node valNode = pair.getValue();
    Object val;
    
    if (!(valNode instanceof AnnotationExpr)) { // normal property
      if (valNode instanceof FieldAccessExpr) { // e.g. Format.Nil, Type.Integer: convert to the proper enum
        FieldAccessExpr fieldExpr = (FieldAccessExpr) valNode;
        String scope = ((NameExpr)fieldExpr.getScope()).getName().asString();
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
        ClassOrInterfaceType classType = (ClassOrInterfaceType) ((ClassExpr) valNode).getType();
        String clsName = classType.getNameAsString();
        String fqn = getFQN(clsName);
        Class clazz;
        try {
          clazz = Class.forName(fqn);
          val = clazz;
        } catch (ClassNotFoundException e) {
          logger.warn("{}: {}", e.getClass().getSimpleName(), fqn);
          val = null;
        }
      }
      // add more special cases here
      else {  // non-of-the above
        val = valNode;          
      }
      
      anoExpr.setPropertyValue(name, val);
    } else { // metadata extension
      switch (name) {
        case "filter": // DAttr.filter
          SelectDef selectDef = new SelectDef();
          valNode.accept(this, selectDef);
          
          anoExpr.setPropertyValue(name, selectDef);
          break;
          
        case "associate": // DAssoc.associate
          AssociateDef associateDef = new AssociateDef();
          valNode.accept(this, associateDef);
          
          anoExpr.setPropertyValue(name, associateDef);
          break;
          
        // add other cases here
      }
      
    }
    */
    
    throw new NotImplementedException(DomainFieldMergeVisitor.class+".visit()");
  }

  /**
   * @effects 
   *  get FQN name for the class whose simple name is <tt>clsName</tt> 
   */
  private String getFQN(String clsName) {
    // check among import statement first
    if (imports != null) {
      for (String importStm : imports) {
        if (importStm.contains(clsName)) {
          return importStm;
        }
      }
    }
    
    // not found in imports then assume the same package as the compilation unit
    if (pkgName != null)
      return pkgName + "." + clsName;
    else 
      return clsName;
  }
}