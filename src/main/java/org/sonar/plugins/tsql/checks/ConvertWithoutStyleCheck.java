package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConvertWithoutStyleCheck extends TSqlCheck {

    public static final String RULE_KEY = "S118_ConvertWithoutStyle";

    private static final Set<String> DATE_TIME_TYPES = new HashSet<>(Arrays.asList(
            "DATE", "DATETIME", "DATETIME2", "DATETIMEOFFSET", "SMALLDATETIME", "TIME"));

    @Override
    public void enterConvertExpr(TSqlParser.ConvertExprContext ctx) {
        if (ctx.data_type() != null && ctx.data_type().identifier() != null
                && DATE_TIME_TYPES.contains(ctx.data_type().identifier().getText().toUpperCase())
                && ctx.INT_LITERAL() == null) {
            reportIssue(ctx, "CONVERT to '" + ctx.data_type().identifier().getText() + "' should specify an explicit style code (third argument) to avoid ambiguous date/time formatting across regional settings.");
        }
    }
}
