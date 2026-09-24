package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class CommitInProgramUnitCheck extends OracleCheck {

    public static final String RULE_KEY = "O105_CommitInProgramUnit";

    @Override
    public void enterCreateProgramUnit(OracleParser.CreateProgramUnitContext ctx) {
        for (OracleParser.ProgramUnitTokenContext token : ctx.programUnitToken()) {
            if ("COMMIT".equalsIgnoreCase(token.getText())) {
                reportIssue(token, "Avoid issuing COMMIT inside a procedure/function/package body; let the calling transaction control commit boundaries to prevent partial or inconsistent commits.");
            }
        }
    }
}
