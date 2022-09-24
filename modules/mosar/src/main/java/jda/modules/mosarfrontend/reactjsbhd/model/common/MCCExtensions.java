package jda.modules.mosarfrontend.reactjsbhd.model.common;

import com.github.javaparser.ast.body.FieldDeclaration;

import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptualmodel.MCC;

public final class MCCExtensions {
    public static FieldDef getIdFieldDef(MCC mcc) {
        return mcc.getDomainClass().getDomainFields()
                .stream()
                .filter(MCCExtensions::isIdField)
                .findFirst()
                .map(ParserToolkit::getFieldDefFull)
                .orElse(null);
    }

    private static boolean isIdField(FieldDeclaration fieldDeclaration) {
        return ParserToolkit.getAnnotation(fieldDeclaration, DAttr.class)
                .getPairs()
                .stream()
                .filter(pair -> pair.getNameAsString().equals("id"))
                .map(ParserToolkit::parseAnoMemberValue)
                .map(val -> (Boolean) val)
                .findFirst()
                .orElse(false);
    }
}
