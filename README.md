# SQL Community Analyzer for SonarQube

Open-source SonarQube plugin for static analysis of Microsoft **T-SQL**, **PostgreSQL**, and **Oracle SQL/PL-SQL** code. Version `1.4.0` uses embedded ANTLR4 grammars and listener-based checks to produce AST-aware issues, NCLOC metrics, and comment-line metrics.

The plugin targets SonarQube Community Edition `9.14+`, including compatible 10.x and LTS releases.

## Features

- Separate SonarQube languages and quality profiles for T-SQL, PostgreSQL, and Oracle.
- AST-based analysis through ANTLR4; checks are not driven by regular expressions.
- SonarQube issue locations mapped to the relevant SQL syntax nodes.
- NCLOC and comment-line metrics for both languages.
- Default `Sonar way` profiles with rules enabled for common quality, security, and performance problems.
- PostgreSQL grammar support for:
  - window functions with `OVER`, `PARTITION BY`, frames, and `FILTER`;
  - CTEs with `WITH`, `RECURSIVE`, and materialization options;
  - arrays, array types, subscripts, constructors, casts, and operators such as `@>`, `<@`, and `&&`.
- Oracle SQL and PL/SQL grammar support for common DML/DDL, `DECLARE ... BEGIN ... END` blocks, procedures, functions, packages, triggers, sequences, hierarchical queries, and Oracle SQL*Plus `/` separators.

## Installation

### From the GitHub release

