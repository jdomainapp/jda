package org.jda.example.productsys.services.machine.model;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DOpt;

import java.util.*;

import org.jda.example.productsys.services.conveyor.model.Conveyor;
import org.jda.example.productsys.services.operator.model.Operator;

/**
 * @overview Represents an assembler machine.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class Assembler extends Machine {

    /*** STATE SPACE **/
    /** BEHAVIOUR SPACE */
    /* Manually added from the Vallecillo et al. paper */
    /**
   * The assemble behaviour as described in the  Vallecillo et al. paper.
   * @requires (attribute condition) // ?? could add a condition that co1 in this.machineInput and co2 in this.machineOutput
   *  co2.nelems+1 <= co2.capacity
   *  
   * @effects (attribute computation)
   *  co1.nelems = co1.nelems-2 and
   *  co2.nelems = co2.nelems+1
   *  
   * @version 
   */
    public void assemble(// input 
    Conveyor co1, // output
    Conveyor co2) {
        co1.setNelems(co1.getNelems() - 2);
        co2.setNelems(co2.getNelems() + 1);
    }

    /**
   * The move behaviour as described in the  Vallecillo et al. paper.
   * @requires (attribute condition)
   *  co2.nelems+1 <= co2.capacity
   *  
   * @effects (attribute computation)
   *  co1.nelems = co1.nelems-1 and
   *  co2.nelems = co2.nelems+1
   *  
   * @version 
   */
    public void move(// current 
    Conveyor co1, // next
    Conveyor co2) {
        co1.setNelems(co1.getNelems() - 1);
        co2.setNelems(co2.getNelems() + 1);
    }

    @DOpt(type = DOpt.Type.DataSourceConstructor, requires = "", effects = "")
    public Assembler(@AttrRef(value = "id") Integer id, @AttrRef(value = "operator") Operator operator) throws ConstraintViolationException {
        super(id, operator);
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor, requires = "", effects = "")
    @DOpt(type = DOpt.Type.RequiredConstructor)
    public Assembler(@AttrRef(value = "operator") Operator operator) throws ConstraintViolationException {
        super(operator);
    }
}
