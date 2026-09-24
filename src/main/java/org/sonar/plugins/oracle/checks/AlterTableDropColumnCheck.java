package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class AlterTableDropColumnCheck extends OracleCheck {

    public static final String RULE_KEY = "O116_AlterTableDropColumn";

    @Override
    public void enterAlterTableAction(OracleParser.AlterTableActionContext ctx) {
        if (ctx.DROP() != null && ctx.COLUMN() != null) {
            reportIssue(ctx, "ALTER TABLE ... DROP COLUMN permanently removes data and cannot be rolled back once committed. Consider marking the column UNUSED first, or verify a backup/rollback plan before dropping it.");
        }
    }
}
