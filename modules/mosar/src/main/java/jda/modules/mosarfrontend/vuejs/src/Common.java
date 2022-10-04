package jda.modules.mosarfrontend.vuejs.src;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.ArrayList;
import java.util.Arrays;

public class Common {
    public static DField[] getFullField(@RequiredParam.MCC NewMCC mcc) {
        if (mcc.getSubDomains().isEmpty())
            return mcc.getDFields();
        else {
            ArrayList<DField> fields = new ArrayList<>(Arrays.asList(mcc.getDFields()));
            mcc.getSubDomains().forEach((s, domain) -> {
                fields.addAll(Arrays.asList(domain.getDFields()));
            });
            return fields.toArray(DField[]::new);
        }

    }
}
