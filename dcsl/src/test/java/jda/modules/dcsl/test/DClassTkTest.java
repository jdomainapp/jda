package jda.modules.dcsl.test;

/**
 * @version 1.0
 * @overview
 */
public class DClassTkTest {
  public static void main(String[] args) {
  }
}


class SomeClass<T, Integer> {
  private T value;
}

class MyClass extends SomeClass<String, Integer> {

}