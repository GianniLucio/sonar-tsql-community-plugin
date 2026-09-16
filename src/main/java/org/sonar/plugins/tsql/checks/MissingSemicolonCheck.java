package org.sonar.plugins.tsql.checks;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class MissingSemicolonCheck extends TSqlCheck {

    public static final String RULE_KEY = "S104_MissingSemicolon";

    @Override
    public void enterStatement_list(TSqlParser.Statement_listContext ctx) {
        int childCount = ctx.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TSqlParser.StatementContext stmt) {
                // Check if following token is a semicolon
                boolean hasSemicolon = false;
                if (i + 1 < childCount && ";".equals(ctx.getChild(i + 1).getText())) {
                    hasSemicolon = true;
                }
                // Exclude compound statements like IF, WHILE, TRY_CATCH, BLOCK where inner statements have their own terminators
                if (!hasSemicolon && isSingleTerminatableStatement(stmt)) {
                    reportIssue(stmt, "Terminate this T-SQL statement with a semicolon ';'. Un-terminated statements are deprecated in Microsoft SQL Server.");
                }
            }
        }
    }

    private boolean isSingleTerminatableStatement(TSqlParser.StatementContext stmt) {
        return stmt.select_stmt() != null
                || stmt.insert_stmt() != null
                || stmt.update_stmt() != null
                || stmt.delete_stmt() != null
                || stmt.merge_stmt() != null
                || stmt.declare_stmt() != null
                || stmt.set_stmt() != null
                || stmt.exec_stmt() != null
                || stmt.create_table_stmt() != null
                || stmt.create_view_stmt() != null
                || stmt.create_index_stmt() != null
                || stmt.drop_stmt() != null
                || stmt.begin_tran_stmt() != null
                || stmt.commit_tran_stmt() != null
                || stmt.rollback_tran_stmt() != null
                || stmt.print_stmt() != null
                || stmt.throw_stmt() != null
                || stmt.raiserror_stmt() != null
                || stmt.return_stmt() != null;
    }
}
