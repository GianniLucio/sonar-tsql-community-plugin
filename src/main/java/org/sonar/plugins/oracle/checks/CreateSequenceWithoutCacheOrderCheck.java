package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class CreateSequenceWithoutCacheOrderCheck extends OracleCheck {

    public static final String RULE_KEY = "O113_CreateSequenceWithoutCacheOrder";

    @Override
    public void enterCreateSequenceStatement(OracleParser.CreateSequenceStatementContext ctx) {
        boolean hasCacheOption = false;
        boolean hasOrderOption = false;
        for (OracleParser.SequenceOptionContext option : ctx.sequenceOption()) {
            if (option.CACHE() != null || option.NOCACHE() != null) {
                hasCacheOption = true;
            }
            if (option.ORDER() != null || option.NOORDER() != null) {
                hasOrderOption = true;
            }
        }
        if (!hasCacheOption || !hasOrderOption) {
            reportIssue(ctx, "CREATE SEQUENCE does not explicitly specify CACHE/NOCACHE and ORDER/NOORDER. Relying on database defaults can cause value gaps or non-deterministic ordering across RAC instances; specify both clauses explicitly.");
        }
    }
}
