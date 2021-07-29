package jda.test.util;

import jda.modules.common.types.Tuple;

public class TupleTest {

  public static void main(String[] args) {
    String[] vals = {
        "hello",
        "world",
        "!"
    };
    
    String val = "hello";
    
    Tuple t = Tuple.newInstance(val);
    System.out.println(t);
    
    t = Tuple.newInstance(vals);
    System.out.println(t);
    
    // error:
    // t = Tuple.newInstance(null);
    
  }

}
