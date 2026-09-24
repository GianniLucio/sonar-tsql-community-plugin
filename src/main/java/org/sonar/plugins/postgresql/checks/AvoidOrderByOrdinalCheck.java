package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

/**
 * P105: Avoid integer column ordinals in ORDER BY
 */
public class AvoidOrderByOrdinalCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P105_AvoidOrderByOrdinal";

    @Override
    public void enterOrderByOrdinal(PostgreSqlParser.OrderByOrdinalContext ctx) {
        reportIssue(ctx, "Avoid ordinal column numbers in ORDER BY (e.g., ORDER BY 1, 2). Use column names instead.");
    }
}
