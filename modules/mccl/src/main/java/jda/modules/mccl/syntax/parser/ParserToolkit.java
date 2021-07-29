/**
 * 
 */
package jda.modules.mccl.syntax.parser;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import jda.modules.mccl.syntax.view.AttributeDesc;

/**
 * @overview 
 *  A toolkit containing shared operations for JavaParser. 
 *  
 * @author dmle
 *
 * @version 1.1 (modified to work with JavaParser 3.2.5
 */
public class ParserToolkit extends jda.modules.dcsl.parser.ParserToolkit {
  
  protected ParserToolkit() {
    super();
  }
  
  /**
   * @effects 
   *  change the name and the value of property <tt>AttribDesc.label</tt> of <tt>vd</tt> to <tt>newName</tt> 
   */
  public static void setViewFieldName(FieldDeclaration vd, String newName) {
    NodeList<VariableDeclarator> vars = vd.getVariables();

    // TODO (if needed): can a field have multiple vars?
    VariableDeclarator n  = vars.get(0);
    n.setName(newName);
    
    // if field has AttributeDesc annotation then change its label property 
    Optional<AnnotationExpr> anoOpt = vd.getAnnotationByClass(AttributeDesc.class);
    if (anoOpt.isPresent()) {
      NormalAnnotationExpr ano = (NormalAnnotationExpr) anoOpt.get();
      List<MemberValuePair> targetPairs = ano.getPairs();
      for (MemberValuePair tpair : targetPairs) {
        //if (tpair.getParentNode().get().getParentNode().get() == ano.get()) {
        // tpair is a property of ano
        if (tpair.getNameAsString().equals("label")) { 
          // property "name"
          // update tpair.value 
          StringLiteralExpr newNameVal = new StringLiteralExpr(newName);
          tpair.setValue(newNameVal);
          break;
        }
        //}
      }
    }
  }
  
 
}
