package org.jda.example.productsys.services.machine.model;

import java.util.Collection;

import org.jda.example.productsys.services.machine.model.io.MachineInput;
import org.jda.example.productsys.services.machine.model.io.MachineOutput;
import org.jda.example.productsys.services.operator.model.Operator;

import java.util.*;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
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
public class Machine {

    /*** STATE SPACE **/
    // attributes
    @DAttr(name = "id", type = Type.Integer, id = true, auto = true, optional = false, mutable = false, min = 1)
    private int id;

    @DAttr(name = "operator", type = Type.Domain, optional = false)
    @DAssoc(ascName = "Machine-assoc-Operator", role = "rm", ascType = AssocType.One2One, endType = AssocEndType.One, associate = @Associate(type = Operator.class, cardMin = 0, cardMax = 1))
    private Operator operator;

    @DAttr(name = "machineInput", type = Type.Collection, serialisable = false, filter = @Select(clazz = MachineInput.class))
    @DAssoc(ascName = "Machine-assoc-MachineInput", role = "rm", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = MachineInput.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
    private Collection<MachineInput> machineInput;

    @DAttr(name = "machineOutput", type = Type.Collection, serialisable = false, filter = @Select(clazz = MachineOutput.class))
    @DAssoc(ascName = "Machine-assoc-MachineOutput", role = "rm", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = MachineOutput.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
    private Collection<MachineOutput> machineOutput;

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

    @DOpt(type = DOpt.Type.Getter, effects = "result = operator")
    @AttrRef(value = "operator")
    public Operator getOperator() {
        return this.operator;
    }

    @DOpt(type = DOpt.Type.Setter, requires = "operator <> null", effects = "self.operator = operator")
    @AttrRef(value = "operator")
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew)
    @AttrRef(value = "operator")
    public boolean setNewOperator(Operator obj) {
        setOperator(obj);
        return false;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = machineInput")
    @AttrRef(value = "machineInput")
    public Collection<MachineInput> getMachineInput() {
        return this.machineInput;
    }

    @DOpt(type = DOpt.Type.Setter, effects = "self.machineInput = machineInput")
    @AttrRef(value = "machineInput")
    public void setMachineInput(Collection<MachineInput> machineInput) {
        this.machineInput = machineInput;
    }

    private int machineInputCount;

    @DOpt(type = DOpt.Type.LinkAdder, effects = "machineInput->forAll(o | machineInput@pre->includes(o) or obj = o) and machineInputCount = machineInputCount@pre + (machineInput->size() - machineInput@pre->size())")
    @AttrRef(value = "machineInput")
    public boolean addMachineInput(MachineInput obj) {
        if (!machineInput.contains(obj)) {
            machineInput.add(obj);
            machineInputCount++;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdder, effects = "machineInput->forAll(o | machineInput@pre->includes(o) or obj->includes(o)) and machineInputCount = machineInputCount@pre + (machineInput->size() - machineInput@pre->size())")
    @AttrRef(value = "machineInput")
    public boolean addMachineInput(Collection<MachineInput> obj) {
        for (MachineInput o : obj) {
            if (!machineInput.contains(o)) {
                machineInput.add(o);
                machineInputCount++;
            }
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew, effects = "machineInput->forAll(o | machineInput@pre->includes(o) or obj = o) and machineInputCount = machineInputCount@pre + (machineInput->size() - machineInput@pre->size())")
    @AttrRef(value = "machineInput")
    public boolean addNewMachineInput(MachineInput obj) {
        machineInput.add(obj);
        machineInputCount++;
        return false;
    }

    @DOpt(type = DOpt.Type.LinkUpdater)
    @AttrRef(value = "machineInput")
    public boolean onUpdateMachineInput(MachineInput obj) {
        // TODO: implement this 
        return false;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "if machineInput->includes(obj) then machineInputCount - 1 >= 0 else true endif", effects = "machineInput->forAll(o | machineInput@pre->includes(o) and obj <> o) and machineInputCount = machineInputCount@pre - (machineInput@pre->size() - machineInput->size())")
    @AttrRef(value = "machineInput")
    public boolean onRemoveMachineInput(MachineInput obj) {
        boolean removed = machineInput.remove(obj);
        if (removed)
            machineInputCount--;
        return removed;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "machineInputCount - obj->select(o | machineInput->includes(o))->size() >= 0", effects = "machineInput->forAll(o | machineInput@pre->includes(o) and obj->excludes(o)) and machineInputCount = machineInputCount@pre - (machineInput@pre->size() - machineInput->size())")
    @AttrRef(value = "machineInput")
    public boolean onRemoveMachineInput(Collection<MachineInput> obj) {
        for (MachineInput o : obj) {
            boolean removed = machineInput.remove(o);
            if (removed)
                machineInputCount--;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkCountGetter)
    @AttrRef(value = "machineInputCount")
    public Integer getMachineInputCount() {
        return machineInputCount;
    }

    @DOpt(type = DOpt.Type.LinkCountSetter)
    @AttrRef(value = "machineInputCount")
    public void setMachineInputCount(int machineInputCount) {
        this.machineInputCount = machineInputCount;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = machineOutput")
    @AttrRef(value = "machineOutput")
    public Collection<MachineOutput> getMachineOutput() {
        return this.machineOutput;
    }

    @DOpt(type = DOpt.Type.Setter, effects = "self.machineOutput = machineOutput")
    @AttrRef(value = "machineOutput")
    public void setMachineOutput(Collection<MachineOutput> machineOutput) {
        this.machineOutput = machineOutput;
    }

    private int machineOutputCount;

    @DOpt(type = DOpt.Type.LinkAdder, effects = "machineOutput->forAll(o | machineOutput@pre->includes(o) or obj = o) and machineOutputCount = machineOutputCount@pre + (machineOutput->size() - machineOutput@pre->size())")
    @AttrRef(value = "machineOutput")
    public boolean addMachineOutput(MachineOutput obj) {
        if (!machineOutput.contains(obj)) {
            machineOutput.add(obj);
            machineOutputCount++;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdder, effects = "machineOutput->forAll(o | machineOutput@pre->includes(o) or obj->includes(o)) and machineOutputCount = machineOutputCount@pre + (machineOutput->size() - machineOutput@pre->size())")
    @AttrRef(value = "machineOutput")
    public boolean addMachineOutput(Collection<MachineOutput> obj) {
        for (MachineOutput o : obj) {
            if (!machineOutput.contains(o)) {
                machineOutput.add(o);
                machineOutputCount++;
            }
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew, effects = "machineOutput->forAll(o | machineOutput@pre->includes(o) or obj = o) and machineOutputCount = machineOutputCount@pre + (machineOutput->size() - machineOutput@pre->size())")
    @AttrRef(value = "machineOutput")
    public boolean addNewMachineOutput(MachineOutput obj) {
        machineOutput.add(obj);
        machineOutputCount++;
        return false;
    }

    @DOpt(type = DOpt.Type.LinkUpdater)
    @AttrRef(value = "machineOutput")
    public boolean onUpdateMachineOutput(MachineOutput obj) {
        // TODO: implement this 
        return false;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "if machineOutput->includes(obj) then machineOutputCount - 1 >= 0 else true endif", effects = "machineOutput->forAll(o | machineOutput@pre->includes(o) and obj <> o) and machineOutputCount = machineOutputCount@pre - (machineOutput@pre->size() - machineOutput->size())")
    @AttrRef(value = "machineOutput")
    public boolean onRemoveMachineOutput(MachineOutput obj) {
        boolean removed = machineOutput.remove(obj);
        if (removed)
            machineOutputCount--;
        return removed;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "machineOutputCount - obj->select(o | machineOutput->includes(o))->size() >= 0", effects = "machineOutput->forAll(o | machineOutput@pre->includes(o) and obj->excludes(o)) and machineOutputCount = machineOutputCount@pre - (machineOutput@pre->size() - machineOutput->size())")
    @AttrRef(value = "machineOutput")
    public boolean onRemoveMachineOutput(Collection<MachineOutput> obj) {
        for (MachineOutput o : obj) {
            boolean removed = machineOutput.remove(o);
            if (removed)
                machineOutputCount--;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkCountGetter)
    @AttrRef(value = "machineOutputCount")
    public Integer getMachineOutputCount() {
        return machineOutputCount;
    }

    @DOpt(type = DOpt.Type.LinkCountSetter)
    @AttrRef(value = "machineOutputCount")
    public void setMachineOutputCount(int machineOutputCount) {
        this.machineOutputCount = machineOutputCount;
    }

    @DOpt(type = DOpt.Type.DataSourceConstructor, requires = "id >= 1 and operator <> null", effects = "self.id = genId(id) and self.operator = operator")
    public Machine(@AttrRef(value = "id") Integer id, @AttrRef(value = "operator") Operator operator) throws ConstraintViolationException {
        this.id = genId(id);
        this.operator = operator;
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor, requires = "operator <> null", effects = "self.operator = operator and self.id = genId(null)")
    @DOpt(type = DOpt.Type.RequiredConstructor)
    public Machine(@AttrRef(value = "operator") Operator operator) throws ConstraintViolationException {
        this.id = genId(null);
        this.operator = operator;
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
