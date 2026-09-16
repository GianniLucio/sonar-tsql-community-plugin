package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class AvoidSelectStarCheck extends TSqlCheck {

    public static final String RULE_KEY = "S101_AvoidSelectStar";

    @Override
    public void enterSelectStar(TSqlParser.SelectStarContext ctx) {
        reportIssue(ctx, "Avoid using 'SELECT *'. Specify column names explicitly to improve performance, maintainability, and avoid unexpected schema breakage.");
    }

    @Override
    public void enterSelectTableStar(TSqlParser.SelectTableStarContext ctx) {
        reportIssue(ctx, "Avoid using 'table.*' in SELECT clause. Explicitly list required column names.");
    }
}
