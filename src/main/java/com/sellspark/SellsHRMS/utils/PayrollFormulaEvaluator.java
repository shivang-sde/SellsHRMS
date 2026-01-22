package com.sellspark.SellsHRMS.utils;

import com.sellspark.SellsHRMS.entity.payroll.SalaryComponent;
import com.sellspark.SellsHRMS.entity.payroll.EmployeeSalaryAssignment;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;


public class PayrollFormulaEvaluator {

    private static final ExpressionParser parser = new SpelExpressionParser();

    public static Double evaluate(SalaryComponent component, EmployeeSalaryAssignment assignment) {
        if (component.getCalculationType() == SalaryComponent.CalculationType.FIXED)
            return component.getAmount();

        if (component.getCalculationType() == SalaryComponent.CalculationType.PERCENTAGE)
            
            return assignment.getBasePay() * extractPercent(component.getFormula());

        if (component.getCalculationType() == SalaryComponent.CalculationType.FORMULA) {
            try {
                StandardEvaluationContext context = new StandardEvaluationContext();
                context.setVariable("BASE", assignment.getBasePay());
                return parser.parseExpression(component.getFormula()).getValue(context, Double.class);
            } catch (Exception e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    private static Double extractPercent(String formula) {
        try {
            return Double.parseDouble(formula.replace("%", "").trim()) / 100;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
