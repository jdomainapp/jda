package jda.test.model.modules;

import java.util.List;

import jda.modules.dcsl.syntax.Select;
import jda.modules.helpviewer.model.print.PrintDesc;
import jda.modules.helpviewer.model.print.PrintFieldDesc;
import jda.modules.mccl.syntax.MCCLConstants;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.MCCLConstants.PageFormat;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.mosa.view.assets.panels.DefaultPanel;

/**
 * Represents the definition of a report
 * @author dmle
 *
 */
@ModuleDescriptor(
    name="ModuleA"
) // end ModuleDescriptor
@PrintDesc(
    pageFormat=PageFormat.Landscape)
public class ModuleA {
  @AttributeDesc(label="DANH SÁCH CÓ KHẨU CHÙA BỬU THẮNG")
  private String title;

  @AttributeDesc(label=MCCLConstants.SYMBOL_ContainerHandle,
      editable=false,
      type=DefaultPanel.class                // for data viewing
  )
  @PrintFieldDesc(
      refId="p1",
      isLabelVisible=false,
      printConfig=ModulePrintA.class) 
  private List persons;

  @AttributeDesc(label="Chọn chữ ký",
      type=JComboField.class,
      ref=@Select(clazz=Object.class,attributes={"description"}),
      isStateEventSource=true
      )
  @PrintFieldDesc(isVisible=false)  
  private Object sigSelect;

  @AttributeDesc(label="Hiển thị <br>chữ ký",
      alignX=AlignmentX.Right,
      type=DefaultPanel.class
      )
  @PrintFieldDesc(
      refId="p2",         // a separate print area
      isLabelVisible=false)
  private Object sig;
}
