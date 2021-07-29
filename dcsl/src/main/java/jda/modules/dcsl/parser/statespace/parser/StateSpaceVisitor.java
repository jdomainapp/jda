/**
 * 
 */
package jda.modules.dcsl.parser.statespace.parser;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.statespace.metadef.DAssocDef;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview 
 *  A {@link VoidVisitor} that extracts from an AST the state space elements.
 *  
 *  <p>Information regarding each extracted domain attribute is added as a tuple ({@link DAttrDef}, {@link FieldDef}) to 
 *  a {@link Map}. This map is passed into the visitor as input.  
 *  
 * @author dmle
 * 
 * @version 5.0
 */
public class StateSpaceVisitor extends VoidVisitorAdapter<Map<DAttrDef, FieldDef>> {

  // package name of the compilation unit (i.e. of the source code file passed in as input)
  private String pkgName;
  // import statements of the compilation unit
  private List<String> imports;
  
  private AttribPropertyVisitor propVisit;
  
  public StateSpaceVisitor(final String pkgName, List<String> imports) {
    this.pkgName = pkgName;
    this.imports = imports;
    propVisit = new AttribPropertyVisitor(pkgName, imports);
    
    //fieldDefVisit = new AttribDefVisitor();//new AttribDefVisitor(pkgName, imports);
  }
  
  @Override
  public void visit(FieldDeclaration fd, Map<DAttrDef, FieldDef> stateSpace) {

    if (stateSpace == null) {
      return;
    }
    
    // extract DAttrDefs
    Optional<AnnotationExpr> anoExprOpt = fd.getAnnotationByClass(DAttr.class);
    if (anoExprOpt.isPresent()) {
      AnnotationExpr anoExpr = anoExprOpt.get();
      
      DAttrDef attr = null;
      
      if (anoExpr != null) {
        attr = new DAttrDef();
        anoExpr.accept(propVisit, attr);
      }

      if (attr != null) {
        // extract field declaration 
        FieldDef field = ParserToolkit.getFieldDef(fd);

        // register attr to field as annotation 
        field.addAnnotation(DAttr.class, attr);
        
        // extract DAssocDefs
        anoExprOpt = fd.getAnnotationByClass(DAssoc.class);
        DAssocDef assoc = null;
        
        if (anoExprOpt.isPresent()) {
          anoExpr = anoExprOpt.get();
          assoc = new DAssocDef();
          anoExpr.accept(propVisit, assoc);
          
          // register assoc to field as annotation
          field.addAnnotation(DAssoc.class, assoc);
        }

        // put (attr, field) to stateSpace
        stateSpace.put(attr, field);
      }
    }
    
  }
}