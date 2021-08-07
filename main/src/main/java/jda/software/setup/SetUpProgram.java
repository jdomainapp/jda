package jda.software.setup;

import jda.modules.setup.model.MasterSetUp;
import jda.mosa.software.basic.Program;

public class SetUpProgram extends Program {
  //application entry point
  public static void main(String[] args) {
    new SetUpProgram().execNonSerialised(MasterSetUp.class, args);
  }
}
