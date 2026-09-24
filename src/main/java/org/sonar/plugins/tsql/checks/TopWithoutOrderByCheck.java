package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class TopWithoutOrderByCheck extends TSqlCheck {

    public static final String RULE_KEY = "S112_TopWithoutOrderBy";

    @Override
    public void enterSelect_stmt(TSqlParser.Select_stmtContext ctx) {
        if (ctx.TOP() != null && ctx.order_by_clause() == null) {
            reportIssue(ctx, "TOP without ORDER BY returns an arbitrary subset of rows. Add an explicit ordering.");
        }
    }
}
