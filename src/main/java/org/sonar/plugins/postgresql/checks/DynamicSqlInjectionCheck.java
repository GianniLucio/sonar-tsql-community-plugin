package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

/**
 * P104: Avoid dynamic SQL with string concatenation
 */
public class DynamicSqlInjectionCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P104_DynamicSqlInjection";

    @Override
    public void enterDynamicSql(PostgreSqlParser.DynamicSqlContext ctx) {
        reportIssue(ctx, "Avoid dynamic SQL with string concatenation (SQL injection vulnerability, CWE-89). Use parameterized queries.");
    }
}
