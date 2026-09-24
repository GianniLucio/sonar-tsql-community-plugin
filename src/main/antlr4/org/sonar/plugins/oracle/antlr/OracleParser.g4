parser grammar OracleParser;

options { tokenVocab=OracleLexer; }

oracleFile: (unit | SEMICOLON | SLASH)* EOF;

unit
    : createProgramUnit
    | plsqlBlock
    | selectStatement
    | insertStatement
    | updateStatement
    | deleteStatement
    | mergeStatement
    | createTableStatement
    | createSequenceStatement
    | alterTableStatement
    ;

createProgramUnit
    : CREATE (OR REPLACE)? (PROCEDURE | FUNCTION | PACKAGE | TRIGGER) identifier programUnitToken*
    ;

programUnitToken: ~(SLASH | EOF);

plsqlBlock
    : DECLARE declarationToken* BEGIN plsqlToken* END
    | BEGIN plsqlToken* END
    ;

declarationToken: ~(BEGIN | END | EOF);

plsqlToken: ~(END | EOF);

selectStatement
    : SELECT (DISTINCT)? selectList FROM tableReference
      (WHERE expression)?
      (GROUP BY expressionList)?
      (HAVING expression)?
      (ORDER BY expressionList)?
      (CONNECT BY expression)?
      (START WITH expression)?
    ;

selectList
    : STAR
    | selectItem (COMMA selectItem)*
    ;

selectItem: expression (AS? alias)?;

tableReference: tableName (AS? alias)?;

tableName: identifier (DOT identifier)?;

insertStatement
    : INSERT INTO tableName (LPAREN columnList RPAREN)?
      (VALUES LPAREN expressionList RPAREN | selectStatement)
    ;

updateStatement: UPDATE tableName SET assignmentList (WHERE whereExpression=expression)?;

deleteStatement: DELETE FROM tableName (WHERE whereExpression=expression)?;

mergeStatement
    : MERGE INTO tableName USING tableReference ON expression
      WHEN MATCHED THEN UPDATE SET assignmentList
      (WHEN NOT MATCHED THEN INSERT (LPAREN columnList RPAREN)? VALUES LPAREN expressionList RPAREN)?
    ;

createTableStatement
    : CREATE TABLE tableName LPAREN tableElement (COMMA tableElement)* RPAREN
    ;

tableElement
    : columnDefinition
    | tableConstraint
    ;

columnDefinition: identifier dataType columnConstraint*;

dataType: identifier (LPAREN NUMBER (COMMA NUMBER)? RPAREN)?;

columnConstraint
    : PRIMARY KEY
    | NOT_NULL
    | UNIQUE
    | DEFAULT expression
    | REFERENCES tableName (LPAREN columnList RPAREN)?
    | CHECK LPAREN expression RPAREN
    ;

tableConstraint
    : (CONSTRAINT identifier)? PRIMARY KEY LPAREN columnList RPAREN
    | (CONSTRAINT identifier)? FOREIGN KEY LPAREN columnList RPAREN REFERENCES tableName (LPAREN columnList RPAREN)?
    | (CONSTRAINT identifier)? UNIQUE LPAREN columnList RPAREN
    | (CONSTRAINT identifier)? CHECK LPAREN expression RPAREN
    ;

createSequenceStatement: CREATE SEQUENCE identifier sequenceOption*;

sequenceOption
    : START WITH NUMBER
    | INCREMENT BY NUMBER
    | MAXVALUE NUMBER
    | NOMAXVALUE
    | MINVALUE NUMBER
    | NOMINVALUE
    | CACHE NUMBER
    | NOCACHE
    | CYCLE
    | NOCYCLE
    | ORDER
    | NOORDER
    ;

alterTableStatement: ALTER TABLE tableName alterTableAction;

alterTableAction
    : ADD tableElement
    | DROP COLUMN identifier
    | MODIFY columnDefinition
    ;

assignmentList: assignment (COMMA assignment)*;

assignment: columnName EQ expression;

columnList: columnName (COMMA columnName)*;

expressionList: expression (COMMA expression)*;

expression
    : expression (STAR | PLUS | MINUS | CONCAT | EQ | NEQ | LTE | GTE | LT | GT) expression
    | functionCall
    | columnName
    | literal
    | ROWNUM
    | PRIOR expression
    | LPAREN expression RPAREN
    ;

functionCall: identifier LPAREN (STAR | expressionList)? RPAREN (OVER LPAREN expression RPAREN)?;

columnName: identifier (DOT identifier)?;

literal: NUMBER | STRING | NULL;

identifier: IDENTIFIER | QUOTED_IDENTIFIER;
alias: identifier;
