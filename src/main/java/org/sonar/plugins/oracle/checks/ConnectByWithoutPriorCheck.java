package org.sonar.plugins.oracle.checks;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sonar.plugins.oracle.antlr.OracleLexer;
import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class ConnectByWithoutPriorCheck extends OracleCheck {

    public static final String RULE_KEY = "O112_ConnectByWithoutPrior";

    @Override
    public void enterSelectStatement(OracleParser.SelectStatementContext ctx) {
        if (ctx.CONNECT() != null && !containsPrior(ctx)) {
            reportIssue(ctx, "CONNECT BY clause does not reference PRIOR. Without a PRIOR comparison the hierarchical relationship is undefined and can lead to an infinite loop or ORA-01436.");
        }
    }

    private boolean containsPrior(ParseTree node) {
        if (node instanceof TerminalNode) {
            Token symbol = ((TerminalNode) node).getSymbol();
            return symbol != null && symbol.getType() == OracleLexer.PRIOR;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            if (containsPrior(node.getChild(i))) {
                return true;
            }
        }
        return false;
    }
}
