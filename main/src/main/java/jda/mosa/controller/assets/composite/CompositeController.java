package jda.mosa.controller.assets.composite;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.exceptions.TerminateOnErrorException;
import jda.modules.common.types.tree.Node;
import jda.modules.common.types.tree.Tree;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.util.MessageCode;
import jda.mosa.controller.assets.util.MethodName;

public class CompositeController<C> extends ControllerBasic {

  /**
   * a Runnable object for executing this
   */
  private Runnable runnable;

  /**
   * a Runnable object for executing a sub-tree of this
   */
  private SubTreeRunnable subTreeRunnable;

  /** the thread pool manager for running runnable tasks defined by this (e.g. {@link #runnable} and {@link #subTreeRunnable})*/
  private ExecutorService threadPool;

  /**
   * Represents the (rooted) execution tree of the {@link RunComponent}s. 
   * Each node of the tree is of type {@link Node<RunComponent>}. Each 
   * parent-child edge specifies the order of execution: parent first, then the child.
   * The children of the same parent are executed in the left-to-right order.  
   * 
   * <p>The execution proceeds similar to the operator pipe <code>|</code> in Unix. 
   * The input of a child is the output of the parent 
   */
  private Tree execTree;

  /** how to restart when an execution node fails */
  private RestartPolicy policy;

  /**
   * to indicate whether or not {@link #execTree} has stopped running. 
   * A run tree
   * is stopped when all of its nodes have stopped their executions (successfully or not).
   * @version 3.1
   * */
  private boolean stopped;
  
  protected static final boolean debug=Toolkit.getDebug(CompositeController.class);
  
  /**A policy that tells what to do when an error occurs while executing the run tree of this controller */
  protected static enum RestartPolicy {
    /** when an error occurred on a node and user wants to restart then 
     * restart all nodes in the tree*/
    All,
    /** when an error occurred on a node and user wants to restart then
     * restart just the failed node 
     */
    Node,
    /** not yet supported*/
    //CurrentSubtree, 
    /** when an error occurs, immediately stop executing the tree (without asking the user
     * for whether to restart)*/
    None
  };
  
  public CompositeController(DODMBasic schema, ApplicationModule module,
      Region moduleGui, ControllerBasic parent, Configuration config) {
    super(schema, module,moduleGui, parent, config);
    // default restart policy
    policy = RestartPolicy.Node;
    
    // thread pool manager
    threadPool = Executors.newCachedThreadPool();
    
    stopped = true; // v3.1
  }

  protected void setRestartPolicy(RestartPolicy pol) {
    policy = pol;
  }
  