Download [`sonar-tsql-community-plugin-1.4.0.jar`](https://github.com/giannicordone/sonar-tsql-community-plugin/releases/download/v1.4.0/sonar-tsql-community-plugin-1.4.0.jar) from the [v1.4.0 release](https://github.com/giannicordone/sonar-tsql-community-plugin/releases/tag/v1.4.0), then copy it to the SonarQube plugins directory:

```bash
cp sonar-tsql-community-plugin-1.4.0.jar "$SONARQUBE_HOME/extensions/plugins/"
```

Restart SonarQube after installing the plugin:

```bash
"$SONARQUBE_HOME/bin/linux-x86-64/sonar.sh" restart
```

The exact restart command depends on the SonarQube installation platform.

### Build from source

Prerequisites:

- JDK 17 or newer
- Apache Maven 3.8 or newer

From the repository root:

```bash
mvn clean package
```

The generated plugin is `target/sonar-tsql-community-plugin-1.4.0.jar`.

## SonarScanner configuration

Create `sonar-project.properties` in the repository containing the SQL scripts:

```properties
sonar.projectKey=my-database-project
sonar.projectName=My Database Project
sonar.projectVersion=1.4.0
sonar.sources=src/sql
sonar.sourceEncoding=UTF-8
```

The defaults are intentionally disjoint so SonarQube can classify files without ambiguity:

```properties
# T-SQL default: .tsql
sonar.tsql.file.suffixes=.tsql

# PostgreSQL defaults: .pgsql,.postgres
sonar.pgsql.file.suffixes=.pgsql,.postgres

# Oracle default: .oracle
sonar.oracle.file.suffixes=.oracle
```

For generic `.sql` files, configure the suffix for exactly one dialect in the project that owns them. For example, a PostgreSQL project can use:

```properties
sonar.pgsql.file.suffixes=.sql,.pgsql,.postgres
sonar.tsql.file.suffixes=.tsql
```

For an Oracle project, assign `.sql` only to Oracle:

```properties
sonar.oracle.file.suffixes=.sql,.oracle
sonar.tsql.file.suffixes=.tsql
sonar.pgsql.file.suffixes=.pgsql,.postgres
```

Run the analysis with SonarScanner:

```bash
sonar-scanner
```

Never assign the same suffix to both languages in the same SonarQube project. A file matching both language patterns causes the analysis to fail because SonarQube cannot decide which language owns it. Oracle scripts should be excluded or analyzed in a project using an Oracle analyzer.

## T-SQL rules

| Rule | Type | Severity | Description |
|---|---|---|---|
| `S101_AvoidSelectStar` | Code Smell | Major | Avoid `SELECT *` and `SELECT table.*`. |
| `S102_AvoidNoLock` | Bug | Critical | Avoid `NOLOCK` and `READUNCOMMITTED` hints. |
| `S103_AvoidSpPrefix` | Code Smell | Major | Avoid user procedures prefixed with `sp_`. |
| `S104_MissingSemicolon` | Code Smell | Minor | Terminate statements with `;`. |
| `S105_AvoidCursor` | Code Smell | Major | Prefer set-based operations over cursors. |
| `S106_TransactionXactAbort` | Bug | Critical | Use `SET XACT_ABORT ON` or `TRY...CATCH` for explicit transactions. |
| `S107_DynamicSqlInjection` | Vulnerability | Blocker | Avoid unsafe dynamic SQL concatenation. |
| `S108_AvoidOrderByOrdinal` | Code Smell | Minor | Avoid numeric ordinals in `ORDER BY`. |
| `S109_TableWithoutPrimaryKey` | Bug | Critical | Define a primary key on created tables. |
| `S110_UpperKeywords` | Code Smell | Info | Write SQL keywords in uppercase. |
| `S111_DmlWithoutWhere` | Bug | Critical | Require a `WHERE` clause on `UPDATE` and `DELETE`. |
| `S112_TopWithoutOrderBy` | Bug | Major | Require `ORDER BY` when using `TOP`. |
| `S113_DeprecatedDataType` | Code Smell | Major | Avoid deprecated SQL Server data types. |
| `S114_AvoidPrintStatement` | Code Smell | Minor | Avoid `PRINT` for operational error handling. |
| `S115_AvoidRaiserror` | Code Smell | Major | Prefer `THROW` over `RAISERROR`. |
| `S116_CartesianProduct` | Bug | Major | Avoid comma joins without a filter. |
| `S117_AvoidWhileLoop` | Code Smell | Major | Prefer set-based operations over row-by-row loops. |
| `S118_ConvertWithoutStyle` | Code Smell | Minor | Specify a style when converting date/time values. |
| `S119_AvoidIndexHint` | Code Smell | Major | Avoid forcing index hints without strong justification. |
| `S120_ProcedureWithoutErrorHandling` | Bug | Major | Procedures should handle errors with `TRY...CATCH`. |

## PostgreSQL rules

| Rule | Type | Severity | Description |
|---|---|---|---|
| `P101_AvoidSelectStar` | Code Smell | Major | Avoid `SELECT *` and `SELECT table.*`. |
| `P102_AvoidCursor` | Code Smell | Major | Prefer set-based operations over cursors. |
| `P103_MissingSemicolon` | Code Smell | Minor | Terminate statements with `;`. |
| `P104_DynamicSqlInjection` | Vulnerability | Blocker | Avoid unsafe dynamic SQL concatenation. |
| `P105_AvoidOrderByOrdinal` | Code Smell | Minor | Avoid numeric ordinals in `ORDER BY`. |
| `P106_TableWithoutPrimaryKey` | Bug | Critical | Define a primary key on created tables. |
| `P107_UpperKeywords` | Code Smell | Info | Write SQL keywords in uppercase. |
| `P108_MissingIndexOnForeignKey` | Bug | Major | Consider indexes for foreign-key columns. |
| `P109_AvoidSerialDataType` | Code Smell | Minor | Prefer identity columns over `SERIAL`. |
| `P110_UnloggedTableUsage` | Bug | Major | Use `UNLOGGED` tables cautiously. |
| `P111_DmlWithoutWhere` | Bug | Critical | Require a `WHERE` clause on `UPDATE` and `DELETE`. |
| `P112_LimitWithoutOrderBy` | Bug | Major | Require `ORDER BY` when using `LIMIT` or `OFFSET`. |
| `P113_OffsetWithoutLimit` | Code Smell | Minor | Avoid standalone `OFFSET` pagination. |
| `P114_CartesianProduct` | Bug | Major | Avoid comma joins without a filter. |
| `P115_RedundantDistinctWithGroupBy` | Code Smell | Minor | Avoid redundant `DISTINCT` with `GROUP BY`. |
| `P116_UselessCheckConstraint` | Bug | Major | Remove check constraints that are always true. |
| `P117_LikeLeadingWildcard` | Code Smell | Major | Avoid leading wildcards that prevent index usage. |
| `P118_NotInWithSubquery` | Bug | Major | Prefer `NOT EXISTS` when NULLs are possible. |
| `P119_DropWithoutIfExists` | Bug | Major | Guard destructive ALTER operations with `IF EXISTS`. |
| `P120_ForeignKeyWithoutOnDeleteAction` | Code Smell | Minor | Make foreign-key delete behavior explicit. |

## Oracle rules

| Rule | Type | Severity | Description |
|---|---|---|---|
| `O101_AvoidSelectStar` | Code Smell | Major | Avoid `SELECT *` queries. |
| `O102_TableWithoutPrimaryKey` | Bug | Critical | Define a primary key on created tables. |
| `O103_UpperKeywords` | Code Smell | Info | Write Oracle SQL and PL/SQL keywords in uppercase. |
| `O104_DmlWithoutWhere` | Bug | Critical | Require a `WHERE` clause on `UPDATE` and `DELETE`. |
| `O105_CommitInProgramUnit` | Bug | Major | Avoid transaction control inside reusable program units. |
| `O106_WhenOthersWithoutRaise` | Bug | Major | Preserve errors handled by `WHEN OTHERS`. |
| `O107_RownumWithoutOrderBy` | Bug | Major | Order rows before applying `ROWNUM`. |
| `O108_Varchar2WithoutSize` | Code Smell | Minor | Declare an explicit `VARCHAR2` size. |
| `O109_UnnamedConstraint` | Code Smell | Minor | Name table constraints explicitly. |
| `O110_ProcedureWithoutExceptionHandling` | Bug | Major | Add exception handling to program units. |
| `O111_InsertWithoutColumnList` | Bug | Major | Specify target columns in `INSERT`. |
| `O112_ConnectByWithoutPrior` | Bug | Major | Use `PRIOR` in hierarchical queries. |
| `O113_CreateSequenceWithoutCacheOrder` | Code Smell | Minor | Make sequence caching and ordering explicit. |
| `O114_MergeWithoutNotMatched` | Code Smell | Major | Handle unmatched rows in `MERGE` where required. |
| `O115_AnonymousBlockWithoutExceptionHandling` | Bug | Major | Add exception handling to anonymous blocks. |
| `O116_AlterTableDropColumn` | Bug | Critical | Review destructive column drops. |
| `O117_HavingWithoutGroupBy` | Code Smell | Minor | Avoid `HAVING` without `GROUP BY`. |
| `O118_ProgramUnitWithoutOrReplace` | Code Smell | Minor | Use `CREATE OR REPLACE` for deployable units. |
| `O119_RollbackInProgramUnit` | Bug | Major | Avoid transaction control inside reusable units. |
| `O120_CheckConstraintNullComparison` | Bug | Major | Do not compare values with `= NULL` or `<> NULL`. |

## Development

The grammars are in `src/main/antlr4`. Maven generates the parser and listener sources during the build. Checks extend the language-specific base check and report issues from ANTLR parser contexts.

Run the full validation suite with:

```bash
mvn clean test
```

Package the SonarQube plugin with:

```bash
mvn clean package
```

When adding a rule, update the corresponding rules definition, default quality profile, sensor rule map, HTML description under `src/main/resources`, and focused unit tests.

## License

See the repository license and project metadata for licensing information.
