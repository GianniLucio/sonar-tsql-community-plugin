package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class AvoidNoLockCheck extends TSqlCheck {

    public static final String RULE_KEY = "S102_AvoidNoLock";

    @Override
    public void enterTable_hint(TSqlParser.Table_hintContext ctx) {
        if (ctx.NOLOCK() != null || ctx.READUNCOMMITTED() != null) {
            reportIssue(ctx, "Avoid using 'NOLOCK' or 'READUNCOMMITTED' table hints. They cause dirty reads, non-repeatable reads, and phantom rows. Consider using Snapshot Isolation instead.");
        }
    }
}
