grammar JavaP;

javap : classDeclaration* ;

classDeclaration : modifiers (CLASS|INTERFACE) typeName extension? implementation? '{' memberDeclaration* '}';
memberDeclaration : methodDeclaration
                  | fieldDeclaration
                  ;

methodDeclaration : modifiers generics? typeName? typeName '(' typeList? ')' throwsBlock? ';' ;
fieldDeclaration : modifiers typeName identifier ';';

throwsBlock : THROWS typeList ;

typeList : typeName (',' typeName)* ;

modifiers : (PUBLIC | PROTECTED | PRIVATE)? STATIC? FINAL? SYNCHRONIZED?
            NATIVE? ABSTRACT? VOLATILE? TRANSIENT? STRICTFP? ;

generics: '<' genericParameter (',' genericParameter)* '>';
genericParameter : identifier
                 | typeName
                 | WILDCARD
                 | genericParameter EXTENDS typeList
                 | genericParameter SUPER typeList
                 ;

extension : EXTENDS typeList;
implementation : IMPLEMENTS typeList;

typeName : identifier
         | VOID | BOOLEAN | BYTE | CHAR | SHORT | INT | LONG | FLOAT | DOUBLE
         | typeName generics
         | typeName ArrayBrackets
         | typeName SEPARATOR typeName
         | typeName ELLIPSIS
         ;

identifier : BaseIdentifier
           | PACKAGEINFO
           ;

ABSTRACT      : 'abstract';
BOOLEAN       : 'boolean';
BYTE          : 'byte';
CHAR          : 'char';
CLASS         : 'class';
DOUBLE        : 'double';
EXTENDS       : 'extends';
FINAL         : 'final';
FLOAT         : 'float';
IMPLEMENTS    : 'implements';
INT           : 'int';
INTERFACE     : 'interface';
LONG          : 'long';
NATIVE        : 'native';
PRIVATE       : 'private';   // ignore - private isn't visible, anyway
PROTECTED     : 'protected'; // ignore - protected isn't visible except to inheritors
PUBLIC        : 'public';
SHORT         : 'short';
STATIC        : 'static';
STRICTFP      : 'strictfp';  // ignore this. # = 2
SUPER         : 'super';
SYNCHRONIZED  : 'synchronized';
THROWS        : 'throws';
TRANSIENT     : 'transient'; // ignore this. # = 1
VOID          : 'void';
VOLATILE      : 'volatile';  // ignore this. # = 6
WILDCARD      : '?' ;
ELLIPSIS      : '...' ;
PACKAGEINFO   : 'package-info' ;
SEPARATOR     : '.' | '/' ;

ArrayBrackets : '[]' ;

BaseIdentifier : JavaLetter JavaLetterOrDigit* ;

fragment
JavaLetter
    :   [a-zA-Z$_] // these are the "java letters" below 0xFF
    |   // covers all characters above 0xFF which are not a surrogate
        ~[\u0000-\u00FF\uD800-\uDBFF]
        {Character.isJavaIdentifierStart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

fragment
JavaLetterOrDigit
    :   [a-zA-Z0-9$_] // these are the "java letters or digits" below 0xFF
    |   // covers all characters above 0xFF which are not a surrogate
        ~[\u0000-\u00FF\uD800-\uDBFF]
        {Character.isJavaIdentifierPart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

WS  :  [ \t\r\n\u000C]+ -> skip
    ;
