package org.sonar.plugins.postgresql.checks;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.sonar.plugins.postgresql.antlr.PostgreSqlLexer;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * P107: Keywords should be in UPPERCASE
 */
public class UpperKeywordsCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P107_UpperKeywords";

    private static final Set<Integer> KEYWORD_TYPES = new HashSet<>(Arrays.asList(
            PostgreSqlLexer.SELECT, PostgreSqlLexer.FROM, PostgreSqlLexer.WHERE,
            PostgreSqlLexer.INSERT, PostgreSqlLexer.INTO, PostgreSqlLexer.VALUES,
            PostgreSqlLexer.UPDATE, PostgreSqlLexer.DELETE, PostgreSqlLexer.SET,
            PostgreSqlLexer.CREATE, PostgreSqlLexer.ALTER, PostgreSqlLexer.DROP,
            PostgreSqlLexer.TABLE, PostgreSqlLexer.INDEX, PostgreSqlLexer.ON,
            PostgreSqlLexer.ORDER, PostgreSqlLexer.BY, PostgreSqlLexer.DESC,
            PostgreSqlLexer.ASC, PostgreSqlLexer.AND, PostgreSqlLexer.OR,
            PostgreSqlLexer.NOT, PostgreSqlLexer.DECLARE, PostgreSqlLexer.CURSOR,
            PostgreSqlLexer.EXECUTE, PostgreSqlLexer.EXEC, PostgreSqlLexer.COMMIT,
            PostgreSqlLexer.ROLLBACK, PostgreSqlLexer.BEGIN, PostgreSqlLexer.WITH,
            PostgreSqlLexer.JOIN, PostgreSqlLexer.INNER, PostgreSqlLexer.LEFT,
            PostgreSqlLexer.RIGHT, PostgreSqlLexer.FULL, PostgreSqlLexer.OVER,
            PostgreSqlLexer.PARTITION, PostgreSqlLexer.ARRAY
    ));

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        if (ctx.getStart() != null) {
            checkToken(ctx.getStart());
        }
        if (ctx.getStop() != null && ctx.getStop() != ctx.getStart()) {
            checkToken(ctx.getStop());
        }
    }

    private void checkToken(Token token) {
        if (token != null && KEYWORD_TYPES.contains(token.getType())) {
            String text = token.getText();
            if (text != null && !text.equals(text.toUpperCase())) {
                reportIssue(token, "SQL keywords should be written in UPPERCASE for readability.");
            }
        }
    }
}
