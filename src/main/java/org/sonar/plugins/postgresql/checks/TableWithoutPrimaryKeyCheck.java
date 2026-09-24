package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

/**
 * P106: Tables should define a PRIMARY KEY
 */
public class TableWithoutPrimaryKeyCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P106_TableWithoutPrimaryKey";

    @Override
    public void enterCreateTableStatement(PostgreSqlParser.CreateTableStatementContext ctx) {
        boolean hasPk = false;

        // Check column-level constraints
        if (ctx.columnDefinition() != null) {
            for (PostgreSqlParser.ColumnDefinitionContext colDef : ctx.columnDefinition()) {
                if (colDef.columnConstraint() != null) {
                    for (PostgreSqlParser.ColumnConstraintContext constraint : colDef.columnConstraint()) {
                        if (constraint.PRIMARY() != null && constraint.KEY() != null) {
                            hasPk = true;
                            break;
                        }
                    }
                }
                if (hasPk) break;
            }
        }

        // Check table-level constraints
        if (!hasPk && ctx.tableConstraint() != null) {
            for (PostgreSqlParser.TableConstraintContext tConstraint : ctx.tableConstraint()) {
                if (tConstraint.PRIMARY() != null && tConstraint.KEY() != null) {
                    hasPk = true;
                    break;
                }
            }
        }

        if (!hasPk) {
            reportIssue(ctx.tableName() != null ? ctx.tableName() : ctx, "Tables should define a PRIMARY KEY constraint.");
        }
    }
}
