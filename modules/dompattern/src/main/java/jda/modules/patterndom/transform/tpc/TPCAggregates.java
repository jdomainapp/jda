package jda.modules.patterndom.transform.tpc;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;
import javax.json.JsonValue;

import com.github.javaparser.ast.Modifier;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.parser.jtransform.JTransform;
import jda.modules.dcsl.parser.jtransform.TPC;
import jda.modules.dcsl.parser.jtransform.assets.ParamName;
import jda.modules.dcsl.util.DClassTk;
import jda.modules.patterndom.assets.aggregates.AGRoot;
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
//    client,
//    rootConstructor, 
    pattern, patternFileExt,
    root, Member1,
//    aggregates, name, 
    boundary
//    , constraints, pattern, patternPkg, patternFileExt
  };
  
  @Override
  protected void init(Dom dom, JsonObject config) {
    // load the dom
    List<String> boundary = new ArrayList<>(); 
    config.getJsonArray(Params.boundary.name())
      .forEach(val -> {
        JsonValue memberFqn = (JsonValue) val; 
        boundary.add(memberFqn.toString().replaceAll("\"", ""));
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
    String member1Cls = config.getString(Params.Member1.name());
    String member1ClsName = DClassTk.getClassNameFromFqn(member1Cls);
    
    // read the pattern definition
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
    
    // transform root class
    String memberFieldName = DClassTk.toCamelCase(member1ClsName)+"s";
    // 1. init: copy the pattern model to replace all references to MemberCls1 by the actual domain class in p-mapping
    patternDom.updateTypeNameRef(rootCls, Params.Member1.name(), member1ClsName);
    // 2. update class declaration
    transf.addDClassIfNotExists(root, Modifier.PUBLIC)
      .addClassImplement(root, AGRoot.class)
    // 3. add member field
      .addFields(root, patternDom, rootCls)
    // 4. Add/update default constructor method
      .addDefaultConstructorIfNotExists(root, patternDom, rootCls)
    /* 5. Add-copy remaining methods:
      1. `addMember`
      2. `updateOnMemberAdded`
      3. `checkInvariants`
      4. `handleEvent`
      5. `updateOnMember1Added`
      6. `updateOnMember1Changed` 
      7. `updateOnMember1Removed`
      8. `commitUpdate`
      9. `rollbackUpdate`
    */
    // method addMember
    .addMethods(root, patternDom, rootCls)
    // add imports
    .addEssentialImport(root, 
        NotFoundException.class, NotPossibleException.class, ConstraintViolationException.class
        )
    .addImport(root, patternDom, rootCls)
    ;
    
    // II. transform member classes
    // TODO: use TPCDomainEvents for each member class in boundary
    
    transf.end();
    
    dom.save();
  }

  @Override
  public String getPatternName() {
    return "Aggregates";
  }
}
