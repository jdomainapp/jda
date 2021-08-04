package jda.test.app;

import java.util.Arrays;

import jda.util.SwTk;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class AppToolkitTest {
  public static void main(String[] args) {
    try {
      SwTk.executeMain(TestApp.class, new String[] {"configure"});
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
