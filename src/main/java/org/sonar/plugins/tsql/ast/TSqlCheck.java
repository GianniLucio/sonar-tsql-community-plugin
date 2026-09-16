package org.sonar.plugins.tsql.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sonar.plugins.tsql.antlr.TSqlParserBaseListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all T-SQL static analysis rules.
 */
public abstract class TSqlCheck extends TSqlParserBaseListener {

    private final List<IssueLocation> issues = new ArrayList<>();
    private String fileContent;

    public void init(String fileContent) {
        this.issues.clear();
        this.fileContent = fileContent;
    }

    public List<IssueLocation> getIssues() {
        return Collections.unmodifiableList(issues);
    }

    public String getFileContent() {
        return fileContent;
    }

    /**
     * Report an issue at a specific AST node context.
     */
    protected void reportIssue(ParserRuleContext ctx, String message) {
        if (ctx == null) {
            return;
        }
        Token start = ctx.getStart();
        Token stop = ctx.getStop() != null ? ctx.getStop() : start;
        int startLine = start.getLine();
        int startCol = start.getCharPositionInLine();
        int endLine = stop.getLine();
        int endCol = stop.getCharPositionInLine() + Math.max(1, (stop.getText() != null ? stop.getText().length() : 1));

        issues.add(new IssueLocation(startLine, startCol, endLine, endCol, message));
    }

    /**
     * Report an issue at a specific token.
     */
    protected void reportIssue(Token token, String message) {
        if (token == null) {
            return;
        }
        int line = token.getLine();
        int startCol = token.getCharPositionInLine();
        int length = token.getText() != null ? token.getText().length() : 1;
        issues.add(new IssueLocation(line, startCol, line, startCol + length, message));
    }

    /**
     * Report an issue at a specific terminal node.
     */
    protected void reportIssue(TerminalNode node, String message) {
        if (node != null) {
            reportIssue(node.getSymbol(), message);
        }
    }

    /**
     * Report an issue at a specific line.
     */
    protected void reportIssue(int line, String message) {
        issues.add(new IssueLocation(line, message));
    }
}
