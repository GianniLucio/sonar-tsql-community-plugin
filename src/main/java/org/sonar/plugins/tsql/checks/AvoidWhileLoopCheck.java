package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class AvoidWhileLoopCheck extends TSqlCheck {

    public static final String RULE_KEY = "S117_AvoidWhileLoop";

    @Override
    public void enterWhile_stmt(TSqlParser.While_stmtContext ctx) {
        reportIssue(ctx, "Avoid procedural WHILE loops. Row-by-row (RBAR) iteration degrades database throughput; prefer set-based queries, CTEs, or window functions.");
    }
}
