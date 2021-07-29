package jda.modules.exportdoc.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jda.modules.exportdoc.controller.html.ObjectHtmlDocumentBuilder;

/**
 * @overview
 *  A tool kit for handing scripting languages used in documents. 
 *  
 * @author dmle
 *
 * @version 3.3
 */
public class ScriptingToolKit {

  /**
   * The name of the global input variable that is used by this builder to pass input data object to 
   * the scripting engine {@link ObjectHtmlDocumentBuilder#scriptEngine}. All embedded scripts evaluated by this engine that need
   * to access the input data object <b>must be defined to access it via this variable</b>. 
   */
  public static final String GlobalScriptInputVarName = "domain";
  
  /**
   * The name of the global output variable that is used by Java to obtain output result(s) from  
   * the scripting engine. All embedded scripts evaluated by this engine that need
   * to generate output <b>must use this variable</b>.  
   */
  private static final String GlobalScriptOutputVarName = "output";
  
  /**a shared global buffer between Java and the scripting contexts that are used by some methods to 
   * convieniently exchange scripting outputs */
  private static ScriptOutput<Object> outputs = new ScriptOutput<>();

  /**Start tag of the embedded Java script, whose end tag is {@link #JJS_END} */
  private static final String JJS_START = "jjs:[";
  /**End tag of the embedded Java script, whose start tag is {@link #JJS_START}*/
  private static final String JJS_END = "]:";

  /**
   * @effects 
   *  create and return a standard built-in nashorn-typed {@link ScriptEngine}.
   */
  public static ScriptEngine getNashornEngine() {
    try {
      return getNashornEngine(null);
    } catch (FileNotFoundException | ScriptException e) {
      // should not happen
      return null;
    }
  }

  /**
   * @effects 
   *  create and return a standard built-in nashorn-typed {@link ScriptEngine} that has executed the script 
   *  contained in the file whose absolute path is <tt>scriptFilePath</tt> 
   *  
   *  <p>throws FileNotFoundException if <tt>scriptFilePath</tt> is invalid, 
   *  ScriptException if an error occurs in script
   */
  public static ScriptEngine getNashornEngine(String scriptFilePath) throws FileNotFoundException, ScriptException {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    
    if (scriptFilePath != null)
      engine.eval(new FileReader(scriptFilePath));
    
    return engine;
  }
  
  /**
   * @requires 
   *  <tt>engine</tt> has executed a script that contains the specified function
   *  
   * @effects 
   *  use <tt>engine</tt> to invoke the top-level function of the script named <tt>funcName</tt> with arguments <tt>args</tt> (if specified).
   *  Return any result that is obtained.
   *  
   * <p>throws ScriptException if an error occurs during invocation of the method, 
   *    NoSuchMethodException if function with given name or matching argument types cannot be found.  
   */
  public static Object evalJjsFunction(ScriptEngine engine, String funcName,
      Object...args) throws NoSuchMethodException, ScriptException {
    Invocable invocable = (Invocable) engine;

    Object result = invocable.invokeFunction(funcName, args);
    
    return result;
  }
  
  /**
   * @requires 
   *  <tt>engine</tt> has been created suitable for the language used in <tt>jjsFrag</tt> 
   *  
   * @effects 
   *  use <tt>engine</tt> to execute the script contained in <tt>jjsFrag</tt> 
   *    
   * <p>throws ScriptException if there is an error in the script
   */
  public static void evalJjsFragSimple(ScriptEngine engine, String jjsFrag) throws ScriptException {
    evalJjsFrag(engine, null, null, jjsFrag);
  }
  
  /**
   * @requires 
   *  <tt>engine</tt> has been created suitable for the language used in <tt>jjsFrag</tt> 
   *  
   * @effects 
   *  use <tt>engine</tt> to execute the script contained in <tt>jjsFrag</tt>, passing in <tt>globalInputObject</tt> as a 
   *  global variable named <tt>globalInputVar</tt>.
   *  
   *  <p>If there are output(s) then return <b>the first of such output</b>.
   *    
   * <p>throws ScriptException if there is an error in the script
   */
  public static Object evalJjsFragSingleOutput(ScriptEngine engine, String globalInputVar, Object globalInputObject, String jjsFrag) throws ScriptException {
    if (!outputs.isEmpty()) outputs.clear();
    
    engine.put(globalInputVar, globalInputObject);
    
    evalJjsFrag(engine, GlobalScriptOutputVarName, outputs, jjsFrag);
    
    if (!outputs.isEmpty()) {
      Object out = outputs.firstValue();
      
      outputs.clear();
      
      return out;
    } else {
      return null;
    }
    
  }
  
