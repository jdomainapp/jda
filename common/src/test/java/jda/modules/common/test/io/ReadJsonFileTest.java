package jda.modules.common.test.io;

import javax.json.JsonObject;

import org.junit.Test;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ReadJsonFileTest {
  @Test
  public void main() {
    Class c = ReadJsonFileTest.class;
    System.out.printf("Class: %s%n", c.getName());

    String jsonFile = "test.json";
    System.out.printf("Json file: %s%n", jsonFile);
    
    try {
      JsonObject json = ToolkitIO.readJSonObjectFile(c, jsonFile);
      System.out.printf("Json object: %s%n", json);
    } catch (NotPossibleException e) {
      e.printStackTrace();
    }
  }
}
