package org.sonar.plugins.oracle.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sonar.plugins.oracle.antlr.OracleParserBaseListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for Oracle SQL and PL/SQL checks.
 */
public abstract class OracleCheck extends OracleParserBaseListener {

    private final List<IssueLocation> issues = new ArrayList<>();
    private String fileContent;

    public void init(String fileContent) {
        issues.clear();
        this.fileContent = fileContent;
    }

    public List<IssueLocation> getIssues() {
        return Collections.unmodifiableList(issues);
    }

    public String getFileContent() {
        return fileContent;
    }

    protected void reportIssue(ParserRuleContext context, String message) {
        if (context == null || context.getStart() == null) {
            return;
        }
        Token start = context.getStart();
        Token stop = context.getStop() == null ? start : context.getStop();
        int endOffset = stop.getCharPositionInLine() + Math.max(1, stop.getText() == null ? 1 : stop.getText().length());
        issues.add(new IssueLocation(start.getLine(), start.getCharPositionInLine(), stop.getLine(), endOffset, message));
    }

    protected void reportIssue(Token token, String message) {
        if (token == null) {
            return;
        }
        int length = token.getText() == null ? 1 : token.getText().length();
        issues.add(new IssueLocation(token.getLine(), token.getCharPositionInLine(), token.getLine(), token.getCharPositionInLine() + length, message));
    }

    protected void reportIssue(TerminalNode node, String message) {
        if (node != null) {
            reportIssue(node.getSymbol(), message);
        }
    }
}
