package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class DmlWithoutWhereCheck extends OracleCheck {

    public static final String RULE_KEY = "O104_DmlWithoutWhere";

    @Override
    public void enterUpdateStatement(OracleParser.UpdateStatementContext ctx) {
        if (ctx.whereExpression == null) {
            reportIssue(ctx, "UPDATE without a WHERE clause modifies every row. Add an explicit filter or document the intentional full-table update.");
        }
    }

    @Override
    public void enterDeleteStatement(OracleParser.DeleteStatementContext ctx) {
        if (ctx.whereExpression == null) {
            reportIssue(ctx, "DELETE without a WHERE clause removes every row. Add an explicit filter or document the intentional full-table delete.");
        }
    }
}
