package org.jda.example.coursemansw.swgen;

import java.io.File;
import java.util.Scanner;

import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.sccl.conceptualmodel.SCC;
import jda.modules.swtool.SwGen;

/**
 * @overview
 *  Demonstrates the software generator component of JDA.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version
 */
public class CourseManSwGen {
  /** number of domain model copies */
  private int counter;
  private SwGen swgen;
  private JsonObject swConfigGen;
  private SCC scc;
  private String swcFqn;
  
  private static Logger logger = LoggerFactory.getLogger(CourseManSwGen.class);
  
  static enum Option {
    C, S, R, X
  };
  
  public static void main(String[] args) throws NotPossibleException {
    JsonObject swConfigGen = ToolkitIO.readJSonObjectFile(
        ToolkitIO.getResourceFilePath("swgenconfig.json"));
    
    CourseManSwGen swgen = 
        new CourseManSwGen(swConfigGen);
    
    
    String[][] options = {
        { Option.C+"", "Generate configuration (MCCs, SCC)"  },
        { Option.S+"", "Generate the software (Remember: setup a PostgreSQL db named \"coursemands\"!)"  },
        { Option.R+"", "Run the software"  },
        { Option.X+"", "Exit"  }
    };
    Option ino;
    
    do {
      System.out.println("\n====================================");
      System.out.println("What would you like to do?");
      for (String[] option : options) {
        System.out.printf("%s - %s%n", option[0], option[1]);
      }
      System.out.println("====================================");
      Scanner input = new Scanner(System.in);
      String in = input.nextLine();
      ino = Option.valueOf(in.toUpperCase());
      
      switch (ino) {
        case C:
          swgen.init();
          swgen.genSCC();        
          break;
        case S:
          swgen.genSoftware();        
          break;
        case R:
          swgen.executeSoftware();                
          break;
      }
      
    } while (ino != Option.X);
    System.out.println("~ End ~");
  }
  
  /**
   * @effects 
   *  initialises this with the number of domain model copies <code>count</code>
   */
  public CourseManSwGen(JsonObject swConfigGen) {
    this.swConfigGen = swConfigGen;
    String domain = swConfigGen.getString("domain");
    String rootPath = swConfigGen.getString("rootPath");
    if (!rootPath.endsWith(File.separator))
      rootPath += File.separator;
    
    String rootSrcPath = rootPath + swConfigGen.getString("rootSrcPath");
    String outputPath = rootPath + swConfigGen.getString("outputPath");

    swgen = new SwGen(domain,
        swConfigGen.getString("modelPkgName"),
        // root source path
        rootSrcPath,
        // outputPath
        outputPath,
        swConfigGen.getString("modulesPkg"),
        swConfigGen.getString("softwarePkg"),
        swConfigGen.getBoolean("includesSubTypes", true),
        swConfigGen.getBoolean("compileSrc", true));
  }

  /**
   * @effects 
   *  initialise resources 
   */
  public void init() throws NotPossibleException {
    swgen.init();
  }
  
  /**
   * @effects 
   *  Executes the software generator and updates {@link #scc} with the result
   */
  public void genSCC() {
    logger.info("Generating software configuration ...");
    scc = swgen.genMCCs()
    .genMainMCC()
    .genSCC()
    .getSCC();
  }
  
  public void genSoftware() {
    String sccFqn;
    if (scc == null) {
      // try to load it
      String sccName = "SCC1";
      sccFqn = swConfigGen.getString("softwarePkg") + ".config." + sccName;
    } else {
      sccFqn = scc.getFqn();
    }
    
    logger.info("Generating software class...\nConfiguration: " + sccFqn);
    swgen.genSwClass(sccFqn);
    this.swcFqn = swgen.getSwcFqn();
    logger.info("Software class: " + swcFqn);
  }
  
  public void executeSoftware() {
    if (swcFqn == null) {
      // assumes it has been created
      swcFqn = swConfigGen.getString("softwarePkg") + "." + swConfigGen.getString("domain") + "Software";
    }
    
    logger.info("Running software: " + swcFqn+ "...");
    swgen.executeSw(swcFqn);
  }
}
