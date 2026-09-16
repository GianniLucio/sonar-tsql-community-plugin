package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class DynamicSqlInjectionCheck extends TSqlCheck {

    public static final String RULE_KEY = "S107_DynamicSqlInjection";

    @Override
    public void enterExec_stmt(TSqlParser.Exec_stmtContext ctx) {
        // Check for EXEC (expr + expr) or dynamic string in EXEC (...)
        if (ctx.LPAREN() != null) {
            boolean hasConcat = (ctx.PLUS() != null && !ctx.PLUS().isEmpty());
            for (TSqlParser.ExpressionContext expr : ctx.expression()) {
                if (containsStringConcatenation(expr)) {
                    hasConcat = true;
                    break;
                }
            }
            if (hasConcat) {
                reportIssue(ctx, "Avoid dynamic SQL execution using string concatenation with '+'. Use 'sp_executesql' with parameterized queries to prevent SQL Injection vulnerabilities.");
                return;
            }
        }

        // Check for EXEC sp_executesql with string concatenation
        if (ctx.proc_name() != null) {
            String procName = ctx.proc_name().getText().toLowerCase();
            if (procName.endsWith("sp_executesql")) {
                for (TSqlParser.Exec_paramContext param : ctx.exec_param()) {
                    if (param.expression() != null && containsStringConcatenation(param.expression())) {
                        reportIssue(param, "Dynamic SQL query string passed to 'sp_executesql' should not be constructed with concatenation. Use typed parameters.");
                        break;
                    }
                }
            }
        }
    }

    private boolean containsStringConcatenation(TSqlParser.ExpressionContext expr) {
        if (expr instanceof TSqlParser.ArithExprContext arith) {
            if (arith.PLUS() != null) {
                return true;
            }
        }
        for (int i = 0; i < expr.getChildCount(); i++) {
            if (expr.getChild(i) instanceof TSqlParser.ExpressionContext childExpr) {
                if (containsStringConcatenation(childExpr)) {
                    return true;
                }
            }
        }
        return false;
    }
}
