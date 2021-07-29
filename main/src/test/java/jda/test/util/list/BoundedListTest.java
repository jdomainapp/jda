package jda.test.util.list;

import java.util.ArrayList;
import java.util.List;

import jda.modules.common.collection.list.BoundedList;


public class BoundedListTest {
  public static void main(String[] args) {
    int bound = 5;
    int[] elements = {1,2,3,4,5};
    
    
    List blist = new BoundedList(bound);
    for (int e : elements) {
      blist.add(e);
    }
    
    System.out.println(blist);
    
    blist.add(6);
    System.out.println("added 6");
    System.out.println(blist);

    blist.add(7);
    System.out.println("added 7");
    System.out.println(blist);

    List col = new ArrayList();
    col.add(8);
    col.add(9);
    col.add(10);
    
    blist.addAll(col);
    System.out.println("added " + col);
    System.out.println(blist);
  }
}
