package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

public class UselessCheckConstraintCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P116_UselessCheckConstraint";

    @Override
    public void enterColumnConstraint(PostgreSqlParser.ColumnConstraintContext ctx) {
        if (ctx.CHECK() != null && ctx.searchCondition() != null && isAlwaysTrue(ctx.searchCondition().expression())) {
            reportIssue(ctx, "This CHECK constraint always evaluates to true and provides no real data validation. Express a meaningful condition or remove the constraint.");
        }
    }

    @Override
    public void enterTableConstraint(PostgreSqlParser.TableConstraintContext ctx) {
        if (ctx.CHECK() != null && ctx.searchCondition() != null && isAlwaysTrue(ctx.searchCondition().expression())) {
            reportIssue(ctx, "This CHECK constraint always evaluates to true and provides no real data validation. Express a meaningful condition or remove the constraint.");
        }
    }

    private boolean isAlwaysTrue(PostgreSqlParser.ExpressionContext expression) {
        if (expression instanceof PostgreSqlParser.KeywordLiteralExprContext) {
            return ((PostgreSqlParser.KeywordLiteralExprContext) expression).TRUE() != null;
        }
        if (expression instanceof PostgreSqlParser.RelationalOpExprContext) {
            PostgreSqlParser.RelationalOpExprContext relational = (PostgreSqlParser.RelationalOpExprContext) expression;
            if (relational.EQ() != null && relational.expression().size() == 2) {
                PostgreSqlParser.ExpressionContext left = relational.expression(0);
                PostgreSqlParser.ExpressionContext right = relational.expression(1);
                return left instanceof PostgreSqlParser.NumberLiteralExprContext
                        && right instanceof PostgreSqlParser.NumberLiteralExprContext
                        && left.getText().equals(right.getText());
            }
        }
        return false;
    }
}
