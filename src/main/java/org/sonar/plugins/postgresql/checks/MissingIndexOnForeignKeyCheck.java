package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

/**
 * P108: Foreign keys should have an index
 */
public class MissingIndexOnForeignKeyCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P108_MissingIndexOnForeignKey";

    @Override
    public void enterTableConstraint(PostgreSqlParser.TableConstraintContext ctx) {
        if (ctx.FOREIGN() != null && ctx.KEY() != null) {
            reportIssue(ctx, "Consider creating an index on foreign key columns for better performance.");
        }
    }

    @Override
    public void enterColumnConstraint(PostgreSqlParser.ColumnConstraintContext ctx) {
        if (ctx.REFERENCES() != null) {
            reportIssue(ctx, "Consider creating an index on foreign key columns for better performance.");
        }
    }
}
