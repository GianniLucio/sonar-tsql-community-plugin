parser grammar TSqlParser;

options {
    tokenVocab = TSqlLexer;
}

tsql_file
    : (batch | SEMI)* EOF
    ;

batch
    : statement_list GO?
    | GO
    ;

statement_list
    : statement (SEMI? statement)* SEMI?
    ;

statement
    : select_stmt
    | insert_stmt
    | update_stmt
    | delete_stmt
    | merge_stmt
    | create_procedure_stmt
    | alter_procedure_stmt
    | create_table_stmt
    | create_view_stmt
    | create_index_stmt
    | drop_stmt
    | begin_tran_stmt
    | commit_tran_stmt
    | rollback_tran_stmt
    | try_catch_stmt
    | if_stmt
    | while_stmt
    | declare_stmt
    | set_stmt
    | exec_stmt
    | cursor_decl_stmt
    | cursor_open_stmt
    | cursor_fetch_stmt
    | cursor_close_stmt
    | cursor_dealloc_stmt
    | print_stmt
    | throw_stmt
    | raiserror_stmt
    | return_stmt
    | use_stmt
    | block_stmt
    ;

block_stmt
    : BEGIN statement_list END
    ;

use_stmt
    : USE identifier
    ;

set_stmt
    : SET (
        XACT_ABORT (ON | ID)
        | NOCOUNT (ON | ID)
        | VARIABLE EQUAL expression
        | identifier (ON | ID)
      )
    ;

declare_stmt
    : DECLARE declare_item (COMMA declare_item)*
    ;

declare_item
    : VARIABLE data_type (EQUAL expression)?
    | VARIABLE TABLE LPAREN column_def (COMMA column_def)* RPAREN
    ;

select_stmt
    : (WITH cte_clause (COMMA cte_clause)*)?
      SELECT (DISTINCT | ALL)? (TOP LPAREN? expression RPAREN?)?
      select_list
      (FROM table_source (COMMA table_source)*)?
      (where_clause)?
      (group_by_clause)?
      (having_clause)?
      (order_by_clause)?
      (option_clause)?
    ;

cte_clause
    : identifier (LPAREN id_list RPAREN)? AS LPAREN select_stmt RPAREN
    ;

select_list
    : STAR # SelectStar
    | select_item (COMMA select_item)* # SelectListItems
    ;

select_item
    : identifier DOT STAR # SelectTableStar
    | expression (AS? (identifier | STRING_LITERAL))? # SelectExpression
    ;

table_source
    : table_name (table_hint_clause)? (AS? identifier)? (join_clause)*
    | LPAREN select_stmt RPAREN AS? identifier (join_clause)*
    ;

table_hint_clause
    : WITH LPAREN table_hint (COMMA table_hint)* RPAREN
    | table_hint
    ;

table_hint
    : NOLOCK
    | READUNCOMMITTED
    | READCOMMITTED
    | REPEATABLEREAD
    | SERIALIZABLE
    | HOLDLOCK
    | UPDLOCK
    | TABLOCK
    | TABLOCKX
    | PAGLOCK
    | ROWLOCK
    | NOWAIT
    | INDEX LPAREN expression (COMMA expression)* RPAREN
    | identifier
    ;

join_clause
    : (INNER | LEFT OUTER? | RIGHT OUTER? | FULL OUTER? | CROSS)? JOIN table_source (ON expression)?
    ;

where_clause
    : WHERE expression
    ;

group_by_clause
    : GROUP BY expression (COMMA expression)*
    ;

having_clause
    : HAVING expression
    ;

order_by_clause
    : ORDER BY order_by_item (COMMA order_by_item)*
    ;

order_by_item
    : expression (ASC | DESC)?
    ;

option_clause
    : OPTION LPAREN (RECOMPILE | identifier) RPAREN
    ;

insert_stmt
    : INSERT INTO? table_name (LPAREN id_list RPAREN)? (
        VALUES LPAREN expression_list RPAREN (COMMA LPAREN expression_list RPAREN)*
        | select_stmt
        | exec_stmt
      )
    ;

update_stmt
    : UPDATE (TOP LPAREN? expression RPAREN?)? table_source
      SET update_elem (COMMA update_elem)*
      (FROM table_source (COMMA table_source)*)?
      (where_clause)?
    ;

update_elem
    : full_column_name EQUAL expression
    | VARIABLE EQUAL expression
    ;

delete_stmt
    : DELETE (TOP LPAREN? expression RPAREN?)? (FROM)? table_source
      (where_clause)?
    ;

merge_stmt
    : MERGE INTO? table_source AS? identifier
      USING table_source AS? identifier
      ON expression
      merge_when_clause+
    ;

merge_when_clause
    : WHEN (MATCHED | NOT MATCHED) (AND expression)? THEN (
        UPDATE SET update_elem (COMMA update_elem)*
        | DELETE
        | INSERT (LPAREN id_list RPAREN)? VALUES LPAREN expression_list RPAREN
      )
    ;

create_table_stmt
    : CREATE TABLE table_name LPAREN table_element (COMMA table_element)* RPAREN
    ;

table_element
    : column_def
    | table_constraint
    ;

column_def
    : identifier data_type (
        IDENTITY (LPAREN INT_LITERAL COMMA INT_LITERAL RPAREN)?
        | NOT? NULL
        | PRIMARY KEY
        | DEFAULT expression
        | CONSTRAINT identifier PRIMARY KEY
        | CONSTRAINT identifier FOREIGN KEY REFERENCES table_name (LPAREN identifier RPAREN)?
        | CONSTRAINT identifier CHECK LPAREN expression RPAREN
      )*
    ;

