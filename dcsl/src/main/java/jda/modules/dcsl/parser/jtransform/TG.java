package jda.modules.dcsl.parser.jtransform;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.parser.jtransform.assets.ParamName;

/**
 * @overview 
 *  A transformation program that performs a set of transformation procedures.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4
 */
public class TG {

  public static enum Params implements ParamName {
    pattern, transfProc
  }
  
  private JsonArray patternConfig;
  private String name; 
  
  public TG(String name, String patternsConfigPath) {
    this.name = name;
    patternConfig = ToolkitIO.readJSonFile(JsonArray.class, patternsConfigPath);
  }
  
  /**
   * @modifies dom
   * @effects 
   *  Transform <code>dom</code> using the transformation procedures specified in <code>config</code>.
   */
  public void exec(Dom dom, JsonArray config)
      throws NotFoundException, NotPossibleException {
    // execute each transformation procedure specified in config
    config.forEach(val -> {
      JsonObject pcfg = (JsonObject) val;
      
      TP transfProc = getTransformProcInstance(
          pcfg.getString(Params.pattern.name()));
      transfProc.exec(dom, pcfg);
    });
  }

  /**
   * @effects 
   *  Create and return a {@link TP} instance defined for the pattern named <code>patternName</code>.
   *  Throws NotPossibleException if failed to create.
   */
  public TP getTransformProcInstance(String patternName) throws NotPossibleException {
    // search in there
    Iterator<JsonValue> it = patternConfig.iterator();
    while (it.hasNext()) {
      JsonObject obj = (JsonObject) it.next();
      if (obj.getString(Params.pattern.name()).equals(patternName)) {
        String transfProcCls = obj.getString(Params.transfProc.name());
        try {
          return (TP) Class.forName(transfProcCls).getConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException
            | IllegalArgumentException | InvocationTargetException
            | SecurityException | ClassNotFoundException e) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT,
              e, new Object[] {transfProcCls, ""});
        }
      }
    }
    
    throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND,
        new Object[] {patternName, ""});
    
  }
  
  @Override
  public String toString() {
    return name+":" + this.getClass().getSimpleName();
  }
}
