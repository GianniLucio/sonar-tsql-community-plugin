package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

/**
 * P119: DROP COLUMN/DROP CONSTRAINT should use IF EXISTS
 */
public class DropWithoutIfExistsCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P119_DropWithoutIfExists";

    @Override
    public void enterAlterTableAction(PostgreSqlParser.AlterTableActionContext ctx) {
        if (ctx.DROP() != null && ctx.IF() == null) {
            reportIssue(ctx, "Use 'IF EXISTS' with DROP COLUMN/DROP CONSTRAINT so the migration does not fail when the object was already removed.");
        }
    }
}
