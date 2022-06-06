package jda.modules.mosarfrontend.common.utils;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import lombok.Data;

@Data
public class DField {
    private DAttr dAttr;
    private DAssoc dAssoc;
}
