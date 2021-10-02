package org.jda.example.productsys.services.conveyor.model;

import java.util.*;

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
 * @overview The conveyor belt sequence.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ConveyorSeq {

    /*** STATE SPACE **/
    // attributes
    @DAttr(name = "id", type = Type.Integer, id = true, auto = true, optional = false, mutable = false, min = 1)
    private int id;

    @DAttr(name = "prev", type = Type.Domain, optional = false)
    @DAssoc(ascName = "ConveyorSeq-assoc-PrevConveyor", role = "rcs", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Conveyor.class, cardMin = 1, cardMax = 1))
    private Conveyor prev;

    @DAttr(name = "next", type = Type.Domain, optional = false)
    @DAssoc(ascName = "ConveyorSeq-assoc-NextConveyor", role = "rcs", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Conveyor.class, cardMin = 1, cardMax = 1))
    private Conveyor next;

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

    @DOpt(type = DOpt.Type.Getter, effects = "result = prev")
    @AttrRef(value = "prev")
    public Conveyor getPrev() {
        return this.prev;
    }

    @DOpt(type = DOpt.Type.Setter, requires = "prev <> null", effects = "self.prev = prev")
    @AttrRef(value = "prev")
    public void setPrev(Conveyor prev) {
        this.prev = prev;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = next")
    @AttrRef(value = "next")
    public Conveyor getNext() {
        return this.next;
    }

    @DOpt(type = DOpt.Type.Setter, requires = "next <> null", effects = "self.next = next")
    @AttrRef(value = "next")
    public void setNext(Conveyor next) {
        this.next = next;
    }

    @DOpt(type = DOpt.Type.DataSourceConstructor, requires = "id >= 1 and prev <> null and next <> null", effects = "self.id = genId(id) and self.prev = prev and self.next = next")
    public ConveyorSeq(@AttrRef(value = "id") Integer id, @AttrRef(value = "prev") Conveyor prev, @AttrRef(value = "next") Conveyor next) throws ConstraintViolationException {
        this.id = genId(id);
        this.prev = prev;
        this.next = next;
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor, requires = "prev <> null and next <> null", effects = "self.prev = prev and self.next = next and self.id = genId(null)")
    @DOpt(type = DOpt.Type.RequiredConstructor)
    public ConveyorSeq(@AttrRef(value = "prev") Conveyor prev, @AttrRef(value = "next") Conveyor next) throws ConstraintViolationException {
        this.id = genId(null);
        this.prev = prev;
        this.next = next;
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
