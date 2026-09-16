package org.sonar.plugins.tsql.ast;

public class IssueLocation {

    private final int startLine;
    private final int startLineOffset;
    private final int endLine;
    private final int endLineOffset;
    private final String message;

    public IssueLocation(int startLine, int startLineOffset, int endLine, int endLineOffset, String message) {
        this.startLine = startLine;
        this.startLineOffset = startLineOffset;
        this.endLine = endLine;
        this.endLineOffset = endLineOffset;
        this.message = message;
    }

    public IssueLocation(int line, String message) {
        this(line, 0, line, 0, message);
    }

    public int getStartLine() {
        return startLine;
    }

    public int getStartLineOffset() {
        return startLineOffset;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getEndLineOffset() {
        return endLineOffset;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "IssueLocation{" +
                "startLine=" + startLine +
                ", startLineOffset=" + startLineOffset +
                ", endLine=" + endLine +
                ", endLineOffset=" + endLineOffset +
                ", message='" + message + '\'' +
                '}';
    }
}
