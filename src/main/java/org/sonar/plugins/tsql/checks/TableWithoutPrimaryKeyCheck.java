package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class TableWithoutPrimaryKeyCheck extends TSqlCheck {

    public static final String RULE_KEY = "S109_TableWithoutPrimaryKey";

    @Override
    public void enterCreate_table_stmt(TSqlParser.Create_table_stmtContext ctx) {
        boolean hasPk = false;

        // Check if temporary table (starts with #)
        if (ctx.table_name() != null && ctx.table_name().getText().startsWith("#")) {
            return;
        }

        for (TSqlParser.Table_elementContext elem : ctx.table_element()) {
            if (elem.column_def() != null) {
                TSqlParser.Column_defContext col = elem.column_def();
                if (col.PRIMARY() != null && !col.PRIMARY().isEmpty() && col.KEY() != null && !col.KEY().isEmpty()) {
                    hasPk = true;
                    break;
                }
            }
            if (elem.table_constraint() != null) {
                TSqlParser.Table_constraintContext constraint = elem.table_constraint();
                if (constraint.PRIMARY() != null && constraint.KEY() != null) {
                    hasPk = true;
                    break;
                }
            }
        }

        if (!hasPk) {
            reportIssue(ctx.table_name() != null ? ctx.table_name() : ctx,
                    "Table definition does not have a PRIMARY KEY constraint. Every relational table should define a Primary Key for identity and indexing.");
        }
    }
}
