package io.github.leque.sexpr.tree;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface SExpr {
    default boolean isBoolean() {
        return false;
    }

    default Optional<Boolean> asBoolean() {
        return Optional.empty();
    }

    default boolean isInteger() {
        return false;
    }

    default Optional<BigInteger> asInteger() {
        return Optional.empty();
    }

    default boolean isFlonum() {
        return false;
    }

    default Optional<BigDecimal> asFlonum() {
        return Optional.empty();
    }

    default boolean isString() {
        return false;
    }

    default Optional<String> asString() {
        return Optional.empty().empty();
    }

    default boolean isSymbol() {
        return false;
    }

    default Optional<String> asSymbol() {
        return Optional.empty();
    }

    default boolean isList() {
        return false;
    }

    default Optional<List<SExpr>> asList() {
        return Optional.empty();
    }

    default boolean isVector() {
        return false;
    }

    default Optional<List<SExpr>> asVector() {
        return Optional.empty();
    }
}
