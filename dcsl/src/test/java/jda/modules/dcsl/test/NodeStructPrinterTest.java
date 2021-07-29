package jda.modules.dcsl.test;

import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import jda.modules.dcsl.parser.ParserToolkit;

/**
 * @overview 
 *
 * @author dmle
 *
 * @version 
 */
public class NodeStructPrinterTest {
  @Test
  public void run() {
    CompilationUnit codeUnit = JavaParser.parse(
        "class Test { "
         + "@Author(name=\"Designer\", date=\"08 Nov 2016\") public static void main(String[] args) { System.out.println(\"Hi\"); } }");
    
    System.out.println(codeUnit);
    
    ParserToolkit.printNodeStruc(codeUnit);
  }
}
