package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class AvoidRaiserrorCheck extends TSqlCheck {

    public static final String RULE_KEY = "S115_AvoidRaiserror";

    @Override
    public void enterRaiserror_stmt(TSqlParser.Raiserror_stmtContext ctx) {
        reportIssue(ctx, "Prefer THROW over RAISERROR. THROW rethrows the original error and supports simpler, more reliable error propagation in TRY...CATCH blocks.");
    }
}
