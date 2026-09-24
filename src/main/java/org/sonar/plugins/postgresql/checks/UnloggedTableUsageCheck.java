package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

/**
 * P110: Use UNLOGGED tables cautiously
 */
public class UnloggedTableUsageCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P110_UnloggedTableUsage";

    @Override
    public void enterCreateTableStatement(PostgreSqlParser.CreateTableStatementContext ctx) {
        if (ctx.UNLOGGED() != null) {
            reportIssue(ctx.UNLOGGED().getSymbol(), "UNLOGGED tables are not crash-safe and data will be lost in case of a crash. Use with caution.");
        }
    }
}
