package jda.modules.dcsl.test.parser;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.junit.Test;

import com.github.javaparser.ast.expr.Expression;

import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ParseAnoPropValTest {
  @Test
  public void main() {
    Class<? extends Annotation> anoCls = DAttr.class;
    String propValStr = 
        //"name: id; type: TypeSerialisable; id: true; optional: false"
        "name: id, type: DAttr.Type.Serializable, id: true, optional: false"
        ;
    Map<String,Expression> props = ParserToolkit.parseAnoPropSpec(
        anoCls,
        propValStr);
    
    System.out.println(propValStr);
    System.out.println(props);
    
    String propKey = "type";
    Expression typeExpr = props.get(propKey);
    System.out.println(typeExpr);
  }
}
