package jda.test.util.list;

import java.util.ArrayList;

import jda.modules.common.collection.list.AccessBasedBoundedList;
import jda.modules.common.collection.list.List;

public class AccessBasedBoundedListTest {
  public static void main(String[] args) {
    int bound = 5;
    int[] elements = {1,2,3,4,5};
    
    
    List<Integer> list = new AccessBasedBoundedList(bound);
    for (int e : elements) {
      list.add(e);
    }
    
    System.out.println(list);
    
    // read some elements
    int e = list.readElement(0);
    e = list.readElement(1);
    e = list.readElement(list.size()-1);
    
    System.out.println("read 0, 1, last");
    
    // add some more elements
    list.add(6);
    System.out.println("added 6");
    System.out.println(list);

    list.add(7);
    System.out.println("added 7");
    System.out.println(list);
    
    list.add(8);
    System.out.println("added 8");
    System.out.println(list);
    
    // read all
    for (int i = 0; i < list.size(); i++) {
      //
      int j = list.readElement(i);
    }
    
    System.out.println("read all");
    
    list.add(9);
    System.out.println("added 9");
    System.out.println(list);
    
    java.util.List col = new ArrayList();
    col.add(8);
    col.add(9);
    col.add(10);
    
    list.addAll(col);
    System.out.println("added " + col);
    System.out.println(list);
  }
}
