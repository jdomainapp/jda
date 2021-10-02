package org.jda.example.productsys.services.machine.model;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

import java.util.*;

import org.jda.example.productsys.services.conveyor.model.Conveyor;
import org.jda.example.productsys.services.operator.model.Operator;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class Quality extends Machine {

    /*** STATE SPACE **/
    // attributes
    @DAttr(name = "conveyor", type = Type.Domain, optional = false)
    @DAssoc(ascName = "Conv-assoc-Quality", role = "rq", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @// self.rc->size()=1 
    Associate(// self.rc->size()=1 
    type = Conveyor.class, // self.rc->size()=1 
    cardMin = 1, cardMax = 1))
    private Conveyor conveyor;

    /*** BEHAVIOUR SPACE **/
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

    @DOpt(type = DOpt.Type.DataSourceConstructor, requires = "conveyor <> null", effects = "self.conveyor = conveyor")
    public Quality(@AttrRef(value = "id") Integer id, @AttrRef(value = "operator") Operator operator, @AttrRef(value = "conveyor") Conveyor conveyor) throws ConstraintViolationException {
        super(id, operator);
        this.conveyor = conveyor;
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor, requires = "conveyor <> null", effects = "self.conveyor = conveyor")
    @DOpt(type = DOpt.Type.RequiredConstructor)
    public Quality(@AttrRef(value = "operator") Operator operator, @AttrRef(value = "conveyor") Conveyor conveyor) throws ConstraintViolationException {
        super(operator);
        this.conveyor = conveyor;
    }
}
