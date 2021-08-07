package jda.modules.patterndom.transform.tpc;

import java.util.Collection;

import javax.json.JsonObject;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.type.Type;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.jtransform.JTransform;
import jda.modules.dcsl.parser.jtransform.TPC;
import jda.modules.dcsl.parser.jtransform.assets.ParamName;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.util.DClassTk;

/**
 * @overview 
 *  Transformation procedure that creates the domain model for the 
 *  DDD pattern MANY-MANY-NORMALISER.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class TPCMNormaliser extends TPC {
  public static enum Params implements ParamName {
    C1,
    C2,
    CNorm,
    symmetrical
  };
  
  @Override
  protected void init(Dom dom, JsonObject config) {
    // load c1, c2 into dom
    String c1Fqn = config.getString(Params.C1.name()),
          c1Name = DClassTk.getClassNameFromFqn(c1Fqn),
          c2Fqn = config.getString(Params.C2.name()),
          c2Name = DClassTk.getClassNameFromFqn(c2Fqn);

    dom.loadClass(c1Name, c1Fqn);
    dom.loadClass(c2Name, c2Fqn);
  }
  
  /**
   * @modifies this
   * @effects 
   *  executes transformation procedure that creates the domain model for the 
   *  DDD pattern MANY-MANY-NORMALISER.
   *  
   *  <p>Record the intermediate transformation result in this.
   */
  @Override
  public void run(Dom dom, JsonObject config) 
      throws NotFoundException, NotPossibleException {
    
    String c1Fqn = config.getString(Params.C1.name());
    String c2Fqn = config.getString(Params.C2.name());
    String cNormFqn = config.getString(Params.CNorm.name());
    Boolean sym = config.getBoolean(Params.symmetrical.name(), 
        Boolean.FALSE);
    
    String c1Name = DClassTk.getClassNameFromFqn(c1Fqn);
    String c2Name = DClassTk.getClassNameFromFqn(c2Fqn);
    String cNormName = DClassTk.getClassNameFromFqn(cNormFqn);
    
    // attribute names derived from class names
    String a1 = DClassTk.getAttribNameFromType(c1Name),
           a2 = DClassTk.getAttribNameFromType(c2Name),
           aNorm = DClassTk.getAttribNameFromType(cNormName);
    aNorm += "s"; // plural because collection-typed
    
    // Types derived from the input classes
    Type t1 = ParserToolkit.createClassOrInterfaceType(c1Name),
         t2 = ParserToolkit.createClassOrInterfaceType(c2Name),
         t1Col = ParserToolkit.createGenericType(Collection.class, c1Name),
         t2Col = ParserToolkit.createGenericType(Collection.class, c2Name),
         tNorm = ParserToolkit.createClassOrInterfaceType(cNormName),
         tNormCol = ParserToolkit.createGenericType(Collection.class, cNormName)
         ;
    
    Class c1, c2;
    try {
      c1 = Class.forName(c1Fqn);
      c2 = Class.forName(c2Fqn);
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, 
          new Object[] {""}, e);
    }
    
    JTransform transf = getTransf(dom);
    transf.begin();
    // 1. Transform CNorm (C3)
    transf.addDClassIfNotExists(cNormFqn, Modifier.PUBLIC)
    .addDField(cNormFqn, a1, t1, Modifier.PRIVATE, "optional: false")
      .addAssocOneMany(cNormFqn, a1, c1, AssocEndType.Many)
    .addDField(cNormFqn, a2, t2, Modifier.PRIVATE,"optional: false")
      .addAssocOneMany(cNormFqn, a2, c2, AssocEndType.Many)
    // add imports
    .addEssentialImport(cNormFqn, 
        c1, c2,
        Collection.class)
    // 2. Transform C1
    .addDField(c1Fqn, a2, t2Col, Modifier.PRIVATE, "serialisable: false")
      .addAssocManyMany(c1Fqn, a2, c2, aNorm)
    .addDField(c1Fqn, aNorm, tNormCol, Modifier.PRIVATE, "serialisable: false")
      .addAssocOneMany(c1Fqn, aNorm, cNormName, AssocEndType.One)
    .addImport(c1Fqn, cNormFqn)
    .addEssentialImport(c1Fqn, 
          c2,
          Collection.class)
    
    // TODO: ?3. symmetrical
    
    ;
    transf.end();
    
    dom.save();
  }
  

  @Override
  public String getPatternName() {
    return "MNormaliser";
  }
}