table_constraint
    : (CONSTRAINT identifier)? (
        PRIMARY KEY LPAREN id_list RPAREN
        | FOREIGN KEY LPAREN id_list RPAREN REFERENCES table_name (LPAREN id_list RPAREN)?
        | UNIQUE LPAREN id_list RPAREN
        | CHECK LPAREN expression RPAREN
      )
    ;

create_view_stmt
    : CREATE VIEW table_name (LPAREN id_list RPAREN)? AS select_stmt
    ;

create_procedure_stmt
    : CREATE (PROCEDURE | PROC) proc_name (LPAREN? proc_param (COMMA proc_param)* RPAREN?)? AS statement_list
    ;

alter_procedure_stmt
    : ALTER (PROCEDURE | PROC) proc_name (LPAREN? proc_param (COMMA proc_param)* RPAREN?)? AS statement_list
    ;

proc_name
    : table_name
    ;

proc_param
    : VARIABLE data_type (EQUAL expression)? (OUTPUT | OUT)?
    ;

create_index_stmt
    : CREATE UNIQUE? INDEX identifier ON table_name LPAREN order_by_item (COMMA order_by_item)* RPAREN
    ;

drop_stmt
    : DROP (TABLE | VIEW | PROCEDURE | PROC | FUNCTION | INDEX | TRIGGER) table_name (COMMA table_name)*
    ;

begin_tran_stmt
    : BEGIN (TRAN | TRANSACTION) identifier?
    ;

commit_tran_stmt
    : COMMIT (TRAN | TRANSACTION)? identifier?
    ;

rollback_tran_stmt
    : ROLLBACK (TRAN | TRANSACTION)? identifier?
    ;

try_catch_stmt
    : BEGIN TRY statement_list END TRY BEGIN CATCH statement_list END CATCH
    ;

if_stmt
    : IF expression statement (ELSE statement)?
    ;

while_stmt
    : WHILE expression statement
    ;

exec_stmt
    : (EXEC | EXECUTE) (
        proc_name (exec_param (COMMA exec_param)*)?
        | LPAREN expression (PLUS expression)* RPAREN
      )
    ;

exec_param
    : (VARIABLE EQUAL)? expression (OUTPUT | OUT)?
    ;

cursor_decl_stmt
    : DECLARE identifier CURSOR (FOR select_stmt)?
    ;

cursor_open_stmt
    : OPEN identifier
    ;

cursor_fetch_stmt
    : FETCH NEXT FROM identifier INTO VARIABLE (COMMA VARIABLE)*
    ;

cursor_close_stmt
    : CLOSE identifier
    ;

cursor_dealloc_stmt
    : DEALLOCATE identifier
    ;

print_stmt
    : PRINT expression
    ;

throw_stmt
    : THROW (expression COMMA expression COMMA expression)?
    ;

raiserror_stmt
    : RAISERROR LPAREN expression COMMA expression COMMA expression RPAREN
    ;

return_stmt
    : RETURN expression?
    ;

table_name
    : (identifier DOT)? (identifier DOT)? identifier
    | TEMP_TABLE_ID
    ;

full_column_name
    : (identifier DOT)? (identifier DOT)? identifier
    ;

id_list
    : identifier (COMMA identifier)*
    ;

expression_list
    : expression (COMMA expression)*
    ;

expression
    : expression (AND | OR) expression # LogicalExpr
    | NOT expression # NotExpr
    | expression comparison_op expression # ComparisonExpr
    | expression (IS NULL | IS NOT NULL) # IsNullExpr
    | expression (NOT? LIKE) expression # LikeExpr
    | expression (NOT? BETWEEN) expression AND expression # BetweenExpr
    | expression (NOT? IN) LPAREN (select_stmt | expression_list) RPAREN # InExpr
    | EXISTS LPAREN select_stmt RPAREN # ExistsExpr
    | expression (PLUS | MINUS | STAR | SLASH | PERCENT) expression # ArithExpr
    | CASE (expression)? (WHEN expression THEN expression)+ (ELSE expression)? END # CaseExpr
    | function_call # FunctionExpr
    | CAST LPAREN expression AS data_type RPAREN # CastExpr
    | CONVERT LPAREN data_type COMMA expression (COMMA INT_LITERAL)? RPAREN # ConvertExpr
    | LPAREN expression RPAREN # ParenExpr
    | LPAREN select_stmt RPAREN # ScalarSubqueryExpr
    | literal # LiteralExpr
    | VARIABLE # VariableExpr
    | full_column_name # ColumnExpr
    ;

function_call
    : identifier LPAREN (STAR | (DISTINCT? expression (COMMA expression)*))? RPAREN (OVER LPAREN (PARTITION BY expression_list)? (order_by_clause)? RPAREN)?
    ;

comparison_op
    : EQUAL
    | NOT_EQUAL
    | LESS_THAN
    | LESS_EQUAL
    | GREATER_THAN
    | GREATER_EQUAL
    ;

literal
    : STRING_LITERAL
    | INT_LITERAL
    | DECIMAL_LITERAL
    | NULL
    ;

data_type
    : identifier (LPAREN (INT_LITERAL | MAX) (COMMA INT_LITERAL)? RPAREN)?
    ;

identifier
    : ID
    | BRACKET_ID
    | QUOTED_ID
    | KEY
    | VALUE
    | TYPE
    | STATUS
    | COUNT
    ;
