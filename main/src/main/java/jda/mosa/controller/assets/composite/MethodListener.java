package jda.mosa.controller.assets.composite;

import java.util.EventListener;

public interface MethodListener extends EventListener {
  /**
   * Invoked when the target of the listener has changed its state.
   * 
   * @param e
   *          a ChangeEvent object
   */
  public void methodPerformed(MethodEvent e);
}
