grammar Scheme;

// Copyright (c) 2018 OOHASHI Daichi

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

// fragments

fragment Digit : [0-9];

fragment HexDigit : Digit | [a-fA-F];

fragment ExplicitSign : '-' | '+';

fragment Sign : ExplicitSign?;

fragment Exp : [eE] Integer;

fragment IntralineWhitespace : [ \t];

fragment LineEnding : '\n' | '\r\n' | '\r';

fragment Whitespace : IntralineWhitespace | LineEnding;

fragment HexEscape : '\\x' HexDigit+ ';';

fragment MnemonicEscape : '\\' [abtnr];

fragment StringOrSymbolElement : HexEscape | MnemonicEscape | '\\' IntralineWhitespace* LineEnding IntralineWhitespace* ;

fragment StringElement : ~[\\"] | '\\' '"' | StringOrSymbolElement;

fragment SymbolElement : ~[\\|] | '\\' '|' | StringOrSymbolElement;

fragment Initial : Letter | SpecialInitial;

fragment Letter : [a-zA-Z];

fragment SpecialInitial : [!$%&*/:<=>?^_~];

fragment Subsequent : Initial | Digit | SpecialSubsequent;

fragment SpecialSubsequent : ExplicitSign | '.' | '@';

fragment PeculiarIdentifier
  : ExplicitSign
  | ExplicitSign SignSubsequent Subsequent*
  | ExplicitSign '.' DotSubsequent Subsequent*
  | '.' DotSubsequent Subsequent*
  ;

fragment DotSubsequent : SignSubsequent | '.';

fragment SignSubsequent : Initial | ExplicitSign | '@';

// lexer rules
Integer : Sign Digit+ Exp?;

Flonum : Integer '.' Digit* Exp?;

True : '#' [Tt] ([Rr] [Uu] [Ee])?;

False : '#' [Ff] ([Aa] [Ll] [Ss] [Ee])?;

LineComment : ';' (~[\r\n])* (LineEnding | EOF);

String : '"' StringElement* '"';

EscapedSymbol : '|' SymbolElement* '|';

Identifier
  : Initial Subsequent*
  | PeculiarIdentifier
  ;

Whitespaces : Whitespace+;

// grammars

whiteSpaces : Whitespaces;

lineComment : LineComment;

datumComment : '#;' sexpr;

intertokenSpace : (whiteSpaces | lineComment | datumComment)*;

integer : Integer;

flonum : Flonum;

true_ : True;

false_ : False;

identifier: Identifier;

escapedIdentifier: EscapedSymbol;

string: String;

list: '(' sexpr* intertokenSpace ')';

dottedList: '(' sexpr+ intertokenSpace '.' sexpr intertokenSpace ')';

vector: '#(' sexpr* intertokenSpace ')';

quoted : '\'' sexpr;

quasiquoted : '`' sexpr;

unquoted : ',' sexpr;

unquoteSplicinged : ',@' sexpr;

abbreviation : quoted | quasiquoted | unquoted | unquoteSplicinged;

sexpr : intertokenSpace (integer | flonum | true_ | false_ | string | identifier | escapedIdentifier | list | dottedList | vector | abbreviation);

sexprEof : sexpr intertokenSpace EOF;
