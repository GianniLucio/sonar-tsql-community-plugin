package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class Varchar2WithoutSizeCheck extends OracleCheck {

    public static final String RULE_KEY = "O108_Varchar2WithoutSize";

    @Override
    public void enterColumnDefinition(OracleParser.ColumnDefinitionContext ctx) {
        OracleParser.DataTypeContext dataType = ctx.dataType();
        if (dataType != null && dataType.identifier() != null
                && "VARCHAR2".equalsIgnoreCase(dataType.identifier().getText())
                && dataType.NUMBER().isEmpty()) {
            reportIssue(dataType, "VARCHAR2 should declare an explicit size (e.g. VARCHAR2(100)); relying on an implicit default risks silent truncation.");
        }
    }
}
