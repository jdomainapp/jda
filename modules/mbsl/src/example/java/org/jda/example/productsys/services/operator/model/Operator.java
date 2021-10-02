package org.jda.example.productsys.services.operator.model;

import java.util.*;

import org.jda.example.productsys.services.machine.model.Machine;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class Operator {

    /*** STATE SPACE **/
    // attributes
    @DAttr(name = "id", type = Type.Integer, id = true, auto = true, optional = false, mutable = false, min = 1)
    private int id;

    @DAttr(name = "machine", type = Type.Domain, optional = false)
    @DAssoc(ascName = "Machine-assoc-Operator", role = "ro", ascType = AssocType.One2One, endType = AssocEndType.One, associate = @Associate(type = Machine.class, cardMin = 1, cardMax = 1))
    private Machine machine;

    /*** BEHAVIOUR SPACE **/
    private static int idCounter;

    @DOpt(type = DOpt.Type.Getter, effects = "result = id")
    @AttrRef(value = "id")
    public int getId() {
        return this.id;
    }

    @DOpt(type = DOpt.Type.AutoAttributeValueGen, effects = "if id = null then idCounter = idCounter + 1 and result = idCounter else if id > idCounter then idCounter = id and result = id else result = id endif endif")
    @AttrRef(value = "id")
    private static int genId(Integer id) {
        Integer val;
        if (id == null) {
            idCounter++;
            val = idCounter;
        } else {
            if (id > idCounter) {
                idCounter = id;
            }
            val = id;
        }
        return val;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = machine")
    @AttrRef(value = "machine")
    public Machine getMachine() {
        return this.machine;
    }

    @DOpt(type = DOpt.Type.Setter, requires = "machine <> null", effects = "self.machine = machine")
    @AttrRef(value = "machine")
    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew)
    @AttrRef(value = "machine")
    public boolean setNewMachine(Machine obj) {
        setMachine(obj);
        return false;
    }

    @DOpt(type = DOpt.Type.DataSourceConstructor, requires = "id >= 1 and machine <> null", effects = "self.id = genId(id) and self.machine = machine")
    public Operator(@AttrRef(value = "id") Integer id, @AttrRef(value = "machine") Machine machine) throws ConstraintViolationException {
        this.id = genId(id);
        this.machine = machine;
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor, requires = "machine <> null", effects = "self.machine = machine and self.id = genId(null)")
    @DOpt(type = DOpt.Type.RequiredConstructor)
    public Operator(@AttrRef(value = "machine") Machine machine) throws ConstraintViolationException {
        this.id = genId(null);
        this.machine = machine;
    }

    @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
    public static void synchWithSource(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal) throws ConstraintViolationException {
        String attribName = attrib.name();
        if (attribName.equals("id")) {
            int maxIdVal = (Integer) maxVal;
            if (maxIdVal > idCounter)
                idCounter = maxIdVal;
        }
    }
}
