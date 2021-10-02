package jda.modules.mbsl.model.graph;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mbsl.exceptions.DomainMessage;

/**
 * @overview 
 *  Factory for {@link Node}
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class NodeFactory {

  private static final Map<NodeType, Class> nodeClsMap;
  static {
    // pre-defined node-types and class map
    nodeClsMap = new HashMap<>();
    nodeClsMap.put(NodeType.Action, Node.class);
    nodeClsMap.put(NodeType.Decision, DecisionNode.class);
    nodeClsMap.put(NodeType.Fork, ForkNode.class);
    nodeClsMap.put(NodeType.Join, JoinNode.class);
    nodeClsMap.put(NodeType.Merge, MergeNode.class);
  }

  /**
   * @effects 
   *  create and return {@link Node} whose label is <tt>label</tt>, whose referenced domain class is <tt>refCls</tt>, the type is based on <tt>type</tt>,
   *  and the service class is <tt>serviceCls</tt>.
   *  
   *  <p>throws NotFoundException if no node class is defined for <tt>type</tt>, 
   *  NotPossibleException if failed to create a node object from the node class.
   */
  public static Node createNode(NodeType type, String label, Class refCls, Class serviceCls) throws NotFoundException, NotPossibleException {
    Class<Node> nodeCls = nodeClsMap.get(type);
    
    if (nodeCls == null) {
      throw new NotFoundException(DomainMessage.ERR_NODE_CLASS_NOT_FOUND_FOR_TYPE, new Object[] {type});
    }
    
    Node node;
    try {
      node = nodeCls.getConstructor(String.class, Class.class, Class.class).newInstance(label, refCls, serviceCls);
    } catch (InstantiationException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {nodeCls, label});
    }
    
    return node;
  }

}
