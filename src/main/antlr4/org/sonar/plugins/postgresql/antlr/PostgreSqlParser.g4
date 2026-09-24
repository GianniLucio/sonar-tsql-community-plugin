parser grammar PostgreSqlParser;

options { tokenVocab=PostgreSqlLexer; }

// Main entry point
program: (statement SEMICOLON?)* EOF;

statement
    : cteStatement
    | selectStatement
    | insertStatement
    | updateStatement
    | deleteStatement
    | createTableStatement
    | createIndexStatement
    | dropTableStatement
    | alterTableStatement
    | declareStatement
    | executeStatement
    | beginTransaction
    | commitStatement
    | rollbackStatement
    ;

// CTEs (Common Table Expressions)
cteStatement: cteClause (selectStatement | insertStatement | updateStatement | deleteStatement);

cteClause: WITH RECURSIVE? cteElement (COMMA cteElement)*;

cteElement: tableName (LPAREN columnNameList RPAREN)? AS (NOT? MATERIALIZED)? LPAREN (selectStatement | insertStatement | updateStatement | deleteStatement) RPAREN;

// SELECT
selectStatement
    : (cteClause)? SELECT (DISTINCT | ALL)? selectList
      (FROM fromClause)?
      (whereClause)?
      (groupByClause)?
      (havingClause)?
      (windowClause)?
      (orderByClause)?
      (limitClause)?
      (offsetClause)?
      ((UNION | INTERSECT | EXCEPT) (ALL | DISTINCT)? selectStatement)?
    ;

selectList
    : STAR # SelectStar
    | selectItem (COMMA selectItem)* # SelectListItems
    ;

selectItem
    : tableName DOT STAR # SelectTableStar
    | expression (AS? alias)? # SelectExpression
    ;

fromClause: tableReference (COMMA tableReference)*;

tableReference: tablePrimary (joinClause)*;

tablePrimary
    : tableName (AS? tableAlias)?
    | LPAREN selectStatement RPAREN (AS? tableAlias)?
    | functionCall (AS? tableAlias)?
    ;

joinClause
    : (joinType? JOIN | CROSS JOIN) tablePrimary (ON searchCondition | USING LPAREN columnNameList RPAREN)?
    ;

joinType: INNER | (LEFT | RIGHT | FULL) OUTER?;

whereClause: WHERE searchCondition;

groupByClause: GROUP BY expression (COMMA expression)*;

havingClause: HAVING searchCondition;

windowClause: WINDOW windowDefinition (COMMA windowDefinition)*;

windowDefinition: IDENTIFIER AS LPAREN windowSpecBody RPAREN;

orderByClause: ORDER BY orderSpecification (COMMA orderSpecification)*;

orderSpecification
    : NUMBER (ASC | DESC)? (NULLS (FIRST | LAST))? # OrderByOrdinal
    | expression (ASC | DESC)? (NULLS (FIRST | LAST))? # OrderByExpr
    ;

limitClause: LIMIT expression;

offsetClause: OFFSET expression (ROWS | ROW)?;

// Window specification
windowSpecification
    : OVER LPAREN windowSpecBody RPAREN
    | OVER existingWindowName
    ;

windowSpecBody: (IDENTIFIER)? (partitionByClause)? (orderByClause)? (frameClause)?;

existingWindowName: IDENTIFIER;

partitionByClause: PARTITION BY expression (COMMA expression)*;

frameClause: (ROWS | RANGE | GROUPS) frameExtent;

frameExtent
    : frameBound
    | BETWEEN frameBound AND frameBound
    ;

frameBound
    : UNBOUNDED PRECEDING
    | UNBOUNDED FOLLOWING
    | CURRENT ROW
    | expression (PRECEDING | FOLLOWING)
    ;

searchCondition: expression;

// Expressions with operator precedence
expression
    : expression LBRACK expression (COLON expression)? RBRACK # ArraySubscriptExpr
    | expression TYPECAST dataType                            # TypecastExpr
    | expression (AT_GT | LT_AT | AMP_AMP | CONCAT) expression # ArrayOpExpr
    | expression (STAR | SLASH | PERCENT) expression          # MultOpExpr
    | expression (PLUS | MINUS) expression                    # AddOpExpr
    | expression (EQ | NEQ | LT | LTE | GT | GTE) (ANY | SOME | ALL) LPAREN expression RPAREN # ArrayQuantifierExpr
    | expression (EQ | NEQ | LT | LTE | GT | GTE) expression  # RelationalOpExpr
    | expression IS NOT? (NULL | TRUE | FALSE)                # IsNullOrBoolExpr
    | expression NOT? BETWEEN expression AND expression       # BetweenExpr
    | expression NOT? IN LPAREN (selectStatement | expression (COMMA expression)*) RPAREN # InExpr
    | expression NOT? (LIKE | ILIKE) expression               # LikeExpr
    | NOT expression                                          # NotExpr
    | expression AND expression                               # AndExpr
    | expression OR expression                                # OrExpr
    | CASE expression? (WHEN searchCondition THEN expression)+ (ELSE expression)? END # CaseExpr
    | EXISTS LPAREN selectStatement RPAREN                    # ExistsExpr
    | arrayConstructor                                        # ArrayConstructorExpr
    | functionCall                                            # FunctionCallExpr
    | columnName                                              # ColumnExpr
    | STRING                                                  # StringLiteralExpr
    | NUMBER                                                  # NumberLiteralExpr
    | (TRUE | FALSE | NULL)                                   # KeywordLiteralExpr
    | LPAREN expression RPAREN                                # ParenthesizedExpr
    | LPAREN selectStatement RPAREN                           # ScalarSubqueryExpr
    ;

// Array constructor
arrayConstructor
    : ARRAY LBRACK (expression (COMMA expression)*)? RBRACK
    | ARRAY LPAREN selectStatement RPAREN
    ;

