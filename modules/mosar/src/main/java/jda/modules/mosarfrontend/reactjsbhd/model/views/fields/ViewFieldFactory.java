package jda.modules.mosarfrontend.reactjsbhd.model.views.fields;

import java.util.Arrays;
import java.util.Optional;

import org.modeshape.common.text.Inflector;

import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.reactjsbhd.model.common.FieldDefExtensions;

public class ViewFieldFactory {

    private static Inflector inflector;

    public static ViewField create(FieldDef fieldDef, Object... extras) {
        Optional<DAttr.Type> fieldDomainType = FieldDefExtensions.getDomainType(fieldDef);
        if (fieldDomainType.isEmpty()) {
            throw new IllegalArgumentException("Not a domain field: " + fieldDomainType);
        }

        if (extras.length == 1 && extras[0] instanceof String) {
            // simple view field
            return SimpleViewField.createUsing(fieldDef, (String) extras[0]);
        }

        if (extras.length == 2) {
            // one to many
            return new OneManyField(fieldDef);
        }

        if (extras.length == 3 && extras[0] instanceof FieldDef) {
            // many to one / one to one
            final FieldDef idFieldDef = (FieldDef) extras[0];
            final String fieldTypeName = fieldDef.getType()
                    .asClassOrInterfaceType()
                    .getNameAsString();
            inflector = Inflector.getInstance();
            final String idLabel = inflector.humanize(inflector.underscore(fieldTypeName))
                    .concat(" ")
                    .concat((String) extras[1]);

            if (extras[2].equals(DAssoc.AssocType.One2Many)) {
                // many to one
                return new ManyOneField(fieldDef, idFieldDef, idLabel);
            }
            return new OneOneField(fieldDef, idFieldDef, idLabel);
        }

        throw new IllegalArgumentException("Argument list do not match any type: " + Arrays.toString(extras));
    }
}
