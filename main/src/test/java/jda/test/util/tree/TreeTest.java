package jda.test.util.tree;

import jda.modules.common.types.tree.Node;
import jda.modules.common.types.tree.Tree;

public class TreeTest {
  
  public static void main(String[] args) {
    Node r = new Node<Integer>(1);
    Tree t = new Tree(r);
    
    boolean aok = false;
    Node n2 = new Node<Integer>(2);
    aok = t.addNode(n2, r);
    System.out.printf("addNode(2,1): %b%n",aok);
    
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
    
    t.addNode(n3,new Node<Integer>(2));
    t.addNode(n6,n2);

    t.addNode(n10,r);
    t.addNode(n11,n10);
    
    t.addNode(n12,n10);

    t.addNode(n4,n3);
    t.addNode(n5,n3);

    t.addNode(n7,n6);
    
    t.addNode(n8,n7);
    t.addNode(n9,n7);
    
    

    boolean repOk = t.repOK();   
    if (repOk)
      System.out.println(t);

    System.out.println("valid: " + repOk);    
  }
}
