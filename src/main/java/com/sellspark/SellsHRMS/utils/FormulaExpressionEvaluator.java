package com.sellspark.SellsHRMS.utils;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unified Formula Evaluation Engine for Payroll, Tax, and Statutory computations.
 * Supports formulas like:
 *  - "BASE * 0.4"
 *  - "(BASE + HRA) * 0.12"
 *  - "COMP:BASIC * 0.4"
 *  - "(GROSS > 20000) ? BASE * 0.1 : BASE * 0.05"
 */

@Slf4j
public class FormulaExpressionEvaluator {

    private static final ExpressionParser parser = new SpelExpressionParser();
    private static final Pattern COMP_PATTERN = Pattern.compile("COMP:([A-Z0-9_]+)");
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\b[A-Z0-9_]+\\b");

    /**
     * Evaluate any payroll formula with the provided context.
     */
    public static double evaluate(String formula, Map<String, Object> context) {
        if (formula == null || formula.isBlank()) return 0.0;

        try {
            // Step 1️⃣: Replace COMP: references with actual values
            String parsedFormula = replaceComponentRefs(formula, context);

            // Step 2️⃣: Replace missing direct tokens (e.g., BASE, BASIC, PF) with "0"
            parsedFormula = replaceMissingTokens(parsedFormula, context);

            // Step 3️⃣: Create safe evaluation context
            StandardEvaluationContext evalContext = new StandardEvaluationContext(context);
            evalContext.addPropertyAccessor(new MapAccessor());
            evalContext.setRootObject(context);
            Object value = parser.parseExpression(parsedFormula).getValue(evalContext);
            if (value instanceof Number num) return num.doubleValue();
            return 0.0;

        } catch (SpelEvaluationException e) {
            log.info("⚠️ Formula evaluation error ( {} ): {}", formula,  e.getMessage());
    
            return 0.0;
        } catch (Exception e) {
            log.error("⚠️ Unexpected formula error ( {} ): {}", formula,  e.getMessage());
            return 0.0;
        }
    }

    /**
     * Validate a formula safely.
     */
    public static boolean validateFormula(String formula, Map<String, Object> sampleContext) {
        if (formula == null || formula.isBlank()) return false;
        try {
            String parsed = replaceComponentRefs(formula, sampleContext);
            parsed = replaceMissingTokens(parsed, sampleContext);
            StandardEvaluationContext ctx = new StandardEvaluationContext();
            sampleContext.forEach((k, v) -> ctx.setVariable(k, v != null ? v : 0.0));
            parser.parseExpression(parsed).getValue(ctx);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Evaluate conditions like "GROSS > 20000 && COUNTRY == 'IN'".
     */
    public static boolean evaluateCondition(String condition, Map<String, Object> context) {
        if (condition == null || condition.isBlank()) return true;
        try {
            String parsed = replaceComponentRefs(condition, context);
            parsed = replaceMissingTokens(parsed, context);
            StandardEvaluationContext ctx = new StandardEvaluationContext();
            context.forEach((k, v) -> ctx.setVariable(k, v != null ? v : 0.0));
            Object result = parser.parseExpression(parsed).getValue(ctx);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            System.err.println("⚠️ Condition evaluation failed (" + condition + "): " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────── Helpers ───────────────────────────────

    /** Replace COMP: references like COMP:BASIC with their values. */
    private static String replaceComponentRefs(String formula, Map<String, Object> context) {
        Matcher matcher = COMP_PATTERN.matcher(formula);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String abbr = matcher.group(1);
            Object value = context.get("COMP:" + abbr);
            double val = (value instanceof Number num) ? num.doubleValue() : 0.0;
            matcher.appendReplacement(sb, String.valueOf(val));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /** Replace any missing direct token (BASE, BASIC, HRA, etc.) with zero. */
    private static String replaceMissingTokens(String formula, Map<String, Object> context) {
        Matcher matcher = TOKEN_PATTERN.matcher(formula);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String token = matcher.group();
            if (!context.containsKey(token)) {
                matcher.appendReplacement(sb, "0");
            } else {
                matcher.appendReplacement(sb, token);
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
