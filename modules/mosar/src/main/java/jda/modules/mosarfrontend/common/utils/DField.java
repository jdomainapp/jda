package jda.modules.mosarfrontend.common.utils;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import lombok.Data;

import java.lang.reflect.Field;

@Data
public class DField {
    private DAttr dAttr;
    private DAssoc dAssoc;
    private String enumName; // null if not enum
    private Enum<?>[] enumValues;

}
