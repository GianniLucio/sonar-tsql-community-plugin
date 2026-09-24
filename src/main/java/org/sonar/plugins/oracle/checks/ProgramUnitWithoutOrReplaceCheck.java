package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class ProgramUnitWithoutOrReplaceCheck extends OracleCheck {

    public static final String RULE_KEY = "O118_ProgramUnitWithoutOrReplace";

    @Override
    public void enterCreateProgramUnit(OracleParser.CreateProgramUnitContext ctx) {
        if (ctx.REPLACE() == null) {
            reportIssue(ctx, "CREATE statement does not use OR REPLACE. Deployment scripts that create procedures, functions, packages, or triggers should be idempotent; add OR REPLACE so re-running the script does not fail because the object already exists.");
        }
    }
}
