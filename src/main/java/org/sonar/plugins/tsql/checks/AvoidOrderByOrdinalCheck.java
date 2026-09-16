package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class AvoidOrderByOrdinalCheck extends TSqlCheck {

    public static final String RULE_KEY = "S108_AvoidOrderByOrdinal";

    @Override
    public void enterOrder_by_item(TSqlParser.Order_by_itemContext ctx) {
        if (ctx.expression() instanceof TSqlParser.LiteralExprContext litCtx) {
            if (litCtx.literal() != null && litCtx.literal().INT_LITERAL() != null) {
                reportIssue(ctx, "Avoid specifying column position numbers in 'ORDER BY' clause. Use explicit column names or aliases to make queries resilient to schema changes.");
            }
        }
    }
}
