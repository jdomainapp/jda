package jda.modules.mosar.test.commandline;

import java.util.Map;

import org.junit.Test;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ProcessBuilderEnvTest {
  @Test
  public void main() {
    ProcessBuilder procBuilder = new ProcessBuilder();
    Map<String, String> env = procBuilder.environment();
    env.forEach((k, v) -> System.out.println("("+k+","+v+")"));
  }
}