  /**
   * @effects 
   *  if execution tree has not been initialised
   *    initialise execution tree as a rooted tree whose root contains <tt>comp</tt>
   *    return the root
   *  else
   *    throws NotPossibleException 
   */
  public Node<RunComponent> init(RunComponent comp) throws NotPossibleException {
    if (execTree == null) {
      Node n = new Node<RunComponent>(comp);
      execTree = new Tree(n);
      return n;
    } else {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, 
          "Cây dữ liệu chạy đã được tạo");
    }
  }
  
  /**
   * @effects
   *  if execution tree has not been initialised
   *    throws NotPossibleException
   *  else 
   *    create a new execution node for <tt>comp</tt> as a child of <tt>parent</tt> 
   *    in the tree
   *    return the new node
   */
  public Node<RunComponent> add(RunComponent comp, Node parent) throws NotPossibleException {
    if (execTree == null)
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, 
          "Cây dữ liệu chạy chưa được tạo");
    
    Node<RunComponent> n = new Node(comp);
    execTree.addNode(n, parent);
    
    return n;
  }

  /**
   * This method simply throws an exception in this class because it should not 
   * be used here. Sub-classes that need to use this method must override. 
   * 
   * @effect 
   *  initialise the run tree <tt>execTree</tt>, 
   *  throws NotPossibleException if fails to do so 
   */
  protected void initRunTree() throws NotPossibleException {
    throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_INITIALISE_RUN_TREE, 
        "Không thể khởi tạo cây dữ liệu");
  }
  
  /**
   * @effects
   *  run <tt>this</tt>, 
   *  if <tt>waitForResult = true</tt> then blocks waiting for the task to finish
   *  @version 
   *  - 2.7.2 <br>
   *  - 3.1: improved to resolve the problem of multiple concurrent executions caused by user accessing 
   *    the same module (via the tools menu) while it is still running
   */
  public void run(boolean waitForResult) {
    /*
     *  initialise the run tree (if not yet done).
     *  This is needed for cases where resources needed to create the run 
     *  nodes (e.g. data controller) that are only available at this stage
     *  
     */
    if (execTree == null) {
      initRunTree();
    }

    // v3.1: resolve the problem of multiple concurrent executions caused by user accessing 
    //    the same module (via the tools menu) while it is still running 
    if (!isStopped()) {
      // TODO: may improve the decision here (e.g. to queue?)
      // for now: concurrent executions are discarded
      
      // show gui (if it has one and has not already been shown) and return
      if (hasGUI()) showGUI();
      return; 
    }
    
    if (runnable == null) {
      runnable = new Runnable() {
        public void run() {
          CompositeController.this.runTree();
        }
      };
    }

    // run task depending on waitForResult
    if (!waitForResult) {
      // run it and return immediately
      threadPool.execute(runnable);
    } else {
      // run and block waiting for it to finish
      try {
        threadPool.submit(runnable).get();
      } catch (Exception e) {
        // should not happen
        //TODO: log it?
        e.printStackTrace();
      }
    }
  }
  
  // v2.7.2: invoke run(false)
  @Override
  public void run() {
    //TODO: do we need preRun() and postRun() here? 
    run(false);
  }

  /**
   * @effects runs each <code>Component</code> in <code>execRun</code> in order
   * 
   * @version 
   * - 3.1: changed to private and keep track of when the tree is stopped
   */
  private void runTree() {
    // run the tree
    
    if (debug)
      System.out.printf("%s.runTree:%n", this);
    
    // v3.1
    if (stopped) stopped = false;
    
    try {
      Node<RunComponent> n = execTree.getRoot();
      List<Node> children;
      Node<RunComponent> child = null;
      // TODO: generalise this
      // for now assume the following tree structure:
      // - tree extends downward to the next level from the last children of a node of the current level 
      // - result is only passed from the execution of one node to the next (never transitively)
      // - the parent's result is used as input for the execution of all children nodes
      //   (the output of the last children is used as input for the next level)
      Object result = runNode(n, null);
      while (n != null && !execTree.isLeaf(n)) {
        // run the children 
        children = execTree.getChildren(n);
        Object childResult=null;
        if (children != null) {
          Iterator<Node> cit = children.iterator();
          while (cit.hasNext()) {
            child = cit.next();
            childResult=runNode(child, result);
          }
        }

        // continue to process the next level, i.e. sub-tree rooted at the last child (if any) 
        // of the current level
        n = child;
        result = childResult;
      }
      
      // v3.1: stops execution
      stopped = true;
    } catch (TerminateOnErrorException e) {
      // an error happend but the user wanted to quit (regardless of the policy)
      // terminate here
      
      // v2.7.3: add this to decide what to do at termination (
      // e.g. it seems in some cases that the tree nodes need to be reinitialised ready for the next run)
      onTerminateRunOnError();
      
      // v3.1: stops execution
      stopped = true;
      
      return;
    } catch (Exception e) {
      // check restart policy
      if (policy == RestartPolicy.All) {
        // restart 
        restart();
      } else {
        // v3.1: stops execution
        stopped = true;
      }
    }
  }

  /**
   * <b>Note</b>: Some run trees contain a listener node (e.g. to wait for input from the user)
   * and may even repeatedly run this node. Such trees will never stop unless the user stops the program.
   * 
   * @effects 
   *  if {@link #execTree} has not stopped running
   *    return <tt>false</tt>
   *  else
   *    return </tt>true</tt>
   * @version 3.1
   */
  private boolean isStopped() {
    return stopped;
  }

  /**
   * Use this method (instead of {@link #runASubTree(domainapp.basics.core.ControllerBasic.MethodName, domainapp.basics.core.ControllerBasic.MethodName)}) 
   * if nodes in the run tree are not uniquely identified by their method names (e.g. there may be several nodes
   * invoking the methods of the same name).
   * 
   * @requires 
   *  the execution tree of this has been initialised /\ 
   *  startNode != null /\ stopNode != null
   * @effects
   *  restarts this from the <tt>Node startNode</tt> and ends
   *  at the <tt>Node stopNode</tt>. 
   *  
   *  <p>Throws NotFoundException if neither of the Nodes is found.
   */
  protected void runASubTree(Node<RunComponent> startNode, Node<RunComponent> stopNode) throws NotFoundException {
    // reset the sub-tree
    // TODO: uncomment the following if the nodes in this sub-tree have not be reset
    // by a previous task
    // resetSubTree(startNode, stopNode);
    
    // run it on a separate thread
    if (subTreeRunnable == null) {
      subTreeRunnable = new SubTreeRunnable(startNode, stopNode);
    } else {
      subTreeRunnable.setRoot(startNode);
      subTreeRunnable.setStopNode(stopNode);
    }
    
    threadPool.execute(subTreeRunnable);// new Thread(subTreeRunnable).start();
  }
  
  /**
   * @requires 
   *  the execution tree of this has been initialised 
   * @effects
   *  restarts this from the <tt>Node</tt> whose <tt>RunComponent</tt>
   *  corresponds to the method named <tt>startMethod</tt> and ends
   *  at the <tt>Node</tt> corresponding to the method named <tt>stopMethod</tt> 
   *  
   *  <p>Throws NotFoundException if neither of the Nodes is found.
   */
  protected void runASubTree(MethodName startMethod, MethodName stopMethod) throws NotFoundException {
    if (debug)
      System.out.println(this + ".runSubTree:\n  from method: " + startMethod.name() + "\n to method: " + stopMethod.name());

    Node<RunComponent> n, startNode = null, stopNode = null;
    RunComponent comp;
    
    // search for theNode
    Iterator<Node> nodes = execTree.getNodes();
    String nodeMethodName;
    while (nodes.hasNext()) {
      n = nodes.next();
      comp = n.getValue();
      nodeMethodName = comp.getMethodName();
      if (nodeMethodName != null) {
        // assumes startMethod and stopMethod are different
        if (nodeMethodName.equals(startMethod.name())) {
          // found the start node
          startNode = n;
        } else if (nodeMethodName.equals(stopMethod.name())) {
          // found the stop node
          stopNode = n;
        }
        
        if (startNode != null && stopNode != null)
          break;
      }
    }
    
    if (startNode == null || stopNode == null)
      throw new NotFoundException(NotFoundException.Code.EXECUTION_NODE_NOT_FOUND,
          "Không tìm thấy nút chạy chương trình {0}", 
          (stopNode == null) ? startMethod.name() : stopMethod.name());
    
    // v2.7.2: moved to separate method
//    // reset the sub-tree
//    // TODO: uncomment the following if the nodes in this sub-tree have not be reset
//    // by a previous task
//    // resetSubTree(startNode, stopNode);
//    
//    // run it on a separate thread
//    if (subTreeRunnable == null) {
//      subTreeRunnable = new SubTreeRunnable(startNode, stopNode);
//    } else {
//      subTreeRunnable.setRoot(startNode);
//      subTreeRunnable.setStopNode(stopNode);
//    }
//    
//    threadPool.execute(subTreeRunnable);// new Thread(subTreeRunnable).start();

    runASubTree(startNode, stopNode);
  }
  
  /**
   * @overview
   *  A sub-type of <tt>Runnable</tt> that enables the execution of a sub-tree of 
   *  the execution tree rooted at a specified node.
   *  
   * @author dmle
   */
  private class SubTreeRunnable implements Runnable {
    private Node<RunComponent> root;
    private Node<RunComponent> stopNode;
    
    private boolean resetAfter = true;
    
    public SubTreeRunnable(Node<RunComponent> root, Node<RunComponent> stopNode) {
      this.root = root;
      this.stopNode = stopNode;
    }
    
    public void setRoot(Node<RunComponent> root) {
      this.root = root;
    }

    public void setStopNode(Node<RunComponent> stopNode) {
      this.stopNode = stopNode;
    }

    @Override
    public void run() {
      if (root != null && stopNode != null) {
        runSubTree(root, stopNode);
        
        // reset after run
        if (resetAfter)
          resetSubTree(root, stopNode);
      }
    }
  }
  
  /**
   * This differs from {@link #runTree()} in that it runs the execution tree
   * on starting from the specified node (as opposed to the whole tree starting from the root). 
   * 
   * @effects 
   *  runs the execution tree starting from node <tt>startNode</tt> and ends
   *  at the node <tt>stopNode</tt>
   *  
   * @version 
   * - 2.6.4.a <br>
   * - 3.1: TODO if sub tree contains listener nodes the must keep track of when these nodes are stopped 
   *      (so that concurrent executions of these nodes will be discarded)
   */
  private void runSubTree(final Node<RunComponent> startNode, final Node<RunComponent> stopNode) {
    // run a sub tree
    try {
      // TODO: generalise this
      // for now assume:
      // - tree extends downward to the next level from the last children of a node of the current level 
      // - result is only passed from the execution of one node to the next (never transitively)
      // - the parent's result is used as input for the execution of all children nodes
      //   (the output of the last children is used as input for the next level)
      Node n = startNode;
      Object result = runNode(n, null);
      
      List<Node> children;
      Node<RunComponent> child = null;
      Object childResult;
      do {
        // process the children (if any) 
        children = execTree.getChildren(n);
        childResult=null;
        if (children != null) {
          Iterator<Node> cit = children.iterator();
          do { //while (cit.hasNext()) {
            child = cit.next();
            childResult=runNode(child, result); // parent result is used as input for all children
          } while (cit.hasNext() && child != stopNode);
          
          if (child != stopNode) {
            // continue to process the next level (i.e. sub-tree rooted at the last children (if any) 
            // of the current level
            n = child;
            result = childResult;
          }
        }
      } while (child != stopNode); // stopped when hits the stop node

    } catch (TerminateOnErrorException e) {
      // an error happend but the user wanted to quit (regardless of the policy)
      // terminate here
      
      // v2.7.3: add this to decide what to do at termination (
      // e.g. it seems in some cases that the tree nodes need to be reinitialised ready for the next run)
      onTerminateRunSubTreeOnError();
      
      return;
    } catch (Exception e) {
      // check restart policy
      if (policy == RestartPolicy.All) {
        // restart from the specified node
        runSubTree(startNode, stopNode);
      }
    }
  }
  
  /**
   * @effects
   *  Execute the <tt>RunComponent</tt> in node <tt>n</tt> and return the result.
   *  <p>Throws TerminateOnErrorException if an error occured during execution of <tt>n</tt> but the user
   *  does not want to retry; Exception if other exception conditions occured
   *  
   */
  private Object runNode(Node<RunComponent> n, Object input) throws TerminateOnErrorException, Exception {
    RunComponent comp = n.getValue();
    // run if this node is not a single-run or has not been run
    if (!comp.getSingleRun() || !comp.isExecuted()) {
      final ControllerBasic mainCtl = getMainController();
      
      if (debug)
        System.out.println("-*- Running " + comp);
      
      boolean succeeds=false;
      do {
        try {
          comp.run(input);
          // wait for execution to complete (esp. for asynchronous call)
          if (debug)
            System.out.println("................");
          while (!comp.isCompleted()) {
            /* v3.1: 
             if (debug)
                System.out.print(".");
              */
            sleep(100);
          }
          
          succeeds=true;
        } catch (Exception e) {
          // v2.6.4.a
          // display error to the user and ask for confirmation to restart
          boolean wantRestart;
          if (e instanceof InvocationTargetException) {
            // target method throws exception
            /*v3.3: use error dialog modality
            wantRestart = mainCtl.displayErrorFromCode(MessageCode.ERROR_RUN_PROGRAM_NODE,  
                e.getCause(), true, comp);
                */
            wantRestart = mainCtl.displayErrorModal(MessageCode.ERROR_RUN_PROGRAM_NODE,
                e.getCause(), true, comp);
          } else {
            /*v3.3: use error dialog modality
            wantRestart = mainCtl.displayErrorFromCode(MessageCode.ERROR_RUN_PROGRAM_NODE, 
                e, true, comp);
                */
            wantRestart = mainCtl.displayErrorModal(MessageCode.ERROR_RUN_PROGRAM_NODE, 
                e, true, comp);
          }
          
          if (policy==RestartPolicy.None || policy==RestartPolicy.All) {
            if (wantRestart) {
              // reflect e to process up-stream
              throw e;
            } else {
              // user does not want to restart, throw a TerminateOnErrorException
              throw new TerminateOnErrorException(e);
            }
          } else if (policy==RestartPolicy.Node) {
            if (wantRestart) {
              // prepare to restart node
              comp.reset();
            } else {
              // user does not want to restart, throw a TerminateOnErrorException
              throw new TerminateOnErrorException(e);
            }
          }
//          if (e instanceof InvocationTargetException) {
//            // target method throws exception
//            mainCtl.displayError(MessageCode.ERROR_RUN_PROGRAM_NODE, 
//                "Lỗi chạy nút chương trình {0}. Bạn có muốn chạy lại không?", e.getCause(), comp);
//          } else {
//            mainCtl.displayError(MessageCode.ERROR_RUN_PROGRAM_NODE,
//                "Lỗi chạy nút chương trình {0}. Bạn có muốn chạy lại không?", e, comp);
//          }
//          
//          if (policy==RestartPolicy.None || policy==RestartPolicy.All) {
//              throw e;
//          } else if (policy==RestartPolicy.Node) {
//            // prepare to restart node
//            comp.reset();
//          }        
        }
      } while (!succeeds);
    }

    Object output = comp.getOutput();

    if (debug)
      System.out.printf("  -> output: %s%n", output);
    
    return output;
  }
  
  /**
   * @effects 
   *  perform tasks needed at termination of a run tree (performed by {@link #runTree()} or one of  
   *  its variants). 
   * @version 2.7.3: stub method (to be implemented by sub-types if needed)
   */
  protected void onTerminateRunOnError() {
    //
  }

  /**
   * @effects 
   *  perform tasks needed at termination of a run of a <b>sub-tree</b> (performed by {@link #runSubTree(Node, Node)} or one of  
   *  its variants). 
   * @version 2.7.3: stub method (to be implemented by sub-types if needed)
   */
  protected void onTerminateRunSubTreeOnError() {
    //
  }

  /**
   * @effects
   *  if <tt>execTree != null</tt> 
   *    reset all run components of <tt>this</tt>  
   *    and re-run <tt>this</tt>
   *  else 
   *    do nothing 
   * @see {@link MethodName#restart}
   */
  public void restart() {
    if (execTree == null) { 
      // do nothing
      //return;
      initRunTree();
    } else {    
      if (debug)
        System.out.println(this + " is restarting");
  
      resetTree();
    }
    
    // run tree again
    runTree();
  }
  
  /**
   * @requires 
   *  the execution tree of this has been initialised
   *   
   * @effects
   *  reset all nodes of {@link #execTree} that are not configured to be a single-run
   *  
   * @see {@link MethodName#resetTree}
   */
  public void resetTree() {
    // reset each of the components
    Iterator<Node> nodes = execTree.getNodes();
    Node<RunComponent> n;
    RunComponent comp;
    while (nodes.hasNext()) {
      n = nodes.next();
      comp=n.getValue();
      // only reset if not single-run
      if (!comp.getSingleRun())
        comp.reset();
    }
  }

  /**
   * @requires 
   *  the execution tree of this has been initialised /\
   *  startNode != null /\ stopNode != null
   *   
   * @effects
   *  reset the sub-tree whose root is <tt>startNode</tt> and ends at the <tt>stopNode</tt>
   */
  private void resetSubTree(Node<RunComponent> startNode, Node<RunComponent> stopNode) {
    // reset the sub-tree starting from the specified node
    Node<RunComponent> n = startNode;
    Node<RunComponent> child;
    RunComponent comp;
    
    // reset n first (if not single-run)
    comp = n.getValue();
    if (!comp.getSingleRun()) comp.reset();
    
    // process the sub-tree rooted at theNode
    List<Node> children = execTree.getChildren(n);
    do { //while (children != null) {
      Iterator<Node> cit = children.iterator();
      do {
        child = cit.next();
        // this is a descendence of n
        // reset (if not single-run)
        comp = child.getValue();
        if (!comp.getSingleRun()) comp.reset();
      } while (cit.hasNext() && (child != stopNode));
      
      if (child != stopNode) {
        // finished the current level, continue the next level 
        n = child;  // root is the last child of the current level
        children = execTree.getChildren(n);
      }
    } while (child != stopNode);
  }
  
  private static void sleep(int millis) {
    // System.out.print(".");
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
    }
  }
  
  /**
   * @requires 
   *  execTree != null
   * @effects 
   *  return an <tt>Iterator</tt> object of all the <tt>Controller</tt>s that make 
   *  up this composite controller.
   */
  public Iterator<ControllerBasic> getComponentControllers() {
    Iterator<Node> nodes = execTree.getNodes();
    Node<RunComponent> n;
    RunComponent comp;
    List<ControllerBasic> controllers = new ArrayList();
    
    ControllerBasic ctl;
    while (nodes.hasNext()) {
      n = nodes.next();
      comp=n.getValue();
      ctl = comp.getController();
      if (ctl != this && !controllers.contains(ctl)) {
        // exclude the composite and duplicates
        controllers.add(ctl);
      }
    }
    
    return controllers.iterator();
  }
  
  /**
   * @effects 
   *  if execTree != null
   *    return its string representation
   *  else
   *    return <tt>null</tt>
   */
  protected String toStringRunTree() {
    if (execTree != null)
      return execTree.toString();
    else
      return null;
  }  
}
