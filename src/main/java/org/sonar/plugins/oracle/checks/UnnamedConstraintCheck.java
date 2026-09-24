package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class UnnamedConstraintCheck extends OracleCheck {

    public static final String RULE_KEY = "O109_UnnamedConstraint";

    @Override
    public void enterTableConstraint(OracleParser.TableConstraintContext ctx) {
        if (ctx.CONSTRAINT() == null) {
            reportIssue(ctx, "Table constraint is not explicitly named. Add a 'CONSTRAINT <name>' clause so the system-generated name does not hinder future maintenance and error diagnosis.");
        }
    }
}
