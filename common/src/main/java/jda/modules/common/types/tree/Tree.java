package jda.modules.common.types.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview A <b>rooted</b> tree is a set of nodes that are connected to to each other by 
 *    edges such that one node, called the root, is connected to some nodes, 
 *    each of these nodes is connected to some other nodes that have not been 
 *    connected, and so on.  
 * 
 *    <p>The following is a <b>top-down</b> recursive design that incrementally build a 
 *    tree by adding leaf nodes.
 *    
 * @attributes 
 *   root    Node                   Node
 *   nodes   Set<Node>              List<Node>           
 *   edges   Set<Edge>   List<Edge>
 *   
 * @object A typical tree T is the tuple <r,N,E>, where 
 *   root(r), nodes(N), and edges(E).  
 *   
 *   <p>Trees are defined recursively as follows: 
 *   Basis
 *    For any node r, T = <r,{r},{}> is a tree. 
 *   Induction
 *    For all node n and tree T' and for some node p in T':
 *    n is not in T' -> 
 *      T = <T'.root, T'.nodes+{n}, T'.edges+{edge(p,n)}> is a tree 
 *   
 * @abstract_properties <pre>
 *    root is required /\ 
 *    nodes contains root /\ 
 *    for any two nodes in nodes:
 *      there is exactly one sequence of edges that connect them
 *  </pre>
 *  
 * @abstractionfunction
 *  AF(t) = <t.root,
 *    {n| n = t.nodes[i], 0<=i<t.nodes.size()}, 
 *    {e| e = edge(t.edges[i][0],t.edges[i][1]), 0<=i<t.edges.size()}>
 *  
 * @rep_invariant
 *  root != null /\ 
 *  root in nodes /\ 
 *  for all n, m in nodes. n neq m /\ 
 *  for all e, f in edges. e neq f /\ 
 *  for all n, m in nodes.
 *    (exists exactly one edge(n,m) in edges \/ 
 *      (exists exactly one node sub-set {n1,...,nk} (k >= 1) 
 *        /\ exactly one edge sub-set 
 *              edge(n,n1),edge(n1,n2),...,edge(nk,m))) 
 * @author dmle
 * @version 
 * - 5.2: made generic
 */
public class Tree<V> {
  private Node<V> root;
  private List<Node<V>> nodes;
  private List<Edge> edges;
  
  // v3.2: singleton transformer
  private static TreeXMLTransformer defaultTransformer;
  
  // constructors
  /**
   * @requires r != null
   * @effects initialise this as <r,{r},{}> 
   */
  public Tree(Node<V> r) {
    // single-node tree
    this.root = r;
    nodes = new Vector<>();
    nodes.add(r);
    edges = new Vector<>();
  }
    
  /**
   * @requires <tt>n != null /\ parent != null</tt> 
   * @effects 
   *  if <tt>parent</tt> is in <tt>nodes</tt>
   *    add <tt>n</tt> as a child of <tt>parent</tt>, i.e. <tt>edge(parent,n)</tt>
   *    with a <tt>tag = null</tt>
   *    return true
   *  else
   *    return false
   */
  public boolean addNode(Node<V> n, Node<V> parent) {
    /**v3.0: invoke addNode() with tag parameter 
    // check that parent is in nodes
    boolean found = false;
    for (Node node : nodes) {
      if (node == parent) {
        found = true;
        break;
      }
    }
    
    if (!found) {
      return false;
    }
    
    // add node
    nodes.add(n);
    
    // create an edge <parent,n>
    Edge e = new Edge(parent,n);
    edges.add(e);
    return true;
    */
    
    return addNode(n, parent, null);
  }
  
  /**
   * @requires <tt>n != null /\ parent != null</tt> 
   * @effects 
   *  if <tt>parent</tt> is in <tt>nodes</tt>
   *    add <tt>n</tt> as a child of <tt>parent</tt> with tag <tt>tag</tt>, i.e. <tt>edge(parent,n,tag)</tt>
   *    return true
   *  else
   *    return false
   * @version 3.0
   */  
  public boolean addNode(Node<V> n, Node<V> parent, Object tag) {
    // check that parent is in nodes
    boolean found = false;
    for (Node<V> node : nodes) {
      if (node == parent) {
        found = true;
        break;
      }
    }
    
    if (!found) {
      return false;
    }
    
    // add node
    nodes.add(n);
    
    // create an edge <parent,n,tag>
    Edge e = new Edge(parent,n, tag);
    
    edges.add(e);
    return true;    
  }
  
