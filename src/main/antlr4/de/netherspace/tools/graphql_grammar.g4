// Describes the GraphQL DSL.
grammar graphql_grammar;

// start symbol:
document                : definition+
                        ;


// set of variables:
definition              : operationDefinition+
                        ;

operationDefinition     : operationType name? variableDefinitions? directives? selectionSet
                        | selectionSet
                        ;

operationType           : SUBSCRIPTION
                        | MUTATION
                        | QUERY
                        ;

name                    : NAME
                        ;

variableDefinitions     : variableDefinition+
                        ;

variableDefinition      : variable COLON type defaultValue?
                        ;

variable                : DOLLARSIGN name
                        ;

type                    : namedType
                        | listType
                        | nonNullType
                        ;

namedType               : name
                        ;

listType                : OPENINGSQUAREBRACKET type CLOSINGSQUAREBRACKET
                        ;

nonNullType             : namedType EXCLAMATIONMARK
                        | listType EXCLAMATIONMARK
                        ;

defaultValue            : EQUALSIGN value
                        ;

value                   : PLACEHOLDER // TODO: https://graphql.github.io/graphql-spec/June2018/#Value
                        ;

directives              : PLACEHOLDER // TODO!
                        ;

selectionSet            : OPENINGCURLYBRACKET selection+ CLOSINGCURLYBRACKET
                        ;

selection               : field
                        | fragmentSpread
                        | inlineFragment
                        ;

field                   : alias? name arguments? directives? selectionSet?
                        ;

alias                   : name
                        ;

arguments               : argument+
                        ;

argument                : name COLON value
                        ;

fragmentSpread          : '...' fragmentName directives
                        ;

fragmentName            : name
                        ;

fragmentDefinition      : FRAGMENT fragmentName typeCondition directives? selectionSet
                        ;

inlineFragment          : '...' typeCondition? directives? selectionSet
                        ;

typeCondition           : ON namedType
                        ;


// set of terminals:

SUBSCRIPTION            : 'subscription'
                        ;

FRAGMENT                : 'fragment'
                        ;

MUTATION                : 'mutation'
                        ;

QUERY                   : 'query'
                        ;

ON                      : 'ON'
                        ;

OPENINGCURLYBRACKET     : '{'
                        ;

CLOSINGCURLYBRACKET     : '}'
                        ;

OPENINGSQUAREBRACKET    : '['
                        ;

CLOSINGSQUAREBRACKET    : ']'
                        ;

EQUALSIGN               : '='
                        ;

DOLLARSIGN              : '$'
                        ;

EXCLAMATIONMARK         : '!'
                        ;

COLON                   : ':'
                        ;


// auxiliary terminals:
NAME                    : [_A-Za-z][_0-9A-Za-z]*
                        ;

PLACEHOLDER             : CHARACTER* ; // TODO: erase!!!

WHITESPACE              : [ \t]+ -> channel(HIDDEN)
                        ;

LINEBREAK               : [\r\n]+ -> skip
                        ;


// fragments, which are part of the grammar but NOT actual terminals:
fragment DIGIT          : [0-9]
                        ;

fragment CHARACTER      : [a-zA-Z]
                        ;

fragment ALLCHARS       : ([a-zA-Z] | '-' | '/' | '_' | '!')
                        ;
