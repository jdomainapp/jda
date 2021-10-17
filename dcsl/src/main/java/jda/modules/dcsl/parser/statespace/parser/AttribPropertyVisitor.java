/**
 * 
 */
package jda.modules.dcsl.parser.statespace.parser;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.statespace.metadef.AssociateDef;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.MetaAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.SelectDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview 
 *  A {@link VoidVisitor} that walks the AST to extract properties of the meta-attributes of each domain attribute. 
 *  
 *  <p>Each property is added to {@link DAttrDef} which acts as the container for all the properties.
 *  
 * @author dmle
 *
 * @version 3.4
 */
public class AttribPropertyVisitor extends VoidVisitorAdapter<MetaAttrDef> {
  
  private String pkgName;
  private List<String> imports;
  
  private static Logger logger = LoggerFactory.getLogger(AttribPropertyVisitor.class.getSimpleName());
  
  /**
   * @effects 
   *  initialise this with the arguments
   */
  public AttribPropertyVisitor(String pkgName, List<String> imports) {
    this.pkgName = pkgName;
    this.imports = imports;
  }

  /**
   * @effects 
   *  initialise this with {@link #pkgName} = null, {@link #imports} = null
   */
  public AttribPropertyVisitor() {
    this(null, null);
  }

  /**
   * @effects 
   *  analyse <tt>pair</tt> to add to {@link DAttrDef} <tt>attrDef</tt>.
   */
  @Override
  public void visit(MemberValuePair pair, MetaAttrDef attrDef) {
    if (attrDef == null) {
      return;
    }
    
    //MetaAttrDef attrDef = (MetaAttrDef) arg;
    
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
      
      attrDef.setPropertyValue(name, val);
    } else { // metadata extension
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
      
    }
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