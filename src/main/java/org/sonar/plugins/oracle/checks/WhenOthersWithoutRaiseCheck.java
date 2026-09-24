package org.sonar.plugins.oracle.checks;

import org.antlr.v4.runtime.ParserRuleContext;
import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

import java.util.ArrayList;
import java.util.List;

public class WhenOthersWithoutRaiseCheck extends OracleCheck {

    public static final String RULE_KEY = "O106_WhenOthersWithoutRaise";

    @Override
    public void enterCreateProgramUnit(OracleParser.CreateProgramUnitContext ctx) {
        checkTokens(ctx.programUnitToken());
    }

    @Override
    public void enterPlsqlBlock(OracleParser.PlsqlBlockContext ctx) {
        List<ParserRuleContext> tokens = new ArrayList<>(ctx.declarationToken());
        tokens.addAll(ctx.plsqlToken());
        checkTokens(tokens);
    }

    private void checkTokens(List<? extends ParserRuleContext> tokens) {
        for (int i = 0; i < tokens.size() - 1; i++) {
            if ("WHEN".equalsIgnoreCase(tokens.get(i).getText()) && "OTHERS".equalsIgnoreCase(tokens.get(i + 1).getText())
                    && !hasRaiseBeforeNextHandler(tokens, i + 2)) {
                reportIssue(tokens.get(i), "Exception handler 'WHEN OTHERS' should re-raise the exception (e.g. RAISE;) instead of silently swallowing it.");
            }
        }
    }

    private boolean hasRaiseBeforeNextHandler(List<? extends ParserRuleContext> tokens, int from) {
        for (int i = from; i < tokens.size(); i++) {
            String text = tokens.get(i).getText();
            if ("WHEN".equalsIgnoreCase(text)) {
                return false;
            }
            if ("RAISE".equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }
}
