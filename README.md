# T-SQL Community Analyzer for SonarQube

Open-source plugin for **SonarQube Community Edition** (compatible with SonarQube 9.x, 10.x, and LTS releases) for static analysis of **Microsoft Transact-SQL (T-SQL)** code and scripts.

The plugin uses an **ANTLR4** parser integrated with Java to generate the AST (Abstract Syntax Tree), calculate code metrics (NCLOC and comment lines), and run static checks for T-SQL quality, security, performance, and best-practice compliance.

---

## 🚀 Key Features

- **SonarQube compatibility**: SonarQube Community Edition 9.x - 10.x+.
- **Supported extensions**: `.sql`, `.tsql` (configurable through the `sonar.tsql.file.suffixes` property).
- **Native AST analysis**: Based on an embedded ANTLR4 T-SQL grammar, with no external runtime dependencies.
- **Default quality profile**: A T-SQL "Sonar way" profile with rules enabled by default.
- **Calculated metrics**: Non-comment lines of code (NCLOC) and comment lines.

---

## 📋 Default Rules

| Rule | Type | Severity | Description |
|---|---|---|---|
| `S101_AvoidSelectStar` | Code Smell | Major | Avoid using `SELECT *` and `SELECT table.*` in queries, views, and stored procedures. |
| `S102_AvoidNoLock` | Bug | Critical | Avoid `NOLOCK` / `READUNCOMMITTED` hints because they can cause dirty reads and phantom data. |
| `S103_AvoidSpPrefix` | Code Smell | Major | User stored procedures should not start with `sp_`, which is reserved for the `master` database. |
| `S104_MissingSemicolon` | Code Smell | Minor | T-SQL statements must end with a semicolon `;` (syntax recommended by Microsoft). |
| `S105_AvoidCursor` | Code Smell | Major | Avoid procedural `CURSOR` usage; prefer set-based operations. |
| `S106_TransactionXactAbort` | Bug | Critical | Explicit transactions must set `SET XACT_ABORT ON` or use `TRY...CATCH` blocks. |
| `S107_DynamicSqlInjection` | Vulnerability | Blocker | Avoid dynamic SQL built through string concatenation `+` (SQL injection vulnerability, CWE-89). |
| `S108_AvoidOrderByOrdinal` | Code Smell | Minor | Avoid ordinal column numbers in `ORDER BY` (for example, `ORDER BY 1, 2`). |
| `S109_TableWithoutPrimaryKey` | Bug | Critical | Tables created with `CREATE TABLE` must define a `PRIMARY KEY` constraint. |
| `S110_UpperKeywords` | Code Smell | Info | SQL keywords should be written in uppercase for readability and consistent style. |

---

## 🛠️ Build and Installation

### 1. Prerequisites
- Java JDK 17+
- Apache Maven 3.8+

### 2. Build the plugin
Run this command from the repository root:
```bash
mvn clean package
```
The generated file will be available at `target/sonar-tsql-community-plugin-1.0.0.jar`.

### 3. Install it in SonarQube
1. Copy the generated `.jar` file to the SonarQube plugins directory:
   ```bash
   cp target/sonar-tsql-community-plugin-1.0.0.jar $SONARQUBE_HOME/extensions/plugins/
   ```
2. Restart the SonarQube server:
   ```bash
   $SONARQUBE_HOME/bin/[OS]/sonar.sh restart
   ```
3. In the SonarQube Web UI, go to **Rules** and filter by the **T-SQL** language to view and customize the rules.

---

## 🔍 Esempio di scansione con SonarScanner

Crea un file `sonar-project.properties` nel repository dei tuoi script SQL:

```properties
sonar.projectKey=my-database-project
sonar.projectName=My Database Project
sonar.projectVersion=1.0

# Directory containing .sql scripts
sonar.sources=src/sql
sonar.sourceEncoding=UTF-8

# (Optional) Recognized file extensions
sonar.tsql.file.suffixes=.sql,.tsql
```

Run the analysis with the SonarScanner CLI:
```bash
sonar-scanner
```

---

## 🧩 Adding New Rules

1. **Create the rule class** in `src/main/java/org/sonar/plugins/tsql/checks/`:
   ```java
   public class MyCustomCheck extends TSqlCheck {
       public static final String RULE_KEY = "S111_MyCustomRule";

       @Override
       public void enterSelect_stmt(TSqlParser.Select_stmtContext ctx) {
           // AST analysis logic
           reportIssue(ctx, "Descriptive violation message");
       }
   }
   ```
2. **Register the rule** in:
   - `TSqlRulesDefinition.java` (metadata, severity, remediation effort, and tags)
   - `TSqlQualityProfile.java` (activation in the default profile)
   - `TSqlSensor.java` (`RULE_MAP` mapping)
3. **Add the HTML documentation** to:
   - `src/main/resources/org/sonar/l10n/tsql/rules/tsqlcommunity/S111_MyCustomRule.html`
4. **Add unit tests** in `src/test/java/org/sonar/plugins/tsql/TSqlChecksTest.java`.
