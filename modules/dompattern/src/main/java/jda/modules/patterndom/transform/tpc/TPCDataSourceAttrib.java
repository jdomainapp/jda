package jda.modules.patterndom.transform.tpc;

import java.util.Collection;

import javax.json.JsonObject;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.type.Type;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.jtransform.JTransform;
import jda.modules.dcsl.parser.jtransform.TPC;
import jda.modules.dcsl.parser.jtransform.assets.ParamName;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.query.AttribExp;
import jda.modules.dcsl.syntax.query.QueryDef;
import jda.modules.dcsl.util.DClassTk;

/**
 * @overview 
 *  Transformation procedure that creates the domain model for the 
 *  DDD pattern DATA SOURCE ATTRIBUTE.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class TPCDataSourceAttrib extends TPC {
  public static enum Params implements ParamName {
    C1,
    C2,
    attrSrc,
    attrBound,
    queryDef,
    AttrBoundFilter
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
   *  DDD pattern DATA SOURCE ATTRIBUTE.
   *  
   *  <p>Record the intermediate transformation result in this.
   */
  @Override
  public void run(Dom dom, JsonObject config) 
      throws NotFoundException, NotPossibleException {
    
    String c1Fqn = config.getString(Params.C1.name());
    String c2Fqn = config.getString(Params.C2.name());
    String c2Name = DClassTk.getClassNameFromFqn(c2Fqn);
    String attrSrc = config.getString(Params.attrSrc.name());
    String attrBound = config.getString(Params.attrBound.name());
//    String attrBoundFilter = (String) paramValMap.get(Params.AttrBoundFilter);
    JsonObject filterQueryJson = config.getJsonObject(Params.queryDef.name());
        
    Class c2;
    try {
      c2 = Class.forName(c2Fqn);
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, 
          new Object[] {c2Fqn}, e);
    }
    
    Type attrSrcType = ParserToolkit.createGenericType(Collection.class, c2Name);
    Type attrBoundType = ParserToolkit.createClassOrInterfaceType(c2Name);
    
    JTransform transf = getTransf(dom);
    transf.begin();
    transf
    .addDClassIfNotExists(c1Fqn, Modifier.PUBLIC)
    .addDField(c1Fqn, attrSrc, attrSrcType, Modifier.PRIVATE, 
      "serialisable: false, virtual: true, sourceQuery: true")
      .addFieldAno(c1Fqn, attrSrc, QueryDef.class, filterQueryJson)
    .addDField(c1Fqn, attrBound, attrBoundType, Modifier.PRIVATE, 
        "sourceAttribute: " + attrSrc)
    .addDGetterMethod(c1Fqn, attrSrc, attrSrcType)
    .addDSetterMethod(c1Fqn, attrSrc, attrSrcType)
    .addDGetterMethod(c1Fqn, attrBound, attrBoundType)
    .addDSetterMethod(c1Fqn, attrBound, attrBoundType)
    // add imports
    .addImport(c1Fqn, 
        c2,
        Collection.class, 
        DAttr.class, DOpt.class, AttrRef.class, DAttr.Type.class,
        QueryDef.class, AttribExp.class, Op.class);
    
    transf.end();
    
    dom.save();
  }
  

  @Override
  public String getPatternName() {
    return "DataSourceAttrib";
  }
}
