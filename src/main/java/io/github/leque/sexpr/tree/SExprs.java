package io.github.leque.sexpr.tree;

import org.antlr.v4.runtime.misc.Pair;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SExprs {
    private SExprs() {
    }

    public static SExpr trueValue() {
        return BooleanValue.TRUE;
    }

    public static SExpr falseValue() {
        return BooleanValue.FALSE;
    }

    public static SExpr characterValue(int codePoint) {
        if (Character.isDefined(codePoint) == false)
            throw new IllegalArgumentException(String.format("argument out of range: 0x%x", codePoint));
        return new CharacterValue(codePoint);
    }

    public static SExpr numberValue(long repr) {
        return new NumberValue(BigDecimal.valueOf(repr));
    }

    public static SExpr numberValue(BigInteger i) {
        return new NumberValue(new BigDecimal(i));
    }

    public static SExpr numberValue(BigDecimal repr) {
        return new NumberValue(repr);
    }

    public static SExpr numberValue(String repr) {
        return new NumberValue(new BigDecimal(repr));
    }

    public static SExpr negativeInfinityValue() {
        return InfinityValue.NEGATIVE;
    }

    public static SExpr positiveInfinityValue() {
        return InfinityValue.POSITIVE;
    }

    public static SExpr nanValue() {
        return NanValue.INSTANCE;
    }

    public static SExpr stringValue(String repr) {
        return new StringValue(repr);
    }

    public static SExpr symbolValue(String repr) {
        return new SymbolValue(repr);
    }

    public static SExpr bytevectorValue(byte... elems) {
        return new BytevectorValue(elems);
    }

    public static SExpr listValue(List<SExpr> repr) {
        return new ListValue(repr);
    }

    public static SExpr listValue(SExpr... reprs) {
        return new ListValue(Arrays.asList(reprs));
    }

    public static SExpr dottedListValue(List<SExpr> elems, SExpr end) {
        if (elems.isEmpty()) {
            throw new IllegalArgumentException("dotted-list should have 2 or more elements");
        }
        return new DottedListValue(elems, end);
    }

    public static SExpr dottedListValue(SExpr elem1, SExpr elem2, SExpr... elems) {
        List<SExpr> es = new ArrayList<>(elems.length + 2);
        es.add(elem1);
        es.add(elem2);
        es.addAll(Arrays.asList(elems));
        return dottedListValue(es.subList(0, es.size() - 1), es.get(es.size() - 1));
    }

    public static SExpr vectorValue(List<SExpr> repr) {
        return new VectorValue(repr);
    }

    public static SExpr vectorValue(SExpr... reprs) {
        return new VectorValue(Arrays.asList(reprs));
    }

    public static final String QUOTE_NAME = "quote";

    public static final String QUASIQUOTE_NAME = "quasiquote";

    public static final String UNQUOTE_NAME = "unquote";

    public static final String UNQUOTE_SPLICING_NAME = "unquote-splicing";

    public static boolean isPositiveInf(String name) {
        return name.equalsIgnoreCase("+inf.0");
    }

    public static boolean isNegativeInf(String name) {
        return name.equalsIgnoreCase("-inf.0");
    }

    public static boolean isPositiveNaN(String name) {
        return name.equalsIgnoreCase("+nan.0");
    }

    public static boolean isNegativeNaN(String name) {
        return name.equalsIgnoreCase("-nan.0");
    }

    public static boolean isNan(String name) {
        return isPositiveNaN(name) || isNegativeNaN(name);
    }

    public static boolean isPositiveI(String name) {
        return name.equalsIgnoreCase("+i");
    }

    public static boolean isNegativeI(String name) {
        return name.equalsIgnoreCase("-i");
    }

    private static void writeString(int[] codePoints, char quote, Appendable buffer) throws IOException {
        buffer.append(quote);
        for (int cp : codePoints) {
            if (cp == quote) {
                buffer.append("\\");
                buffer.append(quote);
            } else if (cp == '\\') {
                buffer.append("\\\\");
            } else if (Character.isISOControl(cp)) {
                buffer.append(String.format("\\x%x;", cp));
            } else {
                switch (cp) {
                    case '\u0007':
                        buffer.append("\\a");
                        break;
                    case '\b':
                        buffer.append("\\b");
                        break;
                    case '\t':
                        buffer.append("\\t");
                        break;
                    case '\n':
                        buffer.append("\\n");
                        break;
                    case '\r':
                        buffer.append("\\r");
                        break;
                    default:
                        char[] cs = Character.toChars(cp);
                        buffer.append(new String(cs, 0, cs.length));
                        break;
                }
            }
        }
        buffer.append(quote);
    }

    private static void writeSeq(List<SExpr> elems, String open, String close, Appendable buffer) throws IOException {
        buffer.append(open);
        String sep = "";
        for (SExpr elem : elems) {
            buffer.append(sep);
            elem.writeTo(buffer);
            sep = " ";
        }
        buffer.append(close);
    }

    enum BooleanValue implements SExpr {
        TRUE(true),
        FALSE(false);

        private final boolean value;
        private final Optional<Boolean> repr;

        private BooleanValue(boolean b) {
            this.value = b;
            this.repr = Optional.of(b);
        }

        @Override
        public boolean isBoolean() {
            return true;
        }

        @Override
        public Optional<Boolean> getBooleanValue() {
            return repr;
        }

        @Override
        public String toString() {
            return this.toWrittenString();
        }

        @Override
        public void writeTo(Appendable buffer) throws IOException {
            buffer.append(this.value ? "#t" : "#f");
        }
    }

    public static class CharacterValue implements SExpr {
        private final int value;
        private final Optional<Integer> repr;

        private CharacterValue(int value) {
            this.value = value;
            this.repr = Optional.of(value);
        }

        @Override
        public boolean isCharacter() {
            return true;
        }

        @Override
        public Optional<Integer> getCharacterCodePoint() {
            return repr;
        }

        @Override
        public String toString() {
            return this.toWrittenString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CharacterValue that = (CharacterValue) o;
            return Objects.equals(repr, that.repr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(repr);
        }

        @Override
        public void writeTo(Appendable buffer) throws IOException {
            switch (this.value) {
                case '\u0007':
                    buffer.append("#\\alarm");
                    break;
                case '\b':
                    buffer.append("#\\backspace");
                    break;
                case '\u007f':
                    buffer.append("#\\delete");
                    break;
                case '\u001b':
                    buffer.append("#\\escape");
                    break;
                case '\n':
                    buffer.append("#\\newline");
                    break;
                case '\u0000':
                    buffer.append("#\\null");
                    break;
                case '\r':
                    buffer.append("#\\return");
                    break;
                case '\u0020':
                    buffer.append("#\\space");
                    break;
                case '\t':
                    buffer.append("#\\tab");
                    break;
                default:
                    if (Character.isISOControl(this.value)) {
                        buffer.append(String.format("#\\x%x", this.value));
                    } else {
                        buffer.append("#\\x" + new String(Character.toChars(this.value)));
                    }
            }
        }
    }

    public static class NumberValue implements SExpr {
        private final Optional<BigDecimal> repr;

        private NumberValue(BigDecimal repr) {
            this.repr = Optional.of(repr);
        }

        @Override
        public boolean isNumber() {
            return true;
        }

        @Override
        public Optional<BigDecimal> getNumberValue() {
            return this.repr;
        }

        @Override
        public String toString() {
            return this.toWrittenString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NumberValue that = (NumberValue) o;
            return this.repr.get().compareTo(that.repr.get()) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(repr);
        }

        @Override
        public void writeTo(Appendable buffer) throws IOException {
            buffer.append(repr.get().toString());
        }
    }

    enum InfinityValue implements SExpr {
        NEGATIVE(Double.NEGATIVE_INFINITY),
        POSITIVE(Double.POSITIVE_INFINITY);

        private Optional<Double> repr;
        private double value;

        InfinityValue(double value) {
            this.value = value;
            this.repr = Optional.of(value);
        }

        @Override
        public boolean isInfinity() {
            return true;
        }

        @Override
        public Optional<Double> getInfinityValue() {
            return this.repr;
        }

        @Override
        public String toString() {
            return this.toWrittenString();
        }

        @Override
        public void writeTo(Appendable buffer) throws IOException {
            if (this.value < 0)
                buffer.append("-inf.0");
            else
                buffer.append("+inf.0");
        }
    }

    enum NanValue implements SExpr {
        INSTANCE;

        private Optional<Double> repr;

        NanValue() {
            this.repr = Optional.of(Double.NaN);
        }

        @Override
        public boolean isNan() {
            return true;
        }

        @Override
        public Optional<Double> getNanValue() {
            return this.repr;
        }

        @Override
        public String toString() {
            return this.toWrittenString();
        }

        @Override
        public void writeTo(Appendable buffer) throws IOException {
            buffer.append("+nan.0");
        }
    }

    public static class StringValue implements SExpr {
        private final Optional<String> repr;

        private StringValue(String s) {
            this.repr = Optional.of(s);
        }

        @Override
        public boolean isString() {
            return true;
        }

        @Override
        public Optional<String> getStringValue() {
            return this.repr;
        }

        @Override
        public String toString() {
            return this.toWrittenString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StringValue that = (StringValue) o;
            return Objects.equals(repr, that.repr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(repr);
        }

        @Override
        public void writeTo(Appendable buffer) throws IOException {
            writeString(repr.get().codePoints().toArray(), '"', buffer);
        }
    }

    public static class SymbolValue implements SExpr {
        private final Optional<String> repr;

        private SymbolValue(String s) {
            this.repr = Optional.of(s);
        }

        @Override
        public boolean isSymbol() {
            return true;
        }

        @Override
        public Optional<String> getSymbolName() {
            return this.repr;
        }

        @Override
        public String toString() {
            return this.toWrittenString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SymbolValue that = (SymbolValue) o;
            return Objects.equals(repr, that.repr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(repr);
        }


        @Override
        public void writeTo(Appendable buffer) throws IOException {
            String name = repr.get();
            int[] codePoints = name.codePoints().toArray();
            if (isSimpleName(name, codePoints)) {
                buffer.append(name);
            } else {
                writeString(codePoints, '|', buffer);
            }
        }

        private boolean isSimpleName(String name, int[] codePoints) {
            int len = codePoints.length;
            if (len == 0)
                return false;

            if (isPositiveInf(name) || isNegativeInf(name)
                    || isPositiveNaN(name) || isNegativeNaN(name)
                    || isPositiveI(name) || isNegativeI(name))
                return false;

            // : Initial Subsequent*
            if (isInitial(codePoints[0]))
                return allSubsequent(codePoints, 1);

            if (isExplicitSign(codePoints[0])) {
                // : ExplicitSign
                if (len == 1)
                    return true;

                // | ExplicitSign SignSubsequent Subsequent*
                if (isSignSubsequent(codePoints[1]))
                    return allSubsequent(codePoints, 2);

                // | ExplicitSign '.' DotSubsequent Subsequent*
                if (len >= 3 && codePoints[1] == '.' && isDotSubsequent(codePoints[2]))
                    return allSubsequent(codePoints, 3);

                return false;
            }

            // | '.' DotSubsequent Subsequent*
            if (codePoints[0] == '.' && len >= 2 && isDotSubsequent(codePoints[1]))
                return allSubsequent(codePoints, 2);

            return false;
        }

        private boolean allSubsequent(int[] codePoints, int start) {
            for (int i = start; i < codePoints.length; ++i) {
                if (isSubsequent(codePoints[i]) == false)
                    return false;
            }
            return true;
        }

        private boolean isInitial(int cp) {
            return isLetter(cp) || isSpecialInitial(cp);
        }

        private boolean isLetter(int cp) {
            return ('a' <= cp && cp <= 'z') || ('A' <= cp && cp <= 'Z');
        }

        private boolean isDigit(int cp) {
            return ('0' <= cp && cp <= '9');
        }

        private boolean isSpecialInitial(int cp) {
            switch (cp) {
                case '!':
                case '$':
                case '%':
                case '&':
                case '*':
                case '/':
                case ':':
                case '<':
                case '=':
                case '>':
                case '?':
                case '^':
                case '_':
                case '~':
                    return true;
                default:
                    return false;
            }
        }

        private boolean isExplicitSign(int cp) {
            return cp == '+' || cp == '-';
        }

        private boolean isSpecialSubsequent(int cp) {
            return isExplicitSign(cp) || cp == '.' || cp == '@';
        }

        private boolean isSubsequent(int cp) {
            return isInitial(cp) || isDigit(cp) || isSpecialSubsequent(cp);
        }

        private boolean isDotSubsequent(int cp) {
            return isSignSubsequent(cp) || cp == '.';
        }

        private boolean isSignSubsequent(int cp) {
            return isInitial(cp) || isExplicitSign(cp) || cp == '@';
        }
    }

    public static class BytevectorValue implements SExpr {
        private final Optional<byte[]> repr;
        private final byte[] value;

        private BytevectorValue(byte[] value) {
            this.value = value;
            this.repr = Optional.of(value);
        }

        @Override
        public boolean isBytevector() {
            return true;
        }

        @Override
        public Optional<byte[]> getBytevectorElements() {
            return repr;
        }

        @Override
        public String toString() {
            return this.toWrittenString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BytevectorValue that = (BytevectorValue) o;
            return Arrays.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(value);
        }

        @Override
        public void writeTo(Appendable buffer) throws IOException {
            buffer.append("#u8(");
            String sep = "";
            for (byte b : this.repr.get()) {
                buffer.append(sep);
                buffer.append(String.format("%d", (int)b - Byte.MIN_VALUE));
                sep = " ";
            }
            buffer.append(")");
        }
    }

    public static class ListValue implements SExpr {
        private final Optional<List<SExpr>> repr;

        private ListValue(List<SExpr> elems) {
            this.repr = Optional.of(Collections.unmodifiableList(elems));
        }

        @Override
        public boolean isList() {
            return true;
        }

        @Override
        public Optional<List<SExpr>> getListElements() {
            return this.repr;
        }

        @Override
        public String toString() {
            return this.toWrittenString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ListValue listValue = (ListValue) o;
            return Objects.equals(repr, listValue.repr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(repr);
        }

        @Override
        public void writeTo(Appendable buffer) throws IOException {
            List<SExpr> elems = repr.get();
            if (elems.size() == 2 && elems.get(0).isSymbol()) {
                String abbr = null;
                switch (elems.get(0).getSymbolName().get()) {
                    case QUOTE_NAME:
                        abbr = "'";
                        break;
                    case QUASIQUOTE_NAME:
                        abbr = "`";
                        break;
                    case UNQUOTE_NAME:
                        abbr = ",";
                        break;
                    case UNQUOTE_SPLICING_NAME:
                        abbr = ",@";
                        break;
                }
                if (abbr != null) {
                    writeAbbreviation(abbr, elems.get(1), buffer);
                    return;
                }
            }
            writeSeq(elems, "(", ")", buffer);
        }

        private void writeAbbreviation(String abbreviation, SExpr elem, Appendable buffer) throws IOException {
            buffer.append(abbreviation);
            elem.writeTo(buffer);
        }
    }

    public static class DottedListValue implements SExpr {
        private final List<SExpr> elements;
        private final SExpr end;
        private final Optional<Pair<List<SExpr>, SExpr>> repr;

        private DottedListValue(List<SExpr> elems, SExpr end) {
            this.elements = Collections.unmodifiableList(elems);
            this.end = end;
            this.repr = Optional.of(new Pair(this.elements, this.end));
        }

        @Override
        public boolean isDottedList() {
            return true;
        }

        @Override
        public Optional<Pair<List<SExpr>, SExpr>> getDottedListElements() {
            return repr;
        }

        @Override
        public String toString() {
            return this.toWrittenString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DottedListValue that = (DottedListValue) o;
            return Objects.equals(repr, that.repr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(repr);
        }

        @Override
        public void writeTo(Appendable buffer) throws IOException {
            writeSeq(this.elements, "(", "", buffer);
            buffer.append(" . ");
            end.writeTo(buffer);
            buffer.append(")");
        }
    }

    public static class VectorValue implements SExpr {
        private final Optional<List<SExpr>> repr;

        private VectorValue(List<SExpr> elems) {
            this.repr = Optional.of(Collections.unmodifiableList(elems));
        }

        @Override
        public boolean isVector() {
            return true;
        }

        @Override
        public Optional<List<SExpr>> getVectorElements() {
            return this.repr;
        }

        @Override
        public String toString() {
            return this.toWrittenString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VectorValue that = (VectorValue) o;
            return Objects.equals(repr, that.repr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(repr);
        }

        @Override
        public void writeTo(Appendable buffer) throws IOException {
            writeSeq(repr.get(), "#(", ")", buffer);
        }
    }
}
