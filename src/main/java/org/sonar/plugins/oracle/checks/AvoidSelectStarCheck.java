package org.sonar.plugins.oracle.checks;

import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class AvoidSelectStarCheck extends OracleCheck {

    public static final String RULE_KEY = "O101_AvoidSelectStar";

    @Override
    public void enterSelectList(OracleParser.SelectListContext context) {
        if (context.STAR() != null) {
            reportIssue(context, "Avoid using 'SELECT *'. Specify column names explicitly.");
        }
    }
}
