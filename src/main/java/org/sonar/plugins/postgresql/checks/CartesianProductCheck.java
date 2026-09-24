package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

public class CartesianProductCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P114_CartesianProduct";

    @Override
    public void enterSelectStatement(PostgreSqlParser.SelectStatementContext ctx) {
        if (ctx.fromClause() != null && ctx.fromClause().tableReference().size() > 1 && ctx.whereClause() == null) {
            reportIssue(ctx, "Multiple tables listed in FROM separated by commas without a WHERE clause produce a cartesian product. Use explicit JOIN...ON syntax or add a filtering WHERE clause.");
        }
    }
}
