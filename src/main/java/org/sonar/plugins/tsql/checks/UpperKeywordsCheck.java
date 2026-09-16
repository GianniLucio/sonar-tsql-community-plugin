package org.sonar.plugins.tsql.checks;

import org.antlr.v4.runtime.Token;
import org.sonar.plugins.tsql.antlr.TSqlLexer;
import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UpperKeywordsCheck extends TSqlCheck {

    public static final String RULE_KEY = "S110_UpperKeywords";

    private static final Set<Integer> KEYWORD_TYPES = new HashSet<>(Arrays.asList(
            TSqlLexer.SELECT, TSqlLexer.FROM, TSqlLexer.WHERE, TSqlLexer.INSERT,
            TSqlLexer.INTO, TSqlLexer.UPDATE, TSqlLexer.DELETE, TSqlLexer.MERGE,
            TSqlLexer.SET, TSqlLexer.VALUES, TSqlLexer.CREATE, TSqlLexer.ALTER,
            TSqlLexer.DROP, TSqlLexer.TABLE, TSqlLexer.VIEW, TSqlLexer.PROCEDURE,
            TSqlLexer.PROC, TSqlLexer.FUNCTION, TSqlLexer.TRIGGER, TSqlLexer.INDEX,
            TSqlLexer.JOIN, TSqlLexer.INNER, TSqlLexer.LEFT, TSqlLexer.RIGHT,
            TSqlLexer.GROUP, TSqlLexer.BY, TSqlLexer.HAVING, TSqlLexer.ORDER,
            TSqlLexer.BEGIN, TSqlLexer.END, TSqlLexer.TRAN, TSqlLexer.TRANSACTION,
            TSqlLexer.COMMIT, TSqlLexer.ROLLBACK, TSqlLexer.EXEC, TSqlLexer.EXECUTE
    ));

    @Override
    public void enterEveryRule(org.antlr.v4.runtime.ParserRuleContext ctx) {
        // Can inspect tokens at start of rule context
        if (ctx.getStart() != null) {
            checkToken(ctx.getStart());
        }
        if (ctx.getStop() != null && ctx.getStop() != ctx.getStart()) {
            checkToken(ctx.getStop());
        }
    }

    public void checkToken(Token token) {
        if (token != null && KEYWORD_TYPES.contains(token.getType())) {
            String text = token.getText();
            if (text != null && !text.equals(text.toUpperCase())) {
                reportIssue(token, "Write SQL keyword '" + text + "' in UPPERCASE ('" + text.toUpperCase() + "') for consistency and readability.");
            }
        }
    }
}
