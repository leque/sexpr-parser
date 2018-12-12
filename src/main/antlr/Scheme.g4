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
fragment A: [Aa];
fragment B: [Bb];
fragment C: [Cc];
fragment D: [Dd];
fragment E: [Ee];
fragment F: [Ff];
fragment G: [Gg];
fragment H: [Hh];
fragment I: [Ii];
fragment J: [Jj];
fragment K: [Kk];
fragment L: [Ll];
fragment M: [Mm];
fragment N: [Nn];
fragment O: [Oo];
fragment P: [Pp];
fragment Q: [Qq];
fragment R: [Rr];
fragment S: [Ss];
fragment T: [Tt];
fragment U: [Uu];
fragment V: [Vv];
fragment W: [Ww];
fragment X: [Xx];
fragment Y: [Yy];
fragment Z: [Zz];

fragment Digit : [0-9];

fragment Digit10 : Digit;

fragment Digit2 : [0-1];

fragment Digit8 : [0-7];

fragment Digit16 : HexDigit;

fragment HexDigit : Digit | [a-fA-F];

fragment ExplicitSign : '-' | '+';

fragment Sign : ExplicitSign?;

fragment Exp : E Digit10+;

fragment IntralineWhitespace : [ \t];

fragment LineEnding : '\n' | '\r\n' | '\r';

fragment Whitespace : IntralineWhitespace | LineEnding;

fragment HexEscape : '\\' X HexDigit+ ';';

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

fragment Radix10 : ('#' D)?;

// lexer rules
Integer10 : Radix10 Sign Digit+;

Integer2 : '#' B Sign Digit2+;

Integer8 : '#' O Sign Digit8+;

Integer16 : '#' X Sign Digit16+;

Flonum : Radix10 Sign
         ( Digit10+ Exp
         | Digit10* '.' Digit10+ Exp?
         | Digit10+ '.' Digit10* Exp?
         );

True : '#' T (R U E)?;

False : '#' F (A L S E)?;

Char : '#\\' .;

HexChar : '#\\' X HexDigit+;

NamedChar : '#\\' CharacterName;

CharacterName : A L A R M
  | B A C K S P A C E
  | D E L E T E
  | E S C A P E
  | N E W L I N E
  | N U L L
  | R E T U R N
  | S P A C E
  | T A B;

LineComment : ';' (~[\r\n])* (LineEnding | EOF);

BlockComment : '#|'
               ( '#'*? BlockComment
               | ('#'* | '|'*) ~[#|])*?
               '|'*?
               '|#';

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

blockComment : BlockComment;

datumComment : '#;' sexpr;

intertokenSpace : (whiteSpaces | lineComment | blockComment | datumComment)*;

integer : Integer10;

integer2 : Integer2;

integer8 : Integer8;

integer16 : Integer16;

flonum : Flonum;

true_ : True;

false_ : False;

char_ : Char;

hexChar : HexChar;

namedChar : NamedChar;

identifier: Identifier;

escapedIdentifier: EscapedSymbol;

string: String;

bytevector: '#u8(' (intertokenSpace (integer | integer2 | integer8 | integer16))* intertokenSpace ')';

list: '(' sexpr* intertokenSpace ')';

dottedList: '(' sexpr+ intertokenSpace '.' sexpr intertokenSpace ')';

vector: '#(' sexpr* intertokenSpace ')';

quoted : '\'' sexpr;

quasiquoted : '`' sexpr;

unquoted : ',' sexpr;

unquoteSplicinged : ',@' sexpr;

abbreviation : quoted | quasiquoted | unquoted | unquoteSplicinged;

sexpr : intertokenSpace
 ( integer | integer2 | integer8 | integer16
 | flonum
 | true_ | false_
 | char_ | hexChar | namedChar
 | string | identifier | escapedIdentifier
 | bytevector
 | list | dottedList | vector
 | abbreviation
 );

sexprEof : sexpr intertokenSpace EOF;
