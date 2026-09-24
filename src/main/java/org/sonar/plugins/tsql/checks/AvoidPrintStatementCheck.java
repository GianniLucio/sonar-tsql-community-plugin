package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class AvoidPrintStatementCheck extends TSqlCheck {

    public static final String RULE_KEY = "S114_AvoidPrintStatement";

    @Override
    public void enterPrint_stmt(TSqlParser.Print_stmtContext ctx) {
        reportIssue(ctx, "Avoid PRINT for diagnostics or error reporting; messages are not guaranteed to reach the client and are invisible to application error handling. Use structured logging or THROW/RAISERROR instead.");
    }
}
