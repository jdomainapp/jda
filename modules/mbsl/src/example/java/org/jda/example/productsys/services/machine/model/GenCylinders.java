package org.jda.example.productsys.services.machine.model;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DOpt;

import java.util.*;

import org.jda.example.productsys.services.operator.model.Operator;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class GenCylinders extends Generator {

    @DOpt(type = DOpt.Type.DataSourceConstructor, requires = "", effects = "")
    public GenCylinders(@AttrRef(value = "id") Integer id, @AttrRef(value = "operator") Operator operator) throws ConstraintViolationException {
        super(id, operator);
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor, requires = "", effects = "")
    @DOpt(type = DOpt.Type.RequiredConstructor)
    public GenCylinders(@AttrRef(value = "operator") Operator operator) throws ConstraintViolationException {
        super(operator);
    }
}
