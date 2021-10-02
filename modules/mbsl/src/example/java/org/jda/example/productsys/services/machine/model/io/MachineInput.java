package org.jda.example.productsys.services.machine.model.io;

import java.util.*;

import org.jda.example.productsys.services.conveyor.model.Conveyor;
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
public class MachineInput {

    /*** STATE SPACE **/
    // attributes
    @DAttr(name = "id", type = Type.Integer, id = true, auto = true, optional = false, mutable = false, min = 1)
    private int id;

    @DAttr(name = "machine", type = Type.Domain, optional = false)
    @DAssoc(ascName = "Machine-assoc-MachineInput", role = "rmi", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Machine.class, cardMin = 1, cardMax = 1))
    private Machine machine;

    @DAttr(name = "conveyor", type = Type.Domain, optional = false)
    @DAssoc(ascName = "Conveyor-assoc-MachineInput", role = "rmi", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Conveyor.class, cardMin = 1, cardMax = 1))
    private Conveyor conveyor;

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

    @DOpt(type = DOpt.Type.Getter, effects = "result = conveyor")
    @AttrRef(value = "conveyor")
    public Conveyor getConveyor() {
        return this.conveyor;
    }

    @DOpt(type = DOpt.Type.Setter, requires = "conveyor <> null", effects = "self.conveyor = conveyor")
    @AttrRef(value = "conveyor")
    public void setConveyor(Conveyor conveyor) {
        this.conveyor = conveyor;
    }

    @DOpt(type = DOpt.Type.DataSourceConstructor, requires = "id >= 1 and machine <> null and conveyor <> null", effects = "self.id = genId(id) and self.machine = machine and self.conveyor = conveyor")
    public MachineInput(@AttrRef(value = "id") Integer id, @AttrRef(value = "machine") Machine machine, @AttrRef(value = "conveyor") Conveyor conveyor) throws ConstraintViolationException {
        this.id = genId(id);
        this.machine = machine;
        this.conveyor = conveyor;
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor, requires = "machine <> null and conveyor <> null", effects = "self.machine = machine and self.conveyor = conveyor and self.id = genId(null)")
    @DOpt(type = DOpt.Type.RequiredConstructor)
    public MachineInput(@AttrRef(value = "machine") Machine machine, @AttrRef(value = "conveyor") Conveyor conveyor) throws ConstraintViolationException {
        this.id = genId(null);
        this.machine = machine;
        this.conveyor = conveyor;
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
