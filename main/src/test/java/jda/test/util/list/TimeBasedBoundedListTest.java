package jda.test.util.list;

import java.util.ArrayList;
import java.util.List;

import jda.modules.common.collection.list.TimeBasedBoundedList;

public class TimeBasedBoundedListTest {
  static int bound = 5;
  static int[] elements = { 1, 2, 3, 4, 5 };

  private static void test1() {
    TimeBasedBoundedList<Integer> list = new TimeBasedBoundedList(bound);
    for (int e : elements) {
      list.add(e);
    }

    System.out.println(list);
    list.printEntryStates();

    // read some elements
    int e = list.readElement(0);
    e = list.readElement(1);
    e = list.readElement(list.size() - 1);

    System.out.println("read indices: 0,1,last");
    // list.printEntryStates();

    // add some more elements
    list.add(6);
    System.out.println("added 6");
    System.out.println(list);
    // list.printEntryStates();

    list.add(7);
    System.out.println("added 7");
    System.out.println(list);
    // list.printEntryStates();

    list.add(8);
    System.out.println("added 8");

    System.out.println(list);
    // list.printEntryStates();

    // read all
    for (int i = 0; i < list.size(); i++) {
      //
      int j = list.readElement(i);
    }

    System.out.println("read all");
    // list.printEntryStates();

    list.add(9);
    System.out.println("added 9");
    System.out.println(list);

    // list.printEntryStates();

    // add collection
    List<Integer> col = new ArrayList();
    col.add(8);
    col.add(9);
    col.add(10);

    list.addAll(col);
    System.out.println("added " + col);
    System.out.println(list);
    // list.printEntryStates();
  }

  private static void test2() {
    TimeBasedBoundedList<Integer> list = new TimeBasedBoundedList(bound);
    for (int e : elements) {
      list.add(e);
    }
    
    System.out.println(list);
    list.printEntryStates();

    // insert
    list.add(0, 11);

    System.out.println(list);
    list.printEntryStates();

    list.add(1, 12);

    System.out.println(list);
    list.printEntryStates();

    list.add(4, 13);

    System.out.println(list);
    list.printEntryStates();
  }
  
  public static void main(String[] args) {
    test1();
    
    //test2();
  }
}
