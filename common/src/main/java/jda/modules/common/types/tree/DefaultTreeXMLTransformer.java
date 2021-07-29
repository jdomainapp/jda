package jda.modules.common.types.tree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview
 *  A default implementation of {@link TreeXMLTransformer} interface which 
 *  transforms a {@link Tree} into an XML string as shown in the following example
 *  
 * @example
 * <pre>
 *  Tree = 
 *     1
 *     |-2
 *     |--6 (tag: x,y,z)
 *     |---7
 *     |----8
 *     |----9
 *     |-10 (tag: t,u)
 *     |--11
 *     |--12 (tag: k,v)
 *     
 *  XML =
 *  <?xml version="1.0" encoding="UTF-8"?>
 *    <tree>
 *      <nodes>
 *        <node root="true" value="1"/>
 *        <node value="2"/>
 *        <node value="6"/>
 *        <node value="10"/>
 *        <node value="11"/>
 *        <node value="12"/>
 *        <node value="7"/>
 *        <node value="8"/>
 *        <node value="9"/>
 *      </nodes>
 *      <edges>
 *        <edge child="2" parent="1"/>
 *        <edge child="6" parent="2" tag="x,y,z"/>
 *        <edge child="10" parent="1" tag="t,u"/>
 *        <edge child="11" parent="10"/>
 *        <edge child="12" parent="10" tag="k,v"/>
 *        <edge child="7" parent="6"/>
 *        <edge child="8" parent="7"/>
 *        <edge child="9" parent="7"/>
 *      </edges>
 *    </tree> 
 * </pre>
 *  
 * @author dmle
 *
 */
public class DefaultTreeXMLTransformer implements TreeXMLTransformer {
  
  @Override
  public String treeToXML(Tree tree, String charSetName) throws NotPossibleException {
    try {
      // create an empty XML document
      DocumentBuilder db = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder();
  
      Document d = db.newDocument();
      d.setXmlStandalone(true);
      
      // create the document (root) element
      Element docE = d.createElement("tree");
      d.appendChild(docE);
  
      Element nodeE, edgeE;
      Node node; Edge edge;
      
      // write nodes set as an XML sub-tree named "nodes"
      Element nodes = d.createElement("nodes");
      docE.appendChild(nodes);
  
      Iterator<Node> nodeIt = tree.getNodes();
      while (nodeIt.hasNext()) {
        node = nodeIt.next();
        
        // node element
        nodeE = d.createElement("node");
        nodeE.setAttribute("value", node.getValueAsString());
        if (node.hasTag())
          nodeE.setAttribute("tag", node.getTagAsString());
        if (tree.isRoot(node)) {
          // root node
          nodeE.setAttribute("root", "true");
        }
        nodes.appendChild(nodeE);
      }
  
      // write edges set to an XML sub-tree named "edges"
      Iterator<Edge> edgeIt = tree.getEdges();
      if (edgeIt != null) {
        Element edges = d.createElement("edges");
        docE.appendChild(edges);
    
        String tag;
        while (edgeIt.hasNext()) {
          edge = edgeIt.next();
          
          // edge element
          edgeE = d.createElement("edge");
          edgeE.setAttribute("parent", edge.getNode1().getValueAsString());
          edgeE.setAttribute("child", edge.getNode2().getValueAsString());
//          tag = edge.getTagAsString();
//          if (tag != null)
//            edgeE.setAttribute("tag", tag);
          if (edge.hasTag()) 
            edgeE.setAttribute("tag", edge.getTagAsString());
          
          edges.appendChild(edgeE);
        }
      }
      
      // write document to a string
      ByteArrayOutputStream outStream;
      Transformer trans = TransformerFactory.newInstance().newTransformer();
      
      outStream = new ByteArrayOutputStream();
      trans.transform(new DOMSource(d), new StreamResult(outStream));
  
      String treeXML = (charSetName != null) ? outStream.toString(charSetName) : outStream.toString();
      return treeXML;
      
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONVERT_TREE_TO_XML, e);
    }
  }
  
  @Override
  public Tree treeFromXML(String treeXml, String charSetName) throws NotPossibleException {
    Document d = null;
    try {
      // create an empty XML document
      DocumentBuilder db = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder();
  
      ByteArrayInputStream inputStream = new ByteArrayInputStream(
          (charSetName != null) ? treeXml.getBytes(charSetName) : treeXml.getBytes());
      
      d = db.parse(inputStream);
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONVERT_TREE_FROM_XML, e);
    }
    
    Element nodeE, edgeE;
    Node node; Edge edge;
    
    Tree tree = null;
    
    // read nodes set and convert them to tree nodes
    Element nodes = (Element) d.getElementsByTagName("nodes").item(0);
    NodeList nodeElements = nodes.getChildNodes();
    
    if (nodeElements == null)
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONVERT_TREE_FROM_XML, new Object[] {"No nodes found"});
    
    int elsCount = nodeElements.getLength();
    String nodeVal, nodeTag;
    String isRootStr;
    boolean isRoot;
    Map<String,Node> nodeMap = new HashMap<>();
    
    for (int i = 0; i < elsCount; i++) {
      nodeE = (Element) nodeElements.item(i);
      
      // node element
      nodeVal = nodeE.getAttribute("value");
      nodeTag = nodeE.getAttribute("tag");
      if (nodeTag.length()==0)
        nodeTag = null;
      isRootStr = nodeE.getAttribute("root");
      isRoot = (isRootStr.length() > 0) ? Boolean.parseBoolean(isRootStr) : false;
      
      //TODO: convert nodeVal, nodeTag to original value type (requires dataType attribute)
      node = new Node(nodeVal, nodeTag);
      
      if (isRoot) {
        // root node
        tree = new Tree(node);
      } 
      
      nodeMap.put(nodeVal, node);
    }

    // read edges set and convert them to tree edges
    Element edges = (Element) d.getElementsByTagName("edges").item(0);
    if (edges != null) {
      NodeList edgeElements = edges.getChildNodes();
    
      elsCount = edgeElements.getLength();
      String parent, child, tag;
      Node parentN, childN;
      for (int i = 0; i < elsCount; i++) {
        edgeE = (Element) edgeElements.item(i);
        
        // edge element
        parent = edgeE.getAttribute("parent");
        child = edgeE.getAttribute("child");
        tag = edgeE.getAttribute("tag");
        
        parentN = nodeMap.get(parent);
        childN = nodeMap.get(child);
        
        if (parentN == null || childN == null) 
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONVERT_TREE_FROM_XML, new Object[] {"Parent or child not found"});
        
        tree.addNode(childN, parentN, tag.isEmpty() ? null : tag);
      }
    }
    
    return tree;
  }
}
