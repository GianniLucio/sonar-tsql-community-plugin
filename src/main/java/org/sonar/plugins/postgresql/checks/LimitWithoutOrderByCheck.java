package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

public class LimitWithoutOrderByCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P112_LimitWithoutOrderBy";

    @Override
    public void enterSelectStatement(PostgreSqlParser.SelectStatementContext ctx) {
        if ((ctx.limitClause() != null || ctx.offsetClause() != null) && ctx.orderByClause() == null) {
            reportIssue(ctx, "LIMIT/OFFSET without ORDER BY can return rows in an arbitrary order. Add an explicit ordering.");
        }
    }
}
