package org.sonar.plugins.tsql.checks;

import org.antlr.v4.runtime.ParserRuleContext;
import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class ProcedureWithoutErrorHandlingCheck extends TSqlCheck {

    public static final String RULE_KEY = "S120_ProcedureWithoutErrorHandling";

    @Override
    public void enterCreate_procedure_stmt(TSqlParser.Create_procedure_stmtContext ctx) {
        check(ctx.statement_list(), ctx);
    }

    @Override
    public void enterAlter_procedure_stmt(TSqlParser.Alter_procedure_stmtContext ctx) {
        check(ctx.statement_list(), ctx);
    }

    private void check(TSqlParser.Statement_listContext statementList, ParserRuleContext ctx) {
        if (statementList == null) {
            return;
        }
        for (TSqlParser.StatementContext statement : statementList.statement()) {
            if (statement.try_catch_stmt() != null) {
                return;
            }
        }
        reportIssue(ctx, "Stored procedure body should handle errors with a 'BEGIN TRY ... END TRY / BEGIN CATCH ... END CATCH' block to avoid unhandled exceptions propagating uncontrolled.");
    }
}
