package jda.modules.patterndom.transform.tpc;

import static jda.modules.dcsl.parser.ParserConstants.TypeSerializable;

import java.io.Serializable;
import java.util.List;

import javax.json.JsonObject;

import com.github.javaparser.ast.Modifier;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.parser.jtransform.JTransform;
import jda.modules.dcsl.parser.jtransform.TPC;
import jda.modules.dcsl.parser.jtransform.assets.ParamName;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.util.DClassTk;

/**
 * @overview 
 *  Transformation procedure that creates the domain model for the 
 *  DDD pattern ENTITES.
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class TPCEntities extends TPC {
  public static enum Params implements ParamName {
    Entity,
    id
  };

  private boolean wildCart;
  private List<String> entities;
  
  @Override
  protected void init(Dom dom, JsonObject config) {
    // load all the entity classes (if exist)
    String fqn = config.getString(Params.Entity.name());
    
    if (fqn.endsWith("*")) {
      wildCart = true;
      // a package
      String pkg = fqn.substring(0, fqn.indexOf("*")-1);
      entities = dom.loadClassesInPackage(pkg);
    } else {
      // a class
      // load c1, c2 into dom
      String cName = DClassTk.getClassNameFromFqn(fqn);

      dom.loadClass(cName, fqn);
    }
    
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
    
    JTransform transf = getTransf(dom);
    transf.begin();
    if (entities != null) {
      entities.forEach(fqn -> {
        runForEntity(transf, fqn, config);
      });
    } else {
      // single entity
      String fqn = config.getString(Params.Entity.name());
      runForEntity(transf, fqn, config);
    }
    
    
    transf.end();
    
    dom.save();
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  private void runForEntity(JTransform transf, String fqn, JsonObject config) {
    String id = config.getString(Params.id.name());
    
    transf
    .addDClassIfNotExists(fqn, Modifier.PUBLIC)
    .addDField(fqn, id, TypeSerializable, Modifier.PRIVATE, "id: true, optional: false")
    .addDGetterMethod(fqn, id, TypeSerializable)
    // add imports
    .addImport(fqn, Serializable.class, 
        DClass.class, DAttr.class, DOpt.class, AttrRef.class, DAttr.Type.class);    
  }

  @Override
  public String getPatternName() {
    return "Entities";
  }
}
