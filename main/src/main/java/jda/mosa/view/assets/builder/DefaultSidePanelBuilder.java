package jda.mosa.view.assets.builder;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;

import jda.modules.common.types.Tuple2;
import jda.modules.common.types.tree.Tree;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.view.View;
import jda.mosa.view.assets.DataContainerToolkit;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.tree.JTreeField;
import jda.util.events.ValueChangeListener;

/**
 * @overview 
 *  Construct a side panel component for a given {@link View}.
 *  
 *  <p>The default behaviour is to show a {@link JTree} of the view containment hirarchy. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2
 */
public class DefaultSidePanelBuilder extends ViewBuilder {



  /**
   * @effects 
   *  create a side panel containing a {@link JTree} showing the view containment hierarchy for <tt>view</tt>
   */
  @Override
  public Component build(View view, Configuration config) {
    JPanel panel = new JPanel();
    
    JTreeField treeField = (JTreeField) DataFieldFactory.createSimpleValuedDataField(
        config, JTreeField.class, null, false);
    
    // register this builder to handle value change event
    treeField.addValueChangeListener(new SidePanelHandler(view));
    
    Tree<LabelledContInfo> contTree = DataContainerToolkit.getViewContainmentTree(view);
    
    treeField.setValue(contTree);

    panel.add(treeField.getGUIComponent());
    
    return panel;
  }

  /**
   * @overview 
   *
   * @author Duc Minh Le (ducmle)
   */
  private class SidePanelHandler implements ValueChangeListener {

    private View view;

    public SidePanelHandler(View view) {
      this.view = view;
    }

    @Override
    public void fieldValueChanged(ChangeEvent e) {
      JTreeField field = (JTreeField) e.getSource();
      LabelledContInfo selectedContInfo = (LabelledContInfo) field.getSelectedNodeInfo();
      
      // activate the corresponding container
      view.activateDataContainerView(selectedContInfo);
    }
  }
  
}
