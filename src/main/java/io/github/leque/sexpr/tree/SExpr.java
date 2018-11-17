package io.github.leque.sexpr.tree;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface SExpr {
    default boolean isBoolean() {
        return false;
    }

    default Optional<Boolean> getBooleanValue() {
        return Optional.empty();
    }

    default boolean isInteger() {
        return false;
    }

    default Optional<BigInteger> getIntegerValue() {
        return Optional.empty();
    }

    default boolean isFlonum() {
        return false;
    }

    default Optional<BigDecimal> getFlonumValue() {
        return Optional.empty();
    }

    default boolean isString() {
        return false;
    }

    default Optional<String> getStringValue() {
        return Optional.empty();
    }

    default boolean isSymbol() {
        return false;
    }

    default Optional<String> getSymbolName() {
        return Optional.empty();
    }

    default boolean isList() {
        return false;
    }

    default Optional<List<SExpr>> getListElements() {
        return Optional.empty();
    }

    default boolean isVector() {
        return false;
    }

    default Optional<List<SExpr>> getVectorElements() {
        return Optional.empty();
    }

    void writeTo(Appendable buffer) throws IOException;

    default String toWrittenString() {
        StringBuilder builder = new StringBuilder();
        try {
            this.writeTo(builder);
        } catch (IOException e) {
            throw new IllegalStateException("must not happen");
        }
        return builder.toString();
    }
}