  /**
   * A relaxed version of {@link #addNode(Node, Node, Object)} that does not require parent to pre-exist in this tree. It is added if not yet exist. This is useful for cases where we want to use the Tree to hold the partial program structure (e.g. a containment tree of a software module) 
   * 
   * @requires <tt>n != null /\ parent != null</tt> 
   * @effects 
   *    add <tt>n</tt> as a child of <tt>parent</tt> with tag <tt>tag</tt>, i.e. <tt>edge(parent,n,tag)</tt>
   *    return true
   * @version 5.6
   */  
  public boolean addNodeFlex(Node<V> n, Node<V> parent, Object tag) {
    /* relaxed version: if parent does not yet exist, add it */
    // check that parent is in nodes
    boolean found = false;
    for (Node<V> node : nodes) {
      if (node == parent) {
        found = true;
        break;
      }
    }
    
    if (!found) {
      nodes.add(parent);
    }
    
    // add node
    nodes.add(n);
    
    // create an edge <parent,n,tag>
    Edge e = new Edge(parent,n, tag);
    
    edges.add(e);
    
    return true;
  }
  
  /**
   * @effects 
   *  return a <tt>Node</tt> object in this, which is the parent of <tt>n</tt>, 
   *  or <tt>null</tt> if <tt>n</tt> is the root node.
   */
  public Node<V> getParent(Node<V> n) {
    if (n == root) {
      return null;
    }
    
    for (Edge e : edges) {
      if (e.getNode2() == n) {
        return e.getNode1();
      }
    }
    
    // should not happen
    return null;
  }
  
  /**
   * @effects 
   *  if <tt>n</tt> is not a leaf then return a <tt>List</tt> of 
   *    children of <tt>n</tt>
   *  else
   *    return null  
   */
  public List<Node<V>> getChildren(Node<V> n) {
    List<Node<V>> children = new ArrayList<>();
    for (Edge e : edges) {
      if (e.getNode1() == n) {
        children.add(e.getNode2());
      }
    }    
    
    return (!children.isEmpty()) ? children : null;
  }
  
  /**
   * @effects
   *  if <tt>n</tt> is a leaf node
   *    return true
   *  else
   *    return false
   */
  public boolean isLeaf(Node<V> n) {
    return (getChildren(n) == null);
  }
  
  /**
   * @effects
   *  return the root of this
   */
  public Node<V> getRoot() {
    return root;
  }
  
  /**
   * @effects
   *  if <tt>n</tt> is root of this
   *    return true
   *  else
   *    return false
   */
  public boolean isRoot(Node<V> n) {
    return (n == root);
  }
  
  /**
   * @effects
   *  return <tt>Iterator</tt> object of the nodes in this
   */
  public Iterator<Node<V>> getNodes() {
    return nodes.iterator();
  }
  
  /**
   * @effects 
   *  if this is not empty AND this is not a single-node tree
   *    return <tt>Iterator</tt> of the edges
   *  else
   *    return <tt>null</tt>
   */
  public Iterator<Edge> getEdges() {
    if (isProper()) {
      return edges.iterator();
    } else {
      return null;
    }
  }

  /**
   * @requires 
   *  parentVal != null /\ childVal != null
   *  
   * @effects 
   *  if exists in <tt>this</tt> an edge <tt>(p,c) where equals(p.value,parentVal) /\ equals(c.value,childVal)</tt> 
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *    
   * @version 3.2
   */
  public boolean hasEdgeByNodeValue(Object parentVal, Object childVal) {
    if (edges == null) {
      return false;
    }
    
    if (parentVal == null || childVal == null)
      return false;
    
    for (Edge e : edges) {
      if (e.getNode1().getValue().equals(parentVal) && 
          e.getNode2().getValue().equals(childVal)) {
        // found the edge
        return true;
      }
    }
    
    // not found
    return false;
  }

  /**
   * @effects 
   *  if this is a proper tree, i.e. this contains at least one edge
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public boolean isProper() {
    return edges != null && edges.size() > 0;
  }
  
  /**
   * @see {@link #toXMLString(TreeXMLTransformer, String)}
   */
  public String toXMLString() throws NotPossibleException {
    return toXMLString(null, null);
  }
  
