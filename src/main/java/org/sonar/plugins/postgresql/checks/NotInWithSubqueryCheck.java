package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

/**
 * P118: Avoid NOT IN with a subquery
 */
public class NotInWithSubqueryCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P118_NotInWithSubquery";

    @Override
    public void enterInExpr(PostgreSqlParser.InExprContext ctx) {
        if (ctx.NOT() != null && ctx.selectStatement() != null) {
            reportIssue(ctx, "Avoid 'NOT IN' with a subquery: if the subquery returns any NULL value the whole condition becomes unknown and no rows are returned. Use 'NOT EXISTS' instead.");
        }
    }
}
