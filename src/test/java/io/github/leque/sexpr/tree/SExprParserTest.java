package io.github.leque.sexpr.tree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SExprParserTest {
    @Test
    public void SExprParser_is_able_to_parse_true() {
        SExpr expected = SExprs.trueValue();
        Assertions.assertAll(
                () -> Assertions.assertEquals(expected, SExprParser.parse("#t")),
                () -> Assertions.assertEquals(expected, SExprParser.parse("#true")),
                () -> Assertions.assertEquals(expected, SExprParser.parse("#T")),
                () -> Assertions.assertEquals(expected, SExprParser.parse("#True"))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_false() {
        SExpr expected = SExprs.falseValue();
        Assertions.assertAll(
                () -> Assertions.assertEquals(expected, SExprParser.parse("#f")),
                () -> Assertions.assertEquals(expected, SExprParser.parse("#false")),
                () -> Assertions.assertEquals(expected, SExprParser.parse("#F")),
                () -> Assertions.assertEquals(expected, SExprParser.parse("#False"))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_integer() {
        String src = "1";
        SExpr expected = SExprs.integerValue(1);
        SExpr parsed = SExprParser.parse(src);
        Assertions.assertAll(
                () -> Assertions.assertEquals(expected, parsed)
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_flonum() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprs.flonumValue("42.0"),
                        SExprParser.parse("42."))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_string() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprs.stringValue("a"),
                        SExprParser.parse("\"a\"")),
                () -> Assertions.assertEquals(
                        SExprs.stringValue("\u0042"),
                        SExprParser.parse("\"\\x42;\"")),
                () -> Assertions.assertEquals(
                        SExprs.stringValue("\n"),
                        SExprParser.parse("\"\\n\"")),
                () -> Assertions.assertEquals(
                        SExprs.stringValue("a\nb"),
                        SExprParser.parse("\"a\\nb\"")),
                () -> Assertions.assertEquals(
                        SExprs.stringValue("a\"b"),
                        SExprParser.parse("\"a\\\"b\"")),
                () -> Assertions.assertEquals(
                        SExprs.stringValue("a|b"),
                        SExprParser.parse("\"a|b\"")),
                () -> Assertions.assertEquals(
                        SExprs.stringValue("\nb"),
                        SExprParser.parse("\"\\nb\"")),
                () -> Assertions.assertEquals(
                        SExprs.stringValue("b"),
                        SExprParser.parse("\"\\  \n  b\""))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_symbol() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprs.symbolValue("call-with-current-continuation"),
                        SExprParser.parse("call-with-current-continuation")),
                () -> Assertions.assertEquals(
                        SExprs.symbolValue("="),
                        SExprParser.parse("=")),
                () -> Assertions.assertEquals(
                        SExprs.symbolValue("+"),
                        SExprParser.parse("+")),
                () -> Assertions.assertEquals(
                        SExprs.symbolValue("-"),
                        SExprParser.parse("-")),
                () -> Assertions.assertEquals(
                        SExprs.symbolValue("..."),
                        SExprParser.parse("..."))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_escaped_symbol() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprs.symbolValue("a"),
                        SExprParser.parse("|a|")),
                () -> Assertions.assertEquals(
                        SExprs.symbolValue("\u0042"),
                        SExprParser.parse("|\\x42;|")),
                () -> Assertions.assertEquals(
                        SExprs.symbolValue("\n"),
                        SExprParser.parse("|\\n|")),
                () -> Assertions.assertEquals(
                        SExprs.symbolValue("a\nb"),
                        SExprParser.parse("|a\\nb|")),
                () -> Assertions.assertEquals(
                        SExprs.symbolValue("a\"b"),
                        SExprParser.parse("|a\"b|")),
                () -> Assertions.assertEquals(
                        SExprs.symbolValue("a|b"),
                        SExprParser.parse("|a\\|b|")),
                () -> Assertions.assertEquals(
                        SExprs.symbolValue("\nb"),
                        SExprParser.parse("|\\nb|")),
                () -> Assertions.assertEquals(
                        SExprs.symbolValue("b"),
                        SExprParser.parse("|\\  \n  b|"))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_list() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprs.listValue(SExprs.trueValue(), SExprs.integerValue(42), SExprs.falseValue()),
                        SExprParser.parse("(#t 42 #f)"))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_dotted_list() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprs.dottedListValue(SExprs.trueValue(), SExprs.falseValue()),
                        SExprParser.parse("(#t . #f)")),
                () -> Assertions.assertEquals(
                        SExprs.dottedListValue(SExprs.trueValue(), SExprs.integerValue(42), SExprs.falseValue()),
                        SExprParser.parse("(#t 42 . #f)"))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_vector() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprs.vectorValue(SExprs.trueValue(), SExprs.integerValue(42), SExprs.falseValue()),
                        SExprParser.parse("#(#t 42 #f)"))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_nested_list() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprs.listValue(
                                SExprs.symbolValue("define"),
                                SExprs.listValue(SExprs.symbolValue("fact"), SExprs.symbolValue("n")),
                                SExprs.listValue(
                                        SExprs.symbolValue("if"),
                                        SExprs.listValue(SExprs.symbolValue("="), SExprs.symbolValue("n"), SExprs.integerValue(0)),
                                        SExprs.integerValue(1),
                                        SExprs.listValue(
                                                SExprs.symbolValue("*"),
                                                SExprs.symbolValue("n"),
                                                SExprs.listValue(
                                                        SExprs.symbolValue("-"),
                                                        SExprs.symbolValue("n"),
                                                        SExprs.integerValue(1))))),
                        SExprParser.parse("(define (fact n) (if (= n 0) 1 (* n (- n 1))))"))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_abbreviations() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprParser.parse("(quote a)"),
                        SExprParser.parse("'a")),
                () -> Assertions.assertEquals(
                        SExprParser.parse("(quasiquote a)"),
                        SExprParser.parse("`a")),
                () -> Assertions.assertEquals(
                        SExprParser.parse("(unquote a)"),
                        SExprParser.parse(",a")),
                () -> Assertions.assertEquals(
                        SExprParser.parse("(unquote-splicing a)"),
                        SExprParser.parse(",@a")),
                () -> Assertions.assertEquals(
                        SExprParser.parse("'a"),
                        SExprParser.parse("'  a")),
                () -> Assertions.assertEquals(
                        SExprParser.parse("`42"),
                        SExprParser.parse("` 42")),
                () -> Assertions.assertEquals(
                        SExprParser.parse(",|foo bar|"),
                        SExprParser.parse(", |foo bar|")),
                () -> Assertions.assertEquals(
                        SExprParser.parse(",@(1 2 3)"),
                        SExprParser.parse(",@ (1 2 3)")),
                () -> Assertions.assertEquals(
                        SExprParser.parse("(quasiquote ((quote (unquote-splicing a))))"),
                        SExprParser.parse("`(',@a)"))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_datum_comment() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprParser.parse("(1 2 3)"),
                        SExprParser.parse("(1 2 #;2.5 3)")),
                () -> Assertions.assertEquals(
                        SExprParser.parse("(1 2 3)"),
                        SExprParser.parse("(1 2 #; #;2.5 2.75 3)")),
                () -> Assertions.assertEquals(
                        SExprParser.parse("(1 2 3)"),
                        SExprParser.parse("(1 2 #; '2.5 3)"))
        );
    }
}