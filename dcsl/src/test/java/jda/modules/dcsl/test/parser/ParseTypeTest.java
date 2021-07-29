package jda.modules.dcsl.test.parser;

import com.github.javaparser.ast.type.Type;

import jda.modules.dcsl.parser.ParserConstants;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ParseTypeTest {
  public static void main(String[] args) {
    Type strArr = ParserConstants.TypeStringArr;
    
    System.out.println("Type: " + strArr);
  }
}
