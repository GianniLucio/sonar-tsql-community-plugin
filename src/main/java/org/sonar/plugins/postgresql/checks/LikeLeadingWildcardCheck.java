package org.sonar.plugins.postgresql.checks;

import org.sonar.plugins.postgresql.antlr.PostgreSqlParser;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;

/**
 * P117: Avoid LIKE/ILIKE patterns with a leading wildcard
 */
public class LikeLeadingWildcardCheck extends PostgreSqlCheck {

    public static final String RULE_KEY = "P117_LikeLeadingWildcard";

    @Override
    public void enterLikeExpr(PostgreSqlParser.LikeExprContext ctx) {
        if (ctx.expression().size() < 2) {
            return;
        }
        PostgreSqlParser.ExpressionContext pattern = ctx.expression(1);
        if (pattern instanceof PostgreSqlParser.StringLiteralExprContext) {
            String text = pattern.getText();
            String unquoted = text.length() >= 2 ? text.substring(1, text.length() - 1) : text;
            if (unquoted.startsWith("%") || unquoted.startsWith("_")) {
                reportIssue(ctx, "A LIKE/ILIKE pattern starting with a wildcard ('%' or '_') cannot use a standard B-tree index and forces a full table scan. Consider a trigram (pg_trgm) index or restructure the search.");
            }
        }
    }
}
