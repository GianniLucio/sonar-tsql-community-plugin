package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

public class OffsetWithoutLimitCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P113_OffsetWithoutLimit";

    @Override
    public void enterSelectStatement(PostgreSqlParser.SelectStatementContext ctx) {
        if (ctx.offsetClause() != null && ctx.limitClause() == null) {
            reportIssue(ctx, "OFFSET without a LIMIT still evaluates and skips every preceding row before returning the remainder of the result set. Add a LIMIT clause to bound the query.");
        }
    }
}
