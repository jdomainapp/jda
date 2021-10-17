package jda.modules.mosar.test.commandline;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import jda.modules.common.collection.map.MapBuilder;
import jda.modules.common.io.ToolkitIO;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ExecuteCmdTest {
  public static void main(String[] args) {
    File feProjPath = new File("/home/ducmle/tmp/restfstool-fe");
    String feProjName = "courseman-fe";
    Map<String, File> cmds =
        new MapBuilder<String, File>(LinkedHashMap.class)
        .put("pwd")
        .put("ls -l")
        .put("touch test", feProjPath)
        .put("npx create-react-app "+feProjName, feProjPath)
        .getMap();
    
    cmds.forEach((cmd, dir) -> {
      System.out.println("--> Executing command: " + cmd + "; Dir: " + dir);
      boolean result = ToolkitIO.executeSysCommand(dir, cmd);
      System.out.println("...result: " + result);
    });
  }
}
