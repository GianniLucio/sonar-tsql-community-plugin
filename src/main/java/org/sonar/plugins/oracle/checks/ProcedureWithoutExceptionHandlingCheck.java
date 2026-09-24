package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class ProcedureWithoutExceptionHandlingCheck extends OracleCheck {

    public static final String RULE_KEY = "O110_ProcedureWithoutExceptionHandling";

    @Override
    public void enterCreateProgramUnit(OracleParser.CreateProgramUnitContext ctx) {
        if (ctx.PROCEDURE() == null && ctx.FUNCTION() == null) {
            return;
        }
        for (OracleParser.ProgramUnitTokenContext token : ctx.programUnitToken()) {
            if ("EXCEPTION".equalsIgnoreCase(token.getText())) {
                return;
            }
        }
        reportIssue(ctx, "Procedure/function body does not define an EXCEPTION handling section. Add an EXCEPTION block to handle and log runtime errors instead of letting them propagate unmanaged.");
    }
}