// Function call (including Window and Aggregate functions)
functionCall
    : functionName LPAREN (STAR | (DISTINCT | ALL)? expressionList)? RPAREN (FILTER LPAREN WHERE searchCondition RPAREN)? (windowSpecification)?
    ;

expressionList: expression (COMMA expression)*;

functionName
    : IDENTIFIER
    | COUNT
    | SUM
    | AVG
    | MIN
    | MAX
    | ARRAY
    ;

// INSERT
insertStatement
    : (cteClause)? INSERT INTO tableName (LPAREN columnNameList RPAREN)?
      (VALUES LPAREN valueList RPAREN (COMMA LPAREN valueList RPAREN)* | selectStatement)
      (returningClause)?
    ;

columnNameList: columnName (COMMA columnName)*;

valueList: expression (COMMA expression)*;

// UPDATE
updateStatement
    : (cteClause)? UPDATE tableName SET setClauseList
      (FROM fromClause)?
      (whereClause)?
      (returningClause)?
    ;

setClauseList: setClause (COMMA setClause)*;

setClause: columnName EQ expression;

// DELETE
deleteStatement
    : (cteClause)? DELETE FROM tableName
      (whereClause)?
      (returningClause)?
    ;

returningClause: RETURNING (STAR | selectItem (COMMA selectItem)*);

// CREATE TABLE
createTableStatement
    : CREATE UNLOGGED? TABLE (IF NOT EXISTS)? tableName LPAREN (columnDefinition | tableConstraint) (COMMA (columnDefinition | tableConstraint))* RPAREN
    ;

columnDefinitionList: columnDefinition (COMMA columnDefinition)*;

columnDefinition: columnName dataType columnConstraint*;

dataType
    : baseDataType (LBRACK NUMBER? RBRACK)* (ARRAY (LBRACK NUMBER? RBRACK)*)?
    ;

baseDataType
    : INT
    | INTEGER
    | BIGINT
    | SMALLINT
    | SERIAL
    | BIGSERIAL
    | SMALLSERIAL
    | VARCHAR (LPAREN NUMBER RPAREN)?
    | CHAR (LPAREN NUMBER RPAREN)?
    | TEXT
    | DECIMAL (LPAREN NUMBER (COMMA NUMBER)? RPAREN)?
    | NUMERIC (LPAREN NUMBER (COMMA NUMBER)? RPAREN)?
    | BOOLEAN
    | TIMESTAMP (LPAREN NUMBER RPAREN)?
    | TIMESTAMPTZ
    | DATE
    | TIME (LPAREN NUMBER RPAREN)?
    | JSON
    | JSONB
    | UUID
    | BYTEA
    | REAL
    | DOUBLE PRECISION
    | IDENTIFIER
    ;

columnConstraint
    : PRIMARY KEY
    | NOT NULL
    | NULL
    | UNIQUE
    | DEFAULT expression
    | GENERATED (ALWAYS | BY DEFAULT) AS IDENTITY
    | REFERENCES tableName (LPAREN columnNameList RPAREN)?
    | CHECK LPAREN searchCondition RPAREN
    | CONSTRAINT IDENTIFIER columnConstraint
    ;

tableConstraint
    : PRIMARY KEY LPAREN columnNameList RPAREN
    | FOREIGN KEY LPAREN columnNameList RPAREN REFERENCES tableName (LPAREN columnNameList RPAREN)? (ON DELETE (CASCADE | RESTRICT))?
    | UNIQUE LPAREN columnNameList RPAREN
    | CHECK LPAREN searchCondition RPAREN
    | CONSTRAINT IDENTIFIER tableConstraint
    ;

// CREATE INDEX
createIndexStatement: CREATE UNIQUE? INDEX (IF NOT EXISTS)? indexName ON tableName (USING IDENTIFIER)? LPAREN indexColumn (COMMA indexColumn)* RPAREN (whereClause)?;

indexColumn: (columnName | LPAREN expression RPAREN) (ASC | DESC)? (NULLS (FIRST | LAST))?;

// DROP TABLE
dropTableStatement: DROP TABLE (IF EXISTS)? tableName (COMMA tableName)* (CASCADE | RESTRICT)?;

// ALTER TABLE
alterTableStatement: ALTER TABLE (IF EXISTS)? tableName alterTableAction (COMMA alterTableAction)*;

alterTableAction
    : ADD COLUMN? columnDefinition
    | ADD tableConstraint
    | DROP COLUMN? (IF EXISTS)? columnName (CASCADE | RESTRICT)?
    | DROP CONSTRAINT (IF EXISTS)? IDENTIFIER (CASCADE | RESTRICT)?
    ;

// DECLARE CURSOR
declareStatement: DECLARE cursorName CURSOR (WITH HOLD | WITHOUT HOLD)? FOR selectStatement;

// EXECUTE
executeStatement: (EXECUTE | EXEC) (STRING | dynamicSql | IDENTIFIER (LPAREN (expression (COMMA expression)*)? RPAREN)?);

dynamicSql: expression (CONCAT | PLUS) expression ((CONCAT | PLUS) expression)*;

// TRANSACTIONS
beginTransaction: BEGIN (TRANSACTION | WORK)?;

commitStatement: COMMIT (TRANSACTION | WORK)?;

rollbackStatement: ROLLBACK (TRANSACTION | WORK)?;

// Identifiers & Aliases
tableName: IDENTIFIER | IDENTIFIER DOT IDENTIFIER;
columnName: IDENTIFIER | IDENTIFIER DOT IDENTIFIER;
indexName: IDENTIFIER;
cursorName: IDENTIFIER;
tableAlias: IDENTIFIER;
alias: IDENTIFIER | STRING;
