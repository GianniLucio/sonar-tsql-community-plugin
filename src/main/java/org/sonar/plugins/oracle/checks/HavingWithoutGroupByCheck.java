package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class HavingWithoutGroupByCheck extends OracleCheck {

    public static final String RULE_KEY = "O117_HavingWithoutGroupBy";

    @Override
    public void enterSelectStatement(OracleParser.SelectStatementContext ctx) {
        if (ctx.HAVING() != null && ctx.GROUP() == null) {
            reportIssue(ctx, "HAVING is used without a GROUP BY clause. HAVING without grouping treats the whole result set as a single group and usually indicates a WHERE clause was intended instead.");
        }
    }
}
