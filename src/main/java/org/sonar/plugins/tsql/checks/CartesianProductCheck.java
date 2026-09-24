package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class CartesianProductCheck extends TSqlCheck {

    public static final String RULE_KEY = "S116_CartesianProduct";

    @Override
    public void enterSelect_stmt(TSqlParser.Select_stmtContext ctx) {
        if (ctx.table_source().size() > 1 && ctx.where_clause() == null) {
            reportIssue(ctx, "Multiple tables listed in FROM separated by commas without a WHERE clause produce a cartesian product. Use explicit JOIN...ON syntax or add a filtering WHERE clause.");
        }
    }
}
