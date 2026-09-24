package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

public class RedundantDistinctWithGroupByCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P115_RedundantDistinctWithGroupBy";

    @Override
    public void enterSelectStatement(PostgreSqlParser.SelectStatementContext ctx) {
        if (!ctx.DISTINCT().isEmpty() && ctx.groupByClause() != null) {
            reportIssue(ctx, "SELECT DISTINCT combined with GROUP BY is redundant: GROUP BY already returns a single row per group. Remove the unnecessary DISTINCT.");
        }
    }
}
