grammar Exp;

file : block EOF ;

block : (statement)* ;

blockWithBrackets : '{' block '}' ;

statement : function   # functionStatement
          | variable   # variableStatement
          | expression # expressionStatement
          | loop       # whileStatement
          | condition  # conditionStatement
          | assigment  # assigmentStatement
          | ret        # returnStatement ;

function : 'fun' Identifier '(' parameterNames? ')' blockWithBrackets ;

variable : 'var' Identifier ('=' expression)? ;

parameterNames : (Identifier ',')* Identifier ;

loop : 'while' '(' expression ')' blockWithBrackets ;

condition : 'if' '(' expression ')' mainBlock=blockWithBrackets ('else' elseBlock=blockWithBrackets)? ;

assigment : Identifier '=' expression ;

ret : 'return' expression;

expression : functionCall                                                             # functionCalling
           | <assoc=left> left=expression op=(MULT | DIV | MOD) right=expression      # binaryOperation
           | <assoc=left> left=expression op=(PLUS | MINUS) right=expression          # binaryOperation
           | <assoc=left> left=expression op=(GT | LT | GEQ | LEQ) right=expression   # binaryOperation
           | <assoc=left> left=expression op=(EQ | NEQ) right=expression              # binaryOperation
           | <assoc=left> left=expression op=AND right=expression                     # binaryOperation
           | <assoc=left> left=expression op=OR right=expression                      # binaryOperation
           | Identifier                                                               # variableReference
           | Literal                                                                  # intLiteral
           | '(' expression ')'                                                       # surroundedExpression ;

functionCall : Identifier '(' arguments? ')' ;

arguments : (expression ',')* expression ;

Identifier : ('a'..'z' | 'A'..'Z') ('a'..'z' | 'A'..'Z' | '0'..'9')* ;

Literal : ('1'..'9') ('0'..'9')* | '0' ;

SKIP_SYMBOLS : (' ' | '\t' | '\r' | '\n' | '//' (.)*? '\n') -> skip ;

PLUS : '+' ;
MINUS : '-' ;
MULT : '*' ;
DIV : '/' ;
MOD : '%' ;
GT : '>' ;
LT : '<' ;
GEQ : '>=' ;
LEQ : '<=' ;
EQ : '==' ;
NEQ : '!=' ;
OR : '||' ;
AND : '&&' ;