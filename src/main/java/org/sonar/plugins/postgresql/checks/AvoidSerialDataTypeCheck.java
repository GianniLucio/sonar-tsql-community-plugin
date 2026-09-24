package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

/**
 * P109: Avoid SERIAL data type, use IDENTITY or GENERATED
 */
public class AvoidSerialDataTypeCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P109_AvoidSerialDataType";

    @Override
    public void enterBaseDataType(PostgreSqlParser.BaseDataTypeContext ctx) {
        if (ctx.SERIAL() != null || ctx.BIGSERIAL() != null || ctx.SMALLSERIAL() != null) {
            reportIssue(ctx, "Avoid SERIAL data type. Use GENERATED ALWAYS AS IDENTITY or explicit sequences for better control.");
        }
    }
}
