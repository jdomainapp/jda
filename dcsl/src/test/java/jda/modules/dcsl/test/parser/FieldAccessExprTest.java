package jda.modules.dcsl.test.parser;

import org.junit.Test;

import com.github.javaparser.ast.expr.FieldAccessExpr;

import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.parser.ParserToolkit;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class FieldAccessExprTest {
  @Test
  public void run() {
    FieldAccessExpr expr = 
        ParserToolkit.createFieldAccessExprFor(PropertyName.controller_command);
    
    System.out.println(expr);
  }
}
