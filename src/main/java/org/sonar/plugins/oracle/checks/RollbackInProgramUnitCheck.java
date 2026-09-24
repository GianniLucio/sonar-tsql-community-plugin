package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class RollbackInProgramUnitCheck extends OracleCheck {

    public static final String RULE_KEY = "O119_RollbackInProgramUnit";

    @Override
    public void enterCreateProgramUnit(OracleParser.CreateProgramUnitContext ctx) {
        for (OracleParser.ProgramUnitTokenContext token : ctx.programUnitToken()) {
            if ("ROLLBACK".equalsIgnoreCase(token.getText())) {
                reportIssue(token, "Avoid issuing ROLLBACK inside a procedure/function/package/trigger body; let the calling transaction control rollback boundaries. Inside a trigger this also raises ORA-04092.");
            }
        }
    }
}
