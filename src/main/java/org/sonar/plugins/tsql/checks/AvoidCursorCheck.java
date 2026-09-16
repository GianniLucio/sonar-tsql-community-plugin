package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class AvoidCursorCheck extends TSqlCheck {

    public static final String RULE_KEY = "S105_AvoidCursor";

    @Override
    public void enterCursor_decl_stmt(TSqlParser.Cursor_decl_stmtContext ctx) {
        reportIssue(ctx, "Avoid using procedural CURSORs. CURSOR operations process row-by-row (RBAR) and degrade database throughput. Prefer set-based queries, CTEs, or window functions.");
    }
}
