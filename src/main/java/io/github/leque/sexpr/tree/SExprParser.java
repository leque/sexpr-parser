package io.github.leque.sexpr.tree;

import io.github.leque.sexpr.antlr.SchemeBaseListener;
import io.github.leque.sexpr.antlr.SchemeLexer;
import io.github.leque.sexpr.antlr.SchemeParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SExprParser {
    private SExprParser() {
    }

    public static SExpr parse(String input) {
        return parse(CharStreams.fromString(input));
    }

    public static SExpr parseSymbol(String input) {
        SchemeLexer lexer = new SchemeLexer(CharStreams.fromString(input));
        TokenStream tokens = new CommonTokenStream(lexer);
        SchemeParser parser = new SchemeParser(tokens);
        SExprListener sexprListener = new SExprListener();
        ParseTreeWalker.DEFAULT.walk(sexprListener, parser.identifier());
        return sexprListener.getParsedExpression();
    }

    private static SExpr parse(CharStream inputStream) {
        SchemeLexer lexer = new SchemeLexer(inputStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        SchemeParser parser = new SchemeParser(tokens);
        SExprListener sexprListener = new SExprListener();
        SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
        parser.addErrorListener(syntaxErrorListener);
        ParseTreeWalker.DEFAULT.walk(sexprListener, parser.sexpr());
        if (syntaxErrorListener.getSyntaxErrors().isEmpty())
            return sexprListener.getParsedExpression();
        else {
            throw new RuntimeException(syntaxErrorListener.toString());
        }
    }

    public static class SyntaxErrorListener extends BaseErrorListener {
        private final List<SyntaxError> syntaxErrors = new ArrayList<>();

        SyntaxErrorListener() {
        }

        List<SyntaxError> getSyntaxErrors() {
            return syntaxErrors;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            syntaxErrors.add(new SyntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e));
        }

        @Override
        public String toString() {
            return Utils.join(syntaxErrors.iterator(), "\n");
        }
    }

    public static class SyntaxError {
        private final Recognizer<?, ?> recognizer;
        private final Object offendingSymbol;
        private final int line;
        private final int charPositionInLine;
        private final String message;
        private final RecognitionException e;

        SyntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            this.recognizer = recognizer;
            this.offendingSymbol = offendingSymbol;
            this.line = line;
            this.charPositionInLine = charPositionInLine;
            this.message = msg;
            this.e = e;
        }

        public Recognizer<?, ?> getRecognizer() {
            return recognizer;
        }

        public Object getOffendingSymbol() {
            return offendingSymbol;
        }

        public int getLine() {
            return line;
        }

        public int getCharPositionInLine() {
            return charPositionInLine;
        }

        public String getMessage() {
            return message;
        }

        public RecognitionException getException() {
            return e;
        }
    }

    public static class SExprListener extends SchemeBaseListener {
        private final ArrayDeque<List<SExpr>> parserStack;

        public SExprListener() {
            parserStack = new ArrayDeque<>();
            newEnv();
        }

        public SExpr getParsedExpression() {
            if (parserStack.size() != 1) {
                throw new IllegalStateException("inconsistent parser stack: parser-stack.size()=" + parserStack.size());
            }
            List<SExpr> parsed = parserStack.getFirst();
            if (parsed.size() != 1) {
                throw new IllegalStateException("inconsistent parser stack: parser-stack.getFirst().size=" + parsed.size());
            }
            return parsed.get(0);
        }

        private void newEnv() {
            parserStack.push(new ArrayList<>());
        }

        private List<SExpr> popEnv() {
            return parserStack.pop();
        }

        private void pushValue(SExpr value) {
            parserStack.getFirst().add(value);
        }

        private SExpr popValue() {
            List<SExpr> xs = parserStack.getFirst();
            return xs.remove(xs.size() - 1);
        }

        private String inputText(ParserRuleContext ctx) {
            int a = ctx.start.getStartIndex();
            int b = ctx.stop.getStopIndex();
            Interval interval = new Interval(a, b);
            return ctx.start.getInputStream().getText(interval);
        }

        @Override
        public void exitTrue_(SchemeParser.True_Context ctx) {
            pushValue(SExprs.trueValue());
            super.exitTrue_(ctx);
        }

        @Override
        public void exitFalse_(SchemeParser.False_Context ctx) {
            pushValue(SExprs.falseValue());
            super.exitFalse_(ctx);
        }

        @Override
        public void exitInteger(SchemeParser.IntegerContext ctx) {
            pushValue(SExprs.integerValue(new BigInteger(inputText(ctx))));
            super.exitInteger(ctx);
        }

        @Override
        public void exitFlonum(SchemeParser.FlonumContext ctx) {
            pushValue(SExprs.flonumValue(new BigDecimal(inputText(ctx))));
            super.exitFlonum(ctx);
        }

        @Override
        public void exitString(SchemeParser.StringContext ctx) {
            String text = inputText(ctx);
            pushValue(SExprs.stringValue(interpretEscapeSequences(text.substring(1, text.length() - 1))));
            super.exitString(ctx);
        }

        private static Map<Character, Character> escapeSequences = new HashMap<Character, Character>() {{
            put('a', '\u0007');
            put('b', '\b');
            put('n', '\n');
            put('r', '\r');
            put('t', '\t');
            put('|', '|');
            put('\"', '\"');
        }};

        private String interpretEscapeSequences(String input) {
            StringBuilder builder = new StringBuilder();
            int pos = 0;
            int p = 0;
            while ((p = input.indexOf('\\', pos)) >= 0) {
                builder.append(input.substring(pos, p));
                char c = input.charAt(p + 1);
                if (escapeSequences.containsKey(c)) {
                    builder.append(escapeSequences.get(c));
                    pos = p + 2;
                } else if (c == 'X' || c == 'x') {
                    int i = input.indexOf(';', p);
                    char[] cs = Character.toChars(Integer.parseInt(input.substring(p + 2, i), 16));
                    builder.append(cs);
                    pos = i + 1;
                } else {
                    throw new IllegalStateException("unknown escape sequence: \\" + c);
                }
            }
            builder.append(input.substring(pos));
            return builder.toString();
        }

        @Override
        public void exitIdentifier(SchemeParser.IdentifierContext ctx) {
            pushValue(SExprs.symbolValue(inputText(ctx)));
            super.exitIdentifier(ctx);
        }

        @Override
        public void exitEscapedIdentifier(SchemeParser.EscapedIdentifierContext ctx) {
            String text = inputText(ctx);
            pushValue(SExprs.symbolValue(interpretEscapeSequences(text.substring(1, text.length() - 1))));
            super.exitEscapedIdentifier(ctx);
        }

        @Override
        public void exitQuoted(SchemeParser.QuotedContext ctx) {
            expandAbbr("quote");
            super.exitQuoted(ctx);
        }

        @Override
        public void exitQuasiquoted(SchemeParser.QuasiquotedContext ctx) {
            expandAbbr("quasiquote");
            super.exitQuasiquoted(ctx);
        }

        @Override
        public void exitUnquoted(SchemeParser.UnquotedContext ctx) {
            expandAbbr("unquote");
            super.exitUnquoted(ctx);
        }

        @Override
        public void exitUnquoteSplicinged(SchemeParser.UnquoteSplicingedContext ctx) {
            expandAbbr("unquote-splicing");
            super.exitUnquoteSplicinged(ctx);
        }

        private void expandAbbr(String sym) {
            SExpr value = popValue();
            pushValue(SExprs.listValue(SExprs.symbolValue(sym), value));
            return;
        }

        @Override
        public void exitDatumComment(SchemeParser.DatumCommentContext ctx) {
            popValue();
            super.exitDatumComment(ctx);
        }

        @Override
        public void enterList(SchemeParser.ListContext ctx) {
            super.enterList(ctx);
            newEnv();
        }

        @Override
        public void exitList(SchemeParser.ListContext ctx) {
            pushValue(SExprs.listValue(popEnv()));
            super.exitList(ctx);
        }

        @Override
        public void enterVector(SchemeParser.VectorContext ctx) {
            super.enterVector(ctx);
            newEnv();
        }

        @Override
        public void exitVector(SchemeParser.VectorContext ctx) {
            pushValue(SExprs.vectorValue(popEnv()));
            super.exitVector(ctx);
        }
    }
}
