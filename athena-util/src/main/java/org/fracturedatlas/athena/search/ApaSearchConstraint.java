/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fracturedatlas.athena.search;

/**
 *
 * @author fintan
 */
public class ApaSearchConstraint {

    Operator oper = null;
    String parameter = null;
    String value = null;

    public ApaSearchConstraint(String param, Operator operator, String val) {
        parameter = param;
        oper = operator;
        value = val;
    }

    public Operator getOper() {
        return oper;
    }

    public void setOper(Operator oper) {
        this.oper = oper;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return parameter + " " + oper + " " + value;
    }
}
