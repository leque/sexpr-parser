grammar Scheme;

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

fragment StringOrSymbolElement : HexEscape | MnemonicEscape;

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

vector: '#(' sexpr* intertokenSpace ')';

quoted : '\'' sexpr;

quasiquoted : '`' sexpr;

unquoted : ',' sexpr;

unquoteSplicinged : ',@' sexpr;

abbreviation : quoted | quasiquoted | unquoted | unquoteSplicinged;

sexpr : intertokenSpace (integer | flonum | true_ | false_ | string | identifier | escapedIdentifier | list | vector | abbreviation);

sexprEof : sexpr intertokenSpace EOF;
