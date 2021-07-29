package jda.software.javadbserver;

import jda.mosa.software.basic.Program;
import jda.software.javadbserver.setup.JavaDbServerSetUp;

public class JavaDbServerProgram extends Program {
  //application entry point
  public static void main(String[] args) {
    new JavaDbServerProgram().execNonSerialised(JavaDbServerSetUp.class, args);
  }
}
