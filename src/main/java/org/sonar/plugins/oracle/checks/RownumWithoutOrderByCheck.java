package org.sonar.plugins.oracle.checks;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sonar.plugins.oracle.antlr.OracleLexer;
import org.sonar.plugins.oracle.antlr.OracleParser;
import org.sonar.plugins.oracle.ast.OracleCheck;

public class RownumWithoutOrderByCheck extends OracleCheck {

    public static final String RULE_KEY = "O107_RownumWithoutOrderBy";

    @Override
    public void enterSelectStatement(OracleParser.SelectStatementContext ctx) {
        if (ctx.ORDER() == null && containsRownum(ctx)) {
            reportIssue(ctx, "ROWNUM is assigned before ORDER BY is applied, so filtering or selecting it without an explicit ORDER BY returns a non-deterministic subset of rows. Add an ORDER BY clause or use ROW_NUMBER() instead.");
        }
    }

    private boolean containsRownum(ParseTree node) {
        if (node instanceof TerminalNode) {
            Token symbol = ((TerminalNode) node).getSymbol();
            return symbol != null && symbol.getType() == OracleLexer.ROWNUM;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            if (containsRownum(node.getChild(i))) {
                return true;
            }
        }
        return false;
    }
}