  /**
   * @effects 
   *  return the XML representation of this using <tt>transformer</tt> (if specified) or 
   *    throws NotPossibleException if failed
   */
  public String toXMLString(TreeXMLTransformer transformer, String charSetName) throws NotPossibleException {
    if (transformer == null) {
      /* v3.2: use singleton
      transformer = new DefaultTreeXMLTransformer();
      */
      if (defaultTransformer == null) {
        defaultTransformer = new DefaultTreeXMLTransformer();
      }
      transformer = defaultTransformer;
    }
    
    return transformer.treeToXML(this, charSetName);
  }
  
  /**
   * @see {@link #fromXMLString(TreeXMLTransformer, String, String)}
   */
  public static Tree fromXMLString(String treeXML) throws NotPossibleException {
    return fromXMLString(null, treeXML, null);
  }
  
  /**
   * @requires 
   *  treeXML != null
   * @effects 
   *  return a {@link Tree} from its XML representation <tt>treeXML</tt> using <tt>transformer</tt> (if specified) or 
   *  throws NotPossibleException if failed
   */
  public static Tree fromXMLString(TreeXMLTransformer transformer, String treeXML, String charSetName) throws NotPossibleException {
    if (transformer == null) {
      /* v3.2: use singleton
      transformer = new DefaultTreeXMLTransformer();
      */
      if (defaultTransformer == null) {
        defaultTransformer = new DefaultTreeXMLTransformer();
      }
      transformer = defaultTransformer;
    }
    
    return transformer.treeFromXML(treeXML, charSetName);
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    toStringVertical(sb, 0, root);
//    toStringHorizontal(sb, 0, root);
    return sb.toString();
  }
  
  /**
   * @requires  
   *  return the string representation of the sub-tree rooted at n
   */
  public String toString(Node<V> n) {
    StringBuffer sb = new StringBuffer();
    toStringVertical(sb, 0, n);
    
    return sb.toString();
  }
  
  /**
   * @effects 
   *  update <tt>sb</tt> with the string representation of the sub-tree
   *  rooted at <tt>n</tt>. 
   */
  private void toStringVertical(StringBuffer sb, int level, Node<V> n) {
    // append next line
    if (level > 0) {
      sb.append("\n");
      sb.append("|");
    }
    
    for (int i = 0; i < level; i++) {
      sb.append("-");
    }
    
    sb.append(n);
    
    // support tagging
    Object tag = getEdgeTagOfChild(n);
    if (tag != null) {
      sb.append(" (tag: ").append(tag.toString()).append(")");
    }
    
    int thisLevel = level;
    for (Edge e : edges) {
      if (e.getNode1() == n) {  // append this child of n
        if (thisLevel == level)
          level++;
        toStringVertical(sb, level, e.getNode2());
      }
    }
    
  }
  
  /**
   * @requires 
   *  n is in this.nodes
   *  
   * @effects
   *  if edge e in this whose child is <tt>n</tt> satisfies: e.tag != null
   *    return e.tag
   *  else
   *    return <tt>null</tt> 
   */
  public Object getEdgeTagOfChild(Node<V> n) {
    if (n == root) {
      return null;
    }
    
    for (Edge e : edges) {
      if (e.getNode2() == n) {
        // found the parent edge
        return e.getTag();
      }
    }
    
    return null;
  }

  /**
   * @requires 
   *  node1Val != null /\ node2Val != null
   *  
   * @effects 
   *  if exists edge e of this s.t: <tt>
   *  e.parent.value.equals(node1Val) /\ 
   *  e.child.value.equals(node2Val) /\ 
   *  e.tag != null </tt>
   *    return <tt>e.tag</tt>
   *  else
   *    return <tt>null</tt>
   */
  public Object getEdgeTagByNodeValue(Object node1Val, Object node2Val) {
    if (edges == null) {
      return null;
    }
    
    if (node1Val == null || node2Val == null)
      return null;
    
    for (Edge e : edges) {
      if (e.getNode1().getValue().equals(node1Val) && 
          e.getNode2().getValue().equals(node2Val) && 
          e.getTag() != null) {
        // found the edge
        return e.getTag();
      }
    }
    
    return null;
  }
  
