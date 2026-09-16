package org.sonar.plugins.tsql.checks;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class TransactionXactAbortCheck extends TSqlCheck {

    public static final String RULE_KEY = "S106_TransactionXactAbort";
    private boolean hasXactAbortOn = false;

    @Override
    public void init(String fileContent) {
        super.init(fileContent);
        this.hasXactAbortOn = false;
    }

    @Override
    public void enterSet_stmt(TSqlParser.Set_stmtContext ctx) {
        if (ctx.XACT_ABORT() != null && ctx.ON() != null) {
            hasXactAbortOn = true;
        }
    }

    @Override
    public void enterBegin_tran_stmt(TSqlParser.Begin_tran_stmtContext ctx) {
        if (!hasXactAbortOn && !isInsideTryCatch(ctx)) {
            reportIssue(ctx, "Explicit transactions should either have 'SET XACT_ABORT ON' configured or be enclosed inside a 'BEGIN TRY ... BEGIN CATCH' block to prevent orphaned transactions on error.");
        }
    }

    private boolean isInsideTryCatch(ParserRuleContext ctx) {
        ParserRuleContext parent = ctx.getParent();
        while (parent != null) {
            if (parent instanceof TSqlParser.Try_catch_stmtContext) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }
}
