package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DeprecatedDataTypeCheck extends TSqlCheck {

    public static final String RULE_KEY = "S113_DeprecatedDataType";

    private static final Set<String> DEPRECATED_TYPES = new HashSet<>(Arrays.asList("TEXT", "NTEXT", "IMAGE"));

    @Override
    public void enterData_type(TSqlParser.Data_typeContext ctx) {
        if (ctx.identifier() != null && DEPRECATED_TYPES.contains(ctx.identifier().getText().toUpperCase())) {
            reportIssue(ctx, "'" + ctx.identifier().getText() + "' is a deprecated data type. Use VARCHAR(MAX), NVARCHAR(MAX), or VARBINARY(MAX) instead.");
        }
    }
}
