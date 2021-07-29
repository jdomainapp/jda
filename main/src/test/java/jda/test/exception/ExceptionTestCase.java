package jda.test.exception;

import org.junit.Test;

import jda.modules.common.exceptions.NotFoundException;

public class ExceptionTestCase {

  @Test
  public void parameterisedMessage() {
    try {
      String s = "object A";
      Double d = 10.0d;
      Object o = new Object() {
        public String toString() {
          return "object B";
        }
      };
      
      // the error message can support up to 10 parameters...
      String s2 = "another object C";
      
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          "Object ''{0}'' at {1} with regards to {2}...", s,d,o, s2);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
  }
}
