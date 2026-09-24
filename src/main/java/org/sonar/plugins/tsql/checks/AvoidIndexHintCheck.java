package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class AvoidIndexHintCheck extends TSqlCheck {

    public static final String RULE_KEY = "S119_AvoidIndexHint";

    @Override
    public void enterTable_hint(TSqlParser.Table_hintContext ctx) {
        if (ctx.INDEX() != null) {
            reportIssue(ctx, "Avoid forcing a specific INDEX table hint. Let the query optimizer choose the best access path; a forced index can become sub-optimal as data distribution changes.");
        }
    }
}