  /**
   * @requires 
   *  <tt>engine</tt> has been created suitable for the language used in <tt>jjsFrag</tt> 
   *  
   * @effects 
   *  use <tt>engine</tt> to execute the script contained in <tt>jjsFrag</tt>, returning any output(s) 
   *  in the shared buffer <tt>outputs</tt> (whose global variable name is <tt>outputVar</tt>).
   *    
   * <p>throws ScriptException if there is an error in the script 
   */
  public static void evalJjsFrag(ScriptEngine engine, String outputVar, ScriptOutput<Object> outputs, String jjsFrag) throws ScriptException {
    if (outputs != null && engine.get(outputVar) == null) {
      engine.put(outputVar, outputs);
    }
    
    engine.eval(jjsFrag);
  }
  
  /**
   * @requires 
   *  <tt>engine</tt> has been created suitable for the language used in the specified script file 
   *  
   * @effects 
   *  use <tt>engine</tt> to execute the script contained in the file whose absolute path is <tt>scriptFragFilePath</tt>, 
   *  returning any output(s)in the shared buffer <tt>outputs</tt> (whose global variable name is <tt>outputVar</tt>).
   *  
   * <p>throws ScriptException if there is an error in the script, FileNotFoundException if the file path is invalid. 
   */
  public static void evalJjsFragFile(ScriptEngine engine, String outputVar, ScriptOutput<Object> outputs, String scriptFragFilePath) throws ScriptException, FileNotFoundException {
    if (outputs != null && engine.get(outputVar) == null) {
      engine.put(outputVar, outputs);
    }
    
    engine.eval(new FileReader(scriptFragFilePath));
  }

  /**
   * @requires 
   *  <tt>engine</tt> has been created suitable for the language used in the specified script file 
   *  
   * @effects 
   *  use <tt>engine</tt> to execute the script contained in the file whose absolute path is <tt>scriptFragFilePath</tt>, 
   *  passing in <tt>globalInputObject</tt> as a global variable named <tt>globalInputVar</tt>.
   *  
   *  <p>If there are output(s) then return <b>the first of such outputs</b>.
   *  
   * <p>throws ScriptException if there is an error in the script, FileNotFoundException if the file path is invalid. 
   */
  public static Object evalJjsFragFileSingleOutput(ScriptEngine engine, 
      String globalInputVar, 
      Object globalInputObject, String scriptFragFilePath) throws FileNotFoundException, ScriptException {
    if (!outputs.isEmpty()) outputs.clear();
      
    engine.put(globalInputVar, globalInputObject);
    
    evalJjsFragFile(engine, GlobalScriptOutputVarName, outputs, scriptFragFilePath);
    
    if (!outputs.isEmpty()) {
      Object out = outputs.firstValue();
      
      outputs.clear();
      
      return out;
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  Format-print <tt>result</tt> to the standard output. 
   */
  public static void printResult(Object result) {
    String resultFormat = "...Result (type: %s) = %s%n";
    
    String type = (result != null) ? result.getClass().getName() : null;
    
    System.out.printf(resultFormat, type, result);
  }

  /**
   * @requires 
   *  content != null /\ content.length > 0
   * @effects 
   *  if there are Java script fragments embedded in <tt>content</tt> 
   *    extract and return them in {@link Collection}
   *  else
   *    return null
   *    
   *  <p>Java script fragments start are enclosed within this pair of symbols <tt>"jjs:[", "]:"</tt>
   * @version 3.3
   */
  public static Collection<String> extractJjsFragments(StringBuffer content) {
    if (content == null || content.length() == 0)
      return null;
    
    Collection<String> jjsFrags = new ArrayList<>();
    
    int start, end;
    final int length = content.length();

    final int jjsEndLength = JJS_END.length();
    
    for (int pos = 0; pos < length; pos++) {
      start = content.indexOf(JJS_START, pos);
      if (start < 0) {
        // no more
        break;
      }
        
      end = content.indexOf(JJS_END, start);
      if (end > 0) {
        // found a frag
        jjsFrags.add(content.substring(start, end+jjsEndLength));
        
        // update position
        pos = end+jjsEndLength-1;
      } else {
        // possibly syntax error: ignore 
        break; // also means no more frags expected
      }
    }
    
    if (!jjsFrags.isEmpty())
      return jjsFrags;
    else
      return null;
  }

  /**
   * @requires 
   *  scriptFrag != null
   *  
   * @effects 
   *  if <tt>scriptFrag</tt> is enclosed by the start and end tags: {@link #JJS_START} and {@link #JJS_END}
   *    extract and return the script content between these tags
   *  else
   *    return <tt>scriptFrag</tt> 
   * @version 3.3
   */
  public static String extractJjsFragContent(String scriptFrag) {
    if (scriptFrag == null)
      return null;
    
    if (scriptFrag.startsWith(JJS_START) && scriptFrag.endsWith(JJS_END)) {
      return scriptFrag.substring(JJS_START.length(), scriptFrag.length()-JJS_END.length());
    } else {
      return scriptFrag;
    }
  }
}
