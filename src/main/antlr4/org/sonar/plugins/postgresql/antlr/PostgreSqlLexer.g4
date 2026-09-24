lexer grammar PostgreSqlLexer;

// Case-insensitive alphabet fragments
fragment A: [aA];
fragment B: [bB];
fragment C: [cC];
fragment D: [dD];
fragment E: [eE];
fragment F: [fF];
fragment G: [gG];
fragment H: [hH];
fragment I: [iI];
fragment J: [jJ];
fragment K: [kK];
fragment L: [lL];
fragment M: [mM];
fragment N: [nN];
fragment O: [oO];
fragment P: [pP];
fragment Q: [qQ];
fragment R: [rR];
fragment S: [sS];
fragment T: [tT];
fragment U: [uU];
fragment V: [vV];
fragment W: [wW];
fragment X: [xX];
fragment Y: [yY];
fragment Z: [zZ];

// PostgreSQL Specific - CTEs
WITH: W I T H;
RECURSIVE: R E C U R S I V E;
MATERIALIZED: M A T E R I A L I Z E D;

// PostgreSQL Specific - Window Functions
OVER: O V E R;
PARTITION: P A R T I T I O N;
ROWS: R O W S;
RANGE: R A N G E;
GROUPS: G R O U P S;
UNBOUNDED: U N B O U N D E D;
PRECEDING: P R E C E D I N G;
FOLLOWING: F O L L O W I N G;
CURRENT: C U R R E N T;
ROW: R O W;
WINDOW: W I N D O W;
FILTER: F I L T E R;
NULLS: N U L L S;
FIRST: F I R S T;
LAST: L A S T;

// PostgreSQL Specific - Arrays
ARRAY: A R R A Y;
ANY: A N Y;
SOME: S O M E;
ALL: A L L;

// Core SQL Keywords
SELECT: S E L E C T;
DISTINCT: D I S T I N C T;
FROM: F R O M;
WHERE: W H E R E;
GROUP: G R O U P;
HAVING: H A V I N G;
ORDER: O R D E R;
BY: B Y;
LIMIT: L I M I T;
OFFSET: O F F S E T;
DESC: D E S C;
ASC: A S C;
FOR: F O R;

INSERT: I N S E R T;
INTO: I N T O;
VALUES: V A L U E S;
UPDATE: U P D A T E;
DELETE: D E L E T E;
SET: S E T;
RETURNING: R E T U R N I N G;

CREATE: C R E A T E;
ALTER: A L T E R;
DROP: D R O P;
TABLE: T A B L E;
UNLOGGED: U N L O G G E D;
VIEW: V I E W;
INDEX: I N D E X;
ON: O N;
IF: I F;
EXISTS: E X I S T S;
ADD: A D D;
COLUMN: C O L U M N;

PRIMARY: P R I M A R Y;
KEY: K E Y;
FOREIGN: F O R E I G N;
REFERENCES: R E F E R E N C E S;
CONSTRAINT: C O N S T R A I N T;
CHECK: C H E C K;
UNIQUE: U N I Q U E;
DEFAULT: D E F A U L T;
CASCADE: C A S C A D E;
RESTRICT: R E S T R I C T;

SERIAL: S E R I A L;
BIGSERIAL: B I G S E R I A L;
SMALLSERIAL: S M A L L S E R I A L;
GENERATED: G E N E R A T E D;
ALWAYS: A L W A Y S;
IDENTITY: I D E N T I T Y;
AS: A S;

JOIN: J O I N;
INNER: I N N E R;
LEFT: L E F T;
RIGHT: R I G H T;
FULL: F U L L;
OUTER: O U T E R;
CROSS: C R O S S;
USING: U S I N G;

UNION: U N I O N;
INTERSECT: I N T E R S E C T;
EXCEPT: E X C E P T;

AND: A N D;
OR: O R;
NOT: N O T;
LIKE: L I K E;
ILIKE: I L I K E;
IN: I N;
IS: I S;
NULL: N U L L;
TRUE: T R U E;
FALSE: F A L S E;
BETWEEN: B E T W E E N;
CASE: C A S E;
WHEN: W H E N;
THEN: T H E N;
ELSE: E L S E;
END: E N D;
CAST: C A S T;

// Built-in Aggregate/Function Keywords
COUNT: C O U N T;
SUM: S U M;
AVG: A V G;
MIN: M I N;
MAX: M A X;

// Procedural / Transactions
DECLARE: D E C L A R E;
CURSOR: C U R S O R;
OPEN: O P E N;
FETCH: F E T C H;
CLOSE: C L O S E;
EXECUTE: E X E C U T E;
EXEC: E X E C;
COMMIT: C O M M I T;
ROLLBACK: R O L L B A C K;
BEGIN: B E G I N;
TRANSACTION: T R A N S A C T I O N;
WORK: W O R K;
HOLD: H O L D;
WITHOUT: W I T H O U T;

// Data Types
INT: I N T;
INTEGER: I N T E G E R;
BIGINT: B I G I N T;
SMALLINT: S M A L L I N T;
VARCHAR: V A R C H A R;
CHAR: C H A R;
TEXT: T E X T;
DECIMAL: D E C I M A L;
NUMERIC: N U M E R I C;
BOOLEAN: B O O L E A N;
TIMESTAMP: T I M E S T A M P;
TIMESTAMPTZ: T I M E S T A M P T Z;
DATE: D A T E;
TIME: T I M E;
ZONE: Z O N E;
JSON: J S O N;
JSONB: J S O N B;
UUID: U U I D;
BYTEA: B Y T E A;
REAL: R E A L;
DOUBLE: D O U B L E;
PRECISION: P R E C I S I O N;

// Operators & Delimiters
TYPECAST: '::';
AT_GT: '@>';
LT_AT: '<@';
AMP_AMP: '&&';
CONCAT: '||';
NEQ: '!=' | '<>';
LTE: '<=';
GTE: '>=';
EQ: '=';
LT: '<';
GT: '>';
PLUS: '+';
MINUS: '-';
STAR: '*';
SLASH: '/';
PERCENT: '%';
COMMA: ',';
DOT: '.';
COLON: ':';
SEMICOLON: ';';
LPAREN: '(';
RPAREN: ')';
LBRACK: '[';
RBRACK: ']';

// Literals
NUMBER: [0-9]+ ('.' [0-9]+)?;
STRING: '\'' (~'\'' | '\'\'')* '\'';
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]* | '"' (~'"' | '""')* '"';

// Comments
LINE_COMMENT: '--' ~[\r\n]* -> channel(HIDDEN);
BLOCK_COMMENT: '/*' .*? '*/' -> channel(HIDDEN);

// Whitespace
WS: [ \t\n\r]+ -> channel(HIDDEN);
