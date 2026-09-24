package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class InsertWithoutColumnListCheck extends OracleCheck {

    public static final String RULE_KEY = "O111_InsertWithoutColumnList";

    @Override
    public void enterInsertStatement(OracleParser.InsertStatementContext ctx) {
        if (ctx.columnList() == null) {
            reportIssue(ctx, "INSERT statement does not specify an explicit column list. Relying on implicit column order is fragile against future schema changes; list the target columns explicitly.");
        }
    }
}
