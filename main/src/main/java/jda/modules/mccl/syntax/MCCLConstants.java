package jda.modules.mccl.syntax;

import javax.print.attribute.standard.MediaSizeName;

import jda.modules.common.CommonConstants;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;

public class MCCLConstants {

  public static final double DEFAULT_TOP_X = -1;

  public static final double DEFAULT_TOP_Y = -1;

  public static final int DEFAULT_HEIGHT = -1;

  public static final int DEFAULT_WIDTH = -1;

  public static final float DEFAULT_HEIGHT_RATIO = -1f;

  public static final float DEFAULT_WIDTH_RATIO = -1f;

  public static final int DEFAULT_FIELD_WIDTH = -1;

  public static final int DEFAULT_FIELD_HEIGHT = -1;

  public static final int STANDARD_FIELD_HEIGHT = 25; // pixels

  public static final String SYMBOL_True = "\u22a8";
  public static final String SYMBOL_EndOfProof = "\u220e";

  public static final String SYMBOL_ContainerHandle = SYMBOL_EndOfProof;

  public static final String DEFAULT_PRINT_REF_ID = "p1";

  public static final String DEFAULT_PRINT_WIDTH = "100%";

  public static final boolean DEFAULT_PRINT_BORDER = true;

  public static final String DEFAULT_FORM_ICON = "formTitleLogo.png";


  // END - MCCL ///

  /***
   * v5.0: this must match the default value of {@link AttributeDesc#type()}.
   */
  public static final Class DEFAULT_DISPLAY_CLASS = CommonConstants.NullType;

  /**
   * v5.0: this matches the default value of
   * {@link AttributeDesc#controllerDesc()} but takes the equivalent String
   * literal
   */
  public static final String DEFAULT_CONTROLLER_DESC_AS_STRING = "@ControllerDesc()";

  /**
   * v5.1: this matches the default value of {@link AttributeDesc#modelDesc()}
   * but takes the equivalent String literal
   */
  public static final String DEFAULT_MODEL_DESC_AS_STRING = "@ModelDesc()";

  /**
   * v5.1c: this matches the default value of
   * {@link AttributeDesc#isStateEventSource()}
   */
  public static final boolean DEFAULT_IS_STATE_EVENT_SOURCE = false;

  /**
   * v5.1c: this matches the default value of {@link ControllerDesc#props()}
   */
  public static final String DEFAULT_CONTROLLER_DESC_PROPS_AS_STRING = "{}";

  // v5.3
  public static final boolean DEFAULT_LOAD_OID_WITH_BOUND_VALUE = false;

  public static final boolean DEFAULT_DISPLAY_OID_WITH_BOUND_VALUE = false;

  /**
   * @overview Represent the page format (used in printing), which is mapped to
   *           corresponding orientation constants in
   *           {@link java.awt.print.PageFormat}.
   * 
   * @author dmle
   */
  public enum PageFormat {
    Portrait(java.awt.print.PageFormat.PORTRAIT), Landscape(
        java.awt.print.PageFormat.LANDSCAPE);

    private int orientation;

    private PageFormat(int orientation) {
      this.orientation = orientation;
    }

    @DAttr(name = "name", id = true, type = DAttr.Type.String, mutable = false, optional = false, length = 10)
    public String getName() {
      return name();
    }

    public int getOrientation() {
      return orientation;
    }
  } // endn PageFormat

  /**
   * @overview Represent printing paper size, which is mapped to the
   *           corresponding {@link MediaSizeName}.
   */
  public enum PaperSize {
    A4(MediaSizeName.ISO_A4), A3(MediaSizeName.ISO_A3), Letter(
        MediaSizeName.NA_LETTER),;
    // add more paper sizes here

    // the corresponding media size name
    private MediaSizeName mediaSize;

    private PaperSize(MediaSizeName mediaSize) {
      this.mediaSize = mediaSize;
    }

    public MediaSizeName getMediaSize() {
      return mediaSize;
    }
  } // end PaperSize

  public enum AlignmentX {
    Left, Center, Right,
    /**
     * Means the same as <tt>null</tt> or not-specified.
     * 
     * @version 5.2:
     */
    Nil;

    @DAttr(name = "name", id = true, type = DAttr.Type.String, mutable = false, optional = false, length = 10)
    public String getName() {
      return name();
    }

    public String getHtmlName() {
      return getName().toLowerCase();
    }
  }

  public enum AlignmentY {
    Top, Middle, Bottom;

    @DAttr(name = "name", id = true, type = DAttr.Type.String, mutable = false, optional = false, length = 10)
    public String getName() {
      return name();
    }
  }

  /**
   * v5.1c: this matches the default value of {@link AttributeDesc#ref()}
   */
  public static final String DEFAULT_SELECT_AS_STRING = "@Select()";

  private MCCLConstants() {
  }

}
