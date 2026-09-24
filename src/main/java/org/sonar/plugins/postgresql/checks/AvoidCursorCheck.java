package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

/**
 * P102: Avoid procedural CURSORs
 */
public class AvoidCursorCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P102_AvoidCursor";

    @Override
    public void enterDeclareStatement(PostgreSqlParser.DeclareStatementContext ctx) {
        if (ctx.CURSOR() != null) {
            reportIssue(ctx, "Avoid procedural CURSORs; prefer set-based operations.");
        }
    }
}
