package jda.modules.mbsl.model.graph.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;
import jda.modules.mbsl.model.appmodules.meta.MAct;
import jda.modules.mbsl.model.graph.Node;
import jda.modules.mbsl.model.graph.NodeType;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;

/**
 * @overview 
 *  Configures the state of a {@link Node}
 *  
 * @author Duc Minh Le (ducmle)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=java.lang.annotation.ElementType.FIELD)
@Repeatable(ANodes.class)
@Documented
public @interface ANode {

//  /**
//   * Name of the activity (must be the same for all nodes in the same activity}
//   */
//  String act();

  /**
   *  The domain class of the module to which this node is associated
   */
  Class refCls();

  /**
   * The type of module service that will be used to perform {@link #actSeq()} of this node.
   * It is only applicable to non-control nodes, and is typically either {@link ControllerBasic} or {@link DataController}.
   * 
   * <br>Default: {@link Null} (i.e. not specified)
   */
  Class serviceCls() default Null.class;
  
  /**
   * this node's label<br>
   * Default: {@link CommonConstants#NullString}
   */
  String label() default CommonConstants.NullString;

  /**
   * this node's type<br>
   * Default: {@link NodeType#Action}
   */
  NodeType nodeType() default NodeType.Action;

  /**
   * this node's sequence of {@link MAct}<br>
   * Default: []
   */
  MAct[] actSeq() default {};

  /**
   * @effects 
   *  the sequence of out-classes, i.e. those mapped to the 
   *  target nodes of a sequence of activity edges originating from this node <br>
   *  Default: []  
   */
  Class[] outClses() default {};

  /**
   *  Whether or not this is the initial class<br>
   *  Default: false
   */
  boolean init() default false;

//  /**
//   * (Optional) Whether or not this node performs the Transformation behaviour of the outgoing edge on behalf of this edge.  
//   * <br>Default: false
//   */
//  boolean transformResult() default false;

}
