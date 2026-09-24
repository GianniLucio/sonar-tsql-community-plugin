package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class MergeWithoutNotMatchedCheck extends OracleCheck {

    public static final String RULE_KEY = "O114_MergeWithoutNotMatched";

    @Override
    public void enterMergeStatement(OracleParser.MergeStatementContext ctx) {
        if (ctx.NOT() == null) {
            reportIssue(ctx, "MERGE statement only handles the WHEN MATCHED case. Add a WHEN NOT MATCHED THEN INSERT clause, or confirm that unmatched source rows should be intentionally ignored.");
        }
    }
}
