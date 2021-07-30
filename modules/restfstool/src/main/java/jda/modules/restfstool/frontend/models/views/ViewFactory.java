package jda.modules.restfstool.frontend.models.views;

import jda.modules.dcsl.parser.ClassAST;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.restfstool.backend.utils.InheritanceUtils;
import jda.modules.restfstool.frontend.utils.MCCUtils;

public final class ViewFactory {
    public static final View createListView(MCC viewDesc) {
        return new ListView(viewDesc);
    }

    public static final View createFormView(Class cls) {
        if (!InheritanceUtils.getSubtypesOf(cls).isEmpty()) {
            return new FormViewWithTypeSelect(cls);
        } else {
            return new FormView(createClassAST(cls));
        }
    }

    private static final ClassAST createClassAST(Class cls) {
        ClassAST classAST = new ClassAST(cls.getSimpleName(),
                MCCUtils.getFullPath(cls).toString());
        return classAST;
    }

}
