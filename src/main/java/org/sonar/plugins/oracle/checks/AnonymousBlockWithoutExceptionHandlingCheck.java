package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class AnonymousBlockWithoutExceptionHandlingCheck extends OracleCheck {

    public static final String RULE_KEY = "O115_AnonymousBlockWithoutExceptionHandling";

    @Override
    public void enterPlsqlBlock(OracleParser.PlsqlBlockContext ctx) {
        for (OracleParser.PlsqlTokenContext token : ctx.plsqlToken()) {
            if ("EXCEPTION".equalsIgnoreCase(token.getText())) {
                return;
            }
        }
        reportIssue(ctx, "Anonymous PL/SQL block does not define an EXCEPTION handling section. Add an EXCEPTION block to handle and log runtime errors instead of letting them propagate unmanaged.");
    }
}
