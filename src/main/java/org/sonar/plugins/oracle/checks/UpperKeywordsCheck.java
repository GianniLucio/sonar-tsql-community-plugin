package org.sonar.plugins.oracle.checks;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.sonar.plugins.oracle.antlr.OracleLexer;
import org.sonar.plugins.oracle.ast.OracleCheck;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UpperKeywordsCheck extends OracleCheck {

    public static final String RULE_KEY = "O103_UpperKeywords";

    private static final Set<Integer> KEYWORDS = new HashSet<>(Arrays.asList(
            OracleLexer.SELECT, OracleLexer.FROM, OracleLexer.WHERE, OracleLexer.INSERT,
            OracleLexer.INTO, OracleLexer.UPDATE, OracleLexer.DELETE, OracleLexer.CREATE,
            OracleLexer.ALTER, OracleLexer.DROP, OracleLexer.TABLE, OracleLexer.PROCEDURE,
            OracleLexer.FUNCTION, OracleLexer.PACKAGE, OracleLexer.TRIGGER, OracleLexer.BEGIN,
            OracleLexer.END, OracleLexer.DECLARE, OracleLexer.EXCEPTION, OracleLexer.PRIMARY,
            OracleLexer.KEY, OracleLexer.REFERENCES, OracleLexer.GROUP, OracleLexer.ORDER,
            OracleLexer.BY, OracleLexer.CONNECT, OracleLexer.START, OracleLexer.WITH,
            OracleLexer.PRIOR, OracleLexer.MERGE, OracleLexer.USING, OracleLexer.COMMIT,
            OracleLexer.ROLLBACK
    ));

    @Override
    public void enterEveryRule(ParserRuleContext context) {
        checkToken(context.getStart());
        if (context.getStop() != null && context.getStop() != context.getStart()) {
            checkToken(context.getStop());
        }
    }

    private void checkToken(Token token) {
        if (token != null && KEYWORDS.contains(token.getType()) && token.getText() != null
                && !token.getText().equals(token.getText().toUpperCase())) {
            reportIssue(token, "Oracle SQL keywords should be written in UPPERCASE for readability.");
        }
    }
}
