package jda.mosa.view.assets.panels.card;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import jda.mosa.view.assets.GUIToolkit;

/**
 * @overview 
 *  A sub-type of {@link JPanel}, whose layout is {@link CardLayout} and is controlled by 
 *  a button panel. This button panel is created within this panel but it is not contained in it.
 *  
 *   <p>Client code must manage the organisation of this panel and the button panel separately.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2 
 */
public abstract class CardPanel extends JPanel {
  /**
   *  auto-generated  
   */
  private static final long serialVersionUID = -482440945135412249L;
  
  protected final static Font labFont = new Font("Times", Font.BOLD, 45);
  protected final static Font butFont = new Font("Verdana", Font.BOLD, 14);
  protected final static Color fgColor = Color.BLUE;
  protected final static Color fgColorArrow = Color.GRAY;
  //private final static Insets butInsets = new Insets(7, 7, 7, 7);

  private static final int BorderThickness = 2;
  private static Border InActiveBorder = BorderFactory.createEmptyBorder(BorderThickness,BorderThickness,BorderThickness,BorderThickness);
  private static Border ActiveLineBorder = BorderFactory.createLineBorder(Color.GRAY, BorderThickness, true);

  private List<JButton> cardButts;
  private JPanel cardButtonsPanel;
  private CardMouseHandler cardMouseHandler;
  private Map<Component, String> compMap;
  
  /**
   * @effects 
   *  initialise this with {@link CardLayout} and a button panel and a {@link CardMouseHandler}.
   */
  public CardPanel() {
    super(new CardLayout());
    
    cardButts = new ArrayList<>();
    compMap = new HashMap<>();
    
    cardButtonsPanel = new JPanel();
    // make components appear next to each other (for arrowed labels between the buttons to appear nicely) 
    ((FlowLayout) cardButtonsPanel.getLayout()).setHgap(0); 
    
    cardMouseHandler = new CardMouseHandler(this);

  }

  /**
   * @requires <tt>cardComp</tt> was added to this by {@link #createCard(JComponent, String)}
   * 
   * @effects 
   *  activate the card for <tt>cardComp</tt>
   */
  public void showCard(Component cardComp) {
    // make the card component visible via the card-layout
    String name = (String) compMap.get(cardComp);
    ((CardLayout)getLayout()).show(this, name);
    
    // update the card buttons
    // update the correspond card button of cardComp to 'active'
    JButton cardBut = getCardButton(cardComp);
    updateButtonOnStateChange(cardBut, true);
    
    // update other buttons to 'not-active'
    toggleOtherButtons(cardBut, false);
  }

  /**
   * @effects 
   *  activate the card controlled by <tt>button</tt>
   */
  public void showCardForButton(JButton button) {
    String name = button.getActionCommand();
    ((CardLayout)getLayout()).show(this, name);
    
    // update the correspond card button of cardComp to 'active'
    updateButtonOnStateChange(button, true);
    
    // update other buttons to 'not-active'
    toggleOtherButtons(button, false);
  }
  
  /**
   * @modifies {@link #cardButts}, {@link #compMap}
   * 
   * @effects 
   *  Create a card for <tt>cardComp</tt>.
   *  Create a {@link JButton} that is used as a command button for controlling <tt>cardComp</tt>.
   *  The button's text = the name associated to <tt>cardComp</tt>.
   *  Add this button to {@link #cardButts}, but not yet to {@link #cardButtonsPanel}.
   *  We will add the buttons to this panel later using {@link #createCardButtonsPanel()}. 
   */
  public JButton createCard(JComponent cardComp, String buttonTxt) {
    // add cardComp to this
    add(cardComp, buttonTxt);
    compMap.put(cardComp, buttonTxt);

    // create a corresponding controll button for cardComp
    JButton but = new JButton(buttonTxt); 
    but.addActionListener(cardMouseHandler);
    
    // customise content area and border
    but.setContentAreaFilled(false);
    //but.setMargin(butInsets);
    
    // font: bold, blue
    but.setFont(butFont);
    but.setForeground(fgColor);
    
    // fixed dimension
    //but.setPreferredSize(butSz);

    cardButts.add(but);

    return but;
  }

  /**
   * To be implemented by sub-types.
   * 
   * @modifies {@link #cardButtonsPanel}
   * @effects 
   *  add {@link #cardButts} to {@link #cardButtonsPanel} in a domain-specific arrangement.
   */
  public abstract void createCardButtonsPanel();

  /**
   * @effects 
   *  turn the <tt>enabled</tt> state of all {@link JButton} in <tt>buttons</tt>, except for <tt>exceptBut</tt>, to <tt>state</tt>. 
   */
  public void toggleOtherButtons(JButton exceptBut, final boolean state) {
    for (JButton b : cardButts) {
      if (b != exceptBut && b.isEnabled() != state) {
        updateButtonOnStateChange(b, state);
      }
    }
  }

  /**
   * @effects 
   *  update <tt>button</tt>'s GUI on <tt>state</tt>
   */
  protected void updateButtonOnStateChange(JButton button, boolean state) {
    GUIToolkit.updateContainerLabelOnVisibilityUpdate(button, state);
    
    if (state == true) {
      // active: use active border
      button.setBorder(ActiveLineBorder);
    } else {
      // inactive: use empty border
      button.setBorder(InActiveBorder);
    }    
  }

  /**
   * @effects 
   *  return {@link #cardButtonsPanel}
   */
  public JPanel getCardButtonsPanel() {
    return cardButtonsPanel;
  }

  /**
   * @effects 
   *    return {@link #cardButts} 
   */
  public List<JButton> getCardButtons() {
    return cardButts;
  }

  /**
   * @effects 
   *  return the corresponding control button for <tt>cardComp</tt> or return <tt>null</tt> if not found.
   */
  public JButton getCardButton(Component cardComp) {
    String name = (String) compMap.get(cardComp);
    for (JButton but : cardButts) {
      if (but.getActionCommand().equals(name)) {
        return but;
      }
    }
    
    return null;
  }
}
