package org.sonar.plugins.postgresql.checks;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

/**
 * P103: Statements should be terminated with a semicolon
 */
public class MissingSemicolonCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P103_MissingSemicolon";

    @Override
    public void enterProgram(PostgreSqlParser.ProgramContext ctx) {
        int childCount = ctx.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof PostgreSqlParser.StatementContext stmt) {
                boolean hasSemicolon = false;
                if (i + 1 < childCount && ";".equals(ctx.getChild(i + 1).getText())) {
                    hasSemicolon = true;
                }
                if (!hasSemicolon) {
                    reportIssue(stmt, "PostgreSQL statements should be terminated with a semicolon.");
                }
            }
        }
    }
}
