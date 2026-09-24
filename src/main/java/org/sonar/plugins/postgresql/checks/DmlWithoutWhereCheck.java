package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

public class DmlWithoutWhereCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P111_DmlWithoutWhere";

    @Override
    public void enterUpdateStatement(PostgreSqlParser.UpdateStatementContext ctx) {
        if (ctx.whereClause() == null) {
            reportIssue(ctx, "UPDATE without a WHERE clause modifies every row. Add an explicit filter or document the intentional full-table update.");
        }
    }

    @Override
    public void enterDeleteStatement(PostgreSqlParser.DeleteStatementContext ctx) {
        if (ctx.whereClause() == null) {
            reportIssue(ctx, "DELETE without a WHERE clause removes every row. Add an explicit filter or document the intentional full-table delete.");
        }
    }
}
