package jda.modules.dcsl.test.parser;

import org.junit.Test;

import com.github.javaparser.ast.CompilationUnit;

import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.util.parser.ASTPrinter;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ParseEnumTypeExpressionTest {
  @Test
  public void run() {    
    String code = "import domainapp.basics.model.config.Configuration.Language;\n" + 
        "import domainapp.basics.model.config.dodm.OsmConfig.ConnectionType;\n" + 
        "import domainapp.model.meta.app.DSDesc;\n" + 
        "import domainapp.model.meta.app.OrgDesc;\n" + 
        "import domainapp.model.meta.app.SysSetUpDesc;\n" + 
        "import domainapp.model.meta.app.SystemDesc;\n" + 
        "\n" + 
        "@SystemDesc(\n" + 
        "    appName=\"CourseMan\",\n" + 
        "    splashScreenLogo=\"coursemanapplogo.jpg\",\n" + 
        "    language=Language.English, \n" + 
        "    dsDesc = @DSDesc(connType = ConnectionType.Client, dsUrl = \"\", password = \"\", type = \"\", user = \"\"), \n" + 
        "    modules = { }, orgDesc = @OrgDesc(address = \"\", logo = \"\", name = \"\"), setUpDesc = @SysSetUpDesc)\n" + 
        "public class TestCode {\n" + 
        "  //\n" + 
        "}";
    
    CompilationUnit cu = ParserToolkit.createInlineJavaParser(code);
    
    ASTPrinter.print(cu);
  }
}
