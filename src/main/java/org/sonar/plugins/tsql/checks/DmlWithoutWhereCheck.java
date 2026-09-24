package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class DmlWithoutWhereCheck extends TSqlCheck {

    public static final String RULE_KEY = "S111_DmlWithoutWhere";

    @Override
    public void enterUpdate_stmt(TSqlParser.Update_stmtContext ctx) {
        if (ctx.where_clause() == null) {
            reportIssue(ctx, "UPDATE without a WHERE clause modifies every row. Add an explicit filter or document the intentional full-table update.");
        }
    }

    @Override
    public void enterDelete_stmt(TSqlParser.Delete_stmtContext ctx) {
        if (ctx.where_clause() == null) {
            reportIssue(ctx, "DELETE without a WHERE clause removes every row. Add an explicit filter or document the intentional full-table delete.");
        }
    }
}
