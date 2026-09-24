package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

/**
 * P101: Avoid 'SELECT *' queries
 */
public class AvoidSelectStarCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P101_AvoidSelectStar";

    @Override
    public void enterSelectStar(PostgreSqlParser.SelectStarContext ctx) {
        reportIssue(ctx, "Avoid using 'SELECT *'. Specify column names explicitly.");
    }

    @Override
    public void enterSelectTableStar(PostgreSqlParser.SelectTableStarContext ctx) {
        reportIssue(ctx, "Avoid using 'table.*' in SELECT clause. Explicitly list required column names.");
    }
}
