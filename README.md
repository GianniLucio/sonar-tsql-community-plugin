# T-SQL and PostgreSQL Community Analyzer for SonarQube

Open-source SonarQube plugin for static analysis of Microsoft **T-SQL** and **PostgreSQL** code. Version `1.1.0` uses embedded ANTLR4 grammars and listener-based checks to produce AST-aware issues, NCLOC metrics, and comment-line metrics.

The plugin targets SonarQube Community Edition `9.14+`, including compatible 10.x and LTS releases.

## Features

- Separate SonarQube languages and quality profiles for T-SQL and PostgreSQL.
- AST-based analysis through ANTLR4; checks are not driven by regular expressions.
- SonarQube issue locations mapped to the relevant SQL syntax nodes.
- NCLOC and comment-line metrics for both languages.
- Default `Sonar way` profiles with rules enabled for common quality, security, and performance problems.
- PostgreSQL grammar support for:
  - window functions with `OVER`, `PARTITION BY`, frames, and `FILTER`;
  - CTEs with `WITH`, `RECURSIVE`, and materialization options;
  - arrays, array types, subscripts, constructors, casts, and operators such as `@>`, `<@`, and `&&`.

## Installation

### From the GitHub release

Download [`sonar-tsql-community-plugin-1.1.0.jar`](https://github.com/giannicordone/sonar-tsql-community-plugin/releases/download/v1.1.0/sonar-tsql-community-plugin-1.1.0.jar) from the [v1.1.0 release](https://github.com/giannicordone/sonar-tsql-community-plugin/releases/tag/v1.1.0), then copy it to the SonarQube plugins directory:

```bash
cp sonar-tsql-community-plugin-1.1.0.jar "$SONARQUBE_HOME/extensions/plugins/"
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

The generated plugin is `target/sonar-tsql-community-plugin-1.1.0.jar`.

## SonarScanner configuration

Create `sonar-project.properties` in the repository containing the SQL scripts:

```properties
sonar.projectKey=my-database-project
sonar.projectName=My Database Project
sonar.projectVersion=1.1.0
sonar.sources=src/sql
sonar.sourceEncoding=UTF-8
```

Use the language-specific suffix properties when the defaults do not match your files:

```properties
# T-SQL defaults: .sql,.tsql
sonar.tsql.file.suffixes=.sql,.tsql

# PostgreSQL defaults: .sql,.pgsql,.postgres
sonar.pgsql.file.suffixes=.sql,.pgsql,.postgres
```

Run the analysis with SonarScanner:

```bash
sonar-scanner
```

Avoid assigning the same suffix to both languages in one project unless the files are separated through project configuration, because `.sql` is recognized by both language definitions by default.

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
