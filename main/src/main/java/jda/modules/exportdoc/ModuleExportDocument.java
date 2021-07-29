package jda.modules.exportdoc;

import static jda.modules.mccl.conceptmodel.view.RegionName.Actions;
import static jda.modules.mccl.conceptmodel.view.RegionName.Add;
import static jda.modules.mccl.conceptmodel.view.RegionName.Delete;
import static jda.modules.mccl.conceptmodel.view.RegionName.Export;
import static jda.modules.mccl.conceptmodel.view.RegionName.First;
import static jda.modules.mccl.conceptmodel.view.RegionName.Last;
import static jda.modules.mccl.conceptmodel.view.RegionName.New;
import static jda.modules.mccl.conceptmodel.view.RegionName.Next;
import static jda.modules.mccl.conceptmodel.view.RegionName.Open;
import static jda.modules.mccl.conceptmodel.view.RegionName.Previous;
import static jda.modules.mccl.conceptmodel.view.RegionName.Update;

import java.util.Collection;

import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.exportdoc.controller.DocumentExportController;
import jda.modules.exportdoc.controller.DocumentExportDataController;
import jda.modules.exportdoc.model.DataDocument;
import jda.modules.exportdoc.page.ModulePage;
import jda.modules.exportdoc.page.model.Page;
import jda.modules.exportdoc.setup.ExportPostSetUp;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.MCCLConstants;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.mosa.view.assets.layout.SingleDataComponentLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;

/**
 * @overview
 *  A template view configuration class for the <tt>Export</tt> module of 
 *  an application.
 *
 *  <p>It uses {@link DocumentExportController} to exports data objects of a currently active
 *  object form and displays them on a content form that is defined by {@link ModulePage}.
 *  This form is capable of displaying content in multiple pages.
 *  
 *  <p>The content form is displayed with the layout of type {@link SingleDataComponentLayoutBuilder}
 *  which displays the content to fill the entire window.  
 *  
 * @author dmle
 */
@ModuleDescriptor(name="ModuleExportDocument",
modelDesc=@ModelDesc(
  model=DataDocument.class
),
viewDesc=@ViewDesc(
    formTitle="Xuất & in dữ liệu",
    imageIcon="export.gif",
    viewType=RegionType.Data, 
    view=View.class,
    //parent=RegionName.Tools,
    // hide buttons
    excludeComponents = {
      Open, First, Last, Next, Previous, 
      Add, New, Delete, Update, 
      Export, // v3.2: required
      Actions
    },
    layoutBuilderType=SingleDataComponentLayoutBuilder.class,
    topX=0.1d, topY=0d, widthRatio=0.8f, heightRatio=0.9f
    ,props={ // v3.2: create the view on start-up 
      @PropertyDesc(name=PropertyName.view_createOnStartUp,valueAsString="true",valueType=Boolean.class)}
),
controllerDesc=@ControllerDesc(
  controller=DocumentExportController.class,
  dataController=DocumentExportDataController.class//v3.2 SimpleDataController.class
),
setUpDesc=@SetUpDesc(postSetUp=ExportPostSetUp.class),
isPrimary=true,
type=ModuleType.System
)
public class ModuleExportDocument {
  @AttributeDesc(label="Tên văn bản",
      //isVisible=false
      props={ // not included in the object form
      @PropertyDesc(name=PropertyName.view_objectForm_dataField_visible,valueAsString="false",valueType=Boolean.class)
    }
  )
  private String name;

  @AttributeDesc(label="Tiêu đề",
      //isVisible=false
      props={ // not included in the object form
      @PropertyDesc(name=PropertyName.view_objectForm_dataField_visible,valueAsString="false",valueType=Boolean.class)
    }      
  )
  private String docTitle;

  @AttributeDesc(label=MCCLConstants.SYMBOL_ContainerHandle,
      editable=false,
      type=DefaultPanel.class
       // v3.1: controllerDesc=@ControllerDesc(
          //openPolicy=OpenPolicy.O,
       // v3.1: objectBrowser=PooledObjectBrowser.class
      // v3.1: )
     // required as part of the overall SingleDataComponentLayoutBuilder.class of the parent  
     ,layoutBuilderType=SingleDataComponentLayoutBuilder.class
  )
  private Collection<Page> pages;
}
