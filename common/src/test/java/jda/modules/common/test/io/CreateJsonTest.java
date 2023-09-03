package jda.modules.common.test.io;

import jda.modules.common.io.ToolkitIO;
import org.junit.Test;

import javax.json.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @version 1.0
 * @overview
 */
public class CreateJsonTest {
  @Test
  public void createJson() {
    Map<String, Object> map = new LinkedHashMap<>();
    for (int i = 1; i <= 7; i++) {
      map.put("year"+i, 2022+i);
    }

    JsonObject json = ToolkitIO.createNewJsonObject("objects", map);

    System.out.println(json);
  }
}
