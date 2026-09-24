package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

/**
 * P120: Foreign key constraints should declare an explicit ON DELETE action
 */
public class ForeignKeyWithoutOnDeleteActionCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P120_ForeignKeyWithoutOnDeleteAction";

    @Override
    public void enterTableConstraint(PostgreSqlParser.TableConstraintContext ctx) {
        if (ctx.FOREIGN() != null && ctx.REFERENCES() != null && ctx.DELETE() == null) {
            reportIssue(ctx, "Foreign key constraint does not declare an explicit 'ON DELETE' action. The implicit NO ACTION default can cause unexpected reference errors; specify CASCADE or RESTRICT deliberately.");
        }
    }
}
