package jda.modules.patterndom.transform.tpc.complex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.type.Type;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.jtransform.JTransform;
import jda.modules.dcsl.parser.jtransform.TPC;
import jda.modules.dcsl.parser.jtransform.assets.ParamName;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.patterndom.assets.aggregates.AGRoot;
import jda.modules.patterndom.assets.aggregates.complex.Aggregate;
import jda.modules.patterndom.util.PatternTk;

/**
 * @overview 
 *  Transformation procedure that creates the domain model for the 
 *  DDD pattern AGGREGATES.
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class TPCAggregates extends TPC {
  public static enum Params implements ParamName {
    client,
    root, rootConstructor, aLinkedMember,
    aggregates, name, boundary, constraints, pattern, patternPkg, patternFileExt
  };
  
  @Override
  protected void init(Dom dom, JsonObject config) {
    // load the dom
    List<String> boundary = new ArrayList<>(); 
    config.getJsonArray(Params.aggregates.name())
      .forEach(val -> {
        JsonObject ag = (JsonObject) val; 
        ag.getJsonArray(Params.boundary.name())
          .forEach(member -> boundary.add(member.toString().replaceAll("\"", "")));
      });
    
    dom.loadClasses(boundary);
  }
  
  /**
   * @modifies this
   * @effects 
   *  executes transformation procedure that creates the domain model for the 
   *  DDD pattern ENTITIES.
   *  
   *  <p>Record the intermediate transformation result in this.
   */
  @Override
  public void run(Dom dom, JsonObject config) 
      throws NotFoundException, NotPossibleException {
    // configuration
    String root = config.getString(Params.root.name());
    String ags = "ags";
    String rootConstrParamTypeStr = config.getString(Params.rootConstructor.name());
    String[] rootConstrParamTypes = rootConstrParamTypeStr.split(",");
    for (int i = 0; i < rootConstrParamTypes.length; i++) 
      rootConstrParamTypes[i] = rootConstrParamTypes[i].trim();
    
    String aLinkedMember = config.getString(Params.aLinkedMember.name());
    
    Type typeAgs = ParserToolkit.createGenericType(Map.class, 
        String.class, Aggregate.class);
    // read the pattern definition
//    String patternPkg = config.getString(Params.patternPkg.name());
    String pattern = config.getString(Params.pattern.name());
    String patternFileExt = config.getString(Params.patternFileExt.name());
//    patternPkg = patternPkg + "." + pattern;
    String patternDefRoot = PatternTk.getPatternDefRoot();
    Dom patternDom = new Dom(patternDefRoot);
    patternDom.loadClasses(ToolkitIO.getFilePath(pattern, patternFileExt), 
        pattern);
    String rootCls = pattern + ".RootCls";
    
    // transformation
    final JTransform transf = getTransf(dom);
    transf.begin();
    
    // transform aggregate classes
//    final List aggregates = new ArrayList<>();
//    final List constraints = new ArrayList<>();
//    config.getJsonArray(Params.aggregates.name())
//    .forEach(val -> {
//      JsonObject ag = (JsonObject) val; 
//      String name = ag.getString("name");
//      Class[] boundary;
//      
//      ag.getJsonArray(Params.boundary.name())
//        .forEach(member -> boundary.add(member.toString().replaceAll("\"", "")));
//      
//    });
    
    // transform root class
    transf.addClassIfNotExists(root, Modifier.PUBLIC)
      .addClassImplement(root, AGRoot.class)
      .addClassAnoIfNotExists(root, DClass.class)
    // field ags
    .addField(root, ags, typeAgs, Modifier.PRIVATE)
    // update constructor
    .addConstructorStmt(root, rootConstrParamTypes, "createAggregate();")
    // method addMember
    .addMethod(root, "addMember", patternDom, rootCls)
    // methods updateOnX 
    
    // method commit
    
    // method rollback
    
    // method createAggregate
    
    // method checkInvariants
    
    // method handleEvent
    
    // add imports
    .addEssentialImport(root, 
        NotFoundException.class, NotPossibleException.class, ConstraintViolationException.class
        )
    .addImport(root, patternDom, rootCls)
    ;
    
    transf.end();
  }
  
  @Override
  public String getPatternName() {
    return "Aggregates";
  }
}
