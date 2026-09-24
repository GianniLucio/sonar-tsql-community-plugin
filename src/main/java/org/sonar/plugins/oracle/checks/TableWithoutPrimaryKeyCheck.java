package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class TableWithoutPrimaryKeyCheck extends OracleCheck {

    public static final String RULE_KEY = "O102_TableWithoutPrimaryKey";

    @Override
    public void enterCreateTableStatement(OracleParser.CreateTableStatementContext context) {
        boolean hasPrimaryKey = false;
        for (OracleParser.TableElementContext element : context.tableElement()) {
            if (element.tableConstraint() != null && element.tableConstraint().PRIMARY() != null) {
                hasPrimaryKey = true;
                break;
            }
            if (element.columnDefinition() != null) {
                for (OracleParser.ColumnConstraintContext constraint : element.columnDefinition().columnConstraint()) {
                    if (constraint.PRIMARY() != null) {
                        hasPrimaryKey = true;
                        break;
                    }
                }
            }
        }
        if (!hasPrimaryKey) {
            reportIssue(context, "Tables should define a PRIMARY KEY constraint.");
        }
    }
}
