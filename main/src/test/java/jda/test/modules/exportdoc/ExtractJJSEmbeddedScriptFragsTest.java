package jda.test.modules.exportdoc;

import java.util.Collection;

import org.junit.Test;

import jda.modules.exportdoc.util.ScriptingToolKit;

/**
 * @overview
 *  Test extracting embedded Java scripts from an HTML content 
 *  
 * @author dmle
 *
 * @version 
 */
public class ExtractJJSEmbeddedScriptFragsTest {
  @Test
  public void testName() throws Exception {
    StringBuffer html = new StringBuffer(
        "<tr>" +
  "<td align=\"center\">{index}</td>" + 
  "jjs:[" +
  "  var outputs;" +
  "  if (domain.hasViewRowSpan())" +
  "    outputs.put(\"actionInfoHtml\",\"<td rowspan=\\\"{viewRowSpan}\\\"> {actionInfo} </td>\");" +
  "  else" +
  "    outputs.put(\"actionInfoHtml\",\"\");" +
  "]:" +
  "<td align=\"center\">{subjectCode}</td>" + 
  "jjs:[" +
  "  if (domain.hasViewRowSpan())" +
  "    outputs.put(\"userNameHtml\",\"<td rowspan=\\\"{viewRowSpan}\\\" align=\\\"center\\\"> {userName} </td>\");" +
  "  else" +
  "    outputs.put(\"userNameHtml\",\"\");" +
  "]:" +   
  "<td align=\"center\">{subjectActStatus}</td>" +
"</tr>");
    
    System.out.println("Html content: \n " + html);
    
    Collection<String> jjsFrags = ScriptingToolKit.extractJjsFragments(html);
   
    System.out.println();
    
    if (jjsFrags != null) {
      System.out.println("Embedded script frags:");
      for (String jjsFrag : jjsFrags) {
        System.out.println(jjsFrag);
      }
    } else {
      System.out.println("No scripts found");
    }
  }
}
