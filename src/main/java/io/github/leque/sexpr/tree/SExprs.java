package io.github.leque.sexpr.tree;

import java.math.BigDecimal;
import java.math.BigInteger;
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

    public static SExpr integerValue(BigInteger repr) {
        return new IntegerValue(repr);
    }

    public static SExpr integerValue(long repr) {
        return new IntegerValue(BigInteger.valueOf(repr));
    }

    public static SExpr flonumValue(BigDecimal repr) {
        return new FlonumValue(repr);
    }

    public static SExpr flonumValue(String repr) {
        return new FlonumValue(new BigDecimal(repr));
    }

    public static SExpr stringValue(String repr) {
        return new StringValue(repr);
    }

    public static SExpr symbolValue(String repr) {
        return new SymbolValue(repr);
    }

    public static SExpr listValue(List<SExpr> repr) {
        return new ListValue(repr);
    }

    public static SExpr listValue(SExpr... reprs) {
        return new ListValue(Arrays.asList(reprs));
    }

    public static SExpr vectorValue(List<SExpr> repr) {
        return new VectorValue(repr);
    }

    public static SExpr vectorValue(SExpr... reprs) {
        return new VectorValue(Arrays.asList(reprs));
    }

    public static class BooleanValue implements SExpr {
        private final Optional<Boolean> repr;

        private static BooleanValue TRUE = new BooleanValue(true);

        private static BooleanValue FALSE = new BooleanValue(false);

        private BooleanValue(boolean b) {
            this.repr = Optional.of(b);
        }

        @Override
        public boolean isBoolean() {
            return true;
        }

        @Override
        public Optional<Boolean> asBoolean() {
            return repr;
        }

        @Override
        public String toString() {
            return "BooleanValue{" + repr.get() + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BooleanValue that = (BooleanValue) o;
            return Objects.equals(repr, that.repr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(repr);
        }
    }

    public static class IntegerValue implements SExpr {
        private final Optional<BigInteger> repr;

        public IntegerValue(BigInteger repr) {
            this.repr = Optional.of(repr);
        }

        @Override
        public boolean isInteger() {
            return true;
        }

        @Override
        public Optional<BigInteger> asInteger() {
            return this.repr;
        }

        @Override
        public String toString() {
            return "IntegerValue{" + repr.get() + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IntegerValue that = (IntegerValue) o;
            return Objects.equals(repr, that.repr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(repr);
        }
    }

    public static class FlonumValue implements SExpr {
        private final Optional<BigDecimal> repr;

        public FlonumValue(BigDecimal repr) {
            this.repr = Optional.of(repr);
        }

        @Override
        public boolean isFlonum() {
            return true;
        }

        @Override
        public Optional<BigDecimal> asFlonum() {
            return this.repr;
        }

        @Override
        public String toString() {
            return "FlonumValue{" + repr.get() + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FlonumValue that = (FlonumValue) o;
            return this.repr.get().compareTo(that.repr.get()) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(repr);
        }
    }

    public static class StringValue implements SExpr {
        private final Optional<String> repr;

        public StringValue(String s) {
            this.repr = Optional.of(s);
        }

        @Override
        public boolean isString() {
            return true;
        }

        @Override
        public Optional<String> asString() {
            return this.repr;
        }

        @Override
        public String toString() {
            return "StringValue{" + repr.get() + '}';
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
    }

    public static class SymbolValue implements SExpr {
        private final Optional<String> repr;

        public SymbolValue(String s) {
            this.repr = Optional.of(s);
        }

        @Override
        public boolean isSymbol() {
            return true;
        }

        @Override
        public Optional<String> asSymbol() {
            return this.repr;
        }

        @Override
        public String toString() {
            return "SymbolValue{" + repr.get() + '}';
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
    }

    public static class ListValue implements SExpr {
        private final Optional<List<SExpr>> repr;

        public ListValue(List<SExpr> elems) {
            this.repr = Optional.of(Collections.unmodifiableList(elems));
        }

        @Override
        public boolean isList() {
            return true;
        }

        @Override
        public Optional<List<SExpr>> asList() {
            return this.repr;
        }

        @Override
        public String toString() {
            return "ListValue{" + repr.get() + '}';
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
    }

    public static class VectorValue implements SExpr {
        private final Optional<List<SExpr>> repr;

        public VectorValue(List<SExpr> elems) {
            this.repr = Optional.of(Collections.unmodifiableList(elems));
        }

        @Override
        public boolean isVector() {
            return true;
        }

        @Override
        public Optional<List<SExpr>> asVector() {
            return this.repr;
        }

        @Override
        public String toString() {
            return "VectorValue{" + repr.get() + '}';
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
    }
}