  /**
   * @effects 
   *  update <tt>sb</tt> with the string representation of the sub-tree
   *  rooted at <tt>n</tt>. 
   */
  private int toStringHorizontal(StringBuffer sb, int pos, Node<V> n) {
    int countN = 0;
    
    // append n at position pos
    for (int i = 0; i < pos; i++) {
      sb.append(" ");
    }
    
    sb.append(n);
    //sb.append("\n");  // next line before printing children
    
    for (Edge e : edges) {
      if (e.getNode1() == n) {  // append this child of n
        if (countN == 0) {
          sb.append("(");
        }
        int countC = toStringHorizontal(sb, countN, e.getNode2());
        // update next child position based on the 
        // number of children that this child has
        if (countC > 1)
          countN += countC-1;
        else
          countN++;
      }
    }
    
    if (countN > 0)
      sb.append(")");
    
    return countN;
  }
  
  /**
   * @effects 
   * if this satisfies abstract properties
   *  return true
   * else
   *  return false
   */
  public boolean repOK() {
    if (root == null) {
      System.err.println("Tree.repOK: root is null");
      return false;
    }
    
    // root != null /\ 
    if (nodes == null || edges == null) {
      System.err.println("Tree.repOK: nodes or edges is not initialised");    
      return false;
    }
  
    
    boolean hasRoot = false;
    for (Node n : nodes) {
      if (n == root) {
        hasRoot=true;
        break;
      }
    }
    if (!hasRoot) {
      System.err.println("Tree.repOK: tree does not contain root");    
      return false;    
    }
    // root in nodes
    
    Node n; 
    Edge eobj;
    for (int i = 0; i < nodes.size(); i++) {
      n = nodes.get(i);
      for (int j = i+1; j < nodes.size(); j++) {
        if (n == nodes.get(j)) {
          System.err.println("Tree.repOK: duplicate node: " + n);
          return false;
        }
      }
    }
    
    //  for all n, m in nodes. n neq m
    
    for (int i = 0; i < edges.size(); i++) {
      eobj = edges.get(i);
      for (int j = i+1; j < edges.size(); j++) {
        if (eobj == edges.get(j)) {
          System.err.println("Tree.repOK: duplicate edge: " + eobj);
          return false;
        }
      }
    }
    //  for all e, f in edges. e neq f
    
    // check: every non-root node has exactly one parent
    Node parent;
    for (Node o : nodes) {
      parent = null;
      if (o != root) {
        for (Edge e : edges) {
          if (e.getNode2() == o) {
            if (parent == null) {
              parent = e.getNode1();
            } else {
              // invalid: two parents
              System.err.println("Tree.repOK: node has two parents: " + o + " -> ("+parent+","+e.getNode1()+")");    
              return false;
            }
          }
        }
        
        if (parent == null) {
          // invalid: no parents
          System.err.println("Tree.repOK: node has no parents: " + o);              
          return false;
        }
        // o has one parent
      }
    }
    
    // check: tree is connected
    // walk the tree from the root and count the number of nodes
    // check that this number is the same as cardinality of nodes
    int count = count(root);
    if (count != nodes.size()) {
      System.err.println("Tree.repOK: tree is not connected");    
      return false;
    }
    
    // this satisfies two properties:
    // (a) every non-root has a unique parent /\
    // (b) connected
    // Together these mean:
    //  for all n, m in nodes.
    //    (exists exactly one edge(n,m) in edges \/ 
    //      (exists exactly one node sub-set {n1,...,nk} (k >= 1) 
    //        /\ exactly one edge sub-set 
    //              edge(n,n1),edge(n1,n2),...,edge(nk,m))) 
    
    return true;
  }
  
  /**
   * A recursive procedure to count the number of nodes in a subtree
   * rooted at n. 
   * 
   * @effects 
   *  if n is a leaf
   *    return 1
   *  else 
   *    return the number of nodes in the sub-tree rooted at n
   */
  private int count(Node<V> n) {
    int count = 1; // includes n
    for (Edge e : edges) {
      if (e.getNode1() == n) {
        count += count(e.getNode2());
      }
    }
    
    return count;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((edges == null) ? 0 : edges.hashCode());
    result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
    result = prime * result + ((root == null) ? 0 : root.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Tree other = (Tree) obj;
    if (edges == null) {
      if (other.edges != null)
        return false;
    } else if (!edges.equals(other.edges))
      return false;
    if (nodes == null) {
      if (other.nodes != null)
        return false;
    } else if (!nodes.equals(other.nodes))
      return false;
    if (root == null) {
      if (other.root != null)
        return false;
    } else if (!root.equals(other.root))
      return false;
    return true;
  }
  
} // end Tree