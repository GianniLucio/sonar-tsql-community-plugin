package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class CheckConstraintNullComparisonCheck extends OracleCheck {

    public static final String RULE_KEY = "O120_CheckConstraintNullComparison";

    @Override
    public void enterColumnConstraint(OracleParser.ColumnConstraintContext ctx) {
        if (ctx.CHECK() != null && containsNullComparison(ctx.expression())) {
            reportIssue(ctx, "CHECK constraint compares a value with NULL using = or <>, which always evaluates to UNKNOWN and never enforces the constraint. Use IS NULL or IS NOT NULL instead.");
        }
    }

    @Override
    public void enterTableConstraint(OracleParser.TableConstraintContext ctx) {
        if (ctx.CHECK() != null && containsNullComparison(ctx.expression())) {
            reportIssue(ctx, "CHECK constraint compares a value with NULL using = or <>, which always evaluates to UNKNOWN and never enforces the constraint. Use IS NULL or IS NOT NULL instead.");
        }
    }

    private boolean containsNullComparison(OracleParser.ExpressionContext ctx) {
        if (ctx == null) {
            return false;
        }
        if ((ctx.EQ() != null || ctx.NEQ() != null) && ctx.expression().size() == 2
                && (isNullLiteral(ctx.expression(0)) || isNullLiteral(ctx.expression(1)))) {
            return true;
        }
        for (OracleParser.ExpressionContext child : ctx.expression()) {
            if (containsNullComparison(child)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNullLiteral(OracleParser.ExpressionContext ctx) {
        return ctx != null && ctx.literal() != null && ctx.literal().NULL() != null;
    }
}
