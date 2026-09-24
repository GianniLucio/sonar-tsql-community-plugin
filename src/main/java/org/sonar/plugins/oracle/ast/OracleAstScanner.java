package org.sonar.plugins.oracle.ast;

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
import org.sonar.plugins.oracle.antlr.OracleLexer;
import org.sonar.plugins.oracle.antlr.OracleParser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OracleAstScanner {

    private static final Logger LOG = LoggerFactory.getLogger(OracleAstScanner.class);

    public static class ScanResult {
        private final ParseTree parseTree;
        private final CommonTokenStream tokenStream;
        private final int ncloc;
        private final int commentLines;
        private final boolean syntaxErrorsEncountered;

        public ScanResult(ParseTree parseTree, CommonTokenStream tokenStream, int ncloc,
                          int commentLines, boolean syntaxErrorsEncountered) {
            this.parseTree = parseTree;
            this.tokenStream = tokenStream;
            this.ncloc = ncloc;
            this.commentLines = commentLines;
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

        public boolean hasSyntaxErrors() {
            return syntaxErrorsEncountered;
        }
    }

    public ScanResult parse(String content) {
        OracleLexer lexer = new OracleLexer(CharStreams.fromString(content));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        OracleParser parser = new OracleParser(tokenStream);
        final boolean[] syntaxErrorOccurred = {false};

        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg, RecognitionException e) {
                syntaxErrorOccurred[0] = true;
                LOG.debug("Oracle syntax error at line {}:{}: {}", line, charPositionInLine, msg);
            }
        });

        ParseTree tree = parser.oracleFile();
        tokenStream.fill();
        Set<Integer> codeLines = new HashSet<>();
        Set<Integer> commentLines = new HashSet<>();
        for (Token token : tokenStream.getTokens()) {
            if (token.getChannel() == OracleLexer.HIDDEN) {
                if (token.getType() == OracleLexer.LINE_COMMENT || token.getType() == OracleLexer.BLOCK_COMMENT) {
                    int startLine = token.getLine();
                    String text = token.getText() == null ? "" : token.getText();
                    int lines = text.split("\\r\\n|\\r|\\n", -1).length;
                    for (int i = 0; i < lines; i++) {
                        commentLines.add(startLine + i);
                    }
                }
            } else if (token.getType() != Token.EOF) {
                codeLines.add(token.getLine());
            }
        }
        return new ScanResult(tree, tokenStream, codeLines.size(), commentLines.size(), syntaxErrorOccurred[0]);
    }

    public void executeRules(ScanResult scanResult, String fileContent, List<OracleCheck> checks) {
        ParseTreeWalker walker = new ParseTreeWalker();
        for (OracleCheck check : checks) {
            check.init(fileContent);
            if (scanResult.getParseTree() != null) {
                walker.walk(check, scanResult.getParseTree());
            }
        }
    }
}
