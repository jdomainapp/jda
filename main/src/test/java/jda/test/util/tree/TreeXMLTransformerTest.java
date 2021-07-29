package jda.test.util.tree;

import static org.junit.Assert.*;

import org.junit.Test;

import jda.modules.common.types.tree.DefaultTreeXMLTransformer;
import jda.modules.common.types.tree.Node;
import jda.modules.common.types.tree.Tree;
import jda.modules.common.types.tree.TreeXMLTransformer;

public class TreeXMLTransformerTest {
  
  @Test
  public void testSpecial1() throws Exception {
    System.out.println("testSpecial1()");
    
    // single node tree
    Node r = new Node<Integer>(1);
    Tree tree = new Tree(r);
    
    doTransform(tree);
  }
  
  @Test
  public void testFull() throws Exception {
    System.out.println("testFull()");
    
    Node r = new Node<Integer>(1);
    Tree tree = new Tree(r);
    
    Node n2 = new Node<Integer>(2);
    tree.addNode(n2, r);
    
    Node n3 = new Node<Integer>(3);
    Node n4 = new Node<Integer>(4);
    Node n5 = new Node<Integer>(5);
    Node n6 = new Node<Integer>(6);
    Node n7 = new Node<Integer>(7);
    Node n8 = new Node<Integer>(8);
    Node n9 = new Node<Integer>(9);
    Node n10 = new Node<Integer>(10);
    Node n11 = new Node<Integer>(11);
    Node n12 = new Node<Integer>(12);
    
    Object tag;
    
    tag = "a,b,c";
    tree.addNode(n3,new Node<Integer>(2), tag);
    tag = "x,y,z";
    tree.addNode(n6,n2, tag);

    tag = "t,u";
    
    tree.addNode(n10,r, tag);
    
    tree.addNode(n11,n10);
    
    tag= "k,v";
    tree.addNode(n12,n10,tag);

    tree.addNode(n4,n3);
    tree.addNode(n5,n3);

    tree.addNode(n7,n6);
    
    tree.addNode(n8,n7);
    tree.addNode(n9,n7);

    doTransform(tree);
  }

  private void doTransform(Tree tree) {
    System.out.printf("Tree: %n%s%n%n", tree);
    
    TreeXMLTransformer transformer = new DefaultTreeXMLTransformer();

    try {
      
      String charSetName = null; //"utf-8";
      // tree to XML
      String treeXML = transformer.treeToXML(tree, charSetName);      
      System.out.printf("treeToXML(): %n%s%n%n", treeXML);
      
      // tree from XML
      Tree t1 = transformer.treeFromXML(treeXML, charSetName);
      System.out.printf("treeFromXML(): %b%n%s%n", t1.repOK(), t1);
      
    } catch (Exception ex) {
      ex.printStackTrace();
    }    
  }
}
