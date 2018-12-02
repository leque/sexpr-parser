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
    public void SExprParser_is_able_to_parse_character() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprs.characterValue('a'),
                        SExprParser.parse("#\\a")),
                () -> Assertions.assertEquals(
                        SExprs.characterValue('1'),
                        SExprParser.parse("#\\1")),
                () -> Assertions.assertEquals(
                        SExprs.characterValue('x'),
                        SExprParser.parse("#\\x")),
                () -> Assertions.assertEquals(
                        SExprs.characterValue('\n'),
                        SExprParser.parse("#\\newline")),
                () -> Assertions.assertEquals(
                        SExprs.characterValue('\n'),
                        SExprParser.parse("#\\NewLine")),
                () -> Assertions.assertEquals(
                        SExprs.characterValue(0x1f600),
                        SExprParser.parse("#\\x1f600")),
                () -> Assertions.assertEquals(
                        SExprs.characterValue(0x1f607),
                        SExprParser.parse("#\\X1F607")),
                () -> Assertions.assertEquals(
                        SExprs.characterValue(0x1f600),
                        SExprParser.parse("#\\😀"))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_integer() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprs.numberValue(42),
                        SExprParser.parse("42")),
                () -> Assertions.assertEquals(
                        SExprs.numberValue(56),
                        SExprParser.parse("#d56")),
                () -> Assertions.assertEquals(
                        SExprs.numberValue(-42),
                        SExprParser.parse("#D-42")),
                () -> Assertions.assertEquals(
                        SExprs.numberValue(0xcafebabeL),
                        SExprParser.parse("#Xcafebabe")),
                () -> Assertions.assertEquals(
                        SExprs.numberValue(-0xdeadbeefL),
                        SExprParser.parse("#x-DeadBeef")),
                () -> Assertions.assertEquals(
                        SExprs.numberValue(0666),
                        SExprParser.parse("#o666")),
                () -> Assertions.assertEquals(
                        SExprs.numberValue(-0644),
                        SExprParser.parse("#O-644")),
                () -> Assertions.assertEquals(
                        SExprs.numberValue(-0b1101),
                        SExprParser.parse("#B-1101")),
                () -> Assertions.assertEquals(
                        SExprs.numberValue(0b101),
                        SExprParser.parse("#b101"))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_flonum() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprs.numberValue("42.0"),
                        SExprParser.parse("42.")),
                () -> Assertions.assertEquals(
                        SExprs.numberValue("42.0"),
                        SExprParser.parse("#d42.")),
                () -> Assertions.assertEquals(
                        SExprs.numberValue("42.0"),
                        SExprParser.parse(".42e2")),
                () -> Assertions.assertEquals(
                        SExprs.numberValue("42.0"),
                        SExprParser.parse("4.2e1")),
                () -> Assertions.assertEquals(
                        SExprs.numberValue("42.0"),
                        SExprParser.parse("42.e0")),
                () -> Assertions.assertEquals(
                        SExprs.numberValue("42.0"),
                        SExprParser.parse("42e0"))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_inf_and_nan() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprs.nanValue(),
                        SExprParser.parse("+nan.0")),
                () -> Assertions.assertEquals(
                        SExprs.nanValue(),
                        SExprParser.parse("-nan.0")),
                () -> Assertions.assertEquals(
                        SExprs.positiveInfinityValue(),
                        SExprParser.parse("+inf.0")),
                () -> Assertions.assertEquals(
                        SExprs.negativeInfinityValue(),
                        SExprParser.parse("-inf.0")),
                () -> Assertions.assertEquals(
                        SExprs.negativeInfinityValue(),
                        SExprParser.parse("-Inf.0"))
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
                        SExprs.stringValue("\u0042"),
                        SExprParser.parse("\"\\X42;\"")),
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
                        SExprs.symbolValue("\u0042"),
                        SExprParser.parse("|\\X42;|")),
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
                        SExprs.listValue(SExprs.trueValue(), SExprs.numberValue(42), SExprs.falseValue()),
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
                        SExprs.dottedListValue(SExprs.trueValue(), SExprs.numberValue(42), SExprs.falseValue()),
                        SExprParser.parse("(#t 42 . #f)"))
        );
    }

    @Test
    public void SExprParser_is_able_to_parse_vector() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        SExprs.vectorValue(SExprs.trueValue(), SExprs.numberValue(42), SExprs.falseValue()),
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
                                        SExprs.listValue(SExprs.symbolValue("="), SExprs.symbolValue("n"), SExprs.numberValue(0)),
                                        SExprs.numberValue(1),
                                        SExprs.listValue(
                                                SExprs.symbolValue("*"),
                                                SExprs.symbolValue("n"),
                                                SExprs.listValue(
                                                        SExprs.symbolValue("-"),
                                                        SExprs.symbolValue("n"),
                                                        SExprs.numberValue(1))))),
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