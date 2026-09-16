package org.sonar.plugins.tsql.ast;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.plugins.tsql.antlr.TSqlLexer;
import org.sonar.plugins.tsql.antlr.TSqlParser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TSqlAstScanner {

    private static final Logger LOG = LoggerFactory.getLogger(TSqlAstScanner.class);

    public static class ScanResult {
        private final ParseTree parseTree;
        private final CommonTokenStream tokenStream;
        private final int ncloc;
        private final int commentLines;
        private final Set<Integer> linesWithCode;
        private final Set<Integer> linesWithComments;
        private final boolean syntaxErrorsEncountered;

        public ScanResult(ParseTree parseTree,
                          CommonTokenStream tokenStream,
                          int ncloc,
                          int commentLines,
                          Set<Integer> linesWithCode,
                          Set<Integer> linesWithComments,
                          boolean syntaxErrorsEncountered) {
            this.parseTree = parseTree;
            this.tokenStream = tokenStream;
            this.ncloc = ncloc;
            this.commentLines = commentLines;
            this.linesWithCode = linesWithCode;
            this.linesWithComments = linesWithComments;
            this.syntaxErrorsEncountered = syntaxErrorsEncountered;
        }

        public ParseTree getParseTree() {
            return parseTree;
        }

        public CommonTokenStream getTokenStream() {
            return tokenStream;
        }

        public int getNcloc() {
            return ncloc;
        }

        public int getCommentLines() {
            return commentLines;
        }

        public Set<Integer> getLinesWithCode() {
            return linesWithCode;
        }

        public Set<Integer> getLinesWithComments() {
            return linesWithComments;
        }

        public boolean hasSyntaxErrors() {
            return syntaxErrorsEncountered;
        }
    }

    public ScanResult parse(String content) {
        TSqlLexer lexer = new TSqlLexer(CharStreams.fromString(content));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        TSqlParser parser = new TSqlParser(tokenStream);

        final boolean[] syntaxErrorOccurred = {false};
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg, RecognitionException e) {
                syntaxErrorOccurred[0] = true;
                LOG.debug("T-SQL syntax error at line {}:{}: {}", line, charPositionInLine, msg);
            }
        });

        ParseTree tree = parser.tsql_file();

        // Calculate metrics
        tokenStream.fill();
        Set<Integer> codeLines = new HashSet<>();
        Set<Integer> commentLines = new HashSet<>();

        for (Token token : tokenStream.getTokens()) {
            int line = token.getLine();
            if (token.getChannel() == TSqlLexer.HIDDEN) {
                if (token.getType() == TSqlLexer.LINE_COMMENT || token.getType() == TSqlLexer.BLOCK_COMMENT) {
                    int startLine = token.getLine();
                    String text = token.getText();
                    int linesInComment = text.split("\r\n|\r|\n", -1).length;
                    for (int i = 0; i < linesInComment; i++) {
                        commentLines.add(startLine + i);
                    }
                }
            } else if (token.getType() != Token.EOF) {
                codeLines.add(line);
            }
        }

        return new ScanResult(
                tree,
                tokenStream,
                codeLines.size(),
                commentLines.size(),
                codeLines,
                commentLines,
                syntaxErrorOccurred[0]
        );
    }

    public void executeRules(ScanResult scanResult, String fileContent, List<TSqlCheck> checks) {
        ParseTreeWalker walker = new ParseTreeWalker();
        for (TSqlCheck check : checks) {
            check.init(fileContent);
            if (scanResult.getParseTree() != null) {
                walker.walk(check, scanResult.getParseTree());
            }
        }
    }
}
